package com.project.jhunting1.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.project.jhunting1.Local.JhuntingDB;
import com.project.jhunting1.Local.NPCDataSource;
import com.project.jhunting1.Local.PlayerDataSource;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.NPCActivity;
import com.project.jhunting1.PlayerActivity;
import com.project.jhunting1.R;
import com.project.jhunting1.Repository.NPCRepo;
import com.project.jhunting1.Repository.PlayerRepo;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FragmentLogin extends Fragment implements AdapterView.OnItemSelectedListener {

    private View view;
    private Context myContext;

    private PlayerRepo playerRepo;
    private NPCRepo npcRepo;

    private Spinner spnLoginType;
    private EditText txtEmail;
    private EditText txtPassword;

    private Player player = null;
    private NPC npc = null;

    private Button btnLogin;

    private String loginType = "";

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FragmentLogin() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_fragment, container, false);
        bindViewComponents();
        spnLoginType.setOnItemSelectedListener(this);
        initializeLoginButtonFeatures();

        return view;
    }

    private void Login(final String email, final String password) {
        try {
            JhuntingDB jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
            playerRepo = PlayerRepo.getInstance(PlayerDataSource.getInstance(jhuntingDB.playerDAO()));
            npcRepo = NPCRepo.getInstance(NPCDataSource.getInstance(jhuntingDB.npc_dao()));
            Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {
                    Intent intent = null;

                    if (loginType.equals("NPC")) {
                        npc = npcRepo.getNPCUernamePassword(email, password);
                        if (!npc.toString().equals("") || npc.toString().length() != 0) {
                            Log.e("Login Status", "Logged in " + npc.getName());
                            intent = new Intent(view.getContext(), NPCActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", npc.getNPC_Id());
                            intent.putExtras(bundle);
                        } else {
                            makeToast("Invalid Email or Password");
                        }

                    } else if (loginType.equals("Player")) {
                        player = playerRepo.getPlayerUernamePassword(email, password);
                        if (!player.toString().equals("") || player.toString().length() != 0) {
                            intent = new Intent(view.getContext(), PlayerActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", player.getPlayer_id());
                            intent.putExtras(bundle);
                            Log.e("Login Status", "Logged in " + player.getName() + ", Id :" + intent.getExtras().getInt("id"));

                        } else {
                            makeToast("Invalid Email or Password");
                        }
                    }
                    if (intent != null) {
                        startActivity(intent);
                    }
                    e.onComplete();
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer() {

                        @Override
                        public void accept(Object o) throws Exception {
                            makeToast("Login Successful");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            makeToast("Invalid email or password");
                            //    Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("Login Error", ex.getMessage());
        }
    }


    public void bindViewComponents(){
        btnLogin = (Button)view.findViewById(R.id.btnLogin);
        spnLoginType = (Spinner)view.findViewById(R.id.spnLoginType);
        txtEmail = (EditText)view.findViewById(R.id.txtUsername);
        txtPassword = (EditText)view.findViewById(R.id.txtPassword);
    }

    private void initializeLoginButtonFeatures(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateLoginForm()) {
                    Login(txtEmail.getText().toString(), txtPassword.getText().toString());
                }
            }
        });
    }



    private boolean validateLoginForm(){
        if (spnLoginType.getSelectedItem().equals("Select Role")){
            spnLoginType.requestFocus();
            makeToast("Please select your role");
            return false;
        }else if (txtEmail.getText().length() == 0 || txtEmail.getText().equals("")){
            txtEmail.requestFocus();
            makeToast("Please enter your email");
            return false;
        } else if (txtPassword.getText().length() == 0 || txtPassword.getText().equals("")) {
            txtPassword.requestFocus();
            makeToast("Please type in your password");
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        loginType = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void clearForm(){
        txtEmail.setText("");
        txtPassword.setText("");
    }

    private void makeToast(String message){
        Toast.makeText(view.getContext(), message , Toast.LENGTH_SHORT).show();
    }
}
