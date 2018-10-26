package com.project.jhunting1.Views;

import android.app.Application;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.project.jhunting1.Local.JhuntingDB;
import com.project.jhunting1.Local.JobDataSource;
import com.project.jhunting1.Local.Job_ApplicationDataSource;
import com.project.jhunting1.Local.PlayerDataSource;
import com.project.jhunting1.Local.PlayerExpertiseDataSource;
import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;
import com.project.jhunting1.R;
import com.project.jhunting1.Repository.JobRepo;
import com.project.jhunting1.Repository.Job_ApplicationRepo;
import com.project.jhunting1.Repository.PlayerExpertiseRepo;
import com.project.jhunting1.Repository.PlayerRepo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FragmentNPCJobApplication extends Fragment{

    private View view;
    JhuntingDB jhuntingDB;
    private static final int UNIQUE_FRAGMENT_GROUP_ID = 4;
    private static final String APPLY_STATUS = "AWAITING APPROVAL";
    private static final String ACCEPT_STATUS = "ON PROGRESS";
    private static final String REJECT_STATUS = "REJECTED";
    private static final String TURNED_IN_STATUS = "AWAITING FEEDBACK";
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String ACCEPT_APPLICATION = "ON PROGRESS";
    private static final String REJECT_APPLICATION = "REJECTED";
    private static final String ALL_JOBS = "ALL JOBS";
    private static final String GIVE_FEEDBACK = "GIVE FEEDBACK";
    private static String FEEDBACK_GIVEN;
    private static final String GOOD_FEEDBACK = "Good";
    private static final String SATISFACTORY_FEEDBACK = "Satisfactory";
    private static final String NOT_BAD_FEEDBACK = "Not Bad";

    private Spinner spnJobTitle;
    private Spinner spnApplicationType;
    private ListView lvApplications;

    private static JobRepo jobRepo;
    private Job_ApplicationRepo job_applicationRepo;
    private PlayerRepo playerRepo;
    private PlayerExpertiseRepo playerExpertiseRepo;

    private Jobs selectedJob;
    private Job_Applications selected_application;

    private List<Jobs> npcJobsForSpinner = new ArrayList<Jobs>();

    private List<Job_Applications> applicationsList = new ArrayList<Job_Applications>();
    private List<Player_Expertise> expertiseList = new ArrayList<Player_Expertise>();
    private ArrayAdapter<Job_Applications> applicationsAdapter;
    private ArrayAdapter<Player_Expertise> expertiseAdapter;
    private ArrayAdapter<Jobs> jobsAdapterForSpinner;

    private String selectedStatus = APPLY_STATUS;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private int npc_id;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.npc_job_applications, container, false);
        npc_id = getArguments().getInt("id");
        jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());

        setUpDatabaseRepository();
        bindView();
        setUpDatabaseAdapters();
        setUpSpinnersFromDatabase();
        enableApplicationListFiltering();
        setUpApplicationForNpcJob();
        makeListScrollable();

        return view;
    }

    private void setUpDatabaseRepository(){
        try {
            jobRepo = jobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
            job_applicationRepo = job_applicationRepo.getInstance(Job_ApplicationDataSource.getInstance(jhuntingDB.job_applicationDAO()));
            playerRepo = PlayerRepo.getInstance(PlayerDataSource.getInstance(jhuntingDB.playerDAO()));
            playerExpertiseRepo = playerExpertiseRepo.getInstance(PlayerExpertiseDataSource.getInstance(jhuntingDB.player_expertiseDAO()));
        }catch (Exception ex){
            Log.e("setUpDatabaseRepo", ex.getMessage());
        }
    }

    private void setUpDatabaseAdapters(){
        try{
            applicationsAdapter = new ArrayAdapter<Job_Applications>(view.getContext(), android.R.layout.simple_list_item_1, applicationsList);
            lvApplications.setAdapter(applicationsAdapter);
            expertiseAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, expertiseList);
        }catch (Exception ex){
            Log.e("setUpDatabaseAdapter", ex.getMessage());
        }
    }

    //LISTING NPC APPLICATIONS THAT ARE 'AWAITING APPROVAL' OR 'IN PROGRESS'---------------------- START!!
    private void  setUpApplicationForNpcJob(){
        try {
            if (selectedJob != null) {
                if (selectedJob.getJob_title().equals("ALL JOBS")) {
                    Disposable disposableApplication = job_applicationRepo.getObservableJobApplicationByNpc(npc_id)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Consumer<List<Job_Applications>>() {
                                @Override
                                public void accept(List<Job_Applications> applications) throws Exception {
                                    applicationsList.clear();
                                    filterOutUnavailableApplication(applications, selectedStatus);
                                }
                            });
                    compositeDisposable.add(disposableApplication);
                } else {
                    Disposable disposableApplication = job_applicationRepo.getObservableJobApplicationByJobId(selectedJob.getJob_id())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Consumer<List<Job_Applications>>() {
                                @Override
                                public void accept(List<Job_Applications> applications) throws Exception {
                                    applicationsList.clear();
                                    filterOutUnavailableApplication(applications, selectedStatus);
                                }
                            });
                    compositeDisposable.add(disposableApplication);
                }
            }
        }catch (Exception ex){
            Log.e("setUpApplicationForNpc", ex.getMessage());
        }
    }

    private void filterOutUnavailableApplication(List<Job_Applications> applications, String applicationType){
        try {
            for (Job_Applications ja : applications) {
                if (ja.getStatus().equals(applicationType)) {
                    if (!applicationsList.contains(ja)) {
                        Job_Applications jobApplications = ja;
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
                        applicationsAdapter.notifyDataSetChanged();
                    }
                }
                if (applicationType.equals(TURNED_IN_STATUS) && FEEDBACK_GIVEN(ja)){
                    if (applicationsList.contains(ja)) {
                        applicationsList.remove(ja);
                        applicationsAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (applicationsList.isEmpty()) {
                applicationsList.clear();
                applicationsAdapter.notifyDataSetChanged();
            }
        }catch (Exception ex){
            Log.e("filterWrongApplication", ex.getMessage());
        }
    }

    private boolean FEEDBACK_GIVEN(Job_Applications job_applications){
        if (job_applications.getPlayer_feedback().equals(GOOD_FEEDBACK) || job_applications.getPlayer_feedback().equals(SATISFACTORY_FEEDBACK)
                || job_applications.getPlayer_feedback().equals(NOT_BAD_FEEDBACK) || job_applications.getStatus().equals(COMPLETED_STATUS)){
            return true;
        }else{
            return false;
        }
    }

    private Job_Applications setPlayer(final Job_Applications job_applications){
        try {
            Disposable disposable = job_applicationRepo.getObservablePlayer(job_applications.getPlayer_id())
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

            if (job_applications.getPlayer() != null) {
                Log.e("Returning Players", job_applications.getPlayer().getName());
            }
        }catch (Exception ex){
            Log.e("Set Player", ex.getMessage());
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
            Log.e("Set Job", ex.getMessage());
        }
        return job_applications;
    }

    //LISTED NPC APPLICATIONS THAT ARE 'AWAITING APPROVAL' OR 'IN PROGRESS'---------------------- FINISH!!

    private void bindView(){
        try {
            spnJobTitle = (Spinner) view.findViewById(R.id.spnNpcApplicationTitle);
            spnApplicationType = (Spinner) view.findViewById(R.id.spnNpcApplicationType);
            lvApplications = (ListView) view.findViewById(R.id.listNpcJobApplications);
            registerForContextMenu(lvApplications);
        }catch (Exception ex){
            Log.e("Bind View", ex.getMessage());
        }
    }

    //CONFIGURING INTERACTION WITH APPLICATIONS IN THE LIST VIEW --------------------------------------------START!!!
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        try {
            AdapterView.AdapterContextMenuInfo applicationInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Select Option:");

            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 3, Menu.NONE, "View Applicant Information");
            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 4, Menu.NONE, "View Job Details");
            if (selectedStatus.equals(APPLY_STATUS)) {
                menu.add(UNIQUE_FRAGMENT_GROUP_ID, 5, Menu.NONE, "Accept Application");
                menu.add(UNIQUE_FRAGMENT_GROUP_ID, 6, Menu.NONE, "Reject Application");
            } else if (selectedStatus.equals(TURNED_IN_STATUS)) {
                menu.add(UNIQUE_FRAGMENT_GROUP_ID, 7, Menu.NONE, "Give Feedback");
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
                    case 3: {//VIEW Applicant Information
                        showApplicantInformationDialog();
                        break;
                    }

                    case 4: {//JOB DESCRIPTION
                        showJobDescriptionDialog();
                        break;
                    }

                    case 5: {//ACCEPT APPLICATION
                        showConfirmationDialog(view.getContext(),ACCEPT_APPLICATION);
                        break;
                    }
                    case 6: {//REJECT APPLICATION
                        showConfirmationDialog(view.getContext(), REJECT_APPLICATION);
                        break;
                    }
                    case 7: {//GIVE FEEDBACK
                        showFeedbackDialogue();
                    }
                }

            }
        }catch (Exception ex){
            Log.e("onContextItemSelected", ex.getMessage());
        }
        return super.onContextItemSelected(item);
    }

    //RETRIEVING APPLICANT INFORMATION ------------------------START!!
    private void showApplicantInformationDialog(){
        try {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.player_info);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            TextView name = (TextView) dialog.findViewById(R.id.tvPlayerInfoName);
            TextView email = (TextView) dialog.findViewById(R.id.tvPlayerInfoEmail);
            TextView telephone = (TextView) dialog.findViewById(R.id.tvPlayerInfoTelephone);
            ListView expertiseList = (ListView) dialog.findViewById(R.id.listPlayerInfoExpertise);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnPlayerInfoCancel);

            assignValuesToApplicantInformation(name, email, telephone, expertiseList);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        } catch (Exception ex) {
            Log.e("showApplicantInfo", ex.getMessage());
        }
    }

    private void assignValuesToApplicantInformation(final TextView name, final TextView email, final TextView telephone, final ListView lvExpertise){
        try {
            Disposable disposable = job_applicationRepo.getObservablePlayer(selected_application.getPlayer_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Player>() {
                        @Override
                        public void accept(Player player) throws Exception {
                            name.setText(player.getName().toString());
                            email.setText(player.getEmail().toString());
                            telephone.setText(Integer.toString(player.getTelephone()));
                            setPlayersExpertise(selected_application.getPlayer_id());
                            expertiseAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, expertiseList);
                            lvExpertise.setAdapter(expertiseAdapter);
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("assignValuesToApplicant", ex.getMessage());
        }
    }

    private void setPlayersExpertise(int player_Id){
        try {
            Disposable disposable = playerRepo.getObservablePlayerExpertises(player_Id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Player_Expertise>>() {
                        @Override
                        public void accept(List<Player_Expertise> player_expertises) throws Exception {
                            for (Player_Expertise expertise : player_expertises){
                                if (expertise.getJob_category()== null){
                                    expertise = setCategory(expertise);
                                    expertiseList.add(expertise);
                                    expertiseAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("getPlayersExpertise", ex.getMessage());
        }

    }

    private Player_Expertise setCategory(final Player_Expertise player_expertise){
        Disposable disposable1 = playerExpertiseRepo.getObservableJobCategory(player_expertise.getCategory_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Job_Category>() {
                    @Override
                    public void accept(Job_Category job_category) throws Exception {
                        player_expertise.setJob_category(job_category);
                        expertiseAdapter.notifyDataSetChanged();
                    }
                });
        compositeDisposable.add(disposable1);
        return player_expertise;
    }
    //RETRIEVING APPLICANT INFORMATION ------------------------FINISHED!!

    private void showJobDescriptionDialog(){
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
            Log.e("showJobDescription: ", ex.getMessage());
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

            final RadioGroup rgFeedback = dialog.findViewById(R.id.rgFeedback);
            Button btnGiveFeedback = (Button) dialog.findViewById(R.id.btnGiveFeedback);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancelFeedback);

            getSelectedFeedback(rgFeedback);
            btnGiveFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FEEDBACK_GIVEN != null) {
                        if (!FEEDBACK_GIVEN.isEmpty()) {
                            showConfirmationDialog(dialog.getContext(), GIVE_FEEDBACK);
                            dialog.dismiss();
                        } else {
                            Log.e("Feedback Error", "Given Feedback is Empty");
                        }
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                                break;

                            case SATISFACTORY_FEEDBACK:
                                FEEDBACK_GIVEN = SATISFACTORY_FEEDBACK;
                                break;

                            case NOT_BAD_FEEDBACK:
                                FEEDBACK_GIVEN = NOT_BAD_FEEDBACK;
                                break;
                        }
                    }
                }
            });
        }catch (Exception ex){
            Log.e("getSelectedFeedback", ex.getMessage());
        }
    }

    private void showConfirmationDialog(Context context, final String transactionType){
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

            if (transactionType.equals(ACCEPT_APPLICATION)) {
                tvTitle.setText(R.string.acceptApplicationConfirmTxt);
            }else if (transactionType.equals(REJECT_APPLICATION)){
                tvTitle.setText(R.string.rejectApplicationConfirmTxt);
            }else if (transactionType.equals(GIVE_FEEDBACK)) {
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

        } catch (Exception ex) {
            Log.e("showConfirmDialog ", ex.getMessage() + " for " + transactionType);
        }
    }
    //CONFIGURING INTERACTION WITH APPLICATIONS IN THE LIST VIEW --------------------------------------------FINISH!!!

    //DATABASE TRANSACTION METHOD -------------------------------------------------------------------------------------START!!
    private void safeDatabaseTransaction(final String transactionName){
        try {
            Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    if (transactionName.equals(ACCEPT_APPLICATION)) {
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
                    }
                    else if(transactionName.equals(REJECT_APPLICATION)){
                        selected_application.setStatus(REJECT_STATUS);
                        job_applicationRepo.updateJobApplication(selected_application);
                        Log.e("Rejected application", "Application Rejected");
                    }
                    else if(transactionName.equals(GIVE_FEEDBACK)){
                        selected_application.setPlayer_feedback(FEEDBACK_GIVEN);
                        if (!selected_application.getNpc_feedback().equals(TURNED_IN_STATUS) &&
                                !selected_application.getPlayer_feedback().equals(TURNED_IN_STATUS)){
                            selected_application.setStatus(COMPLETED_STATUS);
                        }
                        job_applicationRepo.updateJobApplication(selected_application);
                        job_applicationRepo.npcGiveFeedback(FEEDBACK_GIVEN, selected_application.getJob_application_id());
                        Log.e("Give Feedback", "Feedback Given " + Integer.toString(selected_application.getJob_application_id())
                        + " " + selected_application.getNpc_feedback());
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
            Log.e("safeDataTransaction", ex.getMessage());
        }
    }

    //DATABASE TRANSACTION METHOD -------------------------------------------------------------------------------FINISH!!

    //SETTING SPINNER FROM DATABASE ------------------------------------------------------------------------------START!!
    private void setUpSpinnersFromDatabase(){
        try {
            jobsAdapterForSpinner = new ArrayAdapter<Jobs>(view.getContext(), android.R.layout.simple_list_item_1, npcJobsForSpinner);
            spnJobTitle.setAdapter(jobsAdapterForSpinner);
            setUpSpinnerWithJobTitle();
        }catch (Exception ex){
            Log.e("setUpSpinnerFromDb", ex.getMessage());
        }
    }

    private void setUpSpinnerWithJobTitle(){
        try {
            Disposable disposable = jobRepo.getObservableAllJobsByNpcId(npc_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Jobs>>() {
                        @Override
                        public void accept(List<Jobs> jobs) throws Exception {
                            Jobs job = new Jobs(ALL_JOBS);
                            npcJobsForSpinner.add(job);
                            npcJobsForSpinner.addAll(jobs);
                            jobsAdapterForSpinner.notifyDataSetChanged();
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("setUpSpinnerWithJob", ex.getMessage());
        }
    }
    //SETTING SPINNER FROM DATABASE ------------------------------------------------------------------------------FINISH!!

    //ENABLING LIST VIEW FILTER FROM SPINNER ------------------------------------------------------------------------START!!
    private void enableApplicationListFiltering(){
        try {
            enableFilteringByJobTitle();
            enableFilteringByApplicationStatus();

        }catch (Exception ex){
            Log.e("enableListFiltering", ex.getMessage());
        }
    }

    private void enableFilteringByJobTitle(){
        try {
            spnJobTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedJob = (Jobs) adapterView.getItemAtPosition(i);
                    Log.e("Selected Job", selectedJob.getJob_title());
                    if (!selectedJob.getJob_title().equals(ALL_JOBS)) {
                        applicationsList.clear();
                        setUpApplicationForNpcJob();
                        applicationsAdapter.notifyDataSetChanged();
                    } else {
                        setUpApplicationForNpcJob();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }catch (Exception ex){
            Log.e("filterByJobTitle", ex.getMessage());
        }
    }

    private void enableFilteringByApplicationStatus(){
        try {
            spnApplicationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedStatus = (String) adapterView.getItemAtPosition(i);
                    Log.e("Selected Status", selectedStatus);
                    if (selectedJob != null) {
                        if (!selectedJob.getJob_title().equals(ALL_JOBS)) {
                            applicationsList.clear();
                            setUpApplicationForNpcJob();
                            applicationsAdapter.notifyDataSetChanged();
                        } else {
                            setUpApplicationForNpcJob();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }catch (Exception ex){
            Log.e("filterByStatus", ex.getMessage());
        }
    }
    //ENABLING LIST VIEW FILTER FROM SPINNER ------------------------------------------------------------------------START!!

    private void makeListScrollable(){
        lvApplications.setOnTouchListener(new ListView.OnTouchListener() {
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

    private void deleteAllApplications() {
        try {
            Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    List<Job_Applications> allApplications = job_applicationRepo.getAllJobApplications();
                    job_applicationRepo.deleteAllJobApplications(allApplications);
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
            Log.e("deleteAllApplication", ex.getMessage());
        }
    }
}
