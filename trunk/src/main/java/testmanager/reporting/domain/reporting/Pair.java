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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A class aimed at enabling multiple return values in a type-safe way.
 *
 * @param <L> The type of the left-side object
 * @param <R> The type of the right-side object
 *
 * @author Istvan_Pamer
 */
public class Pair<L, R> {
    private L left;
    private R right;

    /**
     * Constructor for Pair object.
     */
    public Pair() {
    }

    /**
     * Constructor for Pair object.
     *
     * @param left The left-side object.
     * @param right The right-side object.
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a new pair.
     * This method uses cloning to create the new pair.
     *
     * @param <LV> type of left value
     * @param <RV> type of right value
     * @param left left value
     * @param right right value
     * @return a new pair with left and right value set
     */
    public static <LV, RV> Pair<LV, RV> create(LV left, RV right) {
        return new Pair<LV, RV>(left, right);
    }

    /**
     * Returns the left-side object.
     *
     * @return The left-side object.
     */
    public L getLeft() {
        return left;
    }

    /**
     * Set left.
     *
     * @param left the left
     */
    public void setLeft(L left) {
        this.left = left;
    }

    /**
     * Returns the right-side object of the pair.
     *
     * @return The right-side object.
     */
    public R getRight() {
        return right;
    }

    /**
     * Set right.
     *
     * @param right the right
     */
    public void setRight(R right) {
        this.right = right;
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
