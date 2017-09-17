package de.ur.mi.android.baudoku;


import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ProjectItem implements Comparable<ProjectItem>{

    public String img;
    public String title;
    public String address;
    public String start;
    public String client;
    public String attendees;
    public int id;
    public int status;

    public ProjectItem(String img, String title, String address, String start, String client, String attendees, int id, int status) {
        this.img = img;
        this.title = title;
        this.address = address;
        this.start = start;
        this.client = client;
        this.attendees = attendees;
        this.id = id;
        this.status = status;
    }

    @Override
    public int compareTo(ProjectItem another) {
        return this.start.compareTo(another.start);
    }

    @Override
    public String toString() {
        return "Title: " + this.title + ", Date: " + this.start;
    }
}
