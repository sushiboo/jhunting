package com.project.jhunting1.Local;

import com.project.jhunting1.DAO.JobDAO;
import com.project.jhunting1.Database.IJobDataSource;
import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;

import java.util.List;

import io.reactivex.Flowable;

public class JobDataSource implements IJobDataSource{

    private JobDAO jobDAO;
    private static JobDataSource mInstance;

    public JobDataSource(JobDAO jobDAO){
        this.jobDAO = jobDAO;
    }

    public static JobDataSource getInstance(JobDAO jobDAO) {
        if(mInstance == null){
            mInstance = new JobDataSource(jobDAO);
        }
        return mInstance;
    }

    @Override
    public Flowable<Jobs> getObservableJob(int job_id) {
        return jobDAO.getObservableJob(job_id);
    }

    @Override
    public Jobs getJob(int job_id) {
        return jobDAO.getJob(job_id);
    }

    @Override
    public Flowable<List<Jobs>> getObservableAllJobs() {
        return jobDAO.getObservableAllJobs();
    }

    @Override
    public Flowable<List<Jobs>> getObservableAllJobsForCategory(int category_id) {
        return jobDAO.getObservableAllJobsForCategory(category_id);
    }

    @Override
    public Flowable<List<Jobs>> getObservableAllJobsNormally() {
        return jobDAO.getObservableAllJobsNormally();
    }

    @Override
    public Flowable<List<Jobs>> getObservableAllJobsByNpcId(int npc_id) {
        return jobDAO.getObservableAllJobsByNpcId(npc_id);
    }

    @Override
    public List<Jobs> getAllJobs() {
        return jobDAO.getAllJobs();
    }

    @Override
    public Job_Category getJobCategory(int category_id) {
        return jobDAO.getJobCategory(category_id);
    }

    @Override
    public long addJob(Jobs job) {
        return jobDAO.addJob(job);
    }

    @Override
    public void insertJobCategory(List<Job_Category> job_categories) {
        jobDAO.insertJobCategory(job_categories);
    }

    @Override
    public List<Job_Category> getAllJobCategory() {
        return jobDAO.getAllJobCategory();
    }

    @Override
    public Flowable<List<Job_Category>> getObservableAllJobCategory() {
        return jobDAO.getObservableAllJobCategory();
    }

    @Override
    public int getJobCategoryId(String jobCategory) {
        return jobDAO.getJobCategoryId(jobCategory);
    }

    @Override
    public void deleteJobCategories(List<Job_Category> job_categories) {
        jobDAO.deleteJobCategories(job_categories);
    }

    @Override
    public Flowable<List<Contract_Duration>> getObservableContractDurations() {
        return jobDAO.getObservableContractDurations();
    }

    @Override
    public List<Contract_Duration> getAllContractDuration() {
        return jobDAO.getAllContractDuration();
    }

    @Override
    public Contract_Duration getContractDurationById(int duration_id) {
        return jobDAO.getContractDurationById(duration_id);
    }

    @Override
    public Flowable<Contract_Duration> getObservableContractDurationById(int duration_id) {
        return jobDAO.getObservableContractDurationById(duration_id);
    }

    @Override
    public void insertDurationOptions(List<Contract_Duration> contract_durations) {
        jobDAO.insertDurationOptions(contract_durations);
    }

    @Override
    public void deleteAllDurationOptions(List<Contract_Duration> contract_durations) {
        jobDAO.deleteAllDurationOptions(contract_durations);
    }

    @Override
    public void deleteJob(Jobs job) {
        jobDAO.deleteJob(job);
    }

    @Override
    public void deleteAllJobs(List<Jobs> job) {
        jobDAO.deleteAllJobs(job);
    }

    @Override
    public void updateJob(Jobs job) {
        jobDAO.updateJob(job);
    }
}
