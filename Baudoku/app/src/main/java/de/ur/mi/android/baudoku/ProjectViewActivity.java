package de.ur.mi.android.baudoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ProjectViewActivity extends AppCompatActivity {

    private ProjectItem project;
    private BaudokuDatabase db;

    private Toolbar toolbar;
    private ImageView imageView;
    private ProjectViewTabAdapter tabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view);
        initDatabase();
        getDisplayProject();
        getUIElements();
        initUIElements();
    }

    private void initDatabase() {
        db = new BaudokuDatabase(this);
        db.open();
    }

    public void getDisplayProject() {
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt(getString(R.string.extra_id));
        project = db.getProjectItem(id);
    }

    public void getUIElements() {
        toolbar = (Toolbar) findViewById(R.id.project_view_activity_toolbar);
        viewPager = (ViewPager) findViewById(R.id.project_view_activity_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.project_view_activity_tabs);
        imageView = (ImageView) findViewById(R.id.project_view_activity_project_img);
        CollapsingToolbarLayout title = (CollapsingToolbarLayout) findViewById(R.id.project_view_activity_project_title);
    }

    private void initUIElements() {
        setSupportActionBar(toolbar);
        tabAdapter = new ProjectViewActivity.ProjectViewTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.project_view_menu_edit_project) {
            Intent startProjectCreateActivityIntent = new Intent(ProjectViewActivity.this, ProjectCreateActivity.class);
            startProjectCreateActivityIntent.putExtra(getString(R.string.extra_id), project.getId());
            db.close();
            startActivity(startProjectCreateActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    public class ProjectViewTabAdapter extends FragmentPagerAdapter {

        public ProjectViewTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int tab) {
            return ProjectViewActivity.ProjectViewTabFragment.newInstance(tab + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int tab) {
            switch (tab) {
                case 0:
                    return "Details";
                case 1:
                    return "Notizen";
            }
            return null;
        }
    }


    public static class ProjectViewTabFragment extends Fragment {

        private static final String PROJECT_VIEW_TAB = "project_view_tab";



        public ProjectViewTabFragment() {
        }

        public static ProjectViewTabFragment newInstance(int tab) {
            ProjectViewActivity.ProjectViewTabFragment fragment = new ProjectViewActivity.ProjectViewTabFragment();
            Bundle args = new Bundle();
            args.putInt(PROJECT_VIEW_TAB, tab);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int tab = getArguments().getInt(PROJECT_VIEW_TAB);
            View rootView;
            if (tab == 2) {
                rootView = inflater.inflate(R.layout.fragment_listview, container, false);
            } else {
                rootView = inflater.inflate(R.layout.fragment_project_view_details, container, false);
            }
            return rootView;
        }
    }


}
