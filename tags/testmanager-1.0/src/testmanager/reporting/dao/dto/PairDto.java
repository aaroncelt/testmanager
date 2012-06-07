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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Class Pair.
 *
 * @author Istvan_Pamer
 */
public class PairDto {

    private Integer id;
    private String left;
    private String right;

    public PairDto() {
    }

    public PairDto(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public final boolean equals(Object other) {
        boolean result;
        if (this == other) {
            result = true;
        } else if (other == null) {
            result = false;
        } else if (getClass().isAssignableFrom(other.getClass())) {
            result = EqualsBuilder.reflectionEquals(this, other);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "PairDto [id=" + id + ", left=" + left + ", right=" + right
                + "]";
    }

}
