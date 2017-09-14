package de.ur.mi.android.baudoku;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ProjectListItem implements Comparable<ProjectListItem> {

    public String pic;
    public String title;
    public String address;
    public GregorianCalendar startDate;

    public boolean status;
    public int projectID;

    public ProjectListItem (String pic, String title, String address, int day, int month, int year, boolean status) {
        this.pic = pic;
        this.title = title;
        this.address = address;
        this.startDate = new GregorianCalendar(year, month, day);
        this.status = status;
    }

    public String getDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.GERMANY);
        return df.format(startDate.getTime());
    }

    public Date getStartDate() {
        return startDate.getTime();
    }

    @Override
    public int compareTo(ProjectListItem another) {
        return getStartDate().compareTo(another.getStartDate());
    }
}
