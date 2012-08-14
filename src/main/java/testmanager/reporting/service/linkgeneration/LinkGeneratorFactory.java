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
package testmanager.reporting.service.linkgeneration;

import java.util.Map;

/**
 * A factory for creating LinkGenerator objects.
 *
 * @author Istvan_Pamer
 */
public class LinkGeneratorFactory {

    Map<String, LinkGeneratorStrategy> strategies;

    /**
     * Instantiates a new link generator factory.
     *
     * @param strategies the strategies mapped for test sets
     */
    public LinkGeneratorFactory(Map<String, LinkGeneratorStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Creates a new LinkGenerator object.
     *
     * @return the link generator strategy
     */
    public LinkGeneratorStrategy getLinkGeneratorStrategy(String setName) {
        LinkGeneratorStrategy result = strategies.get("default");
        if (strategies.get(setName) != null) {
            result = strategies.get(setName);
        }
        return result;
    }

}
