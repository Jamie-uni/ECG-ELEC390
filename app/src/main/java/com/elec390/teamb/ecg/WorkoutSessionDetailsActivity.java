package com.elec390.teamb.ecg;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

public class WorkoutSessionDetailsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session_details);
        String s = this.getIntent().getExtras().getString("SESSION_DETAILS");
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setText(s);
    }
}