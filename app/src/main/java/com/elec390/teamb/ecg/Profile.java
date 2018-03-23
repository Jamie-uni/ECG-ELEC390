package com.elec390.teamb.ecg;

public class Profile
{
    private String profileName;
    private String profileEmail;

    public Profile(String name, String email)
    {
        profileName = name;
        profileEmail = email;
    }

    public Profile(Profile p)
    {
        profileName = p.getName();
        profileEmail = p.getEmail();
    }

    public void setName(String n)
    {
        profileName = n;
    }
    public void setEmail(String e)
    {
        profileEmail = e;
    }

    public String getName()
    {
        return profileName;
    }
    public String getEmail()
    {
        return profileEmail;
    }

}