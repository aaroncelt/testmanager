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
package testmanager.reporting.domain.reporting;

import java.util.HashSet;
import java.util.Set;

/**
 * The Class ErrorComment. Holds comment and other info about an error message.
 *
 * @author Istvan_Pamer
 */
public class ErrorComment {

    private String errorMessage;
    private String comment;
    private String type;
    private Set<String> linkedIds = new HashSet<String>();	// IDs for the test runs which are linked to this comment

    public ErrorComment(String comment, String type) {
        this.comment = comment;
        this.type = type;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getLinkedIds() {
        return linkedIds;
    }

    public ErrorComment addLinkedId(String id) {
        linkedIds.add(id);
        return this;
    }

    public ErrorComment addLinkedIds(Set<String> ids) {
        linkedIds.addAll(ids);
        return this;
    }

    public ErrorComment removeLinkedId(String id) {
        linkedIds.remove(id);
        return this;
    }

    public boolean hasLinkedIds() {
        return !linkedIds.isEmpty();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorComment [errorMessage=" + errorMessage + ", comment="
                + comment + ", type=" + type + ", linkedIds=" + linkedIds + "]";
    }

}
