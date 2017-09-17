package de.ur.mi.android.baudoku;

import android.content.Context;


public class BaudokuDatabase {
    private static int id;

    private ProjectItem dummy;

    public BaudokuDatabase(Context context) {
        this.id = 1;
    }

    public void open() {

    }

    public ProjectItem getProjectItem(int id) {
        return dummy;
    }

    public int insertProjectItem(ProjectItem project) {
        dummy = new ProjectItem(project.getImg(), project.getTitle(), project.getAddress(), project.getStart(), project.getClient(), project.getAttendees(), 1, project.getStatus());
        return 1;
    }

    public void updateProjectItem(ProjectItem projectItem) {
        int id = projectItem.getId();

    }

    public void removeProjectItem(int ID) {

    }
}
