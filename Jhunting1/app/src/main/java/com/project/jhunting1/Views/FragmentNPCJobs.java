package com.project.jhunting1.Views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.project.jhunting1.Local.JhuntingDB;
import com.project.jhunting1.Local.JobDataSource;
import com.project.jhunting1.Local.Job_ApplicationDataSource;
import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Neural_Network.CNN_Model;
import com.project.jhunting1.R;
import com.project.jhunting1.Repository.JobRepo;
import com.project.jhunting1.Repository.Job_ApplicationRepo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.content.res.AssetManager;

public class FragmentNPCJobs extends Fragment{

    private View view;
    private int npc_id;
    JhuntingDB jhuntingDB;
    private static final int UNIQUE_FRAGMENT_GROUP_ID = 3;
    private static final String  ADD_JOB= "ADD_JOB";
    private static final String CLEAR_ALL_JOBS = "CLEAR_ALL_JOBS";
    private static final String UPDATE_JOB = "UPDATE_JOB";
    private static final String APPLY_STATUS = "AWAITING APPROVAL";
    private static final String ACCEPT_STATUS = "ON PROGRESS";
    private static final String TURNED_IN_STATUS = "AWAITING FEEDBACK";
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String DELETE_JOB = "DELETE JOB";
    private static final String EDIT_JOB = "EDIT JOB";

    private EditText txtSearch;
    private FloatingActionButton btnNewJob;
    private ListView lvAllJobs;
    private EditText txtJobTitle;
    private EditText txtJobDescription;
    private Spinner spnContractDuration;
    private Button btnAddJob;
    private Button btnCancel;
    private Button btnUpdateJob;

    private static JobRepo jobRepo;
    private Job_ApplicationRepo job_applicationRepo;

    private int categoryId = 0;
    private int contractDurationId = 0;
    private String selectedCategory = "";
    private Jobs selectedJob;
    private int durationIndex;

    private String searchTxt = "";

    private List<Job_Category> categories = new ArrayList<Job_Category>();
    private List<Jobs> allJobs = new ArrayList<Jobs>();
    private ArrayAdapter<Job_Category> categoryAdapter;
    private ArrayAdapter<Jobs> jobsAdapter;
    private List<Contract_Duration> durations = new ArrayList<Contract_Duration>();
    private Contract_Duration contract_duration = new Contract_Duration();

    private List<Contract_Duration> lst_contract_durations = new ArrayList<Contract_Duration>();
    private ArrayAdapter<Contract_Duration> contract_durationAdapter;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FragmentNPCJobs() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.npc_jobs, container, false);
        jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
        npc_id = getArguments().getInt("id");

        setUpDatabaseRepositories();
        bindViewComponents();
        assignDataAdapters();
        makeListScrollable();
        setUpListView();
        enableAddingJobs();

        return view;
    }

    private void setUpDatabaseRepositories(){
        try{
            jobRepo = jobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
            job_applicationRepo = job_applicationRepo.getInstance(Job_ApplicationDataSource.getInstance(jhuntingDB.job_applicationDAO()));
        }catch (Exception ex){
            Log.e("setUpDatabaseRepository", ex.getMessage());
        }
    }

    private void assignDataAdapters(){
        try{
            contract_durationAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, durations);
            jobsAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, allJobs);
            lvAllJobs.setAdapter(jobsAdapter);
        }catch (Exception ex){
            Log.e("assignDataAdapters", ex.getMessage());
        }
    }

    private void bindViewComponents(){
        try {
            btnNewJob = (FloatingActionButton) view.findViewById(R.id.btnNewJob);
            lvAllJobs = (ListView) view.findViewById(R.id.listNpcJobs);
            registerForContextMenu(lvAllJobs);
        }catch (Exception ex){
            Log.e("bindViewComponents", ex.getMessage());
        }
    }

    //FILLING LIST VIEW WITH JOBS THAT BELONGS TO NPC ---------------------------------------------------START!!
    private void setUpListView(){
        try {
            Disposable disposable = jobRepo.getObservableAllJobsByNpcId(npc_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Jobs>>() {
                        @Override
                        public void accept(final List<Jobs> jobs) throws Exception {
                            allJobs.clear();
                            allJobs.addAll(jobs);
                            jobsAdapter.notifyDataSetChanged();
                            for (final Jobs j : jobs) {
                                if (!searchTxt.isEmpty() || searchTxt.equals("")) {
                                    displayAllNpcJobs(j);
                                }
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("setUpListView", ex.getMessage());
        }
    }

    private void displayAllNpcJobs(final Jobs jobs){
        Disposable applicationDisposable = job_applicationRepo.getObservableJobApplicationByJobId(jobs.getJob_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Job_Applications>>() {
                    @Override
                    public void accept(List<Job_Applications> applications) throws Exception {
                            for (Job_Applications ja : applications) {
                                if (jobs.getJob_id() == ja.getJob_id()) {
                                    if (ja.getStatus().equals(ACCEPT_STATUS) || ja.getStatus().equals(COMPLETED_STATUS)
                                            || ja.getStatus().equals(TURNED_IN_STATUS)) {
                                        allJobs.remove(jobs);
                                        jobsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                    }
                });
        compositeDisposable.add(applicationDisposable);
    }

    private void displaySearchedNpcJobs(Jobs jobs){

    }

    //FILLED LIST VIEW WITH JOBS THAT BELONGS TO NPC ---------------------------------------------------FINISH!!

    //METHODS TO ADD NEW JOBS WHEN 'FOB' IS CLICKED ------------------------------------------------------------START!!
    private void enableAddingJobs(){
        try {
            btnNewJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNewJobForm();
                    //   setUpJobDuration();
                    //  safeDatabaseTransactions(view.getContext(),CLEAR_ALL_JOBS);
                }
            });
        }catch (Exception ex){
            Log.e("enableAddingJobs", ex.getMessage());
        }
    }

    private void showNewJobForm() {
        try {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.form_new_job);
            dialog.show();

            txtJobTitle = (EditText)dialog.findViewById(R.id.txtAddJobTitle);
            txtJobDescription = (EditText)dialog.findViewById(R.id.txtAddJobDescription);
            spnContractDuration = (Spinner)dialog.findViewById(R.id.spnContractDuration);
            btnAddJob = (Button)dialog.findViewById(R.id.btnAddJob);
            btnCancel = (Button)dialog.findViewById(R.id.btnAddJobCancel);

            setUpDurationsSpinner(spnContractDuration);

            spnContractDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Contract_Duration contract_duration = (Contract_Duration) adapterView.getItemAtPosition(i);
                    contractDurationId = contract_duration.getDuration_id();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            btnAddJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateFields()) {
                        safeDatabaseTransactions(ADD_JOB);
                        dialog.dismiss();
                        jobsAdapter.notifyDataSetChanged();
                    }

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
    //METHODS TO ADD NEW JOBS WHEN 'FOB' IS CLICKED ------------------------------------------------------------FINISHED!!

    //METHOD TO SET UP DURATIONS IN THE SPINNER WHEN NEW JOB IS ADDING OR WHEN EDITING JOB ----------------------------------START!!
    private void setUpDurationsSpinner(final Spinner spinner){
        try {
            spinner.setAdapter(contract_durationAdapter);
            Disposable disposable = jobRepo.getObservableContractDurations()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Contract_Duration>>() {
                        @Override
                        public void accept(List<Contract_Duration> contract_durations) throws Exception {
                            durations.clear();
                            durations.addAll(contract_durations);
                            contract_durationAdapter.notifyDataSetChanged();
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("setUpDurationsSpinner", ex.getMessage());
        }
    }

    private void selectValue(Spinner spinner, Contract_Duration value) {
        try {
            for (int i = 0; i < spinner.getCount(); i++) {
                Contract_Duration duration = (Contract_Duration) spinner.getItemAtPosition(i);
                if (duration.getDays() == (value.getDays())) {
                    spinner.setSelection(i);
                    break;
                }
                Log.e("Loop Duration Days", Integer.toString(duration.getDays()));
            }
        }catch (Exception ex){
            Log.e("selectValue", ex.getMessage());
        }
    }
    //METHOD TO SET UP DURATIONS IN THE SPINNER WHEN NEW JOB IS ADDING OR WHEN EDITING JOB ----------------------------------FINISH!!

    //CONFIGURING THE INTERACTION WITH THE LIST VIEW ITEMS WHEN CLICKED ---------------------------------------------------START!!
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Select Option:");

            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 0, Menu.NONE, "Delete Job");
            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 1, Menu.NONE, "Edit Job");
            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 2, Menu.NONE, "View Details");
        }catch (Exception ex){
            Log.e("onCreateContextMenu", ex.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if (item.getGroupId() == UNIQUE_FRAGMENT_GROUP_ID) {
                selectedJob = allJobs.get(info.position);
                switch (item.getItemId()) {
                    case 0: {//Delete Job
                        showConfirmDialog(view.getContext(), DELETE_JOB);
                    }
                    break;

                    case 1: {//Edit
                        showEditJobForms();
                    }
                    break;

                    case 2: {//JOB Details
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

                            btnApply.setVisibility(View.GONE);

                            showAllJobsInLOG();

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
                    break;
                }
            }
        }catch (Exception ex){
            Log.e("onContextItemSelected", ex.getMessage());
        }
        return super.onContextItemSelected(item);
    }

    private void showEditJobForms(){
        try {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.edit_job_information);
            dialog.show();

            txtJobTitle = (EditText)dialog.findViewById(R.id.txtEditJobTitle);
            txtJobDescription = (EditText)dialog.findViewById(R.id.txtEditJobDescription);
            spnContractDuration = (Spinner)dialog.findViewById(R.id.spnEditContractDuration);
            btnUpdateJob = (Button)dialog.findViewById(R.id.btnEditJob);
            btnCancel = (Button)dialog.findViewById(R.id.btnEditJobCancel);

            final int durationId = selectedJob.getContractDurationId();

            Disposable disposableDuration = jobRepo.getObservableContractDurationById(durationId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Contract_Duration>() {
                        @Override
                        public void accept(Contract_Duration duration) throws Exception {
                            contract_duration = duration;
                        }
                    });
            compositeDisposable.add(disposableDuration);

            setUpDurationsSpinner(spnContractDuration);


            txtJobTitle.setText(selectedJob.getJob_title().toString());
            txtJobDescription.setText(selectedJob.getJob_description().toString());

            final String[] loadTime = {"FirstTime"};
            spnContractDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (loadTime[0].equals("FirstTime")){
                        selectValue(spnContractDuration, contract_duration);
                        loadTime[0] = "Changed";
                    }
                    Contract_Duration contract_duration = (Contract_Duration) adapterView.getItemAtPosition(i);
                    contractDurationId = contract_duration.getDuration_id();
                    Log.e("New Spinner position", Integer.toString(contractDurationId));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            btnUpdateJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedJob.setJob_title(txtJobTitle.getText().toString());
                    selectedJob.setJob_description(txtJobDescription.getText().toString());
                    selectedJob.setContractDurationId(contractDurationId);
                    showConfirmDialog(dialog.getContext(), UPDATE_JOB);
                    dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        }catch (Exception ex){
            Log.e("Error", ex.getMessage());
        }
    }

    private boolean validateFields(){
        try {
            if (txtJobTitle.getText().toString().isEmpty()) {
                txtJobTitle.requestFocus();
                makeToast("Please Enter Job Title");
                return false;
            } else if (txtJobDescription.getText().toString().isEmpty()) {
                txtJobDescription.requestFocus();
                makeToast("Job Description cannot be empty");
                return false;
            } else {
                return true;
            }
        }catch (Exception ex){
            makeToast("Unknown Error");
            Log.e("validateFields", ex.getMessage());
            return false;
        }
    }

    private void showConfirmDialog(Context context, final String transactionType){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.confirmation_dialogue);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            TextView tvTitle = (TextView) dialog.findViewById(R.id.tvConfirmationTitle);
            Button btnYes = (Button) dialog.findViewById(R.id.btnApplyJobYes);
            Button btnNo = (Button) dialog.findViewById(R.id.btnApplyJobNo);


            if (transactionType.equals(ADD_JOB)) {
                tvTitle.setText(R.string.jobAddConfirmTxt);
            } else if (transactionType.equals(UPDATE_JOB)) {
                tvTitle.setText(R.string.jobUpdateConfirmTxt);
            } else if (transactionType.equals(DELETE_JOB)) {
                tvTitle.setText(R.string.jobDeleteConfirmTxt);
            }

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    safeDatabaseTransactions(transactionType);
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
        }catch(Exception ex){
            Log.e("showConfirmDialog", ex.getMessage());
        }
    }
    //CONFIGURING THE INTERACTION WITH THE LIST VIEW ITEMS WHEN CLICKED ---------------------------------------------------FINISH!!


    //A SAFE WAY TO PERSIST DATA INTO THE DATABASE --------------------------------------------------------------------------START!!
    private void safeDatabaseTransactions(final String transactionName){
        try {
            Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {

                    if (transactionName.equals(DELETE_JOB)) {
                        if (selectedJob != null) {
                            List<Job_Applications> selectedJobApplications = job_applicationRepo.getJobApplicationByJobId(selectedJob.getJob_id());
                            boolean applicationOnProgress = false;
                            for (Job_Applications applications: selectedJobApplications){
                                if (applications.getStatus().equals(ACCEPT_STATUS)){
                                    applicationOnProgress = true;
                                }
                            }
                            if (!applicationOnProgress) {
                                job_applicationRepo.deleteAllJobApplications(selectedJobApplications);
                                jobRepo.deleteJob(selectedJob);
                                Log.e("Delete Job", "Successfully deleted Job");
                            }else {
                                Log.e("Delete Job", "Application is on progress");
                            }
                        }
                    } else if (transactionName.equals(ADD_JOB)) {
                        CNN_Model cnn_model = CNN_Model.initialize(view.getContext().getAssets());
                        String textToClassify = txtJobDescription.getText().toString();

                        String[] cnn_output = cnn_model.classify(textToClassify);
                        String bestMatch = cnn_output[0];
                        String second_bestMatch = cnn_output[1];
                        int category1 = jobRepo.getJobCategoryId(bestMatch);
                        int category2 = jobRepo.getJobCategoryId(second_bestMatch);
                        Log.e("CNN_OUTPUT : ", cnn_output[0] + " , " + cnn_output[1]);

                        final Jobs job = new Jobs(npc_id, txtJobTitle.getText().toString(), txtJobDescription.getText().toString(), contractDurationId, category1, category2);
                        int insertedJob = (int) jobRepo.addJob(job);
                        Jobs addedRecord = jobRepo.getJob(insertedJob);
                        Log.e("New Job", addedRecord.getJob_description() + " , " + addedRecord.getContractDurationId());
                    }

                    else if (transactionName.equals(UPDATE_JOB)) {
                        if (selectedJob != null) {
                            jobRepo.updateJob(selectedJob);
                            Log.e("Updated Job Job", selectedJob.getJob_description() + " , " + selectedJob.getContractDurationId());
                        }
                    }
                    else if (transactionName.equals(CLEAR_ALL_JOBS)) {
                        List<Jobs> jobs = jobRepo.getAllJobs();
                        if (!jobs.isEmpty()) {
                            jobRepo.deleteAllJobs(jobs);
                        }
                    }
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer() {

                        @Override
                        public void accept(Object o) throws Exception {
                            makeToast("Transaction successful");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("Throwable", throwable.getMessage());
                        }
                    });
            compositeDisposable.add(disposable);

        }catch (Exception ex){
            Log.e("safeDatabaseTransaction", ex.getMessage());
        }
    }
    //A SAFE WAY TO PERSIST DATA INTO THE DATABASE --------------------------------------------------------------------------START!!

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

    private void showAllJobsInLOG(){
        Disposable d = jobRepo.getObservableAllJobsNormally()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Jobs>>() {
                    @Override
                    public void accept(List<Jobs> jobs) throws Exception {
                        for (Jobs j : jobs){
                            Log.e("All Jobs", j.getJob_title());
                        }
                    }
                });
        compositeDisposable.add(d);
    }

    //HELPFUL METHODS
    private void setUpJobDuration(){
        try {
            compositeDisposable = new CompositeDisposable();
            JhuntingDB jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
            jobRepo = JobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));

            Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {
                    List<Contract_Duration> contract_durations = new ArrayList<Contract_Duration>();
                    contract_durations = jobRepo.getAllContractDuration();
                    if (contract_durations.isEmpty()) {
                        contract_durations.add(new Contract_Duration(7, 8, 6, 4));
                        contract_durations.add(new Contract_Duration(14, 15, 12, 10));
                        contract_durations.add(new Contract_Duration(30, 30, 25, 20));
                        contract_durations.add(new Contract_Duration(90, 60, 50, 40));
                        contract_durations.add(new Contract_Duration(180, 110, 90, 80));
                        contract_durations.add(new Contract_Duration(360, 240, 220, 200));
                        jobRepo.insertDurationOptions(contract_durations);

                    }

                    for (Contract_Duration cd : jobRepo.getAllContractDuration()) {
                        Log.e("Contract Duration", new StringBuilder("Days: " + cd.getDays() + ", Good Score: " + cd.getGoodScore() + ", " +
                                "Sat Score: " + cd.getSatisfactoryScore() + ", Not Bad Score: " + cd.getNotBadScore()).toString());
                    }

                    e.onComplete();
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer() {

                        @Override
                        public void accept(Object o) throws Exception {
                            Toast.makeText(view.getContext(), "Successful", Toast.LENGTH_SHORT).show();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("setUpJobDuration", ex.getMessage());
        }

    }

    private void deleteAllJobs() {
        try {
            Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    List<Jobs> allJobs = jobRepo.getAllJobs();
                    jobRepo.deleteAllJobs(allJobs);
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
            Log.e("deleteAllJobs", ex.getMessage());
        }
    }

}
