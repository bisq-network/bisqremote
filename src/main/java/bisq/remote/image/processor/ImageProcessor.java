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

package bisq.remote.image.processor;

import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Processes a {@link BufferedImage} and returns an {@link Optional<T>} containing the
 * result if successful, or {@link Optional#empty()} if there is no result.
 * @param <T> The object type of the result.
 */
public interface ImageProcessor<T> {
    Optional<T> process(BufferedImage image);
}
