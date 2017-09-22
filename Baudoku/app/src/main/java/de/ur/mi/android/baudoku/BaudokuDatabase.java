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

    private static final String DATABASE_TABLE_PROJECTS = "projects";
    private static final String DATABASE_TABLE_NOTES = "notes";

    //table keys for projects table
    private static final String PROJECT_KEY_ID = "_id";
    private static final String PROJECT_KEY_IMG_PATH = "imgPath";
    private static final String PROJECT_KEY_TITLE = "title";
    private static final String PROJECT_KEY_ADDRESS = "address";
    private static final String PROJECT_KEY_START = "start";
    private static final String PROJECT_KEY_CLIENT = "client";
    private static final String PROJECT_KEY_ATTENDEES = "attendees";
    private static final String PROJECT_KEY_STATUS = "status";

    private static final String[] ALL_COLUMS_PROJECTS = new String[] {PROJECT_KEY_ID, PROJECT_KEY_IMG_PATH,
            PROJECT_KEY_TITLE, PROJECT_KEY_ADDRESS, PROJECT_KEY_START, PROJECT_KEY_CLIENT, PROJECT_KEY_ATTENDEES, PROJECT_KEY_STATUS};

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
        projectValues.put(PROJECT_KEY_ADDRESS, project.getAddress());
        projectValues.put(PROJECT_KEY_START, project.getStart());
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
        projectValues.put(PROJECT_KEY_ADDRESS, project.getAddress());
        projectValues.put(PROJECT_KEY_START, project.getStart());
        projectValues.put(PROJECT_KEY_CLIENT, project.getClient());
        projectValues.put(PROJECT_KEY_ATTENDEES, project.getAttendees());
        projectValues.put(PROJECT_KEY_STATUS, project.getStatus());
        db.update(DATABASE_TABLE_PROJECTS, projectValues, PROJECT_KEY_ID + "=?", new String[] {String.valueOf(project.getId())});
    }

    public ProjectItem getProjectItemFromCursor(Cursor cursor) {
        String imgPath = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_IMG_PATH));
        String title = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_TITLE));
        String address = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_ADDRESS));
        String start = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_START));
        String client = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_CLIENT));
        String attendees = cursor.getString(cursor.getColumnIndex(PROJECT_KEY_ATTENDEES));
        int id = cursor.getInt(cursor.getColumnIndex(PROJECT_KEY_ID));
        int status = cursor.getInt(cursor.getColumnIndex(PROJECT_KEY_STATUS));
        return new ProjectItem(imgPath, title, address, start, client, attendees, id, status);
    }


    public ArrayList<NoteItem> getAllNotes(int projectID) {
        ArrayList<NoteItem> notes = new ArrayList<NoteItem>();

        return notes;
    }


    private class BaudokuDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE_TABLE_PROJECTS = "create table "
                + DATABASE_TABLE_PROJECTS + " (" + PROJECT_KEY_ID + " integer primary key autoincrement, "
                + PROJECT_KEY_IMG_PATH + " text not null, "
                + PROJECT_KEY_TITLE + " text not null, "
                + PROJECT_KEY_ADDRESS + " text not null, "
                + PROJECT_KEY_START + " text not null, "
                + PROJECT_KEY_CLIENT + " text not null, "
                + PROJECT_KEY_ATTENDEES + " text not null, "
                + PROJECT_KEY_STATUS + " integer not null"
                + ");";

        public BaudokuDBOpenHelper(Context c, String dbname,
                                SQLiteDatabase.CursorFactory factory, int version) {
            super(c, dbname, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_TABLE_PROJECTS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
