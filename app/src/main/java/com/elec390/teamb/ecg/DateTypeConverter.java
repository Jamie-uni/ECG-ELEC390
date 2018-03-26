package com.elec390.teamb.ecg;

import android.arch.persistence.room.TypeConverter;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date type converters for Room
 */

public class DateTypeConverter {
    private static final String DATE_FORMAT = "MM-dd-yyyyy-h:mm:ssaa";
    // Method stringToDate converts a String with the formart
    @TypeConverter
    public static Date stringToDate(String value) {
        Date d = null;
        try {
            d = value == null ? null : new SimpleDateFormat(DATE_FORMAT).parse(value);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }
        return d;
    }
    @TypeConverter
    public static String dateToString(Date date) {
        return date == null ? null : DateFormat.format(DATE_FORMAT, date).toString();
    }
}