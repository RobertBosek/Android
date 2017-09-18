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

    private static final String KEY_ID = "_id";
    private static final String KEY_IMG_PATH = "imgPath";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_START = "start";
    private static final String KEY_CLIENT = "client";
    private static final String KEY_ATTENDEES = "attendees";
    private static final String KEY_STATUS = "status";

    private static final String[] ALL_COLUMS_PROJECTS = new String[] {KEY_ID, KEY_IMG_PATH,
            KEY_TITLE, KEY_ADDRESS, KEY_START, KEY_CLIENT, KEY_ATTENDEES, KEY_STATUS};

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
        projectValues.put(KEY_IMG_PATH, project.getImgPath());
        projectValues.put(KEY_TITLE, project.getTitle());
        projectValues.put(KEY_ADDRESS, project.getAddress());
        projectValues.put(KEY_START, project.getStart());
        projectValues.put(KEY_CLIENT, project.getClient());
        projectValues.put(KEY_ATTENDEES, project.getAttendees());
        projectValues.put(KEY_STATUS, project.getStatus());
        long id = db.insert(DATABASE_TABLE_PROJECTS, null, projectValues);
        return (int) id;
    }

    public void removeProjectItem(int id) {
        String toDelete = KEY_ID + "=?";
        String[] deleteArguments = new String[]{String.valueOf(id)};
        db.delete(DATABASE_TABLE_PROJECTS, toDelete, deleteArguments);
    }

    public ProjectItem getProjectItem(int id) {
        ProjectItem project = null;
        Cursor cursor = db.query(DATABASE_TABLE_PROJECTS, ALL_COLUMS_PROJECTS, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            project = getItemFromCursor(cursor);
        }
        return project;
    }

    public ArrayList<ProjectItem> getAllProjects(int status) {
        ArrayList<ProjectItem> projects = new ArrayList<ProjectItem>();
        Cursor cursor = db.query(DATABASE_TABLE_PROJECTS, ALL_COLUMS_PROJECTS, KEY_STATUS + "=?", new String[] {String.valueOf(status)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ProjectItem project = getItemFromCursor(cursor);
                projects.add(project);
            } while (cursor.moveToNext());
        }
        return projects;
    }

    public void updateProjectItem(ProjectItem project) {
        ContentValues projectValues = new ContentValues();
        projectValues.put(KEY_IMG_PATH, project.getImgPath());
        projectValues.put(KEY_TITLE, project.getTitle());
        projectValues.put(KEY_ADDRESS, project.getAddress());
        projectValues.put(KEY_START, project.getStart());
        projectValues.put(KEY_CLIENT, project.getClient());
        projectValues.put(KEY_ATTENDEES, project.getAttendees());
        projectValues.put(KEY_STATUS, project.getStatus());
        db.update(DATABASE_TABLE_PROJECTS, projectValues, KEY_ID + "=?", new String[] {String.valueOf(project.getId())});
    }

    public ProjectItem getItemFromCursor(Cursor cursor) {
        String imgPath = cursor.getString(cursor.getColumnIndex(KEY_IMG_PATH));
        String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
        String address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
        String start = cursor.getString(cursor.getColumnIndex(KEY_START));
        String client = cursor.getString(cursor.getColumnIndex(KEY_CLIENT));
        String attendees = cursor.getString(cursor.getColumnIndex(KEY_ATTENDEES));
        int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
        int status = cursor.getInt(cursor.getColumnIndex(KEY_STATUS));
        return new ProjectItem(imgPath, title, address, start, client, attendees, id, status);
    }


    private class BaudokuDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE_TABLE_PROJECTS = "create table "
                + DATABASE_TABLE_PROJECTS + " (" + KEY_ID + " integer primary key autoincrement, "
                + KEY_IMG_PATH + " text not null, "
                + KEY_TITLE + " text not null, "
                + KEY_ADDRESS + " text not null, "
                + KEY_START + " text not null, "
                + KEY_CLIENT + " text not null, "
                + KEY_ATTENDEES + " text not null, "
                + KEY_STATUS + " integer not null"
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
