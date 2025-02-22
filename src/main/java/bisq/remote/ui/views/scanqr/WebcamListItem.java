/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.remote.ui.views.scanqr;

import com.github.sarxos.webcam.Webcam;

import java.util.Objects;

public record WebcamListItem(Webcam webcam) {
    @Override
    public String toString() {
        return webcam.getName();
    }

    @Override
    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof WebcamListItem)) {
            return false;
        }
        return Objects.equals(this.toString(), otherObject.toString());
    }
}
