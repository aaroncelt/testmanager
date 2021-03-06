/**
 * TestManager - test tracking and management system.
 * Copyright (C) 2012  Istvan Pamer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package testmanager.reporting.service.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import testmanager.reporting.domain.reporting.ErrorComment;
import testmanager.reporting.domain.reporting.TestRunData;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * The Class ErrorCommentManager. Handles the error messages and its comments for the system.
 *
 * @author Istvan_Pamer
 */
public class ErrorCommentManager {

    private int messageId = 0; // automatically incremented ID for the error messages in the memory
    private Map<String, Map<Integer, ErrorComment>> errorComments = Maps.newConcurrentMap(); // error message, auto inc. ID, comment object
    private Map<String, ErrorComment> errorCommentPatterns = Maps.newConcurrentMap();

    /**
     * Gets the comment for an ErrorComment.
     *
     * @param errorMessage the error message
     * @param errorId the error id
     * @return the comment
     */
    public String getComment(String errorMessage, Integer errorId) {
        String result = null;
        if (errorMessage != null && errorId != null && getErrorComments(errorMessage).get(errorId) != null) {
            result = getErrorComments(errorMessage).get(errorId).getComment();
        }
        return result;
    }

    /**
     * Gets the type for an ErrorComment.
     *
     * @param errorMessage the error message
     * @param errorId the error id
     * @return the type
     */
    public String getType(String errorMessage, Integer errorId) {
        String result = null;
        if (errorMessage != null && errorId != null && getErrorComments(errorMessage).get(errorId) != null) {
            result = getErrorComments(errorMessage).get(errorId).getType();
        }
        return result;
    }

    /**
     * Suggest comment for an error message.
     *
     * @param errorMessage the error message
     * @return the comment suggestion
     */
    public String getCommentSuggestion(String errorMessage) {
        return getSuggestedErrorComment(errorMessage).getComment();
    }

    /**
     * Suggest type for an error message.
     *
     * @param errorMessage the error message
     * @return the type suggestion
     */
    public String getTypeSuggestion(String errorMessage) {
        return getSuggestedErrorComment(errorMessage).getType();
    }

    private ErrorComment getSuggestedErrorComment(String errorMessage) {
        ErrorComment errorCommentFromPattern = getMatchingErrorComment(errorMessage);
        ErrorComment errorCommentFromHistory = getErrorComment(errorMessage);
        return Objects.firstNonNull(errorCommentFromPattern, errorCommentFromHistory);
    }

    private ErrorComment getErrorComment(String errorMessage) {
        ErrorComment retval = new ErrorComment(null, null);
        Map<Integer, ErrorComment> errorComments = getErrorComments(errorMessage);
        Iterator<Entry<Integer, ErrorComment>> it = errorComments.entrySet().iterator();
        while (it.hasNext() && retval.getComment() == null) {
            retval = it.next().getValue();
        }
        return retval;
    }

    private ErrorComment getMatchingErrorComment(String errorMessage) {
        String matchedKey = null;
        for (String key : errorCommentPatterns.keySet()) {
            Matcher matcher = Pattern.compile(key).matcher(Objects.firstNonNull(errorMessage, ""));
            if (matcher.find()) {
                matchedKey = key;
                break;
            }
        }
        return matchedKey != null ? errorCommentPatterns.get(matchedKey) : null;
    }

    private Map<Integer, ErrorComment> getErrorComments(String errorMessage) {
        Map<Integer, ErrorComment> errorCommentsMap = errorMessage != null ? errorComments.get(errorMessage) : null;
        return Objects.firstNonNull(errorCommentsMap, new HashMap<Integer, ErrorComment>());
    }

    /**
     * Sets the comment.
     *
     * @param errorMessage the error message
     * @param errorId the error id of the test which called the method. If null, then no comment was set for the test run.
     * @param comment the comment
     * @return the integer ID of the error message
     */
    public synchronized Integer setComment(String errorMessage, Integer errorId, ErrorComment comment) {
        Integer result = null;

        if (errorMessage != null) {
            Map<Integer, ErrorComment> map = getErrorComments(errorMessage);
            if (map.isEmpty()) {
                // no such error message yet in the memory
                map.put(messageId, comment);
                result = new Integer(messageId);
                messageId++;
                errorComments.put(errorMessage, map);
            } else {
                // check if the comment which we want to add, is already exists for the error message
                for (Integer i : map.keySet()) {
                    // TODO: can the comment and/or type be null?
                    if (map.get(i).getComment().equals(comment.getComment()) && map.get(i).getType().equals(comment.getType())) {
                        result = i;
                        map.get(i).addLinkedIds(comment.getLinkedIds());
                    }
                }
                // if comment already existed and modified remove old linkages (out of the scope of for because of ConcurrentModificationException)
                if (result != null && errorId != null) {
                    map.get(errorId).getLinkedIds().removeAll(comment.getLinkedIds());
                    if (map.get(errorId).getLinkedIds().isEmpty()) {
                        map.remove(errorId);
                    }
                    if (getErrorComments(errorMessage).isEmpty()) {
                        errorComments.remove(errorMessage);
                    }
                }

                // no such comment for the message in the memory yet in this if
                if (errorId == null && result == null) { // no comment yet for the test run
                    // putting new comment for the error message
                    map.put(messageId, comment);
                    result = new Integer(messageId);
                    messageId++;
                } else if (result == null) { // there already was a comment for the test run
                    if (map.get(errorId).getLinkedIds().size() == 1) {
                        // updating comment for the error message
                        map.put(errorId, comment);
                        result = errorId;
                    } else {
                        // if more, then create new
                        map.put(messageId, comment);
                        result = new Integer(messageId);
                        messageId++;
                        map.get(errorId).getLinkedIds().removeAll(comment.getLinkedIds());
                    }
                }
            }
        }

        return result;
    }

    /**
     * Removes linkings on the set's runs.
     *
     * @param setRunManager the set run manager
     */
    public synchronized void removeSetLinkings(SetRunManager setRunManager) {
        for (TestRunData data : setRunManager.getAllTestRunData()) {
            if (data.getErrorCommentId() != null // there was a comment for the run
                    && errorComments.get(data.getErrorMessage()) != null // the message exists in the map
                    && errorComments.get(data.getErrorMessage()).get(data.getErrorCommentId()) != null) { // the massage have the saved comment
                errorComments.get(data.getErrorMessage()).get(data.getErrorCommentId()).removeLinkedId(data.getIdFull());
            }
        }
    }

    /**
     * Clean the comments memory database.
     * Remove comments with zero linkings.
     * Remove error messages with zero comments.
     * Should be called only from DataLyfecycleManager. Used as a recurring job.
     */
    public synchronized void cleanComments() {
        for (String message : errorComments.keySet()) {
            if (getErrorComments(message) != null) {
                // remove comments with 0 links
                List<Integer> removeErrorIds = new ArrayList<Integer>();
                for (Integer errorId : getErrorComments(message).keySet()) {
                    if (!getErrorComments(message).get(errorId).hasLinkedIds()) {
                        removeErrorIds.add(errorId);
                    }
                }
                for (Integer errorId : removeErrorIds) {
                    getErrorComments(message).remove(errorId);
                }
            }
            if (getErrorComments(message) == null || getErrorComments(message).isEmpty()) {
                // remove message
                errorComments.remove(message);
            }
        }
    }

    public Map<String, ErrorComment> getErrorCommentPatterns() {
        return errorCommentPatterns;
    }

    public Map<String, Map<Integer, ErrorComment>> getErrorComments() {
        return errorComments;
    }

    public void deleteErrorCommentPattern(String pattern) {
        errorCommentPatterns.remove(pattern);
    }

    public void addErrorCommentPattern(String pattern, String type, String comment) {
        errorCommentPatterns.put(pattern, new ErrorComment(comment, type));
    }
}
