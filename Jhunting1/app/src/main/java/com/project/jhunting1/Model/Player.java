package com.project.jhunting1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

import io.reactivex.annotations.NonNull;

@Entity(tableName = "Player")
public class Player {
    @NonNull
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name="Player_Id")
    private int player_id;

    @ColumnInfo(name="name")
    private String name;

    @ColumnInfo(name= "telephone")
    private int telephone;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "password")
    private String password;

    @Ignore
    private List<Player_Expertise> player_expertises;

    @Ignore
    private List<Job_Applications> player_Job_Applications;

    public Player() {
    }

    @Ignore
    public Player(int player_id) {
        this.player_id = player_id;
    }

    @Ignore
    public Player(String name, int telephone, String email) {
        this.name = name;
        this.telephone = telephone;
        this.email = email;
    }

    @Ignore
    public Player(String name, int telephone, String email, String password) {
        this.name = name;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
    }

    public int getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
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

    public List<Player_Expertise> getPlayer_expertises() {
        return player_expertises;
    }

    public void setPlayer_expertises(List<Player_Expertise> player_expertises) {
        this.player_expertises = player_expertises;
    }

    public List<Job_Applications> getPlayer_Job_Applications() {
        return player_Job_Applications;
    }

    public void setPlayer_Job_Applications(List<Job_Applications> player_Job_Applications) {
        this.player_Job_Applications = player_Job_Applications;
    }

    @Override
    public String toString(){
        return new StringBuilder(name).toString();
    }
}
