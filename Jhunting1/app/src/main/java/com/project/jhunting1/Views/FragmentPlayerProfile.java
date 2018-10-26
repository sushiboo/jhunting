package com.project.jhunting1.Views;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.project.jhunting1.Local.JhuntingDB;
import com.project.jhunting1.Local.JobDataSource;
import com.project.jhunting1.Local.PlayerDataSource;
import com.project.jhunting1.Local.PlayerExpertiseDataSource;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;
import com.project.jhunting1.R;
import com.project.jhunting1.Repository.JobRepo;
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

public class FragmentPlayerProfile extends Fragment{

    private View view;
    private int playerId;
    private JhuntingDB jhuntingDB;
    private static final int UNIQUE_FRAGMENT_GROUP_ID = 6;

    private static final String DELETE_EXPERTISE = "DELETE EXPERTISE";
    private static final String UPDATE_PLAYER  = "UPDATE_PLAYER";
    private static final String ADD_EXPERTISE = "ADD_EXPERTISE";
    private static final String UPDATE_SUCCESSFUL = "UPDATE_SUCCESSFUL";
    private static final String UPDATE_FAILED = "UPDATE_FAILED";
    private static final String UPDATING_WITH_PASSWORD = "UPDATING WITH PASSWORD";
    private static final String READY_FOR_UPDATE = "READY FOR UPDATE";
    private static final String EMPTY_FIELDS = "EMPTY_FIELD";

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvTelephone;

    private EditText txtEditName;
    private EditText txtEditEmail;
    private EditText txtEditTelephone;
    private EditText txtOldPassword;
    private EditText txtNewPassword;
    private Button btnUpdate;
    private Button btnCancel;
    private Button btnAddExpertise;

    private int selectedCategoryId;

    private String name = "";
    private int telephone = 0;
    private String email = "";
    private String password = "";

    private ListView lvExpertises;

    private Button btnEdit;

    private PlayerRepo playerRepo;
    private PlayerExpertiseRepo playerExpertiseRepo;
    private JobRepo jobRepo;
    private Player playerToEdit = new Player();
    private List<Player_Expertise> player_expertiseToEdit = new ArrayList<Player_Expertise>();

    private List<Player_Expertise> player_expertises = new ArrayList<Player_Expertise>();
    private ArrayAdapter<Player_Expertise> expertiseAdapter;

    private Player_Expertise selectedExpertise;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.player_profile, container, false);
        jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
        playerId = getArguments().getInt("id");
        instantiateRepositories();
        displayPlayerInformation();
        bindProfileViewComponents();
        instantiateAdapters();
        makeExpertiseListScrollable(lvExpertises);
        enableEditingPlayerInfo();
        enableAddingExpertise();
        return view;
    }

    private void instantiateRepositories(){
        playerRepo = PlayerRepo.getInstance(PlayerDataSource.getInstance(jhuntingDB.playerDAO()));
        playerExpertiseRepo = PlayerExpertiseRepo.getInstance(PlayerExpertiseDataSource.getInstance(jhuntingDB.player_expertiseDAO()));
        jobRepo = jobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
    }

    private void instantiateAdapters(){
        expertiseAdapter = new ArrayAdapter<Player_Expertise>(view.getContext(), android.R.layout.simple_list_item_1, player_expertises);
        lvExpertises.setAdapter(expertiseAdapter);
    }

    public void bindProfileViewComponents(){
        try {
            tvName = (TextView) view.findViewById(R.id.tvPlayerProfileName);
            tvEmail = (TextView) view.findViewById(R.id.tvPlayerProfileEmail);
            tvTelephone = (TextView) view.findViewById(R.id.tvPlayerProfileTelephone);
            lvExpertises = (ListView) view.findViewById(R.id.listPlayerProfileExpertise);
            btnEdit = (Button) view.findViewById(R.id.btnEditPlayer);
            btnAddExpertise = (Button) view.findViewById(R.id.btnPlayerProfileAddExpertise);

            registerForContextMenu(lvExpertises);
        }catch (Exception ex){
            Log.e("bindComponent", ex.getMessage());
        }
    }

    private void displayPlayerInformation(){
        try {
            Disposable disposable = playerRepo.getObservablePlayerById(playerId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Player>() {
                        @Override
                        public void accept(Player player) throws Exception {
                            playerToEdit = player;
                            tvName.setText(player.getName().toString());
                            tvEmail.setText(player.getEmail().toString());
                            tvTelephone.setText(Integer.toString(player.getTelephone()));
                            setPlayersExpertise();
                            expertiseAdapter.notifyDataSetChanged();
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("displayPlayerInfo", ex.getMessage());
        }
    }

    //EDITING PLAYER NORMAL INFORMATION ------------------------------------------------------------------------------------START!!
    public void enableEditingPlayerInfo(){
        try {
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditPlayerDialog();
                }
            });
        }catch (Exception ex){
            Log.e("enableEditingPlayer", ex.getMessage());
        }
    }

    private void showEditPlayerDialog() {
        try {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.form_dialogue_edit_user);
            dialog.show();

            txtEditName = (EditText) dialog.findViewById(R.id.txtEditUserName);
            txtEditEmail = (EditText) dialog.findViewById(R.id.txtEditUserEmail);
            txtEditTelephone = (EditText) dialog.findViewById(R.id.txtEditUserTelephone);
            txtOldPassword = (EditText) dialog.findViewById(R.id.txtEditUserOldPassword);
            txtNewPassword = (EditText) dialog.findViewById(R.id.txtEditUserNewPassword);
            btnUpdate = (Button) dialog.findViewById(R.id.btnUserUpdate);
            btnCancel = (Button) dialog.findViewById(R.id.btnUserCancel);

            showPlayerInfoInDialog();

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (updatePlayer(dialog.getContext()).equals(UPDATE_SUCCESSFUL)) {
                        dialog.dismiss();
                    }else{
                        makeToast(view.getContext(),"Update Failed");
                        dialog.dismiss();
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
            Log.e("Dialog Creation Error", ex.getMessage());
        }
    }

    private void showPlayerInfoInDialog(){
        try {
            Disposable disposable = playerRepo.getObservablePlayerById(playerId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Player>() {
                        @Override
                        public void accept(Player player) throws Exception {
                            txtEditName.setText(player.getName().toString());
                            txtEditEmail.setText(player.getEmail().toString());
                            txtEditTelephone.setText(Integer.toString(player.getTelephone()));
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("showPlayerInfoInDialog", ex.getMessage());
        }
    }

    private String updatePlayer(Context context){
        try{
            if (validatePlayerEditForm().equals(READY_FOR_UPDATE) || validatePlayerEditForm().equals(UPDATING_WITH_PASSWORD)) {
                playerToEdit.setName(txtEditName.getText().toString());
                playerToEdit.setEmail(txtEditEmail.getText().toString());
                playerToEdit.setTelephone(Integer.parseInt(txtEditTelephone.getText().toString()));
                if(validatePlayerEditForm().equals(UPDATING_WITH_PASSWORD)) {
                    playerToEdit.setPassword(txtNewPassword.getText().toString());
                }
                showConfirmDialog(context, UPDATE_PLAYER);
            }
            return UPDATE_SUCCESSFUL;
        }catch (Exception ex){
            Log.e("updatePlayer", ex.getMessage());
            return UPDATE_FAILED;
        }
    }

    private String validatePlayerEditForm(){
        try {
            if (txtEditName.getText().length() == 0 || txtEditName.getText().equals("")) {
                txtEditName.requestFocus();
                makeToast(view.getContext(), "Name field cannot be empty");
                return EMPTY_FIELDS;
            } else if (txtEditTelephone.getText().length() == 0 || txtEditTelephone.getText().equals("")) {
                txtEditTelephone.requestFocus();
                makeToast(view.getContext(), "Telephone field cannot be empty");
                return EMPTY_FIELDS;
            } else if (txtEditEmail.getText().length() == 0 || txtEditEmail.getText().equals("")) {
                txtEditEmail.requestFocus();
                makeToast(view.getContext(), "Email field cannot be empty");
                return EMPTY_FIELDS;
            } else if (txtOldPassword.getText().length() != 0 && txtNewPassword.getText().length() != 0) {
                if (txtOldPassword.getText().toString().equals(playerToEdit.getPassword().toString())) {
                    return UPDATING_WITH_PASSWORD;
                } else {
                    txtOldPassword.requestFocus();
                    makeToast(view.getContext(), "Old password does not match");
                    return EMPTY_FIELDS;
                }
            } else {
                name = txtEditName.getText().toString();
                telephone = Integer.parseInt(txtEditTelephone.getText().toString());
                email = txtEditEmail.getText().toString();
                return READY_FOR_UPDATE;
            }
        }catch (Exception ex){
            Log.e("validatePlayerEditForm", ex.getMessage());
            return EMPTY_FIELDS;
        }

    }
    //EDITING PLAYER NORMAL INFORMATION ------------------------------------------------------------------------------------FINISH!!

    //ENABLE EDITING EXPERTISE LIST -----------------------------------------------------------------------------------------START!!
    private void enableAddingExpertise(){
        try {
            btnAddExpertise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddExpertiseDialog();
                }
            });
        }catch (Exception ex){
            Log.e("enableAddingExpertise", ex.getMessage());
        }
    }

    private void showAddExpertiseDialog(){
        try {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.new_expertise_dialog);
            dialog.show();

            final ListView lvDialogExpertise = (ListView)dialog.findViewById(R.id.lstProfileAddExpert);
            final Spinner spnExpertise = (Spinner) dialog.findViewById(R.id.spnPlayerProfileAddExpertise);
            Button btnDialogAddExpertise = (Button)dialog.findViewById(R.id.btnPlayerProfileDialogAddExpertise);
            Button btnDialogCancel = (Button)dialog.findViewById(R.id.btnPlayerProfileDialogCancelExpertise);
            Button btnFinishedAdding = (Button)dialog.findViewById(R.id.btnPlayerProfileDialogFinished);

            lvDialogExpertise.setAdapter(expertiseAdapter);

            makeExpertiseListScrollable(lvDialogExpertise);

            List<Job_Category> jobCategoryForSpinner = new ArrayList<Job_Category>();
            ArrayAdapter<Job_Category> categoryAdapterForSpinner = new ArrayAdapter<Job_Category>(view.getContext(),
                    android.R.layout.simple_list_item_1, jobCategoryForSpinner);
            spnExpertise.setAdapter(categoryAdapterForSpinner);

            setPlayersExpertise();

            fixSpinnerWithRemainingExpertise(jobCategoryForSpinner, categoryAdapterForSpinner);

            spnExpertise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedCategoryId = ((Job_Category)adapterView.getItemAtPosition(i)).getJc_id();
                    Log.e("Selected Category", Integer.toString(selectedCategoryId));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            btnDialogAddExpertise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSelectedCategory(spnExpertise);
                    showConfirmDialog(dialog.getContext(), ADD_EXPERTISE);
                    expertiseAdapter.notifyDataSetChanged();
                }
            });

            btnFinishedAdding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    expertiseAdapter.notifyDataSetChanged();
                }
            });

            btnDialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        }
        catch (Exception ex){
            Log.e("showAddExpertiseDialog", ex.getMessage());
        }
    }

    private void setPlayersExpertise(){
        try {
            Disposable disposable = playerRepo.getObservablePlayerExpertises(playerId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Player_Expertise>>() {
                        @Override
                        public void accept(List<Player_Expertise> pe) throws Exception {
                            player_expertises.clear();
                            for (Player_Expertise expertise: pe){
                                if (expertise.getJob_category() == null){
                                    expertise = setCategory(expertise);
                                    player_expertises.add(expertise);
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

    private void fixSpinnerWithRemainingExpertise(final List<Job_Category> categorySpinner,
                                                  final ArrayAdapter<Job_Category> spinnerAdapter){
        try {
            Disposable disposable = jobRepo.getObservableAllJobCategory()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Job_Category>>() {
                        @Override
                        public void accept(final List<Job_Category> all_job_categories) throws Exception {
                            categorySpinner.addAll(all_job_categories);
                            spinnerAdapter.notifyDataSetChanged();
                            Disposable disposable1 = playerRepo.getObservableCategoriesForPlayer(playerId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<Job_Category>>() {
                                        @Override
                                        public void accept(List<Job_Category> player_job_categories) throws Exception {
                                            for (Job_Category allCategory : all_job_categories) {
                                                for (Job_Category playerCategory : player_job_categories) {
                                                    String category1 = allCategory.getCategory();
                                                    String category2 = playerCategory.getCategory();
                                                    if (category1.equals(category2)) {
                                                        categorySpinner.remove(allCategory);
                                                        spinnerAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                            compositeDisposable.add(disposable1);
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("fixSpinnerRemainExpert", ex.getMessage());
        }
    }


    private void getSelectedCategory(Spinner spinner){
        try {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedCategoryId = ((Job_Category) adapterView.getItemAtPosition(i)).getJc_id();
                    Log.e("Selected Category", Integer.toString(selectedCategoryId));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }catch (Exception ex){
            Log.e("getSelectedCategory", ex.getMessage());
        }
    }
    //ENABLE EDITING EXPERTISE LIST ---------------------------------------------------------------------------------------FINISH!!

    //DELETE EXPERTISE FROM LIST ------------------------------------------------------------------------------------------START!!
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo jobinfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
        try {
            menu.setHeaderTitle("Select Option:");
            menu.add(UNIQUE_FRAGMENT_GROUP_ID, 7, Menu.NONE, "Delete Expertise");
        }catch (Exception ex){
            Log.e("onCreateContextMenu", ex.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo jobinfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if (item.getGroupId() == UNIQUE_FRAGMENT_GROUP_ID) {
                selectedExpertise = player_expertises.get(jobinfo.position);
                switch (item.getItemId()) {
                    case 7: {//DELETE EXPERTISE
                        showConfirmDialog(view.getContext(), DELETE_EXPERTISE);
                    }

                }
            }
        }catch (Exception ex){
            Log.e("onContextItemSelected", ex.getMessage());
        }
        return super.onContextItemSelected(item);
    }
    //DELETE EXPERTISE FROM LIST -------------------------------------------------------------------------------FINISH!!

    private void showConfirmDialog(final Context context, final String transactionName){
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

            if (transactionName.equals(ADD_EXPERTISE)) {
                tvTitle.setText(R.string.addExpertiseConfirmTxt);
            } else if (transactionName.equals(DELETE_EXPERTISE)) {
                tvTitle.setText(R.string.deleteExpertiseConfirmTxt);
            } else if (transactionName.equals(UPDATE_PLAYER)) {
                tvTitle.setText(R.string.updatePlayerConfirmTxt);
            }

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    safeDatabaseTransaction(transactionName);
                    dialog.dismiss();
                    expertiseAdapter.notifyDataSetChanged();
                    showRelevantCompletionMessage(context, transactionName);
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

    private void showRelevantCompletionMessage(Context context, String transactionName){
        try {
            switch (transactionName) {
                case UPDATE_PLAYER:
                    makeToast(context, "Player Updated");
                    break;

                case DELETE_EXPERTISE:
                    makeToast(context, "Expertise Deleted");
                    break;

                case ADD_EXPERTISE:
                    makeToast(context, "Expertise Added");
            }
        }catch (Exception ex){
            Log.e("showRelevantMessage", ex.getMessage());
        }
    }

    private void safeDatabaseTransaction(final String transactionName){
        try {
            Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {
                    if (transactionName.equals(UPDATE_PLAYER)) {
                        if (playerToEdit != null) {
                            playerRepo.updatePlayer(playerToEdit);
                        }
                    } else if (transactionName.equals(ADD_EXPERTISE)) {
                        if (selectedCategoryId != 0) {
                            Player_Expertise newExpertise = new Player_Expertise(playerId, selectedCategoryId, 0, 0);
                            playerExpertiseRepo.insertNewExpertise(newExpertise);
                        }
                    } else if (transactionName.equals(DELETE_EXPERTISE)) {
                        if (selectedExpertise != null) {
                            playerExpertiseRepo.deleteExpertise(selectedExpertise);
                        }
                    }
                    e.onComplete();
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
                            Log.e("Error", throwable.getMessage());
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("safeDatabaseTransaction", ex.getMessage());
        }
    }

    private void makeExpertiseListScrollable(ListView listView){
        try {
            listView.setOnTouchListener(new ListView.OnTouchListener() {
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
            Log.e("makeExpertiseListScroll", ex.getMessage());
        }
    }

    private void makeToast(Context context, String message){
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            Log.e("makeToast", ex.getMessage());
        }
    }
}
