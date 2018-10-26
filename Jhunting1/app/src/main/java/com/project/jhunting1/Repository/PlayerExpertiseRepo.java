package com.project.jhunting1.Repository;

import com.project.jhunting1.Database.IPlayerExpertiseDataSource;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

public class PlayerExpertiseRepo implements IPlayerExpertiseDataSource{

    private IPlayerExpertiseDataSource localDataSource;
    private static PlayerExpertiseRepo mInstance;

    public PlayerExpertiseRepo(IPlayerExpertiseDataSource localDataSource){
        this.localDataSource = localDataSource;
    }

    public static PlayerExpertiseRepo getInstance(IPlayerExpertiseDataSource localDataSource) {
        if(mInstance == null){
            mInstance = new PlayerExpertiseRepo(localDataSource);
        }
        return mInstance;
    }

    @Override
    public List<Player_Expertise> getAllExpertise() {
        return localDataSource.getAllExpertise();
    }

    @Override
    public Job_Category getJobCategory(int job_categoryId) {
        return localDataSource.getJobCategory(job_categoryId);
    }

    @Override
    public Flowable<Job_Category> getObservableJobCategory(int job_categoryId) {
        return localDataSource.getObservableJobCategory(job_categoryId);
    }

    @Override
    public void insertNewExpertise(Player_Expertise player_expertise) {
        localDataSource.insertNewExpertise(player_expertise);
    }

    @Override
    public void deleteExpertise(Player_Expertise player_expertise) {
        localDataSource.deleteExpertise(player_expertise);
    }

    @Override
    public void updateExpertise(Player_Expertise player_expertise) {
        localDataSource.updateExpertise(player_expertise);
    }
}
