package com.project.jhunting1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

@Entity (tableName = "Contract_Duration")
public class Contract_Duration {
    @NonNull
    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = "duration_id")
    private int duration_id;

    @ColumnInfo(name = "days")
    private int days;

    @ColumnInfo (name = "Good_Score")
    private int goodScore;

    @ColumnInfo (name = "Satisfactory_Score")
    private int satisfactoryScore;

    @ColumnInfo(name = "Not_Bad_Score")
    private int notBadScore;

    public Contract_Duration() {
    }

    @Ignore
    public Contract_Duration(int days, int goodScore, int satisfactoryScore, int notBadScore) {
        this.days = days;
        this.goodScore = goodScore;
        this.satisfactoryScore = satisfactoryScore;
        this.notBadScore = notBadScore;
    }

    public int getDuration_id() {
        return duration_id;
    }

    public void setDuration_id(int duration_id) {
        this.duration_id = duration_id;
    }

    public int getGoodScore() {
        return goodScore;
    }

    public void setGoodScore(int goodScore) {
        this.goodScore = goodScore;
    }

    public int getSatisfactoryScore() {
        return satisfactoryScore;
    }

    public void setSatisfactoryScore(int satisfactoryScore) {
        this.satisfactoryScore = satisfactoryScore;
    }

    public int getNotBadScore() {
        return notBadScore;
    }

    public void setNotBadScore(int notBadScore) {
        this.notBadScore = notBadScore;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    @Override
    public String toString(){
        return new StringBuilder(Integer.toString(days)).toString();
    }
}
