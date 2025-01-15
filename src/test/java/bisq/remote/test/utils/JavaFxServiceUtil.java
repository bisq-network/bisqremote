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
import javafx.concurrent.Service;
import javafx.concurrent.Worker;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Utility for working with a JavaFX {@link Service}, for testing purposes.
 *
 * @param <V> the type of object returned by the Service
 */
public class JavaFxServiceUtil<V> {
    private static final long DEFAULT_TIMEOUT_MILLIS = 30_000L;

    private final Service<V> service;
    private final long timeout;

    /**
     * By default, the maximum time to wait for the service will be {@value DEFAULT_TIMEOUT_MILLIS}
     * milliseconds.
     *
     * @param service the {@link Service} that you want to work with
     */
    public JavaFxServiceUtil(Service<V> service) {
        this(service, DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * @param service the {@link Service} that you want to work with
     * @param timeout the maximum time to wait for the service in milliseconds
     */
    public JavaFxServiceUtil(Service<V> service, long timeout) {
        this.service = service;
        this.timeout = timeout;
    }

    /**
     * Wait for the service to transition to a desired state.
     *
     * @return {@link ServiceStatus} representing the status of the service when it transitioned to
     *     the desired state.
     */
    public ServiceStatus<V> waitForServiceDesiredState(Worker.State desiredState) {
        CompletableFuture<ServiceStatus<V>> stateCompletableFuture =
                createDesiredStateCompletableFuture(desiredState);
        return getFuture(stateCompletableFuture, "Service did not change state to " + desiredState);
    }

    /**
     * Wait for the service to update its value from the specified current value.
     *
     * @return {@link ServiceStatus} representing the status of the service when it updated its
     *     value.
     */
    public ServiceStatus<V> waitForServiceUpdateValue(V currentValue) {
        CompletableFuture<ServiceStatus<V>> valueUpdateCompletableFuture =
                createValueUpdateCompletableFuture(currentValue);
        return getFuture(valueUpdateCompletableFuture, "Service did not update its value");
    }

    /**
     * Wait for the service to have a null value.
     *
     * @return {@link ServiceStatus} representing the status of the service when its value changed
     *     to null.
     */
    public ServiceStatus<V> waitForServiceNullValue() {
        CompletableFuture<ServiceStatus<V>> valueNullCompletableFuture =
                createValueNullCompletableFuture();
        return getFuture(valueNullCompletableFuture, "Service did not update its value to null");
    }

    /**
     * Waits if necessary for at most the number of milliseconds defined by {@link #timeout} for the
     * future to complete, and then returns its result.
     */
    private ServiceStatus<V> getFuture(
            CompletableFuture<ServiceStatus<V>> completableFuture, String assertionErrorMessage) {
        try {
            return completableFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            throw new AssertionError(assertionErrorMessage);
        }
    }

    /**
     * Creates a {@link CompletableFuture} that informs us when the state of the service has reached
     * a desired state, returning the status of the service at that given time.
     */
    private CompletableFuture<ServiceStatus<V>> createDesiredStateCompletableFuture(
            Worker.State desiredState) {
        CompletableFuture<ServiceStatus<V>> completableFuture = new CompletableFuture<>();
        Platform.runLater(
                () -> {
                    if (Objects.equals(service.getState(), desiredState)) {
                        completableFuture.complete(completeFuture());
                    }
                    service.stateProperty()
                            .addListener(
                                    (observable, oldValue, newValue) -> {
                                        if (newValue.equals(desiredState)) {
                                            completableFuture.complete(completeFuture());
                                        }
                                    });
                });
        return completableFuture;
    }

    /**
     * Creates a {@link CompletableFuture} that informs us when the value of the service has
     * changed, returning the status of the service at that given time.
     */
    private CompletableFuture<ServiceStatus<V>> createValueUpdateCompletableFuture(V currentValue) {
        CompletableFuture<ServiceStatus<V>> completableFuture = new CompletableFuture<>();
        Platform.runLater(
                () -> {
                    if (!Objects.equals(service.getValue(), currentValue)) {
                        completableFuture.complete(completeFuture());
                    }
                    service.valueProperty()
                            .addListener(
                                    (observable, oldValue, newValue) -> {
                                        if (!Objects.equals(newValue, currentValue)) {
                                            completableFuture.complete(completeFuture());
                                        }
                                    });
                });
        return completableFuture;
    }

    /**
     * Creates a {@link CompletableFuture} that informs us when the value of the service is null,
     * returning the status of the service at that given time.
     */
    private CompletableFuture<ServiceStatus<V>> createValueNullCompletableFuture() {
        CompletableFuture<ServiceStatus<V>> completableFuture = new CompletableFuture<>();
        Platform.runLater(
                () -> {
                    if (Objects.isNull(service.getValue())) {
                        completableFuture.complete(completeFuture());
                    }
                    service.valueProperty()
                            .addListener(
                                    (observable, oldValue, newValue) -> {
                                        if (newValue == null) {
                                            completableFuture.complete(completeFuture());
                                        }
                                    });
                });
        return completableFuture;
    }

    /** Complete the future by returning the current status of the service. */
    private ServiceStatus<V> completeFuture() {
        return new ServiceStatus<>(
                service.getMessage(), service.getState(),
                service.getValue(), service.getException());
    }

    /** DTO used for representing the status of the service. */
    public record ServiceStatus<V>(
            String message, Worker.State state, V value, Throwable exception) {}
}
