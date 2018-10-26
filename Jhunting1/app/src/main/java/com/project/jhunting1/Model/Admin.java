package com.project.jhunting1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

@Entity(tableName = "Admin")
public class Admin {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Admin_ID")
    private int admin_id;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    public Admin() {
    }

    @Ignore
    public Admin(int admin_id) {
        this.admin_id = admin_id;
    }

    @Ignore
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
