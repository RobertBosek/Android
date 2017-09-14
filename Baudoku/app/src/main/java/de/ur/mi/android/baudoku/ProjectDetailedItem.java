package de.ur.mi.android.baudoku;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class ProjectDetailedItem {

    public String pic;
    public String title;
    public String address;
    public GregorianCalendar startDate;

    public String client;
    public String attendees;
    public HashMap<String, String> otherDetails;


    public ProjectDetailedItem(String pic, String title, String address, int day, int month, int year, String client, String attendees, HashMap<String, String> otherDetails) {
        this.pic = pic;
        this.title = title;
        this.address = address;
        startDate = new GregorianCalendar(year, month, day);
        this.client = client;
        this.attendees = attendees;
        this.otherDetails = otherDetails;
    }

    public String getFormattedDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.GERMANY);
        return df.format(startDate.getTime());
    }

    @Override
    public String toString() {
        return "Name: ";
    }
}
