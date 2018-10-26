package com.project.jhunting1.Repository;

import com.project.jhunting1.Database.IJobDataSource;
import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;

import java.util.List;

import io.reactivex.Flowable;

public class JobRepo implements IJobDataSource{

    private IJobDataSource localDataSource;
    private static JobRepo mInstance;

    public JobRepo(IJobDataSource localDataSource){
        this.localDataSource = localDataSource;
    }

    public static JobRepo getInstance(IJobDataSource jobDataSource) {
        if (mInstance == null){
            mInstance = new JobRepo(jobDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<Jobs> getObservableJob(int job_id) {
        return localDataSource.getObservableJob(job_id);
    }

    @Override
    public Jobs getJob(int job_id) {
        return localDataSource.getJob(job_id);
    }

    @Override
    public Flowable<List<Jobs>> getObservableAllJobs() {
        return localDataSource.getObservableAllJobs();
    }

    @Override
    public Flowable<List<Jobs>> getObservableAllJobsForCategory(int category_id) {
        return localDataSource.getObservableAllJobsForCategory(category_id);
    }

    @Override
    public Flowable<List<Jobs>> getObservableAllJobsNormally() {
        return localDataSource.getObservableAllJobsNormally();
    }

    @Override
    public Flowable<List<Jobs>> getObservableAllJobsByNpcId(int npc_id) {
        return localDataSource.getObservableAllJobsByNpcId(npc_id);
    }

    @Override
    public List<Jobs> getAllJobs() {
        return localDataSource.getAllJobs();
    }

    @Override
    public Job_Category getJobCategory(int category_id) {
        return localDataSource.getJobCategory(category_id);
    }

    @Override
    public long addJob(Jobs job) {
        return localDataSource.addJob(job);
    }

    @Override
    public void insertJobCategory(List<Job_Category> job_categories) {
        localDataSource.insertJobCategory(job_categories);
    }

    @Override
    public List<Job_Category> getAllJobCategory() {
        return localDataSource.getAllJobCategory();
    }

    @Override
    public Flowable<List<Job_Category>> getObservableAllJobCategory() {
        return localDataSource.getObservableAllJobCategory();
    }

    @Override
    public int getJobCategoryId(String jobCategory) {
        return localDataSource.getJobCategoryId(jobCategory);
    }

    @Override
    public void deleteJobCategories(List<Job_Category> job_categories) {
        localDataSource.deleteJobCategories(job_categories);
    }

    @Override
    public Flowable<List<Contract_Duration>> getObservableContractDurations() {
        return localDataSource.getObservableContractDurations();
    }

    @Override
    public List<Contract_Duration> getAllContractDuration() {
        return localDataSource.getAllContractDuration();
    }

    @Override
    public Contract_Duration getContractDurationById(int duration_id) {
        return localDataSource.getContractDurationById(duration_id);
    }

    @Override
    public Flowable<Contract_Duration> getObservableContractDurationById(int duration_id) {
        return localDataSource.getObservableContractDurationById(duration_id);
    }

    @Override
    public void insertDurationOptions(List<Contract_Duration> contract_durations) {
        localDataSource.insertDurationOptions(contract_durations);
    }

    @Override
    public void deleteAllDurationOptions(List<Contract_Duration> contract_durations) {
        localDataSource.deleteAllDurationOptions(contract_durations);
    }

    @Override
    public void deleteJob(Jobs job) {
        localDataSource.deleteJob(job);
    }

    @Override
    public void deleteAllJobs(List<Jobs> job) {
        localDataSource.deleteAllJobs(job);
    }

    @Override
    public void updateJob(Jobs job) {
        localDataSource.updateJob(job);
    }
}
