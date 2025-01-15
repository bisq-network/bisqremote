# Bisq Remote

This tool is intended to be used during development and testing of the Bisq Android and iOS apps.
It can be used to send push notifications rather than relying on the Bisq client to send the notifications.
More documentation can be found [here](https://github.com/bisq-network/bisqremote/wiki).

## Prerequisites

The following is necessary to build and run this application.
- Java 17

## How to Build

```shell
./mvnw clean install
```

## How to Run

```shell
./mvnw clean javafx:run
```
