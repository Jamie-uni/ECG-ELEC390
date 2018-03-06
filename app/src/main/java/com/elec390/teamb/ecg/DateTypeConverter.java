package com.elec390.teamb.ecg;

import android.arch.persistence.room.TypeConverter;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date converter for database
 */
public class DateTypeConverter {
    @TypeConverter
    public Date fromTimestamp(String value) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyyy-h:mm:ssaa");
        return value == null ? null : sdf.parse(value);
    }

    @TypeConverter
    public String dateToTimestamp(Date date) {
        return date == null ? null : DateFormat.format("MM-dd-yyyyy-h:mm:ssaa", date).toString();
    }
}