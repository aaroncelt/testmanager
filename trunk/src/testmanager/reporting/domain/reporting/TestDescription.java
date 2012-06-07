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

import java.util.List;
import java.util.Set;

/**
 * The Class TestDescription.
 *
 * @author Istvan_Pamer
 */
public class TestDescription {

    private String testName;
    private Set<String> parameterSets;	// all known parameter sets for the test - like 1.item: a,b,c 2.item: 1,2,3
    private Set<String> containingSets;	// the test is in these test sets

    private List<String> preConditions;
    private List<String> postConditions;
    private List<String> stepsAndVPs;

    public Set<String> getContainingSets() {
        return containingSets;
    }

    public void setContainingSets(Set<String> containingSets) {
        this.containingSets = containingSets;
    }

    public List<String> getStepsAndVPs() {
        return stepsAndVPs;
    }

    public void setStepsAndVPs(List<String> stepsAndVPs) {
        this.stepsAndVPs = stepsAndVPs;
    }

    public Set<String> getParameterSets() {
        return parameterSets;
    }

    public void setParameterSets(Set<String> parameterSets) {
        this.parameterSets = parameterSets;
    }

    public List<String> getPreConditions() {
        return preConditions;
    }

    public void setPreConditions(List<String> preConditions) {
        this.preConditions = preConditions;
    }

    public List<String> getPostConditions() {
        return postConditions;
    }

    public void setPostConditions(List<String> postConditions) {
        this.postConditions = postConditions;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

}
