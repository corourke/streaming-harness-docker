package com.kinetica.ktest;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller extends NotificationBroadcasterSupport implements ControllerMBean {

    // Thread safe state variables
    private AtomicInteger state = new AtomicInteger(2);
    // 0 EXIT
    // 1 RUNNING
    // 2 WAITING
    private AtomicInteger rate = new AtomicInteger(1);
    private long sequenceNumber;

    Controller() {
        sequenceNumber = 1;
    }

    @Override
    public void setRate(int newRate) {
        int oldRate = rate.get();
        rate.set(newRate);

        Notification n = new AttributeChangeNotification(this,
                sequenceNumber++, System.currentTimeMillis(),
                "Trx Rate Changed", "Rate", "int",
                oldRate, rate.get());

        sendNotification(n);
        System.out.println("Rate changed"); //TODO:
    }

    @Override
    public void setState(int newState) {
        int oldState = state.get();
        state.set(newState);

        Notification n = new AttributeChangeNotification(this,
                sequenceNumber++, System.currentTimeMillis(),
                "Trx State Changed", "State", "int",
                oldState, state.get());

        sendNotification(n);
        System.out.println("State changed"); //TODO:
    }

    @Override
    public int getRate() {
        return rate.get();
    }

    @Override
    public int getState() {
        return state.get();
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[]{
                AttributeChangeNotification.ATTRIBUTE_CHANGE
        };
        System.out.println("MBeanNotificationInfo called "); //TODO:
        String name = AttributeChangeNotification.class.getName();
        String description = "An attribute of this MBean has changed";
        MBeanNotificationInfo info =
                new MBeanNotificationInfo(types, name, description);
        return new MBeanNotificationInfo[]{info};
    }


}