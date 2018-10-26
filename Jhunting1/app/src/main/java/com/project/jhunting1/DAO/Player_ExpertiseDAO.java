package com.project.jhunting1.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class Player_ExpertiseDAO {

    @Query("Select * from Player_Expertise")
    public abstract List<Player_Expertise> getAllExpertise();


    @Query("Select * from Job_Category where JC_ID =:job_categoryId")
    public abstract Job_Category getJobCategory(int job_categoryId);

    @Query("Select * from Job_Category where JC_ID =:job_categoryId")
    public abstract Flowable<Job_Category> getObservableJobCategory(int job_categoryId);

    @Insert
    public abstract void insertNewExpertise(Player_Expertise player_expertise);

    @Delete
    public abstract void deleteExpertise(Player_Expertise player_expertise);

    @Update
    public abstract void updateExpertise(Player_Expertise player_expertise);

}
