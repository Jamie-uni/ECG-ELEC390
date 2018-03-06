package com.elec390.teamb.ecg;

// import java.time.LocalDate;
// import java.time.LocalTime;
import java.util.Date;
// import java.util.Map;

public class ECGSession {
//    private LocalDate date;
//    private LocalTime startTime;
//    private LocalTime stopTime:
    private Date startTime;
    private Date stopTime;
//    private Map<LocalTime,String> timestampedComments;
    public ECGSession() {
        startTime = new Date(); // Record the current system clock time
    }
    public void stopSession() {
        stopTime = new Date(); // Record the current system clock time
    }
}