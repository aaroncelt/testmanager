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
package testmanager.reporting.dao.dto;


/**
 * The Class CheckPointDto.
 *
 * @author Istvan_Pamer
 */
public class CheckPointDto {

    private Integer id;
    private String message;		// message for the checkpoint.
    private String mainType;	// main type of the checkpoint. Can be the page where the check occurs when checking web applications.
    private String subType;		// sub type of the checkpoint.
    private String state;		// state of the check (ResultState) - passed / failed

    public Integer getId() {
        return id;
    }
    public CheckPointDto setId(Integer id) {
        this.id = id;
        return this;
    }
    public String getMessage() {
        return message;
    }
    public CheckPointDto setMessage(String message) {
        this.message = message;
        return this;
    }
    public String getMainType() {
        return mainType;
    }
    public CheckPointDto setMainType(String mainType) {
        this.mainType = mainType;
        return this;
    }
    public String getSubType() {
        return subType;
    }
    public CheckPointDto setSubType(String subType) {
        this.subType = subType;
        return this;
    }
    public String getState() {
        return state;
    }
    public CheckPointDto setState(String state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        return "CheckPointDto [id=" + id + ", message=" + message
                + ", mainType=" + mainType + ", subType=" + subType
                + ", state=" + state + "]";
    }

}
