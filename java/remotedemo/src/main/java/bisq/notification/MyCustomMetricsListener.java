package bisq.notification;

import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientMetricsListener;

public class MyCustomMetricsListener implements ApnsClientMetricsListener {
    @Override
    public void handleWriteFailure(ApnsClient apnsClient, long l) {

    }

    @Override
    public void handleNotificationSent(ApnsClient apnsClient, long l) {

    }

    @Override
    public void handleNotificationAccepted(ApnsClient apnsClient, long l) {

    }

    @Override
    public void handleNotificationRejected(ApnsClient apnsClient, long l) {

    }

    @Override
    public void handleConnectionAdded(ApnsClient apnsClient) {

    }

    @Override
    public void handleConnectionRemoved(ApnsClient apnsClient) {

    }

    @Override
    public void handleConnectionCreationFailed(ApnsClient apnsClient) {

    }
}
