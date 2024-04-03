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

package bisq.remote.test.utils;

import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Utility methods for working with JavaFX threads. */
public final class JavaFxThreadUtil {
    private static final long DEFAULT_TIMEOUT_MILLIS = 5000L;

    /**
     * This method is used to execute code on the JavaFX thread. It is intended to be used in
     * testing scenarios only because the internal exception handling is optimized for testing
     * purposes.
     *
     * <p>This method is blocking. It will execute the given runnable and wait for it to be
     * finished. If the runnable does not complete within {@value DEFAULT_TIMEOUT_MILLIS}
     * milliseconds, a {@link TimeoutException} will be thrown. If you need to specify the timeout
     * value, use {@link #runInFxThread(Runnable, long)}.
     *
     * <p>This method will catch exceptions produced by executing the runnable. If an {@link
     * AssertionError} is thrown by the runnable, for example because a test assertion has failed,
     * the assertion error will be rethrown by this method so that the JUnit test runner will
     * properly handle it and show the test failure.
     *
     * @param code the code to execute on the JavaFX thread
     */
    public static void runInFxThread(Runnable code)
            throws ExecutionException, InterruptedException, TimeoutException {
        runInFxThread(code, DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * Similar to {@link #runInFxThread(Runnable)}, but takes a timeout parameter.
     *
     * @param code the code to execute on the JavaFX thread
     * @param timeout the maximum time to wait in milliseconds before execution is aborted
     */
    public static void runInFxThread(Runnable code, long timeout)
            throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Object> future = new CompletableFuture<>();

        Platform.runLater(
                () -> {
                    try {
                        code.run();
                    } catch (AssertionError e) {
                        future.complete(e);
                        return;
                    }
                    future.complete(null);
                });

        Object result = future.get(timeout, TimeUnit.MILLISECONDS);

        if (result instanceof AssertionError assertionError) {
            throw assertionError;
        }
    }

    /**
     * This method is used to wait until the UI thread has done all work that was queued via {@link
     * Platform#runLater(Runnable)}.
     *
     * <p>If the UI thread does not complete within {@value DEFAULT_TIMEOUT_MILLIS} milliseconds, a
     * {@link TimeoutException} will be thrown. If you need to specify the timeout value, use {@link
     * #waitForUiThread(long)}.
     */
    public static void waitForUiThread()
            throws ExecutionException, InterruptedException, TimeoutException {
        waitForUiThread(DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * Similar to {@link #waitForUiThread()}, but takes a timeout parameter.
     *
     * @param timeout the maximum time to wait in milliseconds
     */
    public static void waitForUiThread(long timeout)
            throws ExecutionException, InterruptedException, TimeoutException {
        runInFxThread(() -> {}, timeout);
    }
}
