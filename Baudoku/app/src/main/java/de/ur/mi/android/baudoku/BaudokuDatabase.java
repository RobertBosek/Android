package de.ur.mi.android.baudoku;

import android.content.Context;


public class BaudokuDatabase {
    private static int id;

    public BaudokuDatabase(Context context) {
        this.id = 1;
    }

    public void open() {

    }

    public ProjectItem getProjectItem(int id) {
        ProjectItem project = new ProjectItem("", "", "", "", "", "", id, 0);
        return project;
    }

    public int insertProjectItem(ProjectItem project) {
        return id;
    }

    public void updateProjectItem(ProjectItem projectItem) {
        int id = projectItem.getId();

    }

    public void removeProjectItem(int ID) {

    }
}
