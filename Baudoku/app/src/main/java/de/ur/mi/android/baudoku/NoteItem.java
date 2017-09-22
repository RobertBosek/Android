package de.ur.mi.android.baudoku;


public class NoteItem implements Comparable<NoteItem>{

    private int id;
    private int projectID;
    private String date;
    private String temperature;
    private String weather;

    private String imgPath;
    private String note;
    private String memoPath;

    public NoteItem (int id, int projectID) {
        this.id = id;
        this.projectID = projectID;
    }

    public NoteItem (int id, int projectID, String date, String temperature, String weather, String imgPath, String note, String memoPath) {
        this.id = id;
        this.projectID = projectID;
        this.date = date;
        this.temperature = temperature;
        this.weather = weather;
        this.imgPath = imgPath;
        this.note = note;
        this.memoPath = memoPath;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setNotes(String note) {
        this.note = note;
    }

    public void setMemoPath(String memoPath) {
        this.memoPath = memoPath;
    }

    public int getId() {
        return id;
    }

    public int getProjectID() {
        return projectID;
    }

    public String getDate() {
        return date;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWeather() {
        return weather;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getNote() {
        return note;
    }

    public String getMemoPath() {
        return memoPath;
    }

    @Override
    public int compareTo(NoteItem another) {
        return this.getDate().compareTo(another.getDate());
    }
}
