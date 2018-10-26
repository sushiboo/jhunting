package com.project.jhunting1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys =  @ForeignKey(entity = Player.class, parentColumns = "Player_Id", childColumns = "Player_Id", onDelete = CASCADE))
public class Player_Expertise {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PE_ID")
    private int pe_id;

    @ColumnInfo(name = "Player_Id")
    private int player_id;

    @ColumnInfo(name = "category_id")
    private int category_id;

    @Ignore
    private Job_Category job_category;

    @ColumnInfo(name = "exp_points")
    private int exp_points;

    @ColumnInfo(name="level")
    private int level;

    public Player_Expertise() {
    }

    @Ignore
    public Player_Expertise(int pe_id) {
        this.pe_id = pe_id;
    }

    @Ignore
    public Player_Expertise(int player_id, int category_id, int exp_points, int level) {
        this.player_id = player_id;
        this.category_id = category_id;
        this.exp_points = exp_points;
        this.level = level;
    }

    public int getPe_id() {
        return pe_id;
    }

    public void setPe_id(int pe_id) {
        this.pe_id = pe_id;
    }

    public int getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public Job_Category getJob_category() {
        return job_category;
    }

    public void setJob_category(Job_Category job_category) {
        this.job_category = job_category;
    }

    public int getExp_points() {
        return exp_points;
    }

    public void setExp_points(int exp_points) {
        this.exp_points = exp_points;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    @Override
    public String toString(){
        String category = ".";
        if (job_category != null){
            category = job_category.getCategory();
        }
        return new StringBuilder(" Job Category: " + category + "\n Exp Points: " + exp_points + "\n Level: " + level).toString();
    }
}
