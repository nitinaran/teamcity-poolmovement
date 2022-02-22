package com.teamcity.poolmovement;

import org.jetbrains.annotations.NotNull;

import java.util.Date;


public class BuildQueueState {

    private final boolean myQueueEnabled;

    @NotNull
    private final Date myTimestamp;

    public BuildQueueState(boolean enabled,
                           @NotNull Date timestamp) {
        myQueueEnabled = enabled;
        myTimestamp = timestamp;
    }

    public boolean isQueueEnabled() {
        return myQueueEnabled;
    }

    @NotNull
    public Date getTimestamp() {
        return myTimestamp;
    }

    @NotNull
    public boolean isPoolMovementEnabled() {
        boolean result = false;
        long difference = new Date().getTime() - getTimestamp().getTime();
        long TIME_DIFFERENCE_MS = 600000;
        if (isQueueEnabled() && difference > TIME_DIFFERENCE_MS) {
            result = true;
        }
        return result;
    }
}