package com.project.jhunting1.Views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.jhunting1.Local.JhuntingDB;
import com.project.jhunting1.Local.NPCDataSource;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;
import com.project.jhunting1.Model.Player_Expertise;
import com.project.jhunting1.R;
import com.project.jhunting1.Repository.NPCRepo;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FragmentNpcProfile extends Fragment{

    private View view;
    private JhuntingDB jhuntingDB;
    private int npc_id;

    private static final String EMPTY_FIELD = "EMPTY FIELD";
    private static final String READY_FOR_UPDATE = "READY FOR UPDATE";
    private static final String UPDATING_WITH_PASSWORD = "UPDATING WITH PASSWORD";
    private static final String UPDATE_NPC = "UPDATE NPC";

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvTelephone;
    private TextView tvTrustLevel;
    private TextView tvTrustExp;

    private TextView txtEditName;
    private TextView txtEditEmail;
    private TextView txtEditTelephone;
    private TextView txtOldPassword;
    private TextView txtNewPassword;
    private Button btnUpdate;
    private Button btnCancel;

    private String name = "";
    private int telephone = 0;
    private String email = "";
    private String password = "";

    private Button btnEdit;

    private NPCRepo npcRepo;

    private NPC npcToEdit = new NPC();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.npc_profile, container, false);
        jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
        npc_id = getArguments().getInt("id");
        instantiateRepositories();
        bindViewComponents();
        //displayNpcInformation();
        showNpcInformation();
        enableEditingNpcInfo();
        return view;
    }

    private void instantiateRepositories(){
        npcRepo = NPCRepo.getInstance(NPCDataSource.getInstance(jhuntingDB.npc_dao()));
    }

    public void bindViewComponents(){
        try {
            tvName = (TextView) view.findViewById(R.id.tvNpcProfileName);
            tvEmail = (TextView) view.findViewById(R.id.tvNpcProfileEmail);
            tvTelephone = (TextView) view.findViewById(R.id.tvNpcProfileTelephone);
            tvTrustLevel = (TextView) view.findViewById(R.id.tvNpcProfileTrustLevel);
            tvTrustExp = (TextView) view.findViewById(R.id.tvNpcProfileTrustExp);
            btnEdit = (Button) view.findViewById(R.id.btnEditNPC);
        }catch (Exception ex){
            Log.e("bindViewComponent", ex.getMessage());
        }
    }

    private void showNpcInformation(){
        try{
            Disposable disposable = npcRepo.getObservableNPCById(npc_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<NPC>() {
                        @Override
                        public void accept(final NPC npc) throws Exception {
                            if (npc != null) {
                                npcToEdit= npc;
                                Disposable disposableTrustWorth = npcRepo.getObservableNpcTrustworth(npc_id)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Consumer<NPC_Trustworth>() {
                                            @Override
                                            public void accept(NPC_Trustworth npc_trustworth) throws Exception {
                                                tvName.setText(npc.getName());
                                                tvEmail.setText(npc.getEmail());
                                                tvTelephone.setText(Integer.toString(npc.getTelephone()));
                                                if (npc_trustworth!= null) {
                                                    tvTrustExp.setText(": " + Integer.toString(npc_trustworth.getTrust_points()));
                                                    tvTrustLevel.setText(": " + Integer.toString(npc_trustworth.getTrust_level()));
                                                }
                                            }
                                        });
                                compositeDisposable.add(disposableTrustWorth);
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        }catch (Exception ex){
            Log.e("showNpcInformation", ex.getMessage());
        }
    }

    public void enableEditingNpcInfo(){
        try {
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditNpcDialog();
                }
            });
        }catch (Exception ex){
            Log.e("enableEditingNpc", ex.getMessage());
        }
    }

    private void showEditNpcDialog() {
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

            txtEditName.setText(npcToEdit.getName().toString());
            txtEditEmail.setText(npcToEdit.getEmail().toString());
            txtEditTelephone.setText(Integer.toString(npcToEdit.getTelephone()));

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String validateForm = validateEditForm(dialog.getContext());
                    if (validateForm.equals(READY_FOR_UPDATE) || validateForm.equals(UPDATING_WITH_PASSWORD)) {
                        npcToEdit.setName(txtEditName.getText().toString());
                        npcToEdit.setEmail(txtEditEmail.getText().toString());
                        npcToEdit.setTelephone(Integer.parseInt(txtEditTelephone.getText().toString()));
                        if(validateForm.equals(UPDATING_WITH_PASSWORD)) {
                            npcToEdit.setPassword(txtNewPassword.getText().toString());
                        }
                        showConfirmDialog(dialog.getContext(), UPDATE_NPC);
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

    private String validateEditForm(Context context){
        try {
            if (txtEditName.getText().length() == 0 || txtEditName.getText().equals("")) {
                txtEditName.requestFocus();
                makeToast(context,"Name field cannot be empty");
                return EMPTY_FIELD;
            } else if (txtEditTelephone.getText().length() == 0 || txtEditTelephone.getText().equals("")) {
                txtEditTelephone.requestFocus();
                makeToast(context,"Telephone field cannot be empty");
                return EMPTY_FIELD;
            } else if (txtEditEmail.getText().length() == 0 || txtEditEmail.getText().equals("")) {
                txtEditEmail.requestFocus();
                makeToast(context,"Email field cannot be empty");
                return EMPTY_FIELD;
            } else if (txtOldPassword.getText().length() != 0 && txtNewPassword.getText().length() != 0) {
                if (txtOldPassword.getText().toString().equals(npcToEdit.getPassword().toString())) {
                    return UPDATING_WITH_PASSWORD;
                } else {
                    txtOldPassword.requestFocus();
                    makeToast(context,"Old password does not match");
                    return EMPTY_FIELD;
                }
            } else {
                name = txtEditName.getText().toString();
                telephone = Integer.parseInt(txtEditTelephone.getText().toString());
                email = txtEditEmail.getText().toString();
                return READY_FOR_UPDATE;
            }
        }catch (Exception ex){
            Log.e("validateForm", ex.getMessage());
            return EMPTY_FIELD;
        }
    }

    private void showConfirmDialog(final Context context, final String transactionName) {
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

            if (transactionName.equals(UPDATE_NPC)) {
                tvTitle.setText(R.string.updateNPCConfirmTxt);
            }

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    safeDatabaseTransaction(transactionName);
                    dialog.dismiss();
                    showRelevantCompletionMessage(context, transactionName);
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } catch (Exception ex) {
            Log.e("showConfirmDialog", ex.getMessage());
        }
    }

    private void showRelevantCompletionMessage(Context context, String transactionName){
        try {
            switch (transactionName) {
                case UPDATE_NPC:
                    makeToast(context, "Npc Updated");
                    break;
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
                    if (transactionName.equals(UPDATE_NPC)) {
                        if (npcToEdit != null) {
                            npcRepo.updateNPC(npcToEdit);
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

    private void updateView(){
        tvName.setText(npcToEdit.getName());
        tvEmail.setText(npcToEdit.getEmail());
        tvTelephone.setText(Integer.toString(npcToEdit.getTelephone()));
        if (npcToEdit.getNpc_trustworth() != null) {
            tvTrustLevel.setText(Integer.toString(npcToEdit.getNpc_trustworth().getTrust_level()));
            tvTrustExp.setText(Integer.toString(npcToEdit.getNpc_trustworth().getTrust_points()));
        }
    }

    private void makeToast(Context context, String message){
        Toast.makeText(view.getContext(), message , Toast.LENGTH_SHORT).show();
    }
}
