package com.elec390.teamb.ecg;

import java.util.Date;

/**
 * Used for storing the stopTime, startTime, and timestampedComments of the
 * current ECG session
 */
public class ECGSession {
    private final Date startTime;
    private Date stopTime;
    private String timestampedComments = "";
    public ECGSession() {
        startTime = new Date(); // Record the current system clock time
    }
    public void stopSession() {
        stopTime = new Date(); // Record the current system clock time
    }
    public void addComment(String c) {
        Date d = new Date();
        timestampedComments += DateTypeConverter.dateToString(d) + ":   " + c + "\n";
    }
    public Date getStartTime() {
        return startTime;
    }
    public Date getStopTime() {
        return stopTime;
    }
    public String getTimestampedComments() {
        return timestampedComments;
    }
}