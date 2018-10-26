package com.project.jhunting1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

@Entity
public class Job_Category {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name= "JC_ID")
    private int jc_id;

    @ColumnInfo(name= "category")
    private String category;

    public Job_Category(){}

    @Ignore
    public Job_Category(String category) {
        this.category = category;
    }

    public int getJc_id() {
        return jc_id;
    }

    public void setJc_id(int jc_id) {
        this.jc_id = jc_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    @Override
    public String toString(){
        return new StringBuilder(category).append("\n").toString();
    }
}
