package de.ur.mi.android.baudoku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProjectListActivity extends AppCompatActivity {

    private static BaudokuDatabase db;
    private Context context = this;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageButton btnCreateProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        getUIElements();
        initUIElements();
    }

    @Override
    protected void onResume() {
        initDatabase();
        ProjectListTabAdapter tabAdapter = new ProjectListTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        super.onResume();
    }

    private void getUIElements() {
        toolbar = (Toolbar) findViewById(R.id.project_list_app_bar);
        tabLayout = (TabLayout) findViewById(R.id.project_list_activity_tabs);
        viewPager = (ViewPager) findViewById(R.id.project_list_activity_view_pager);
        btnCreateProject = (ImageButton) findViewById(R.id.project_list_activity_btnCreate_project);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_project_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.project_list_menu_info:
                Intent startAppInfoActivityIntent = new Intent(ProjectListActivity.this, AppInfoActivity.class);
                startActivity(startAppInfoActivityIntent);
            default:
                break;
        }

        return true;
    }

    private void initUIElements() {
        setSupportActionBar(toolbar);
        btnCreateProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startProjectCreateActivityIntent = new Intent(ProjectListActivity.this, ProjectCreateActivity.class);
                startProjectCreateActivityIntent.putExtra(getString(R.string.intent_extra_key_id_project), -1);
                startActivity(startProjectCreateActivityIntent);
            }
        });

    }

    private void initDatabase() {
        db = new BaudokuDatabase(this);
    }


    public class ProjectListTabAdapter extends FragmentPagerAdapter {

        public ProjectListTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int tab) {
            return ProjectListTabFragment.newInstance(tab + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "laufend";
                case 1:
                    return "beendet";
            }
            return null;
        }
    }


    public static class ProjectListTabFragment extends Fragment {

        private static final String PROJECT_LIST_TAB = "project_list_tab";

        private View rootView;
        private ArrayList<ProjectItem> projects;
        private ProjectListAdapter projectsAdapter;
        private ListView projectsList;
        private TextView emptyListText;

        public ProjectListTabFragment() {
        }

        public static ProjectListTabFragment newInstance(int tab) {
            ProjectListTabFragment fragment = new ProjectListTabFragment();
            Bundle args = new Bundle();
            args.putInt(PROJECT_LIST_TAB, tab);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_project_list, container, false);
            getFragmentUIElements();
            setListAdapter();
            setListeners();
            refreshList();
            return rootView;
        }

        public void getFragmentUIElements() {
            projectsList = (ListView) rootView.findViewById(R.id.fragment_project_list_projectslistview);
            emptyListText = (TextView) rootView.findViewById(R.id.fragment_project_list_emptyprojectslist);
            emptyListText.setText(R.string.text_no_existing_projects);
        }

        private void setListAdapter() {
            projects = new ArrayList<ProjectItem>();
            projectsAdapter = new ProjectListAdapter(getContext(), projects);
            projectsList.setAdapter(projectsAdapter);
            projectsList.setEmptyView(emptyListText);
            registerForContextMenu(projectsList);
        }

        private void setListeners() {
            projectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showProjectView(projects.get(position).getId());
                }
            });
        }

        private void showProjectView(int id) {
            Intent startProjectViewActivityIntent = new Intent(getActivity(), ProjectViewActivity.class);
            startProjectViewActivityIntent.putExtra(getString(R.string.intent_extra_key_id_project), id);
            getActivity().startActivity(startProjectViewActivityIntent);
        }

        public void refreshList() {
            ArrayList<ProjectItem> temp;
            int tab = getArguments().getInt(PROJECT_LIST_TAB);
            db.open();
            if (tab == 1) {
                temp = db.getAllProjects(ProjectItem.STATUS_PENDING);
            } else {
                temp = db.getAllProjects(ProjectItem.STATUS_FINISHED);
                temp.addAll(db.getAllProjects(ProjectItem.STATUS_CANCELED));
            }
            db.close();
            projects.clear();
            projects.addAll(temp);
            projectsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_menu_list, menu);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            ProjectItem selected = projects.get(info.position);
            switch (item.getItemId()) {
                case R.id.context_menu_edit:
                    showProjectCreate(selected.getId());
                    return true;
                case R.id.context_menu_delete:
                    makeDeleteDialog(selected);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        private void showProjectCreate(int id) {
            Intent startProjectCreateActivityIntent = new Intent(getActivity(), ProjectCreateActivity.class);
            startProjectCreateActivityIntent.putExtra(getString(R.string.intent_extra_key_id_project), id);
            startActivity(startProjectCreateActivityIntent);
        }

        private void makeDeleteDialog(final ProjectItem project) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.dialog_title_delete_project)
                    .setMessage(R.string.dialog_text_delete_project)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            db.open();
                            db.removeProjectItem(project.getId());
                            db.close();
                            refreshList();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }


    private static class ProjectListAdapter extends ArrayAdapter<ProjectItem> {
        private ArrayList<ProjectItem> projects;
        private Context context;

        public ProjectListAdapter(Context context, ArrayList<ProjectItem> projects) {
            super(context, R.layout.item_project_list, projects);
            this.context = context;
            this.projects = projects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.item_project_list, null);

            }

            ProjectItem project = projects.get(position);

            if (project != null) {
                ImageView imgView = (ImageView) v.findViewById(R.id.project_list_item_img_view);
                ImageHelper.setPic(project.getImgPath(), imgView);

                TextView titleView = (TextView) v.findViewById(R.id.project_list_item_title_view);
                titleView.setText(project.getTitle());
                TextView startView = (TextView) v.findViewById(R.id.project_list_item_start_view);
                startView.setText(project.getStart());
                TextView addressView = (TextView) v.findViewById(R.id.project_list_item_address_view);
                addressView.setText(project.getAddress());
                TextView cityView = (TextView) v.findViewById(R.id.project_list_item_city_view);
                cityView.setText(project.getCity());

                ImageView statusView = (ImageView) v.findViewById(R.id.project_list_item_status);
                int status = project.getStatus();
                if (status == -1) {
                    statusView.setImageDrawable(context.getDrawable(R.drawable.ic_canceled));
                } else if (status == 1) {
                    statusView.setImageDrawable(context.getDrawable(R.drawable.ic_finished));
                }
            }

            return v;
        }

    }
}
