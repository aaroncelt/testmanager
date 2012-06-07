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
package testmanager.reporting.dao.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import testmanager.reporting.dao.dto.CheckPointDto;
import testmanager.reporting.dao.dto.PairDto;
import testmanager.reporting.dao.dto.TestRunDto;
import testmanager.reporting.domain.reporting.CheckPoint;
import testmanager.reporting.domain.reporting.ResultState;
import testmanager.reporting.domain.reporting.TestRunData;
import testmanager.reporting.service.linkgeneration.LinkGeneratorStrategy;
import testmanager.reporting.service.reporting.ErrorCommentManager;
import testmanager.reporting.service.reporting.SetRunManager;

/**
 * The Class TestRunConverter.
 *
 * @author Istvan_Pamer
 */
public final class TestRunConverter {

    private TestRunConverter() {
    }

    /**
     * Convert data to dto.
     *
     * @param data the data
     * @return the test run dto
     */
    public static TestRunDto convertDataToDto(TestRunData data) {
        TestRunDto dto = new TestRunDto();
        dto.setSetName(data.getSetName());
        dto.setSetStartDate(data.getSetStartDate());
        dto.setTestName(data.getTestName());
        dto.setParamName(data.getParamName());
        dto.setEnvironment(convertMapToListOfPairs(data.getEnvironment()));
        dto.setParams(convertMapToListOfPairs(data.getParams()));
        dto.setCheckPoints(convertCheckPointsToCheckPointDtos(data.getCheckPoints()));
        dto.setErrorMessage(data.getErrorMessage());
        dto.setErrorComment(data.getErrorComment());
        dto.setErrorType(data.getErrorType());
        dto.setStartDate(data.getStartDate());
        dto.setStopDate(data.getStopDate());
        dto.setState(data.getState().toString());	// notice: this is not the same type: enum -> String
        return dto;
    }

    /**
     * Convert dto to data.
     *
     * @param dto the dto
     * @param strategy the strategy
     * @return the test run data
     */
    public static TestRunData convertDtoToData(TestRunDto dto, SetRunManager setRunManager, LinkGeneratorStrategy strategy, ErrorCommentManager errorCommentManager) {
        TestRunData data = TestRunData.createTestRunData(dto.getTestName(), dto.getParamName(), setRunManager, strategy, errorCommentManager);
        data.setEnvironment(convertListOfPairsToMap(dto.getEnvironment()));
        data.setParams(convertListOfPairsToMap(dto.getParams()));
        convertCheckPointDtosToCheckPoints(dto.getCheckPoints(), data);
        data.setErrorMessage(dto.getErrorMessage());
        data.setErrorComment(dto.getErrorComment(), dto.getErrorType());
        data.setStartDate(dto.getStartDate());
        data.setStopDate(dto.getStopDate());
        data.setState(ResultState.valueOf(dto.getState()));	// notice: this is not the same type: String -> enum
        data.setExecutionTime(dto.getStopDate().getTime() - dto.getStartDate().getTime());
        data.generateResultLink();
        return data;
    }

    private static List<PairDto> convertMapToListOfPairs(Map<String, String> map) {
        List<PairDto> result = null;
        if (map != null && !map.isEmpty()) {
            result = new ArrayList<PairDto>();
            for (String key : map.keySet()) {
                result.add(new PairDto(key, map.get(key)));
            }
        }
        return result;
    }

    private static Map<String, String> convertListOfPairsToMap(List<PairDto> list) {
        Map<String, String> result = null;
        if (list != null && !list.isEmpty()) {
            result = new HashMap<String, String>();
            for (PairDto pair : list) {
                result.put(pair.getLeft(), pair.getRight());
            }
        }
        return result;
    }

    private static List<CheckPointDto> convertCheckPointsToCheckPointDtos(List<CheckPoint> checkPoints) {
        List<CheckPointDto> result = null;
        if (checkPoints != null && !checkPoints.isEmpty()) {
            result = new ArrayList<CheckPointDto>();
            for (CheckPoint cp : checkPoints) {
                result.add(new CheckPointDto()
                    .setMessage(cp.getMessage())
                    .setMainType(cp.getMainType())
                    .setSubType(cp.getSubType())
                    .setState(cp.getState().toString()));
            }
        }
        return result;
    }

    private static void convertCheckPointDtosToCheckPoints(List<CheckPointDto> checkPointDtos, TestRunData data) {
        if (checkPointDtos != null && !checkPointDtos.isEmpty()) {
            for (CheckPointDto dto : checkPointDtos) {
                data.addCheckPoint(new CheckPoint()
                    .setMessage(dto.getMessage())
                    .setMainType(dto.getMainType())
                    .setSubType(dto.getSubType())
                    .setState(ResultState.valueOf(dto.getState())));
            }
        }
    }

}
