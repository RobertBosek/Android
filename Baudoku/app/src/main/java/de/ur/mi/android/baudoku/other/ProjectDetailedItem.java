package de.ur.mi.android.baudoku;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class ProjectDetailedItem {

    public String image;
    public String title;
    public String address;
    public GregorianCalendar start;

    public String client;
    public String attendees;
    public HashMap<String, String> otherDetails;


    public ProjectDetailedItem(String image, String title, String address, int day, int month, int year, String client, String attendees, HashMap<String, String> otherDetails) {
        this.image = image;
        this.title = title;
        this.address = address;
        start = new GregorianCalendar(year, month, day);
        this.client = client;
        this.attendees = attendees;
        this.otherDetails = otherDetails;
    }

    public String getFormattedDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.GERMANY);
        return df.format(start.getTime());
    }

    @Override
    public String toString() {
        return "Name: ";
    }
}
