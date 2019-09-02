package com.kinetica.ktest;

public interface ControllerMBean {

    public void setRate(int newRate);

    public void setState(int newState);

    public int getRate();

    public int getState();

}