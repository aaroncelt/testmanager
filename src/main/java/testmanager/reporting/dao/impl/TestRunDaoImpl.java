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
package testmanager.reporting.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.StopWatch;

import testmanager.reporting.dao.TestRunDao;
import testmanager.reporting.dao.dto.CheckPointDto;
import testmanager.reporting.dao.dto.LabelDto;
import testmanager.reporting.dao.dto.PairDto;
import testmanager.reporting.dao.dto.TestRunDto;
import testmanager.reporting.domain.reporting.ErrorComment;

/**
 * The Class TestRunDaoImpl.
 * 
 * @author Istvan_Pamer
 */
public class TestRunDaoImpl extends SqlMapClientDaoSupport implements TestRunDao {

	@Override
	public void insertTestRun(TestRunDto testRunDto) {
		SqlMapClientTemplate template = getSqlMapClientTemplate();

		// If line 38 synchronization fails, check tables with this SQL
		// SELECT count(*) AS cnt1,`ErrorMessage`,`MessageId` FROM
		// `test_run_message`
		// WHERE `Comment` is NULL
		// GROUP BY `ErrorMessage`
		// HAVING cnt1 > 1

		// insert test run data if its not inserted
		if ((Integer) template.queryForObject("getTestRunDataId", testRunDto) == null) {
			// insert error message for the run if the message is available
			Integer errorMessageId = null;
			if (testRunDto.getErrorMessage() != null && !testRunDto.getErrorMessage().isEmpty()) {
				synchronized (testRunDto.getErrorMessage()) { // must run single
																// threaded
					// check if the message is already in the DB with NULL
					// comment
					errorMessageId = (Integer) template.queryForObject("getTestRunMessageIdNullComment", testRunDto);
					if (errorMessageId == null) {
						// if not, add the message with NULL comment
						testRunDto.setErrorComment(null);
						errorMessageId = (Integer) template.insert("insertTestRunMessage", testRunDto);
					}
				}
			}
			testRunDto.setErrorMessageId(errorMessageId);
			template.insert("insertTestRunData", testRunDto);

			// insert params for the run if available
			if (testRunDto.getParams() != null && !testRunDto.getParams().isEmpty()) {
				template.insert("insertTestRunParams", testRunDto);
			}
			// insert envitonment for the run if available
			if (testRunDto.getEnvironment() != null && !testRunDto.getEnvironment().isEmpty()) {
				template.insert("insertTestRunEnv", testRunDto);
			}
			// insert check points for the run if available
			if (testRunDto.getCheckPoints() != null && !testRunDto.getCheckPoints().isEmpty()) {
				template.insert("insertTestRunCheckPoints", testRunDto);
			}
			// insert labels for the run if available
			if (testRunDto.getLabels() != null && !testRunDto.getLabels().isEmpty()) {
				template.insert("insertTestRunLabels", testRunDto);
			}
		}
	}

	@Override
	public void updateTestRun(TestRunDto testRunDto) {
		SqlMapClientTemplate template = getSqlMapClientTemplate();

		// 1. Every new message is put into the DB with NULL comment
		// 2. A message with NULL comment will get a suggestion from the memory
		// and it is not saved until the user saves it from the UI

		// update test run message
		Integer errorMessageId = null;
		String comment = null;
		if (testRunDto.getErrorMessage() != null && !testRunDto.getErrorMessage().isEmpty()) {
			// a comment is coming for a valid error message with NULL or
			// already updated comment
			comment = (String) template.queryForObject("getCommentForTestRunData", testRunDto); // get
																								// Comment
																								// for
																								// SetName
																								// AND
																								// SetDate
																								// AND
																								// TestName
																								// AND
																								// ParamName
			if (comment == null) { // no comment for the test run
				errorMessageId = (Integer) template.queryForObject("getErrorCommentId", testRunDto); // get
																										// MessageId
																										// for
																										// ErrorMessage
																										// AND
																										// Comment
																										// AND
																										// Type
				if (errorMessageId != null) { // update message id for the run
					testRunDto.setErrorMessageId(errorMessageId);
					template.update("updateTestRunDataMessageId", testRunDto);
				} else { // insert new comment
					errorMessageId = (Integer) template.insert("insertTestRunMessage", testRunDto);
					testRunDto.setErrorMessageId(errorMessageId);
					template.update("updateTestRunDataMessageId", testRunDto);
				}
			} else { // there was a comment for the test run
				// getMessageIdForTestRunData - get TestRunMessageId for
				// SetName, SetDate, TestName, ParamName
				// getErrorCommentId - get MessageId for ErrorMessage, Comment,
				// Type
				// getCommentCount - get Comment number for ErrorMessage,
				// Comment, Type (tables: data x message)
				// getMessageReferenceCount - get TestRunMessageId reference
				// number - TestRunMessageId=#value#
				Integer oldId = (Integer) template.queryForObject("getMessageIdForTestRunData", testRunDto); // get
																												// the
																												// ID
																												// of
																												// the
																												// current
																												// (old)
																												// comment
				errorMessageId = (Integer) template.queryForObject("getErrorCommentId", testRunDto); // check
																										// if
																										// the
																										// new
																										// comment
																										// is
																										// already
																										// exists
																										// in
																										// the
																										// DB
				if (errorMessageId != null) { // the new comment was found in
												// the DB
					// replace the ID on the test run for the new message
					testRunDto.setErrorMessageId(errorMessageId);
					template.update("updateTestRunDataMessageId", testRunDto);
					// check if the old comment has references - if not remove
					// it
					if ((Integer) template.queryForObject("getMessageReferenceCount", oldId) == 0) { // get
																										// the
																										// number
																										// of
																										// references
																										// on
																										// the
																										// old
																										// id
						template.delete("deleteErrorComment", oldId);
					}
				} else { // the new comment was not found in the DB - new
							// comment should be inserted
					if ((Integer) template.queryForObject("getMessageReferenceCount", oldId) == 1) { // 1
																										// reference
																										// -
																										// update
																										// old
																										// comment
						errorMessageId = (Integer) template.queryForObject("getMessageIdForTestRunData", testRunDto);
						testRunDto.setErrorMessageId(errorMessageId);
						template.update("updateTestRunMessageComment", testRunDto);
					} else { // more reference - put new comment, update the
								// messageId reference for the test run
						errorMessageId = (Integer) template.insert("insertTestRunMessage", testRunDto);
						testRunDto.setErrorMessageId(errorMessageId);
						template.update("updateTestRunDataMessageId", testRunDto);
					}
				}
			}
		}
	}

	@Override
	public List<TestRunDto> getTestRunFromDate(Date date) {
		SqlMapClientTemplate template = getSqlMapClientTemplate();

		@SuppressWarnings("unchecked")
		List<TestRunDto> result = template.queryForList("getTestRunDataListFromDate", date);
		@SuppressWarnings("unchecked")
		List<PairDto> params = template.queryForList("getTestRunParamsForId", date);
		@SuppressWarnings("unchecked")
		List<PairDto> env = template.queryForList("getTestRunEnvForId", date);
		@SuppressWarnings("unchecked")
		List<CheckPointDto> checkPoints = template.queryForList("getTestRunCheckPointsForId", date);
		@SuppressWarnings("unchecked")
		List<LabelDto> labels = template.queryForList("getTestRunLabelsForId", date);

		int counterParam = 0;
		int counterEnv = 0;
		int counterCP = 0;
		int counterLabels = 0;
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (int i = 0; i < result.size(); i++) {
			while (counterParam < params.size() && result.get(i).getTestRunId().equals(params.get(counterParam).getId())) {
				if (result.get(i).getParams() == null) {
					result.get(i).setParams(new ArrayList<PairDto>());
				}
				result.get(i).getParams().add(params.get(counterParam));
				counterParam++;
			}
			while (counterEnv < env.size() && result.get(i).getTestRunId().equals(env.get(counterEnv).getId())) {
				if (result.get(i).getEnvironment() == null) {
					result.get(i).setEnvironment(new ArrayList<PairDto>());
				}
				result.get(i).getEnvironment().add(env.get(counterEnv));
				counterEnv++;
			}
			while (counterCP < checkPoints.size() && result.get(i).getTestRunId().equals(checkPoints.get(counterCP).getId())) {
				if (result.get(i).getCheckPoints() == null) {
					result.get(i).setCheckPoints(new ArrayList<CheckPointDto>());
				}
				result.get(i).getCheckPoints().add(checkPoints.get(counterCP));
				counterCP++;
			}
			while (counterLabels < labels.size() && result.get(i).getTestRunId().equals(labels.get(counterLabels).getTestRunId())) {
				if (result.get(i).getLabels() == null) {
					result.get(i).setLabels(new ArrayList<LabelDto>());
				}
				result.get(i).getLabels().add(labels.get(counterLabels));
				counterLabels++;
			}

		}
		stopWatch.stop();

		System.out.println(stopWatch.getTotalTimeSeconds());
		return result;
	}

	@Override
	public void deleteSetRun(String setName, Date setStartDate) {
		SqlMapClientTemplate template = getSqlMapClientTemplate();

		TestRunDto dto = new TestRunDto();
		dto.setSetName(setName);
		dto.setSetStartDate(setStartDate);
		@SuppressWarnings("unchecked")
		List<Integer> result = template.queryForList("getSetRunIds", dto); // size
																			// ==
																			// 0
																			// if
																			// no
																			// results
		if (result.size() != 0) {
			template.delete("deleteSetRun", dto);
			template.delete("deleteSetRunParams", result);
			template.delete("deleteSetRunEnv", result);
			template.delete("deleteSetRunCheckPoints", result);
		}
	}

	@Override
	public void deleteErrorComment(ErrorComment removed) {
		SqlMapClientTemplate template = getSqlMapClientTemplate();

		@SuppressWarnings("unchecked")
		List<Integer> result = template.queryForList("getErrorCommentIds", removed); // size
																						// ==
																						// 0
																						// if
																						// no
																						// results
																						// -
																						// this
																						// should
																						// return
																						// 1
																						// element
		for (Integer i : result) {
			template.delete("clearCommentForTestRun", i);
			template.update("deleteErrorComment", i);
		}
	}

	@Override
	public void cleanTestRunMessage() {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		@SuppressWarnings("unchecked")
		List<Integer> result = template.queryForList("selectCleanTestRunMessage"); // size
																					// ==
																					// 0
																					// if
																					// no
																					// results
		if (result.size() != 0) {
			template.delete("cleanTestRunMessage", result);
		}
	}

	@Override
	public void cleanOldTestRunData(Date keepFromDate) {
		SqlMapClientTemplate template = getSqlMapClientTemplate();

		template.delete("cleanTestRunParamsOlderThen", keepFromDate);
		template.delete("cleanTestRunEnvOlderThen", keepFromDate);
		template.delete("cleanTestRunCheckPointsOlderThen", keepFromDate);
		template.delete("cleanTestRunDataOlderThen", keepFromDate); // must be
																	// the last
																	// one
	}

}
