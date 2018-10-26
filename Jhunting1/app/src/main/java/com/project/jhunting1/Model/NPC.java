package com.project.jhunting1.Model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

import io.reactivex.annotations.NonNull;

@Entity(tableName = "NPC")
public class NPC {
    @NonNull
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "NPC_ID")
    private int NPC_Id;

    @ColumnInfo(name= "name")
    private String name;

    @ColumnInfo(name = "telephone")
    private int telephone;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "password")
    private String password;

    @Embedded(prefix = "npc_trustworth")
    private NPC_Trustworth npc_trustworth;

    @Ignore
    private List<Jobs> jobsList;

    public NPC() {
    }

    @Ignore
    public NPC(int NPC_Id) {
        this.NPC_Id = NPC_Id;
    }

    @Ignore
    public NPC(String name, int telephone, String email, String password) {
        this.name = name;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
    }

    public int getNPC_Id() {
        return NPC_Id;
    }

    public void setNPC_Id(int NPC_Id) {
        this.NPC_Id = NPC_Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public NPC_Trustworth getNpc_trustworth() {
        return npc_trustworth;
    }

    public void setNpc_trustworth(NPC_Trustworth npc_trustworth) {
        this.npc_trustworth = npc_trustworth;
    }

    public List<Jobs> getJobsList() {
        return jobsList;
    }

    public void setJobsList(List<Jobs> jobsList) {
        this.jobsList = jobsList;
    }

    @Override
    public String toString(){
        return new StringBuilder("Name: " + name).append("\n").append("Telephone: " + telephone).append("\n").append("Email: " + email).append("\n").toString();
    }
}
