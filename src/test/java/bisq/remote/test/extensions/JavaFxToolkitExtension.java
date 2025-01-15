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

package bisq.remote.test.extensions;

import bisq.remote.test.utils.JavaFxThreadUtil;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * This JUnit 5 extension is used to start a JavaFX thread before executing tests, so that you can
 * use {@link Platform#runLater} and similar constructs in your tests.
 *
 * <p>However, this extension will not execute tests on the JavaFX thread. To do so, use {@link
 * JavaFxThreadUtil#runInFxThread(Runnable)}.
 */
public class JavaFxToolkitExtension implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // The JavaFX runtime is already running
        }
    }
}
