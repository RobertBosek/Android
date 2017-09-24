package de.ur.mi.android.baudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class BaudokuDatabase {
    private static final String DATABASE_NAME = "Baudoku.db";
    private static final int DATABASE_VERSION = 1;

    //tables in database
    private static final String DATABASE_TABLE_PROJECTS = "projects";
    private static final String DATABASE_TABLE_NOTES = "notes";

    //table keys for projects table
    private static final String PROJECT_KEY_ID = "_id";
    private static final String PROJECT_KEY_IMG_PATH = "imgPath";
    private static final String PROJECT_KEY_TITLE = "title";
    private static final String PROJECT_KEY_START = "start";
    private static final String PROJECT_KEY_ADDRESS = "address";
    private static final String PROJECT_KEY_CITY = "city";
    private static final String PROJECT_KEY_CLIENT = "client";
    private static final String PROJECT_KEY_ATTENDEES = "attendees";
    private static final String PROJECT_KEY_STATUS = "status";

    //table keys for projects table
    private static final String NOTES_KEY_ID = "_id";
    private static final String NOTES_KEY_PROJECT_ID = "projectID";
    private static final String NOTES_KEY_DATE = "date";
    private static final String NOTES_KEY_TEMPERATURE = "temperature";
    private static final String NOTES_KEY_WEATHER = "weather";
    private static final String NOTES_KEY_IMG_PATH = "imgPath";
    private static final String NOTES_KEY_NOTE = "note";
    private static final String NOTES_KEY_MEMO_PATH = "memoPath";

    private static final String[] ALL_COLUMS_PROJECTS = new String[] {PROJECT_KEY_ID, PROJECT_KEY_IMG_PATH,
            PROJECT_KEY_TITLE, PROJECT_KEY_START, PROJECT_KEY_ADDRESS, PROJECT_KEY_CITY, PROJECT_KEY_CLIENT,
            PROJECT_KEY_ATTENDEES, PROJECT_KEY_STATUS};
    private static final String[] ALL_COLUMS_NOTES = new String[] {NOTES_KEY_ID, NOTES_KEY_PROJECT_ID,
            NOTES_KEY_DATE, NOTES_KEY_TEMPERATURE, NOTES_KEY_WEATHER, NOTES_KEY_IMG_PATH, NOTES_KEY_NOTE,
            NOTES_KEY_MEMO_PATH};



    private BaudokuDBOpenHelper dbHelper;

    private SQLiteDatabase db;

    public BaudokuDatabase(Context context) {
        dbHelper = new BaudokuDBOpenHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    public void open() throws SQLException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        db.close();
    }

    public int insertProjectItem(ProjectItem project) {
        ContentValues projectValues = new ContentValues();
        projectValues.put(PROJECT_KEY_IMG_PATH, project.getImgPath());
        projectValues.put(PROJECT_KEY_TITLE, project.getTitle());
        projectValues.put(PROJECT_KEY_START, project.getStart());
        projectValues.put(PROJECT_KEY_ADDRESS, project.getAddress());
        projectValues.put(PROJECT_KEY_CITY, project.getCity());
        projectValues.put(PROJECT_KEY_CLIENT, project.getClient());
        projectValues.put(PROJECT_KEY_ATTENDEES, project.getAttendees());
        projectValues.put(PROJECT_KEY_STATUS, project.getStatus());
        long id = db.insert(DATABASE_TABLE_PROJECTS, null, projectValues);
        return (int) id;
    }

    public void removeProjectItem(int id) {
        String toDelete = PROJECT_KEY_ID + "=?";
        String[] deleteArguments = new String[]{String.valueOf(id)};
        db.delete(DATABASE_TABLE_PROJECTS, toDelete, deleteArguments);
    }

    public ProjectItem getProjectItem(int id) {
        ProjectItem project = null;
        Cursor cursor = db.query(DATABASE_TABLE_PROJECTS, ALL_COLUMS_PROJECTS, PROJECT_KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            project = getProjectItemFromCursor(cursor);
        }
        return project;
    }

    public ArrayList<ProjectItem> getAllProjects(int status) {
        ArrayList<ProjectItem> projects = new ArrayList<ProjectItem>();
        Cursor cursor = db.query(DATABASE_TABLE_PROJECTS, ALL_COLUMS_PROJECTS, PROJECT_KEY_STATUS + "=?", new String[] {String.valueOf(status)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ProjectItem project = getProjectItemFromCursor(cursor);
                projects.add(project);
            } while (cursor.moveToNext());
        }
        return projects;
    }

    public void updateProjectItem(ProjectItem project) {
        ContentValues projectValues = new ContentValues();
        projectValues.put(PROJECT_KEY_IMG_PATH, project.getImgPath());
        projectValues.put(PROJECT_KEY_TITLE, project.getTitle());
        projectValues.put(PROJECT_KEY_START, project.getStart());
        projectValues.put(PROJECT_KEY_ADDRESS, project.getAddress());
        projectValues.put(PROJECT_KEY_CITY, project.getCity());
        projectValues.put(PROJECT_KEY_CLIENT, project.getClient());
        projectValues.put(PROJECT_KEY_ATTENDEES, project.getAttendees());
        projectValues.put(PROJECT_KEY_STATUS, project.getStatus());
        db.update(DATABASE_TABLE_PROJECTS, projectValues, PROJECT_KEY_ID + "=?", new String[] {String.valueOf(project.getId())});
    }

    public ProjectItem getProjectItemFromCursor(Cursor cursor) {
        String imgPath = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_IMG_PATH));
        String title = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_TITLE));
        String start = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_START));
        String address = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_ADDRESS));
        String city = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_CITY));
        String client = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_CLIENT));
        String attendees = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_ATTENDEES));
        int id = cursor.getInt(cursor.getColumnIndex(PROJECT_KEY_ID));
        int status = cursor.getInt(cursor.getColumnIndex(PROJECT_KEY_STATUS));
        return new ProjectItem(imgPath,  title, start, address, city, client, attendees, id, status);
    }


    public int insertNoteItem(NoteItem note) {
        ContentValues noteValues = new ContentValues();
        noteValues.put(NOTES_KEY_PROJECT_ID, note.getProjectID());
        noteValues.put(NOTES_KEY_DATE, note.getDate());
        noteValues.put(NOTES_KEY_TEMPERATURE, note.getTemperature());
        noteValues.put(NOTES_KEY_WEATHER, note.getWeather());
        noteValues.put(NOTES_KEY_IMG_PATH, note.getImgPath());
        noteValues.put(NOTES_KEY_NOTE, note.getNote());
        noteValues.put(NOTES_KEY_MEMO_PATH, note.getMemoPath());
        long id = db.insert(DATABASE_TABLE_NOTES, null, noteValues);
        return (int) id;
    }

    public void removeNoteItem(int id) {
        String toDelete = NOTES_KEY_ID + "=?";
        String[] deleteArguments = new String[]{String.valueOf(id)};
        db.delete(DATABASE_TABLE_NOTES, toDelete, deleteArguments);
    }

    public NoteItem getNoteItem(int id) {
        NoteItem note = null;
        Cursor cursor = db.query(DATABASE_TABLE_NOTES, ALL_COLUMS_NOTES, NOTES_KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            note = getNoteItemFromCursor(cursor);
        }
        return note;
    }

    public ArrayList<NoteItem> getAllNotes(int projectID) {
        ArrayList<NoteItem> notes = new ArrayList<NoteItem>();
        Cursor cursor = db.query(DATABASE_TABLE_NOTES, ALL_COLUMS_NOTES, NOTES_KEY_PROJECT_ID + "=?", new String[] {String.valueOf(projectID)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                NoteItem note = getNoteItemFromCursor(cursor);
                notes.add(note);
            } while (cursor.moveToNext());
        }
        return notes;
    }

    public void updateNoteItem(NoteItem note) {
        ContentValues noteValues = new ContentValues();
        noteValues.put(NOTES_KEY_PROJECT_ID, note.getProjectID());
        noteValues.put(NOTES_KEY_DATE, note.getDate());
        noteValues.put(NOTES_KEY_TEMPERATURE, note.getTemperature());
        noteValues.put(NOTES_KEY_WEATHER, note.getWeather());
        noteValues.put(NOTES_KEY_IMG_PATH, note.getImgPath());
        noteValues.put(NOTES_KEY_NOTE, note.getNote());
        noteValues.put(NOTES_KEY_MEMO_PATH, note.getMemoPath());
        db.update(DATABASE_TABLE_NOTES, noteValues, NOTES_KEY_ID + "=?", new String[] {String.valueOf(note.getId())});
    }

    public NoteItem getNoteItemFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(NOTES_KEY_ID));
        int projectID = cursor.getInt(cursor.getColumnIndex(NOTES_KEY_PROJECT_ID));
        String date = cursor.getString(cursor.getColumnIndex(NOTES_KEY_DATE));
        String temperature = cursor.getString(cursor.getColumnIndex(NOTES_KEY_TEMPERATURE));
        String weather = cursor.getString(cursor.getColumnIndex(NOTES_KEY_WEATHER));
        String imgPath = cursor.getString(cursor.getColumnIndex(NOTES_KEY_IMG_PATH));
        String note = cursor.getString(cursor.getColumnIndex(NOTES_KEY_NOTE));
        String memoPath = cursor.getString(cursor.getColumnIndex(NOTES_KEY_MEMO_PATH));
        return new NoteItem(id, projectID, date, temperature, weather, imgPath, note, memoPath);
    }


    private class BaudokuDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE_TABLE_PROJECTS = "create table "
                + DATABASE_TABLE_PROJECTS + " ("
                + PROJECT_KEY_ID + " integer primary key autoincrement, "
                + PROJECT_KEY_IMG_PATH + " text not null, "
                + PROJECT_KEY_TITLE + " text not null, "
                + PROJECT_KEY_START + " text not null, "
                + PROJECT_KEY_ADDRESS + " text not null, "
                + PROJECT_KEY_CITY + " text not null, "
                + PROJECT_KEY_CLIENT + " text not null, "
                + PROJECT_KEY_ATTENDEES + " text not null, "
                + PROJECT_KEY_STATUS + " integer not null"
                + ");";
        private static final String DATABASE_CREATE_TABLE_NOTES = "create table "
                + DATABASE_TABLE_NOTES + " ("
                + NOTES_KEY_ID + " integer primary key autoincrement, "
                + NOTES_KEY_PROJECT_ID + " integer not null, "
                + NOTES_KEY_DATE + " text not null, "
                + NOTES_KEY_TEMPERATURE + " text not null, "
                + NOTES_KEY_WEATHER + " text not null, "
                + NOTES_KEY_IMG_PATH + " text not null, "
                + NOTES_KEY_NOTE + " text not null, "
                + NOTES_KEY_MEMO_PATH + " text not null"
                + ");";



        public BaudokuDBOpenHelper(Context c, String dbname,
                                SQLiteDatabase.CursorFactory factory, int version) {
            super(c, dbname, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_TABLE_PROJECTS);
            db.execSQL(DATABASE_CREATE_TABLE_NOTES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
