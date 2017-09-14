package de.ur.mi.android.baudoku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;




public class ProjectListAdapter extends ArrayAdapter<ProjectListItem> {
    private ArrayList<ProjectListItem> projectList;
    private Context context;

    public ProjectListAdapter(Context context, ArrayList<ProjectListItem> projectList) {
        super(context, R.layout.project_list_row, projectList);
        this.context = context;
        this.projectList = projectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.project_list_row, null);

        }

        ProjectListItem project = projectList.get(position);

        if (project != null) {
            ImageView projectImage = (ImageView) v.findViewById(R.id.project_img);
            TextView projectTitle = (TextView) v.findViewById(R.id.title_view);
            TextView projectAddress = (TextView) v.findViewById(R.id.address_view);
            TextView projectDate = (TextView) v.findViewById(R.id.date_view);

            projectTitle.setText(project.title);
            projectAddress.setText(project.address);
            projectDate.setText(project.getDate());
        }

        return v;
    }

}