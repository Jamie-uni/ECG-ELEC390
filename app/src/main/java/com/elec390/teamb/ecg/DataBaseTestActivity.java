package com.elec390.teamb.ecg;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class DataBaseTestActivity extends AppCompatActivity {
    private SessionDatabase sd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_test);
        sd = DatabaseInitializer.getDatabase(this);
        String text = printSessions();
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        tv1.setText(text);
    }
    public void generateSession(View v) {
        DatabaseInitializer.populateAsync(sd);
        String text = printSessions();
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        tv1.setText(text);
    }
    private String printSessions() {
        String text = "";
        List<SessionEntity> sessions = DatabaseInitializer.getSessions(sd);
        for(int i=0 ; i<sessions.size() ; i++) {
            text += "Session #" + sessions.get(i).sId + "\n"+ sessions.get(i).mSessionStart
                    + "\n" + sessions.get(i).mSessionEnd + "\n" + sessions.get(i).mSessionDataFileName
                    + "\n" + sessions.get(i).mSessionCommentsFileName + "\n";
        }
        return text;
    }
}
