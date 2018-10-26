package com.project.jhunting1.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.project.jhunting1.Local.JhuntingDB;
import com.project.jhunting1.Local.JobDataSource;
import com.project.jhunting1.Local.NPCDataSource;
import com.project.jhunting1.Local.PlayerDataSource;
import com.project.jhunting1.Local.PlayerExpertiseDataSource;
import com.project.jhunting1.MainActivity;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;
import com.project.jhunting1.R;
import com.project.jhunting1.Repository.JobRepo;
import com.project.jhunting1.Repository.NPCRepo;
import com.project.jhunting1.Repository.PlayerExpertiseRepo;
import com.project.jhunting1.Repository.PlayerRepo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FragmentRegister extends Fragment implements AdapterView.OnItemSelectedListener{
    View view;
    JhuntingDB jhuntingDB;

    private static final String SET_UP_CATEGORY = "SET_UP";
    private static final String DELETE_ALL_CATEGORY = "DELETE_ALL";
    private Spinner rolesSpinner;
    private Intent intentRegisterType;
    private String type = "";

    private EditText txtName;
    private EditText txtTelephone;
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnRegister;

    private String name = "";
    private int telephone = 0;
    private String email = "";
    private String password = "";
    private int categoryId = 0;
    private String selectedCategory = "";

    private Button btnAddExpertise;
    private List<String> expertises = new ArrayList<String>();
    private List<Player_Expertise> player_expertises = new ArrayList<Player_Expertise>();
    private ListView expertiseList;
    private Spinner spnExpertise;

    private PlayerRepo playerRepo;
    private NPCRepo npcRepo;
    private JobRepo jobRepo;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ArrayAdapter<String> expertiseAdapter;
    private List<Job_Category> categories = new ArrayList<Job_Category>();
    private ArrayAdapter<Job_Category> adapter;

    public FragmentRegister() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setUpViewFragmentAndComponents(inflater, container);

        if (rolesSpinner != null) {
            rolesSpinner.setOnItemSelectedListener(this);
        }

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String itemSelected = adapterView.getItemAtPosition(position).toString();
        if (itemSelected.equals("NPC")) {
            intentRegisterType = null;
            intentRegisterType = new Intent(view.getContext(), MainActivity.class);
            Bundle roleType = new Bundle();
            roleType.putString("Role", "NPC");
            intentRegisterType.putExtras(roleType);

        } else if (itemSelected.equals("Player")) {
            intentRegisterType = null;
            intentRegisterType = new Intent(view.getContext(), MainActivity.class);
            Bundle roleType = new Bundle();
            roleType.putString("Role", "Player");
            intentRegisterType.putExtras(roleType);
        }
        if (intentRegisterType != null){
            startActivity(intentRegisterType);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setUpViewFragmentAndComponents(LayoutInflater inflater, @Nullable ViewGroup container){

        if (type.equals("Player")) {
            rolesSpinner = null;
            view = null;
            view = inflater.inflate(R.layout.player_register_fragment, container,false);
            jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
            npcRepo = NPCRepo.getInstance(NPCDataSource.getInstance(jhuntingDB.npc_dao()));
            jobRepo = JobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
            rolesSpinner = (Spinner)view.findViewById(R.id.spnPlayerRegisterType);
            rolesSpinner.setSelection(1, false);
            bindPlayerViewComponents();
            setUpAddExpertise();
            makeListScrollable();
            displayCurrentExpertiseList();
            PlayerRegistrationSetup();
            //checkAllPlayers();
          //  setUpJobCategories();

        } else if(type.equals("NPC")){
            view = inflater.inflate(R.layout.npc_register_fragment, container, false);
            jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
            npcRepo = NPCRepo.getInstance(NPCDataSource.getInstance(jhuntingDB.npc_dao()));
            jobRepo = JobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
            rolesSpinner = (Spinner) view.findViewById(R.id.spnNpcRegisterType);
            rolesSpinner.setSelection(2, false);
            bindNpcViewComponents();
            NpcRegistrationSetup();
            //checkAllNpc();

        } else {
            view = inflater.inflate(R.layout.player_register_fragment, container,false);
            jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
            npcRepo = NPCRepo.getInstance(NPCDataSource.getInstance(jhuntingDB.npc_dao()));
            jobRepo = JobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
            rolesSpinner = (Spinner)view.findViewById(R.id.spnPlayerRegisterType);
        }
    }

    private void setUpAddExpertise(){
        Disposable disposable = jobRepo.getObservableAllJobCategory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Job_Category>>() {
                    @Override
                    public void accept(List<Job_Category> job_categories) throws Exception {
                        onGetAllJobCategorySuccess(job_categories);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Error", throwable.getMessage());
                        makeToast(throwable.getMessage());
                    }

                });
        compositeDisposable.add(disposable);

        btnAddExpertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedCategory.equals("")) {
                    expertises.add(selectedCategory);
                    expertiseList.setAdapter(expertiseAdapter);
                    expertiseAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void onGetAllJobCategorySuccess(List<Job_Category> job_categories) {
        categories.clear();
        categories.addAll(job_categories);
        adapter.notifyDataSetChanged();
        spnExpertise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Job_Category job_category = (Job_Category) adapterView.getItemAtPosition(i);
                categoryId = job_category.getJc_id();
                selectedCategory = job_category.getCategory();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private NPC getNpcInfoFromView(){
        NPC npc = new NPC(name, telephone, email, password);
        return npc;
    }

    private void addNpc(final NPC npc){
        Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                int npc_id = (int)npcRepo.insertNPC(npc);
                NPC insertedNpc = npcRepo.getNPCById(npc_id);
                NPC_Trustworth npc_trustworth = new NPC_Trustworth(insertedNpc.getNPC_Id(), 0, 0 );
                npcRepo.insertNpcTrustWorth(npc_trustworth);
                npcRepo.updateNPC(insertedNpc);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {

                    @Override
                    public void accept(Object o) throws Exception {
                        Toast.makeText(view.getContext(), "NPC Added", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void NpcRegistrationSetup(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateRegistrationForm()) {
                    NPC npc = getNpcInfoFromView();
                    addNpc(npc);

                    Toast.makeText(view.getContext(),  "NPC " + npc.getName() + " has been registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Player getPlayerInfoFromView(){
        Player player = new Player(name, telephone, email, password);
        return player;
    }

    private void addPlayerWithExpertise(final Player player){
        playerRepo = playerRepo.getInstance(PlayerDataSource.getInstance(jhuntingDB.playerDAO()));
        jobRepo = jobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                long returnedId =  playerRepo.insertPlayer(player);
                int playerId = (int)returnedId;
                for (String s: expertises) {

                    int jobCategoryId = jobRepo.getJobCategoryId(s);
                    Player_Expertise player_expertise = new Player_Expertise(playerId, jobCategoryId, 0, 0);
                    player_expertises.add(player_expertise);
                }
                playerRepo.insertPlayerExpertises(player_expertises);


                //CHECKING DATABASE IF DATA IS INSERTED!!!
                List<Player> allPlayers = new ArrayList<Player>();
                allPlayers = playerRepo.getAllPlayers();
                if (!allPlayers.isEmpty()){
                    for (Player p: allPlayers){
                        Log.e("Players", p.toString());
                    }
                }

                List<Player_Expertise> testExpertise = new ArrayList<Player_Expertise>();
                testExpertise = playerRepo.getPlayerExpertises(playerId);
                if(!testExpertise.isEmpty()){
                    for (Player_Expertise pe: testExpertise){
                        Log.e("Player Expertise", pe.toString());
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
                        Toast.makeText(view.getContext(), "Player Added", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void PlayerRegistrationSetup(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateRegistrationForm()) {
                    Player player = getPlayerInfoFromView();
                    addPlayerWithExpertise(player);

                    makeToast("Player " + player.getName() + " has been registered");
                }
            }
        });
    }

    private boolean validateRegistrationForm(){
        if (txtName.getText().length() == 0 || txtName.getText().equals("")){
            txtName.requestFocus();
            makeToast("Name field cannot be empty");
            return false;
        } else if (txtTelephone.getText().length() == 0 || txtTelephone.getText().equals("")){
            txtTelephone.requestFocus();
            makeToast("Telephone field cannot be empty");
            return false;
        } else if (txtEmail.getText().length() == 0 || txtEmail.getText().equals("")){
            txtEmail.requestFocus();
            makeToast("Email field cannot be empty");
            return false;
        } else if (txtPassword.getText().length() == 0 || txtPassword.getText().equals("")){
            txtPassword.requestFocus();
            makeToast("Password field cannot be empty");
            return false;
        } else if (type.equals("Player") && expertises.isEmpty()){
            spnExpertise.requestFocus();
            makeToast("Add at least one expertise");
            return false;
        }
        else{
            name = txtName.getText().toString();
            telephone = Integer.parseInt(txtTelephone.getText().toString());
            email = txtEmail.getText().toString();
            password = txtPassword.getText().toString();

            return true;
        }

    }

    private void displayCurrentExpertiseList(){
        expertiseAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, expertises);
        expertiseList.setAdapter(expertiseAdapter);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private void bindNpcViewComponents(){
        txtName = view.findViewById(R.id.txtRegisterNpcName);
        txtTelephone = view.findViewById(R.id.txtRegisterNpcTelephone);
        txtEmail = view.findViewById(R.id.txtRegisterNpcEmail);
        txtPassword = view.findViewById(R.id.txtRegisterNpcPassword);
        btnRegister = view.findViewById(R.id.btnNpcRegister);
    }

    private void bindPlayerViewComponents(){
        txtName = (EditText)view.findViewById(R.id.txtRegisterPlayerName);
        txtTelephone = (EditText)view.findViewById(R.id.txtRegisterPlayerTelephone);
        txtEmail = (EditText)view.findViewById(R.id.txtRegisterPlayerEmail);
        txtPassword = (EditText)view.findViewById(R.id.txtRegisterPlayerPassword);
        spnExpertise = (Spinner)view.findViewById(R.id.spnRegisterPlayerExpertise);
        btnAddExpertise =(Button)view.findViewById(R.id.btnRegisterAddExpertise);
        btnRegister = (Button) view.findViewById(R.id.btnPlayerRegister);
        expertiseList = (ListView)view.findViewById(R.id.listRegisterPlayerExpertise);
        adapter = new ArrayAdapter<Job_Category>(view.getContext(), android.R.layout.simple_spinner_item, categories);
        spnExpertise.setAdapter(adapter);
    }

    private void makeListScrollable(){
        expertiseList.setOnTouchListener(new ListView.OnTouchListener() {
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


    private void checkAllPlayers(){
        compositeDisposable = new CompositeDisposable();
        JhuntingDB jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
        playerRepo = PlayerRepo.getInstance(PlayerDataSource.getInstance(jhuntingDB.playerDAO()));
        final PlayerExpertiseRepo playerExpertiseRepo = PlayerExpertiseRepo.getInstance(PlayerExpertiseDataSource.getInstance(jhuntingDB.player_expertiseDAO()));
        final List<Job_Category> myCategories = new ArrayList<Job_Category>();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {

                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        List<Player> allPlayers = new ArrayList<Player>();
                        allPlayers = playerRepo.getAllPlayers();
                        if (!allPlayers.isEmpty()) {
                            for (Player p : allPlayers) {
                                Log.e("Players", p.toString());
                                List<Player_Expertise> testExpertise = new ArrayList<Player_Expertise>();
                                testExpertise = playerRepo.getPlayerExpertises(p.getPlayer_id());
                                if (!testExpertise.isEmpty()) {
                                    for (Player_Expertise pe : testExpertise) {
                                        Log.e("Player Expertise ID", pe.toString());
                                    }
                                }
                            }
                        }

                        //playerRepo.deleteAllPlayer(allPlayers);

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
                            }
                        });
            }
        });
    }

    private void checkAllNpc(){
        final List<Job_Category> myCategories = new ArrayList<Job_Category>();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {

                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        List<NPC> allNpc = new ArrayList<NPC>();
                        List<NPC_Trustworth> npc_trustworths = new ArrayList<NPC_Trustworth>();
                        allNpc = npcRepo.getAllNpc();
                        npc_trustworths = npcRepo.getAllNpcTrustworth();
                        if (!allNpc.isEmpty()) {
                            for (NPC n : allNpc) {
                                Log.e("NPC", n.toString());
                            }
                        }
                        else{
                            Log.e("NPC", "Empty");
                        }
                        if (!npc_trustworths.isEmpty()){
                            for (NPC_Trustworth nt: npc_trustworths){
                                Log.e("Trustworths", nt.toString());
                            }
                        }

                        //playerRepo.deleteAllPlayer(allPlayers);

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
                            }
                        });
            }
        });
    }

    private void editJobCategories(final String type){
        final List<Job_Category> myCategories = new ArrayList<Job_Category>();
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                List<Job_Category> allCategory = new ArrayList<Job_Category>();
                allCategory = jobRepo.getAllJobCategory();
                if (type.equals(SET_UP_CATEGORY)) {
                    if (allCategory.isEmpty()) {
                        myCategories.add(new Job_Category("Programmer"));
                        myCategories.add(new Job_Category("Construction"));
                        myCategories.add(new Job_Category("Accounting"));
                        myCategories.add(new Job_Category("Community Service"));
                        myCategories.add(new Job_Category("Legal"));
                        jobRepo.insertJobCategory(myCategories);

                    }
                }else if (type.equals(DELETE_ALL_CATEGORY)){
                    jobRepo.deleteJobCategories(allCategory);
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

    }

    private void clearForm(){
        txtName.setText("");
        txtTelephone.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
    }

}
