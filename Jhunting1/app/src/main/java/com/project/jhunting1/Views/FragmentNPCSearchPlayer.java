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
import com.project.jhunting1.Local.PlayerDataSource;
import com.project.jhunting1.Local.PlayerExpertiseDataSource;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;
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
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FragmentNPCSearchPlayer extends Fragment{

    private View view;
    JhuntingDB jhuntingDB;
    private static final int UNIQUE_FRAGMENT_GROUP_ID = 5;
    private static final String REQUEST_PLAYER = "REQUESTED";
    private static final String REQUEST_STATUS = "REQUESTED";
    private static final String ALL_JOBS = "ALL JOBS";
    private static final String APPLY_STATUS = "AWAITING APPROVAL";
    private static final String ACCEPT_STATUS = "ON PROGRESS";
    private static final String TURNED_IN_STATUS = "AWAITING FEEDBACK";
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final Boolean EXISTS = true;
    private static final Boolean NOT_EXIST = false;

    private Spinner spnJobTitle;
    private ListView lvPlayers;

    private static JobRepo jobRepo;
    private Job_ApplicationRepo job_applicationRepo;
    private PlayerRepo playerRepo;
    private PlayerExpertiseRepo playerExpertiseRepo;

    private Player selectedPlayer;
    private Jobs selectedJob;

    private List<Jobs> npcJobsForSpinner = new ArrayList<Jobs>();

    private List<Player> playerList = new ArrayList<Player>();
    private List<Player_Expertise> expertiseList = new ArrayList<Player_Expertise>();
    private ArrayAdapter<Player> playerArrayAdapter;
    private ArrayAdapter<Player_Expertise> expertiseAdapter;
    private ArrayAdapter<Jobs> jobsAdapterForSpinner;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private int npc_id;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_player, container, false);
        npc_id = getArguments().getInt("id");
        jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());

        setUpDatabaseRepository();
        bindView();
        setUpDatabaseAdapters();
        setUpSpinnersFromDatabase();
        enablePlayerListFiltering();
        setUpPlayerList();
        makeListScrollable();

        return view;
    }

    private void setUpDatabaseRepository(){
        try {
            jobRepo = jobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
            job_applicationRepo = job_applicationRepo.getInstance(Job_ApplicationDataSource.getInstance(jhuntingDB.job_applicationDAO()));
            playerRepo = playerRepo.getInstance(PlayerDataSource.getInstance(jhuntingDB.playerDAO()));
            playerExpertiseRepo = playerExpertiseRepo.getInstance(PlayerExpertiseDataSource.getInstance(jhuntingDB.player_expertiseDAO()));
        }catch (Exception ex){
            Log.e("setUpDatabaseRepo", ex.getMessage());
        }
    }

    private void setUpDatabaseAdapters(){
        try{
            playerArrayAdapter = new ArrayAdapter<Player>(view.getContext(), android.R.layout.simple_list_item_1, playerList);
            lvPlayers.setAdapter(playerArrayAdapter);
            expertiseAdapter = new ArrayAdapter<Player_Expertise>(view.getContext(), android.R.layout.simple_list_item_1, expertiseList);
        }catch (Exception ex){
            Log.e("setUpDatabaseAdapter", ex.getMessage());
        }
    }

    //LISTING PLAYERS THAT MATCH THE JOB CATEGORY ------------------------------------------------------------------------- START!!
    private void  setUpPlayerList(){
        try {
            if (selectedJob != null) {
                    Disposable disposablePlayers = playerRepo.getObservablePlayersForCategory(selectedJob.getJob_category1(), selectedJob.getJob_category2())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Consumer<List<Player>>() {
                                @Override
                                public void accept(List<Player> players) throws Exception {
                                    playerList.clear();
                                    playerList.addAll(players);
                                    removePlayersWithExistingAppplication(players);
                                    playerArrayAdapter.notifyDataSetChanged();
                                    int count = 0;
                                    List<Player> tempPlayers = new ArrayList<Player>(players);
                                    for (Player p : tempPlayers){
                                        if (tempPlayers.contains(p)){
                                            count ++;
                                            if (count > 1) {
                                                playerList.remove(p);
                                                playerArrayAdapter.notifyDataSetChanged();
                                                count = 0;
                                            }
                                        }
                                    }

                                }
                            });
                    compositeDisposable.add(disposablePlayers);
            }
        }catch (Exception ex){
            Log.e("setUpApplicationForNpc", ex.getMessage());
        }
    }

    private void removePlayersWithExistingAppplication(List<Player> players){
        try {
            for (final Player player : players) {
                Disposable disposable = job_applicationRepo.getObservableJobApplicationByPlayerForJob(player.getPlayer_id(), selectedJob.getJob_id())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Job_Applications>() {
                            @Override
                            public void accept(Job_Applications job_applications) throws Exception {
                                if (job_applications != null) {
                                    Log.e("Application Exist", job_applications.getStatus());
                                    if (!jobApplicationStatusAvailable(job_applications)) {
                                        Log.e("Application Status", Boolean.toString(jobApplicationStatusAvailable(job_applications)));
                                        playerList.remove(player);
                                        playerArrayAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                compositeDisposable.add(disposable);
            }
        }catch (Exception ex){
            Log.e("removePlayersExisting", ex.getMessage());
        }
    }

    private boolean jobApplicationStatusAvailable(Job_Applications applications){
        try {
            if (applications.getStatus().equals(APPLY_STATUS) || applications.getStatus().equals(REQUEST_STATUS)
                    || applications.getStatus().equals(ACCEPT_STATUS) || applications.getStatus().equals(TURNED_IN_STATUS)
                    || applications.getStatus().equals(COMPLETED_STATUS)) {
                return false;
            } else {
                return true;
            }
        }catch (Exception ex){
            Log.e("jobApplicationStatusAv", ex.getMessage());
        }
        return false;
    }

    //LISTING PLAYERS THAT MATCH THE JOB CATEGORY ------------------------------------------------------------------------- FINISH!!

    private void bindView(){
        try {
            spnJobTitle = (Spinner) view.findViewById(R.id.spnSearchPlayerJobTitle);
            lvPlayers = (ListView) view.findViewById(R.id.listSearchPlayers);
            registerForContextMenu(lvPlayers);
        }catch (Exception ex){
            Log.e("Bind View", ex.getMessage());
        }
    }

    //CONFIGURING INTERACTION WITH PLAYERS IN THE LIST VIEW --------------------------------------------START!!!
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        try {
            AdapterView.AdapterContextMenuInfo applicationInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Select Option:");

            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 8, Menu.NONE, "View Player Information");
            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 9, Menu.NONE, "Request Player");
        }catch (Exception ex){
            Log.e("onCreateContextMenu", ex.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo applicationInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if (item.getGroupId() == UNIQUE_FRAGMENT_GROUP_ID) {
                selectedPlayer = playerList.get(applicationInfo.position);
                switch (item.getItemId()) {
                    case 8: {//VIEW Applicant Information
                        showApplicantInformationDialog();
                        break;
                    }case 9: {//Request Player
                        showConfirmationDialog(view.getContext(), REQUEST_PLAYER);
                    }
                }

            }
        }catch (Exception ex){
            Log.e("onContextItemSelected", ex.getMessage());
        }
        return super.onContextItemSelected(item);
    }

    //RETRIEVING PLAYER INFORMATION ------------------------START!!
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
            Disposable disposable = job_applicationRepo.getObservablePlayer(selectedPlayer.getPlayer_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Player>() {
                        @Override
                        public void accept(Player player) throws Exception {
                            expertiseList.clear();
                            lvExpertise.setAdapter(expertiseAdapter);
                            name.setText(player.getName().toString());
                            email.setText(player.getEmail().toString());
                            telephone.setText(Integer.toString(player.getTelephone()));
                            setPlayersExpertise(selectedPlayer.getPlayer_id());
                            expertiseAdapter.notifyDataSetChanged();
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
                                    if (expertise.getJob_category() == null) {
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
        try {
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
        }catch (Exception ex){
            Log.e("setCategory", ex.getMessage());
        }
        return player_expertise;
    }
    //RETRIEVING PLAYERS INFORMATION ------------------------FINISHED!!

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

            if (transactionType.equals(REQUEST_PLAYER)) {
                tvTitle.setText(R.string.requestPlayerConfirmTxt);
            }

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    safeDatabaseTransaction(transactionType);
                    dialog.dismiss();
                    playerArrayAdapter.notifyDataSetChanged();
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
    //CONFIGURING INTERACTION WITH PLAYERS IN THE LIST VIEW --------------------------------------------FINISH!!!

    //DATABASE TRANSACTION METHOD -------------------------------------------------------------------------------------START!!
    private void safeDatabaseTransaction(final String transactionName){
        try {
            Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    if (transactionName.equals(REQUEST_PLAYER)){
                        Job_Applications jobApplication = new Job_Applications(selectedJob.getJob_id(), selectedPlayer.getPlayer_id(), REQUEST_STATUS);
                        if (!jobApplication.getStatus().isEmpty()){
                            List<Job_Applications> applicationsList = job_applicationRepo.getJobApplicationByJobId(selectedJob.getJob_id());
                            if (!applicationExists(applicationsList, jobApplication)) {
                                job_applicationRepo.insertJobApplication(jobApplication);
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
            Log.e("safeDataTransaction", ex.getMessage());
        }
    }

    private boolean applicationExists(List<Job_Applications> applications, Job_Applications job_application){
        try {
            for (Job_Applications application : applications) {
                if (selectedJob.getJob_id() == job_application.getJob_id() && application.getPlayer_id() == job_application.getPlayer_id()
                        && application.getStatus().equals(APPLY_STATUS)) {
                    return EXISTS;
                }
            }
        }catch (Exception ex){
            Log.e("applicationExists", ex.getMessage());
            return NOT_EXIST;
        }
        return NOT_EXIST;
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
                            npcJobsForSpinner.clear();
                            npcJobsForSpinner.addAll(jobs);
                            jobsAdapterForSpinner.notifyDataSetChanged();
                            for (final Jobs job: jobs){
                                Disposable jobDisposable = job_applicationRepo.getObservableJobApplicationByJobId(job.getJob_id())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Consumer<List<Job_Applications>>() {
                                            @Override
                                            public void accept(List<Job_Applications> applications) throws Exception {
                                                for (Job_Applications jobApplications : applications){
                                                    if (job.getJob_id() == jobApplications.getJob_id()
                                                            && jobApplications.getStatus().equals(ACCEPT_STATUS) ||
                                                            jobApplications.getStatus().equals(TURNED_IN_STATUS) ||
                                                            jobApplications.getStatus().equals(COMPLETED_STATUS)){
                                                        npcJobsForSpinner.remove(job);
                                                        jobsAdapterForSpinner.notifyDataSetChanged();
                                                    }
                                                    Log.e("Spinner Dataset Loop", job.getJob_title() + " , " + jobApplications.getStatus());
                                                }
                                            }
                                        });
                                compositeDisposable.add(jobDisposable);

                            }

                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("setUpSpinnerWithJob", ex.getMessage());
        }
    }
    //SETTING SPINNER FROM DATABASE ------------------------------------------------------------------------------FINISH!!

    //ENABLING LIST VIEW FILTER FROM SPINNER ------------------------------------------------------------------------START!!
    private void enablePlayerListFiltering(){
        try {
            enableFilteringByJobTitle();

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
                        playerList.clear();
                        setUpPlayerList();
                        playerArrayAdapter.notifyDataSetChanged();
                    } else {
                        setUpPlayerList();
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

    //ENABLING LIST VIEW FILTER FROM SPINNER ------------------------------------------------------------------------START!!

    private void makeListScrollable(){
        try {
            lvPlayers.setOnTouchListener(new ListView.OnTouchListener() {
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
        }catch (Exception ex){
            Log.e("MakeListScrollable", ex.getMessage());
        }
    }

    private void makeToast(Context context, String message){
        Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
    }

}
