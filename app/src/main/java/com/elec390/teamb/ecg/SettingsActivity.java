package com.elec390.teamb.ecg;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity
{
    protected Toolbar tool_bar;
    protected EditText name_edit_text;
    protected EditText email_edit_text;
    protected Button save_button;
    protected Profile profile;
    protected SharedPreferenceHelper shared_preference_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tool_bar = (Toolbar) findViewById(R.id.toolbar);
        tool_bar.setTitle("User Profile Information");
        setSupportActionBar(tool_bar);
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);
        shared_preference_helper = new SharedPreferenceHelper(SettingsActivity.this);
        profile = new Profile(shared_preference_helper.getProfile());
        name_edit_text = (EditText) findViewById(R.id.nameEditText);
        email_edit_text = (EditText) findViewById(R.id.emailEditText);
        name_edit_text.setText(profile.getName(), TextView.BufferType.NORMAL);
        email_edit_text.setText(profile.getEmail(), TextView.BufferType.NORMAL);
        name_edit_text.setFocusable(false);
        email_edit_text.setFocusable(false);
        save_button = (Button) findViewById(R.id.saveSettingsButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflate_menu = getMenuInflater();
        inflate_menu.inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.editProfileButton:
                Toast.makeText(SettingsActivity.this, "You are now in Edit Mode", Toast.LENGTH_SHORT).show();

                name_edit_text.setFocusableInTouchMode(true);
                email_edit_text.setFocusableInTouchMode(true);
                save_button.setVisibility(View.VISIBLE);
                tool_bar.setTitle("Edit Mode");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveChanges(View view)
    {
        Toast.makeText(SettingsActivity.this, "All data entered is being saved.", Toast.LENGTH_SHORT).show();

        profile.setName(name_edit_text.getText().toString());
        profile.setEmail(email_edit_text.getText().toString());
        shared_preference_helper.saveProfile(profile);
        name_edit_text.setFocusable(false);
        email_edit_text.setFocusable(false);
        save_button.setVisibility(View.INVISIBLE);
        tool_bar.setTitle("User Profile Information");
    }
}