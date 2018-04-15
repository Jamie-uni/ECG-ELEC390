package com.elec390.teamb.ecg;

/**
 * Class holds doctor and patient information
 */
public class Profile
{
    private String profileName;
    private String profileEmail;
    private String profileDrName;
    private String profileDrEmail;

    public Profile(String name, String email, String drName, String drEmail)
    {
        profileName = name;
        profileEmail = email;
        profileDrName = drName;
        profileDrEmail = drEmail;
    }

    public Profile(Profile p)
    {
        profileName = p.getName();
        profileEmail = p.getEmail();
        profileDrName = p.getDrName();
        profileDrEmail = p.getDrEmail();
    }

    public void setName(String n)
    {
        profileName = n;
    }
    public void setEmail(String e) {profileEmail = e;}
    public void setDrName(String D) {profileDrName = D; }
    public void setDrEmail(String De) {profileDrEmail = De;}

    public String getName()
    {
        return profileName;
    }
    public String getEmail() {return profileEmail;}
    public String getDrEmail() { return profileDrEmail;}
    public String getDrName() { return profileDrName; }

}