package de.ur.mi.android.baudoku;


public class NoteItem {

    private int id;
    private String imgPath;
    private String date;
    private String weather;

    private String memoPath;
    private String notes;

    public NoteItem (int id) {
        this.id = id;
    }

    public NoteItem (int id, String imgPath, String date, String weather, String memoPath, String notes) {
        this.id = id;
        this.imgPath = imgPath;
        this.date = date;
        this.weather = weather;
        this.memoPath = memoPath;
        this.notes = notes;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setMemoPath(String memoPath) {
        this.memoPath = memoPath;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getDate() {
        return date;
    }

    public String getWeather() {
        return weather;
    }

    public String getMemoPath() {
        return memoPath;
    }

    public String getNotes() {
        return notes;
    }
}
