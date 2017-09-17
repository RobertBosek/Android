package de.ur.mi.android.baudoku;


import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ProjectItem implements Comparable<ProjectItem>{

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_CANCELED = -1;



    private String img;
    private String title;
    private String address;
    private String start;
    private String client;
    private String attendees;
    private int id;
    private int status;

    public ProjectItem(int id) {
        this.id = id;
        this.status = STATUS_PENDING;
    }

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

    public void setImg(String img) {
        this.img = img;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImg() {
        return this.img;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAddress() {
        return this.address;
    }

    public String getStart() {
        return this.start;
    }

    public String getClient() {
        return this.client;
    }

    public String getAttendees() {
        return this.attendees;
    }

    public int getStatus() {
        return this.status;
    }

    public int getId() {
        return this.id;
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
