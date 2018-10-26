package com.project.jhunting1.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.design.widget.FloatingActionButton;

import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface JobDAO {

    @Query("Select * from Jobs where Job_Id=:job_id")
    public Flowable<Jobs> getObservableJob(int job_id);

    @Query("Select * from Jobs where Job_Id=:job_id")
    public Jobs getJob(int job_id);

    @Query("Select * from Jobs")
    public Flowable<List<Jobs>> getObservableAllJobs();

    @Query("Select * from Jobs where job_category1 =:category_id or job_category2 =:category_id")
    public Flowable<List<Jobs>> getObservableAllJobsForCategory(int category_id);

    @Query("Select * from Jobs")
    public Flowable<List<Jobs>> getObservableAllJobsNormally();

    @Query("Select * from Jobs where npc_id=:npc_id")
    public Flowable<List<Jobs>> getObservableAllJobsByNpcId(int npc_id);

    @Query("Select * from Jobs")
    public List<Jobs> getAllJobs();

    @Query("Select * from Job_Category where JC_ID =:category_id")
    public Job_Category getJobCategory(int category_id);

    @Insert
    public void insertJobCategory(List<Job_Category> job_categories);

    @Query("Select * from Job_Category")
    public List<Job_Category> getAllJobCategory();

    @Query("Select * from Job_Category")
    public Flowable<List<Job_Category>> getObservableAllJobCategory();

    @Query("Select JC_ID from Job_Category where category =:jobCategory")
    public int getJobCategoryId(String jobCategory);

    @Delete
    public void deleteJobCategories(List<Job_Category> job_categories);

    @Query("Select * from Contract_Duration")
    public Flowable<List<Contract_Duration>> getObservableContractDurations();

    @Query("Select * from Contract_Duration")
    public List<Contract_Duration> getAllContractDuration();

    @Query("Select * from Contract_Duration where duration_id=:duration_id")
    public Contract_Duration getContractDurationById(int duration_id);

    @Query("Select * from Contract_Duration where duration_id=:duration_id")
    public Flowable<Contract_Duration> getObservableContractDurationById(int duration_id);

    @Insert
    public long addJob(Jobs job);

    @Insert
    public void insertDurationOptions (List<Contract_Duration> contract_durations);

    @Delete
    public void deleteAllDurationOptions(List<Contract_Duration> contract_durations);

    @Delete
    public void deleteJob(Jobs job);

    @Delete
    public void deleteAllJobs(List<Jobs> job);

    @Update
    public void updateJob(Jobs job);
}
