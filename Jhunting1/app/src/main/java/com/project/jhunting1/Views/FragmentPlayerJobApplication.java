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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;
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

public class FragmentPlayerJobApplication extends Fragment{

    private View view;
    JhuntingDB jhuntingDB;
    private static final int UNIQUE_FRAGMENT_GROUP_ID = 2;
    private static final String APPLY_STATUS = "AWAITING APPROVAL";
    private static final String ACCEPT_STATUS = "ON PROGRESS";
    private static final String REJECT_STATUS = "REJECTED";
    private static final String TURNED_IN_STATUS = "AWAITING FEEDBACK";
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String REQUESTED_STATUS = "REQUESTED";
    private static final String ACCEPT_REQUEST = "ON PROGRESS";
    private static final String REJECT_REQUEST = "REJECTED";
    private static final String DELETE_APPLICATION = "DELETE_APPLICATION";
    private static final String TURN_IN_APPLICATION = "AWAITING FEEDBACK";
    private static final String GIVE_FEEDBACK = "GIVE FEEDBACK";
    private static String FEEDBACK_GIVEN;
    private static final String GOOD_FEEDBACK = "Good";
    private static final String SATISFACTORY_FEEDBACK = "Satisfactory";
    private static final String NOT_BAD_FEEDBACK = "Not Bad";

    private Spinner spnApplicationType;
    private ListView lvApplications;

    private static JobRepo jobRepo;
    private Job_ApplicationRepo job_applicationRepo;

    private Job_Applications selected_application;

    private List<Job_Applications> applicationsList = new ArrayList<Job_Applications>();
    private ArrayAdapter<Job_Applications> applicationsAdapter;

    private String selectedStatus = APPLY_STATUS;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private int player_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.player_job_applications, container, false);
        player_id = getArguments().getInt("id");
        jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());

        setUpDatabaseRepository();
        bindView();
        setUpDatabaseAdapters();
        listAllApplicationsForPlayer();
        enableApplicationListFiltering();

        return view;
    }

    private void setUpDatabaseRepository(){
        try{
            jobRepo = jobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
            job_applicationRepo = job_applicationRepo.getInstance(Job_ApplicationDataSource.getInstance(jhuntingDB.job_applicationDAO()));
        }catch (Exception ex){
            Log.e("setUpDatabaseRepository", ex.getMessage());
        }
    }

    private void setUpDatabaseAdapters(){
        try{
            applicationsAdapter = new ArrayAdapter<Job_Applications>(view.getContext(), android.R.layout.simple_list_item_1, applicationsList);
            lvApplications.setAdapter(applicationsAdapter);
        }catch (Exception ex){
            Log.e("setUpDatabaseAdapters", ex.getMessage());
        }
    }

    //LISTING NPC APPLICATIONS THAT ARE 'AWAITING APPROVAL' OR 'IN PROGRESS'---------------------- START
    private void listAllApplicationsForPlayer(){
        try {
            Disposable disposable = job_applicationRepo.getObservableJobApplicationByPlayerId(player_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Job_Applications>>() {
                        @Override
                        public void accept(List<Job_Applications> applications) throws Exception {
                            applicationsList.clear();
                            applicationsAdapter.notifyDataSetChanged();
                            filterOutUnavailableApplication(applications, selectedStatus);
                        }
                    });

            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("listAllApplication", ex.getMessage());
        }
    }

    private void filterOutUnavailableApplication(List<Job_Applications> applications, String applicationType){
        try {
            for (final Job_Applications ja : applications) {
                if (ja.getStatus().equals(applicationType)) {
                    if (!applicationsList.contains(ja)) {
                        Job_Applications jobApplications = new Job_Applications();
                        if (ja.getPlayer() == null) {
                            jobApplications = setPlayer(ja);
                        }
                        if (ja.getJobs() == null) {
                            jobApplications = setJob(ja);
                        }
                        applicationsList.add(jobApplications);
                        applicationsAdapter.notifyDataSetChanged();
                    }
                }
                if (ja.getStatus().equals(REJECT_STATUS) && !applicationType.equals(REJECT_STATUS)) {
                    if (applicationsList.contains(ja)) {
                        applicationsList.remove(ja);
                    }
                }
                if (applicationType.equals(TURNED_IN_STATUS) && FEEDBACK_GIVEN(ja)){
                    if (applicationsList.contains(ja)) {
                        applicationsList.remove(ja);
                        applicationsAdapter.notifyDataSetChanged();
                    }
                }
            }
        }catch (Exception ex){
            Log.e("filterWrongApplication", ex.getMessage());
        }
    }

    private boolean FEEDBACK_GIVEN(Job_Applications job_applications){
        if (job_applications.getNpc_feedback().equals(GOOD_FEEDBACK) || job_applications.getNpc_feedback().equals(SATISFACTORY_FEEDBACK)
                || job_applications.getNpc_feedback().equals(NOT_BAD_FEEDBACK) || job_applications.getStatus().equals(COMPLETED_STATUS)){
            return true;
        }else{
            return false;
        }
    }

    private Job_Applications setPlayer(final Job_Applications job_applications){
        try {
            Disposable disposable = job_applicationRepo.getObservablePlayer(player_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Player>() {
                        @Override
                        public void accept(Player player) throws Exception {
                            if (job_applications.getPlayer() == null) {
                                job_applications.setPlayer(player);
                                applicationsAdapter.notifyDataSetChanged();
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("setPlayer", ex.getMessage());
        }
        return job_applications;
    }

    private Job_Applications setJob(final Job_Applications job_applications){
        try {
            Disposable disposable = job_applicationRepo.getObservableJob(job_applications.getJob_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Jobs>() {
                        @Override
                        public void accept(Jobs jobs) throws Exception {
                            if (job_applications.getJobs() == null) {
                                job_applications.setJobs(jobs);
                                applicationsAdapter.notifyDataSetChanged();
                            }
                        }
                    });
            compositeDisposable.add(disposable);

            if (job_applications.getJobs() != null) {
                Log.e("Returning Jobs job", job_applications.getJobs().getJob_title());
            }
        }catch (Exception ex){
            Log.e("setJob", ex.getMessage());
        }
        return job_applications;
    }

    //LISTED NPC APPLICATIONS THAT ARE 'AWAITING APPROVA' OR 'IN PROGRESS'---------------------- FINISH!!

    private void bindView(){
        try {
            spnApplicationType = (Spinner) view.findViewById(R.id.spnPlayerApplicationType);
            lvApplications = (ListView) view.findViewById(R.id.listPlayerJobApplications);
            registerForContextMenu(view.findViewById(R.id.listPlayerJobApplications));
        }catch (Exception ex){
            Log.e("bindView", ex.getMessage());
        }
    }

    private void enableApplicationListFiltering() {
        try {
            spnApplicationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedStatus = (String) adapterView.getItemAtPosition(i);
                    Log.e("Selected Status", selectedStatus);
                    applicationsList.clear();
                    if (applicationsList.isEmpty()) {
                        listAllApplicationsForPlayer();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }catch (Exception ex){
            Log.e("enableListFilter", ex.getMessage());
        }
    }


    //CONFIGURING INTERACTION WITH APPLICATIONS IN THE LIST VIEW --------------------------------------------START!!!
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        try {
            AdapterView.AdapterContextMenuInfo applicationInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Select Option:");

            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 3, Menu.NONE, "View Job Details");
            if (selectedStatus.equals(APPLY_STATUS) || selectedStatus.equals(REJECT_STATUS)) {
                menu.add(UNIQUE_FRAGMENT_GROUP_ID, 4, Menu.NONE, "Delete Application");
            } else if (selectedStatus.equals(ACCEPT_STATUS)) {
                menu.add(UNIQUE_FRAGMENT_GROUP_ID, 5, Menu.NONE, "Turn in Job");
            } else if (selectedStatus.equals(TURNED_IN_STATUS)) {
                menu.add(UNIQUE_FRAGMENT_GROUP_ID, 6, Menu.NONE, "Give Feedback");
            }else if (selectedStatus.equals(REQUESTED_STATUS)) {
                menu.add(UNIQUE_FRAGMENT_GROUP_ID, 7, Menu.NONE, "Accept Request");
                menu.add(UNIQUE_FRAGMENT_GROUP_ID, 8, Menu.NONE, "Reject Request");
            }
        }catch (Exception ex){
            Log.e("onCreateContextMenu", ex.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo applicationInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if (item.getGroupId() == UNIQUE_FRAGMENT_GROUP_ID) {
                selected_application = applicationsList.get(applicationInfo.position);
                switch (item.getItemId()) {
                    case 3: {//View job Details
                        showJobDetailsDialog();
                    }
                    break;

                    case 4: {//DELETE APPLICATION
                        showConfirmDialog(view.getContext(), DELETE_APPLICATION);
                        break;
                    }


                    case 5: {//TURN IN JOB
                        showConfirmDialog(view.getContext(), TURN_IN_APPLICATION);
                        break;
                    }

                    case 6: {//GIVE FEEDBACK
                        showFeedbackDialogue();
                        break;
                    }

                    case 7:{//ACCEPT REQUEST
                        showConfirmDialog(view.getContext(), ACCEPT_REQUEST);
                        break;
                    }

                    case 8: {//REJECT REQUEST
                        showConfirmDialog(view.getContext(), REJECT_REQUEST);
                        break;
                    }

                }
            }
        }catch (Exception ex){
            Log.e("onContextItemSelected", ex.getMessage());
        }
        return super.onContextItemSelected(item);
    }


    private void showJobDetailsDialog(){
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

            //Getting Job info
            Disposable disposable = jobRepo.getObservableJob(selected_application.getJob_id())
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

            if (transactionType.equals(TURN_IN_APPLICATION)) {
                tvTitle.setText(R.string.turnInApplicationConfirmTxt);

            } else if (transactionType.equals(DELETE_APPLICATION)) {
                tvTitle.setText(R.string.deleteApplicationConfirmTxt);
            } else if (transactionType.equals(GIVE_FEEDBACK)) {
                tvTitle.setText(R.string.giveFeedbackConfirmTxt);
            }

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    safeDatabaseTransaction(transactionType);
                    dialog.dismiss();
                    applicationsAdapter.notifyDataSetChanged();
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }catch (Exception ex){
            Log.e("showConfirmDialog", ex.getMessage());
        }
    }

    private void showFeedbackDialogue(){
        try {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.give_feedback_dialogue);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            final RadioGroup rgFeedback = (RadioGroup)dialog.findViewById(R.id.rgFeedback);
            Button btnGiveFeedback = (Button) dialog.findViewById(R.id.btnGiveFeedback);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancelFeedback);

            getSelectedFeedback(rgFeedback);
            btnGiveFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FEEDBACK_GIVEN != null) {
                        if (!FEEDBACK_GIVEN.isEmpty()) {
                            showConfirmDialog(view.getContext(), GIVE_FEEDBACK);
                            dialog.dismiss();
                        } else {
                            Log.e("Feedback Error", "Given Feedback is Empty");
                        }
                    }else{
                        dialog.dismiss();
                        makeToast(view,"FEEDBACK GIVEN IS NULL");
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FEEDBACK_GIVEN != null){
                        Log.e("Feedback", FEEDBACK_GIVEN);
                    }
                    dialog.dismiss();
                }
            });
        }catch (Exception ex){
            Log.e("showFeedbackDialogue", ex.getMessage());
        }

    }

    private void getSelectedFeedback(final RadioGroup rgFeedback){
        try {
            rgFeedback.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    RadioButton checkedButton = (RadioButton) rgFeedback.findViewById(checkedId);
                    boolean isChecked = checkedButton.isChecked();
                    if (isChecked) {
                        Log.e("CHECKED", checkedButton.getText().toString());
                        String feedback = checkedButton.getText().toString();
                        switch (feedback) {
                            case GOOD_FEEDBACK:
                                FEEDBACK_GIVEN = GOOD_FEEDBACK;
                                Log.e("Checked", FEEDBACK_GIVEN);
                                break;

                            case SATISFACTORY_FEEDBACK:
                                FEEDBACK_GIVEN = SATISFACTORY_FEEDBACK;
                                Log.e("Checked", FEEDBACK_GIVEN);
                                break;

                            case NOT_BAD_FEEDBACK:
                                FEEDBACK_GIVEN = NOT_BAD_FEEDBACK;
                                Log.e("Checked", FEEDBACK_GIVEN);
                                break;
                        }
                    }
                }
            });
        }catch (Exception ex){
            Log.e("getSelectedFeedback", ex.getMessage());
        }
    }
    //CONFIGURING INTERACTION WITH APPLICATIONS IN THE LIST VIEW --------------------------------------------FINISH!!!

    //DATABASE TRANSACTION METHOD -------------------------------------------------------------------------------------START!!
    private void safeDatabaseTransaction(final String transactionName){
        try {
            Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    if (transactionName.equals(TURN_IN_APPLICATION)) {
                        selected_application.setStatus(TURN_IN_APPLICATION);
                        selected_application.setPlayer_feedback(TURNED_IN_STATUS);
                        selected_application.setNpc_feedback(TURNED_IN_STATUS);
                        job_applicationRepo.updateJobApplication(selected_application);
                        Log.e("TURN IN APPLICATION", "Application Turned in");
                    }else if(transactionName.equals(DELETE_APPLICATION)){
                        job_applicationRepo.deleteJobApplication(selected_application);
                        Log.e("DELETE APPLICATION", "Application Deleted");
                    }else if(transactionName.equals(GIVE_FEEDBACK)){
                        selected_application.setNpc_feedback(FEEDBACK_GIVEN);
                        if (!selected_application.getNpc_feedback().equals(TURNED_IN_STATUS) &&
                                !selected_application.getPlayer_feedback().equals(TURNED_IN_STATUS)){
                            selected_application.setStatus(COMPLETED_STATUS);
                        }
                        job_applicationRepo.updateJobApplication(selected_application);
                        job_applicationRepo.userGivesFeedback(FEEDBACK_GIVEN, selected_application.getJob_application_id());
                        Log.e("Give Feedback", "Feedback Given " + Integer.toString(selected_application.getJob_application_id())
                                + " " + selected_application.getPlayer_feedback());
                    }else if (transactionName.equals(ACCEPT_REQUEST)) {
                        selected_application.setStatus(ACCEPT_STATUS);
                        job_applicationRepo.updateJobApplication(selected_application);
                        Log.e("Application Accepted", Integer.toString(selected_application.getJob_application_id()) + ", Accepted Application: " + selected_application.getStatus());

                        //Rejecting other applications for the same job
                        List<Job_Applications> applications = new ArrayList<>(job_applicationRepo.getJobApplicationByJobId(selected_application.getJob_id()));
                        for (Job_Applications jobApplications: applications){
                            if (jobApplications.getStatus().equals(APPLY_STATUS)) {
                                jobApplications.setStatus(REJECT_STATUS);
                                job_applicationRepo.updateJobApplication(jobApplications);
                                Log.e("Rejecting applications",Integer.toString(jobApplications.getJob_application_id()) + ", " + jobApplications.getStatus());
                                Log.e("Application exists", "Exists: " + Integer.toString(jobApplications.getJob_application_id()));
                            } else{
                                Log.e("Application Not EXIST", "Not exist: " + Integer.toString(jobApplications.getJob_application_id()));
                            }
                        }
                    }else if(transactionName.equals(REJECT_REQUEST)){
                        selected_application.setStatus(REJECT_STATUS);
                        job_applicationRepo.updateJobApplication(selected_application);
                        Log.e("Rejected application", "Application Rejected");
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
    //DATABASE TRANSACTION METHOD -------------------------------------------------------------------------------------FINISH!!

    private void makeToast(View view, String message){
        Toast.makeText(view.getContext(), message , Toast.LENGTH_SHORT).show();
    }
}
