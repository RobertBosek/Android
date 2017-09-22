package de.ur.mi.android.baudoku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ProjectViewActivity extends AppCompatActivity {

    private static ProjectItem project;
    private static BaudokuDatabase db;

    private Toolbar toolbar;
    private ImageView imgView;
    private ProjectViewTabAdapter tabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CollapsingToolbarLayout title;

    private Button btnCanceled;
    private Button btnDeleted;
    private Button btnFinished;



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
    }

    public void getDisplayProject() {
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt(getString(R.string.intent_extra_key_project_id));
        db.open();
        project = db.getProjectItem(id);
        db.close();
    }

    public void getUIElements() {
        toolbar = (Toolbar) findViewById(R.id.project_view_activity_toolbar);
        viewPager = (ViewPager) findViewById(R.id.project_view_activity_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.project_view_activity_tabs);
        imgView = (ImageView) findViewById(R.id.project_view_activity_project_img);
        btnCanceled = (Button) findViewById(R.id.project_view_activity_btnCanceled);
        btnDeleted = (Button) findViewById(R.id.project_view_activity_btnDeleted);
        btnFinished = (Button) findViewById(R.id.project_view_activity_btnFinished);
    }

    private void initUIElements() {
        setSupportActionBar(toolbar);

        tabAdapter = new ProjectViewActivity.ProjectViewTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        CollapsingToolbarLayout title = (CollapsingToolbarLayout) findViewById(R.id.project_view_activity_title);
        title.setTitle(project.getTitle());

        if (!project.getImgPath().equals("")) {
            ImageHelper.setPic(project.getImgPath(), imgView);
        }

        btnCanceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                makeCancelDialog();
            }
        });
        btnDeleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                makeDeleteDialog();
            }
        });
        btnFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                makeFinishDialog();
            }
        });
    }

    private void makeCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_cancel_project)
                .setMessage(R.string.dialog_text_cancel_project)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        project.setStatus(ProjectItem.STATUS_CANCELED);
                        db.open();
                        db.updateProjectItem(project);
                        db.close();
                        startListActivity();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void makeDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_delete_project)
                .setMessage(R.string.dialog_text_delete_project)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.open();
                        db.removeProjectItem(project.getId());
                        db.close();
                        startListActivity();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void makeFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_finish_project)
                .setMessage(R.string.dialog_text_finish_project)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        project.setStatus(ProjectItem.STATUS_FINISHED);
                        db.open();
                        db.updateProjectItem(project);
                        db.close();
                        startListActivity();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void startListActivity() {
        Intent startProjectListActivityIntent = new Intent(ProjectViewActivity.this, ProjectListActivity.class);
        startActivity(startProjectListActivityIntent);
        finish();
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
            startProjectCreateActivityIntent.putExtra(getString(R.string.intent_extra_key_project_id), project.getId());
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

        private View rootView;

        private TextView addressView;
        private TextView startView;
        private TextView clientView;
        private TextView attendeesView;

        private FloatingActionButton addNote;
        private ListView notesList;
        private TextView emptyListText;
        private ArrayList<NoteItem> notes;
        private ProjectViewActivity.NoteListAdapter notesAdapter;

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
            if (tab == 2) {
                rootView = inflater.inflate(R.layout.fragment_project_view_notes, container, false);
                initNotesFragment();
            } else {
                rootView = inflater.inflate(R.layout.fragment_project_view_details, container, false);
                initDetailsFragment();
            }
            return rootView;
        }

        private void initNotesFragment() {
            getNotesFragmentUIElements();
            setListAdapter();
            setListeners();
            refreshList();
        }

        public void getNotesFragmentUIElements() {
            addNote = (FloatingActionButton) rootView.findViewById(R.id.fragment_project_view_notes);
            notesList = (ListView) rootView.findViewById(R.id.fragment_list_view);
            emptyListText = (TextView) rootView.findViewById(R.id.fragment_list_empty);
            emptyListText.setText(R.string.text_no_existing_notes);
        }

        private void setListAdapter() {
            notes = new ArrayList<NoteItem>();
            notesAdapter = new ProjectViewActivity.NoteListAdapter(getContext(), notes);
            notesList.setAdapter(notesAdapter);
            notesList.setEmptyView(emptyListText);
        }

        private void setListeners() {
            notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showNoteView(position);
                }
            });

            notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    showContextMenu(position);
                    return true;
                }
            });

            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startNoteCreateActivityIntent = new Intent(getActivity(), NoteCreateActivity.class);
                    startNoteCreateActivityIntent.putExtra(getString(R.string.intent_extra_key_project_id), project.getId());
                    startNoteCreateActivityIntent.putExtra(getString(R.string.intent_extra_key_note_id), -1);
                    startActivity(startNoteCreateActivityIntent);
                }
            });
        }

        private void showNoteView(int position) {
            int id = notes.get(position).getId();
            Intent startNoteViewActivityIntent = new Intent(getActivity(), ProjectViewActivity.class);
            startNoteViewActivityIntent.putExtra(getString(R.string.intent_extra_key_project_id), project.getId());
            startNoteViewActivityIntent.putExtra(getString(R.string.intent_extra_key_note_id), id);
            getActivity().startActivity(startNoteViewActivityIntent);
        }

        private void showContextMenu(int position) {

        }

        public void refreshList() {
            ArrayList<NoteItem> temp;
            db.open();
            temp = db.getAllNotes(project.getId());
            db.close();
            notes.clear();
            notes.addAll(temp);
            notesAdapter.notifyDataSetChanged();
        }


        private void initDetailsFragment() {
            getDetailsFragmentUIElements();
            setDetails();
        }

        private void getDetailsFragmentUIElements() {
            addressView = (TextView) rootView.findViewById(R.id.project_view_detail_address);
            startView = (TextView) rootView.findViewById(R.id.project_view_detail_start);
            clientView = (TextView) rootView.findViewById(R.id.project_view_detail_client);
            attendeesView = (TextView) rootView.findViewById(R.id.project_view_detail_attendees);
        }

        private void setDetails() {
            addressView.setText(project.getAddress());
            startView.setText(project.getStart());
            clientView.setText(project.getClient());
            attendeesView.setText(project.getAttendees());
        }
    }

    private static class NoteListAdapter extends ArrayAdapter<NoteItem> {
        private ArrayList<NoteItem> notes;
        private Context context;

        public NoteListAdapter(Context context, ArrayList<NoteItem> notes) {
            super(context, R.layout.item_note_list, notes);
            this.context = context;
            this.notes = notes;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.item_project_list, null);

            }

            NoteItem note = notes.get(position);

            if (project != null) {
                ImageView img = (ImageView) v.findViewById(R.id.project_view_note_item_img_view);
                TextView date = (TextView) v.findViewById(R.id.project_view_note_item_date_view);
                date.setText(note.getDate());
                ImageView weather = (ImageView) v.findViewById(R.id.project_view_note_item_weather);
            }

            return v;
        }

    }
}
