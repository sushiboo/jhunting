package com.project.jhunting1.Views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.project.jhunting1.Local.JhuntingDB;
import com.project.jhunting1.Local.JobDataSource;
import com.project.jhunting1.Local.Job_ApplicationDataSource;
import com.project.jhunting1.Local.NPCDataSource;
import com.project.jhunting1.Local.PlayerDataSource;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;
import com.project.jhunting1.Model.Player_Expertise;
import com.project.jhunting1.R;
import com.project.jhunting1.Repository.JobRepo;
import com.project.jhunting1.Repository.Job_ApplicationRepo;
import com.project.jhunting1.Repository.NPCRepo;
import com.project.jhunting1.Repository.PlayerRepo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FragmentAllJobs extends Fragment{

    private View view;
    private int player_id;
    JhuntingDB jhuntingDB;
    private static final int UNIQUE_FRAGMENT_GROUP_ID = 1;
    private static final String APPLY_STATUS = "AWAITING APPROVAL";
    private static final String ACCEPT_STATUS = "ON PROGRESS";
    private static final String TURNED_IN_STATUS = "AWAITING FEEDBACK";
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String NEW_APPLICATION = "NEW APPLICATION";
    private static final Boolean EXISTS = true;
    private static final Boolean NOT_EXIST = false;

    private ListView lvAllJobs;
    private Spinner spnJobsCategory;

    private static JobRepo jobRepo;
    private static NPCRepo npcRepo;
    private static PlayerRepo playerRepo;
    private static Job_ApplicationRepo job_applicationRepo;
    private Jobs selectedJob;

    Job_Applications job_application = new Job_Applications();

    private int categoryId;

    private List<Jobs> allJobs = new ArrayList<Jobs>();
    private ArrayAdapter<Jobs> jobsAdapter;

    private List<Job_Category> categories = new ArrayList<Job_Category>();
    private ArrayAdapter<Job_Category> categoryAdapter;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FragmentAllJobs() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.all_jobs, container, false);
        bindView();
        jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
        jobRepo = jobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
        npcRepo = npcRepo.getInstance(NPCDataSource.getInstance(jhuntingDB.npc_dao()));
        playerRepo = playerRepo.getInstance(PlayerDataSource.getInstance(jhuntingDB.playerDAO()));
        job_applicationRepo = job_applicationRepo.getInstance(Job_ApplicationDataSource.getInstance(jhuntingDB.job_applicationDAO()));
        player_id = getArguments().getInt("id");


        setUpSpinnerFeatures();

        jobsAdapter = new ArrayAdapter<Jobs>(view.getContext(), android.R.layout.simple_list_item_1, allJobs);
        lvAllJobs.setAdapter(jobsAdapter);

        makeListScrollable();
        setUpListView();

        return view;
    }

    private void bindView(){
        lvAllJobs = (ListView)view.findViewById(R.id.listPlayerAllJobs);
        registerForContextMenu(lvAllJobs);
        spnJobsCategory = (Spinner)view.findViewById(R.id.spnPlayerSearchJobCategory);
    }

    private void safeDatabaseTransactions(final String transactionName){

        try {
            Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    if (transactionName.equals(NEW_APPLICATION)) {
                        if (!job_application.getStatus().isEmpty()) {
                            List<Job_Applications> applicationsList = job_applicationRepo.getJobApplicationByJobId(job_application.getJob_id());
                            if (!applicationExists(applicationsList, job_application)) {
                                job_applicationRepo.insertJobApplication(job_application);
                            }else{
                                Log.e("Error", "Application already exists");
                            }
                        }
                    }
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer() {

                        @Override
                        public void accept(Object o) throws Exception {
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("Throwable", throwable.getMessage());
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("Error", ex.getMessage());
        }
    }

    private boolean applicationExists(List<Job_Applications> applications, Job_Applications job_application){
        for (Job_Applications application : applications){
            if (selectedJob.getJob_id() == job_application.getJob_id() && application.getPlayer_id() == job_application.getPlayer_id()
                    && application.getStatus().equals(APPLY_STATUS)){
                return EXISTS;
            }
        }
        return NOT_EXIST;
    }

    private void setUpListView(){
        Disposable disposable = jobRepo.getObservableAllJobsForCategory(categoryId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Jobs>>() {
                    @Override
                    public void accept(final List<Jobs> jobs) throws Exception {
                        Log.e("Listview Category id", Integer.toString(categoryId));
                        Disposable applicationDisposable = job_applicationRepo.getObservableJobApplicationByPlayerId(player_id)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<List<Job_Applications>>() {
                                    @Override
                                    public void accept(List<Job_Applications> applications) throws Exception {
                                        allJobs.clear();
                                        allJobs.addAll(jobs);
                                        jobsAdapter.notifyDataSetChanged();
                                        for (Jobs j : jobs){
                                            for (Job_Applications ja : applications) {
                                                if (j.getJob_id() == ja.getJob_id()) {
                                                    if (ja.getStatus().equals(ACCEPT_STATUS) || ja.getStatus().equals(COMPLETED_STATUS)
                                                            || ja.getStatus().equals(TURNED_IN_STATUS)) {
                                                        if (allJobs.contains(j)) {
                                                            allJobs.remove(j);
                                                            jobsAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                    if (player_id == ja.getPlayer_id()) {
                                                        if (ja.getStatus().equals(APPLY_STATUS) || ja.getStatus().equals(COMPLETED_STATUS)
                                                                || ja.getStatus().equals(TURNED_IN_STATUS)) {
                                                            if (allJobs.contains(j)) {
                                                                allJobs.remove(j);
                                                                jobsAdapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (Jobs jobs1 : allJobs){
                                            Log.e("Jobs category", Integer.toString(jobs1.getJob_category1()) + " , " +
                                            Integer.toString(jobs1.getJob_category2()));
                                        }

                                    }
                                });
                        compositeDisposable.add(applicationDisposable);

                    }
                });
        compositeDisposable.add(disposable);
    }

    private void setUpSpinnerFeatures(){
        categoryAdapter = new ArrayAdapter<Job_Category>(view.getContext(), android.R.layout.simple_spinner_item, categories);
        spnJobsCategory.setAdapter(categoryAdapter);
        Disposable disposable = playerRepo.getObservableCategoriesForPlayer(player_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Job_Category>>() {
                    @Override
                    public void accept(List<Job_Category> job_categories) throws Exception {
                        categories.clear();
                        categories.addAll(job_categories);
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
        compositeDisposable.add(disposable);

        spnJobsCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Job_Category job_category = (Job_Category) adapterView.getItemAtPosition(i);
                categoryId = job_category.getJc_id();
                Log.e("Category id changed", Integer.toString(categoryId));
                setUpListView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo jobinfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Select Option:");

        menu.add(UNIQUE_FRAGMENT_GROUP_ID, 0, Menu.NONE, "Apply");
        menu.add(UNIQUE_FRAGMENT_GROUP_ID, 1, Menu.NONE, "View Details");
        menu.add(UNIQUE_FRAGMENT_GROUP_ID, 2, Menu.NONE, "View NPC Info");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo jobinfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if (item.getGroupId() == UNIQUE_FRAGMENT_GROUP_ID){
        selectedJob = allJobs.get(jobinfo.position);
        switch (item.getItemId()) {
            case 0: {//APPLY
                applyJob();
            }
            break;

            case 1: {//JOB DESCRIPTION
                showJobInfo();
            }
            break;

            case 2: {//NPC INFO
                showNpcInfo();
            }
            break;
        }
        }
        return super.onContextItemSelected(item);
    }

    private void applyJob(){
        Disposable disposable = playerRepo.getObservablePlayerExpertises(player_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Player_Expertise>>() {
                    @Override
                    public void accept(List<Player_Expertise> player_expertises) throws Exception {
                        for (int i = 0; i < player_expertises.size(); i++){
                            Player_Expertise pe = (Player_Expertise)player_expertises.get(i);
                            if (pe.getCategory_id() == selectedJob.getJob_category1() ||
                                    pe.getCategory_id() == selectedJob.getJob_category2()){
                                job_application = new Job_Applications(selectedJob.getJob_id(), player_id, APPLY_STATUS);
                                showApplyConfirmDialog(view.getContext());
                                break;
                            }else if ((pe.getCategory_id() != selectedJob.getJob_category1() ||
                                    pe.getCategory_id() != selectedJob.getJob_category2()) && i == player_expertises.size() -1){
                                Log.e("applyJob", "User not qualified");
                            }
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void showJobInfo(){
        try {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.job_info);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            final TextView jobTitle = (TextView) dialog.findViewById(R.id.tvJobTitle);
            final TextView jobDescription = (TextView) dialog.findViewById(R.id.tvJobInfo);
            final Button btnApply = (Button) dialog.findViewById(R.id.btnJobInfoApply);
            final Button btnCancel = (Button) dialog.findViewById(R.id.btnJobInfoCancel);

            Disposable disposable = jobRepo.getObservableJob(selectedJob.getJob_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Jobs>() {
                        @Override
                        public void accept(Jobs jobs) throws Exception {
                            jobTitle.setText(jobs.getJob_title().toString());
                            jobDescription.setText("Description: \n " + jobs.getJob_description().toString());
                        }
                    });
            compositeDisposable.add(disposable);

            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    job_application = new Job_Applications(selectedJob.getJob_id(), player_id, APPLY_STATUS);
                    showApplyConfirmDialog(dialog.getContext());
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    private void showNpcInfo(){
        try {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.npc_info);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            final TextView tvNpcName = (TextView) dialog.findViewById(R.id.tvNpcInfoName);
            final TextView tvNpcEmail = (TextView) dialog.findViewById(R.id.tvNpcInfoEmail);
            final TextView tvNpcTelephone = (TextView) dialog.findViewById(R.id.tvNpcInfoTelephone);
            final TextView tvNpcTrustLevel = (TextView) dialog.findViewById(R.id.tvNpcInfoTrustLevel);
            final TextView tvNpcTrustPoints = (TextView) dialog.findViewById(R.id.tvNpcInfoTrustExp);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnNpcInfoCancel);

            Disposable disposable = npcRepo.getObservableNPCById(selectedJob.getNpc_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<NPC>() {
                        @Override
                        public void accept(NPC npc) throws Exception {
                            tvNpcName.setText(": " + npc.getName().toString());
                            tvNpcEmail.setText(": " + npc.getEmail().toString());
                            tvNpcTelephone.setText(": " + Integer.toString(npc.getTelephone()));
                            Disposable trustWorthDisposable = npcRepo.getObservableNpcTrustworth(npc.getNPC_Id())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<NPC_Trustworth>() {
                                        @Override
                                        public void accept(NPC_Trustworth npc_trustworth) throws Exception {
                                            tvNpcTrustLevel.setText(": " + Integer.toString(npc_trustworth.getTrust_level()));
                                            tvNpcTrustPoints.setText(": " + Integer.toString(npc_trustworth.getTrust_points()));
                                        }
                                    });
                            compositeDisposable.add(trustWorthDisposable);
                        }
                    });
            compositeDisposable.add(disposable);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    private void showApplyConfirmDialog(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialogue);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Button btnYes = (Button)dialog.findViewById(R.id.btnApplyJobYes);
        Button btnNo = (Button)dialog.findViewById(R.id.btnApplyJobNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                safeDatabaseTransactions(NEW_APPLICATION);
                dialog.dismiss();
                jobsAdapter.notifyDataSetChanged();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void makeListScrollable(){
        lvAllJobs.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    private void makeToast(String message){
        Toast.makeText(view.getContext(), message , Toast.LENGTH_SHORT).show();
    }
}
