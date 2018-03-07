package com.elec390.teamb.ecg;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class DataBaseTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_test);
        SessionDatabase sd = DatabaseInitializer.getDatabase(this);
        DatabaseInitializer.populateAsync(sd);
        List<SessionEntity> sessions = DatabaseInitializer.getSessions(sd);
        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);
        TextView tv3 = findViewById(R.id.tv3);
        TextView tv4 = findViewById(R.id.tv4);
        tv1.setText(DateTypeConverter.dateToString(sessions.get(0).mSessionStart));
        tv2.setText(DateTypeConverter.dateToString(sessions.get(0).mSessionEnd));
        tv3.setText(sessions.get(0).mSessionCommentsFileName);
        tv4.setText(sessions.get(0).mSessionDataFileName);
    }
}
