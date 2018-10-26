package com.project.jhunting1.Database;

import android.arch.persistence.room.Query;

import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;

import java.util.List;

import io.reactivex.Flowable;

public interface IJobDataSource {

    public Flowable<Jobs> getObservableJob(int job_id);
    public Jobs getJob(int job_id);
    public Flowable<List<Jobs>> getObservableAllJobs();
    public Flowable<List<Jobs>> getObservableAllJobsForCategory(int category_id);
    public Flowable<List<Jobs>> getObservableAllJobsNormally();
    public Flowable<List<Jobs>> getObservableAllJobsByNpcId(int npc_id);
    public List<Jobs> getAllJobs();
    public Job_Category getJobCategory(int category_id);
    long addJob(Jobs job);
    public void insertJobCategory(List<Job_Category> job_categories);
    public List<Job_Category> getAllJobCategory();
    public Flowable<List<Job_Category>> getObservableAllJobCategory();
    public int getJobCategoryId(String jobCategory);
    public void deleteJobCategories(List<Job_Category> job_categories);
    public Flowable<List<Contract_Duration>> getObservableContractDurations();
    public List<Contract_Duration> getAllContractDuration();
    public Contract_Duration getContractDurationById(int duration_id);
    public Flowable<Contract_Duration> getObservableContractDurationById(int duration_id);
    public void insertDurationOptions (List<Contract_Duration> contract_durations);
    public void deleteAllDurationOptions(List<Contract_Duration> contract_durations);
    void deleteJob(Jobs job);
    public void deleteAllJobs(List<Jobs> job);
    void updateJob(Jobs job);

}
