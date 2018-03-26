package com.elec390.teamb.ecg;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    // Define private member variables
    private EditText nameEditText, emailEditText;
    private Button saveButton;
    private Profile profile;
    private SharedPreferenceHelper sharedPreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Use the controller SharedPreferenceHelper to retrieve the profile from SharedPreferences
        sharedPreferenceHelper = new SharedPreferenceHelper(SettingsActivity.this);
        profile = new Profile(sharedPreferenceHelper.getProfile());
        // Setup the EditTexts and save Button
          nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        nameEditText.setText(profile.getName(), TextView.BufferType.NORMAL);
        emailEditText.setText(profile.getEmail(), TextView.BufferType.NORMAL);
        nameEditText.setFocusable(false);
        emailEditText.setFocusable(false);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setVisibility(View.INVISIBLE);
    }
    // Creates the options menu, which contains Edit
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        return true;
    }
    // Called when an item from the options menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfileButton:
                // Edit is selected, the EditTexts become editable and the save button appears
                Toast.makeText(SettingsActivity.this, "Switching to edit mode...", Toast.LENGTH_SHORT).show();
                nameEditText.setFocusableInTouchMode(true);
                emailEditText.setFocusableInTouchMode(true);
                saveButton.setVisibility(View.VISIBLE);
  //              toolBar.setTitle("Edit Profile");
                return true;
            default:
                // Invoke the superclass
                return super.onOptionsItemSelected(item);
        }
    }

    // Method called from the xml layouts saveButton onClick. If the user input is valid,
    // the profile gets changed and stored in SharedPreferences
    public void saveChanges(View view) {
        if(checkForValidInput()) {
            Toast.makeText(SettingsActivity.this, "Saving profile...", Toast.LENGTH_SHORT).show();
            profile.setName(nameEditText.getText().toString());
            profile.setEmail(emailEditText.getText().toString());
            sharedPreferenceHelper.saveProfile(profile);
            nameEditText.setFocusable(false);
            emailEditText.setFocusable(false);
            saveButton.setVisibility(View.INVISIBLE);
        }
    }
    // Checks if the user input in the EditTexts is valid. Constraints for the upper bounds
    // were made in activity_profile.xml using maxLength
    private boolean checkForValidInput() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        if(name.length() < 3) {
            Toast.makeText(SettingsActivity.this, "Name is too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(email.length() < 6) {
            Toast.makeText(SettingsActivity.this, "Email is too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SettingsActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }
}