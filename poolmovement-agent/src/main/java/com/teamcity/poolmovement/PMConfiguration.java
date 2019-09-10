package com.teamcity.poolmovement;

public class PMConfiguration {
    private boolean myConfigEnabled = false;
    private int myConfigIdleTimeout = 30*60*60;
    private String myConfigIdlePoolName = null;
    private String myConfigIdleCron = "* * * ? * SUN,SAT *";

    public String getMyConfigIdleCron() {
        return myConfigIdleCron;
    }

    void setMyConfigIdleCron(String myConfigIdleCron) {
        this.myConfigIdleCron = myConfigIdleCron;
    }

    boolean isMyConfigEnabled() {
        return myConfigEnabled;
    }

    void setMyConfigEnabled(boolean myConfigEnabled) {
        this.myConfigEnabled = myConfigEnabled;
    }

    int getMyConfigIdleTimeout() {
        return myConfigIdleTimeout;
    }

    void setMyConfigIdleTimeout(int myConfigIdleTimeout) {
        this.myConfigIdleTimeout = myConfigIdleTimeout;
    }

    String getMyConfigIdlePoolName() {
        return myConfigIdlePoolName;
    }

    void setMyConfigIdlePoolName(String myConfigIdlePoolName) {
        this.myConfigIdlePoolName = myConfigIdlePoolName;
    }

    @Override
    public String toString() {
        return String.format("myConfigEnabled: %s%n" +
                "myConfigIdleTimeout: %s%n" +
                "myConfigIdlePoolName: %s%n" +
                "myConfigIdleCron: %s",
                myConfigEnabled,
                myConfigIdleTimeout,
                myConfigIdlePoolName,
                myConfigIdleCron);
    }
}
