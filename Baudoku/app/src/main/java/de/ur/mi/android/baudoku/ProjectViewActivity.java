package de.ur.mi.android.baudoku;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ProjectViewActivity extends AppCompatActivity {

    private ProjectViewActivity.ProjectViewSectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view);

        mSectionsPagerAdapter = new ProjectViewActivity.ProjectViewSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.project_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.project_view_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public static class ProjectViewFragment extends Fragment {
        private static final String PROJECT_VIEW_SECTION_NUMBER = "section_number";

        public static ProjectViewActivity.ProjectViewFragment newInstance(int sectionNumber) {
            ProjectViewActivity.ProjectViewFragment fragment = new ProjectViewActivity.ProjectViewFragment();
            Bundle args = new Bundle();
            args.putInt(PROJECT_VIEW_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_view, container, false);

            return rootView;
        }
    }

    public class ProjectViewSectionsPagerAdapter extends FragmentPagerAdapter {

        public ProjectViewSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ProjectViewActivity.ProjectViewFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Notizen";
            }
            return null;
        }
    }
}
