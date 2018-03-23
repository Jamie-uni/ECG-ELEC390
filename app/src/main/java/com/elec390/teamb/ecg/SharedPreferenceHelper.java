package com.elec390.teamb.ecg;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper
{
    private final SharedPreferences sharedPreferences;

    //constrcutor
    public SharedPreferenceHelper(Context context)
    {
        sharedPreferences = context.getSharedPreferences("ProfilePreference",
                Context.MODE_PRIVATE);
    }

    //save a profile by storing the class variables in SharedPreferences
    public void saveProfile(Profile profile)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profileName", profile.getName());
        editor.putString("profileEmail", profile.getEmail());
        editor.apply();
        editor.commit();
    }

    //returns the profile saved in SharedPreferences
    public Profile getProfile()
    {

        return new Profile(sharedPreferences.getString("profileName", null),
                sharedPreferences.getString("profileEmail", null));
    }
}
