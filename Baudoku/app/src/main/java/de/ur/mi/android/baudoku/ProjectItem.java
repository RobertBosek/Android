package de.ur.mi.android.baudoku;


public class ProjectItem implements Comparable<ProjectItem>{

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_CANCELED = -1;

    private String imgPath;
    private String title;
    private String start;
    private String address;
    private String city;
    private String client;
    private String attendees;
    private int id;
    private int status;

    public ProjectItem(int id) {
        this.id = id;
        this.status = STATUS_PENDING;
    }

    public ProjectItem(String imgPath, String title, String start, String address, String city, String client, String attendees, int id, int status) {
        this.imgPath = imgPath;
        this.title = title;
        this.start = start;
        this.address = address;
        this.city = city;
        this.client = client;
        this.attendees = attendees;
        this.id = id;
        this.status = status;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getImgPath() {
        return this.imgPath;
    }

    public String getTitle() {
        return this.title;
    }

    public String getStart() {
        return this.start;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCity() {
        return this.city;
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
        return this.getStart().compareTo(another.getStart());
    }

}
