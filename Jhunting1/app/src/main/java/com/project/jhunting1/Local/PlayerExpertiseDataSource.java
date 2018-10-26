package com.project.jhunting1.Local;

import com.project.jhunting1.DAO.Player_ExpertiseDAO;
import com.project.jhunting1.Database.IPlayerExpertiseDataSource;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

public class PlayerExpertiseDataSource implements IPlayerExpertiseDataSource{

    private Player_ExpertiseDAO player_expertiseDAO;
    private static PlayerExpertiseDataSource mInstance;

    public PlayerExpertiseDataSource(Player_ExpertiseDAO player_expertiseDAO) {
        this.player_expertiseDAO = player_expertiseDAO;
    }

    public static PlayerExpertiseDataSource getInstance(Player_ExpertiseDAO player_expertiseDAO) {
        if (mInstance == null){
            mInstance = new PlayerExpertiseDataSource(player_expertiseDAO);
        }
        return mInstance;
    }

    @Override
    public List<Player_Expertise> getAllExpertise() {
        return player_expertiseDAO.getAllExpertise();
    }

    @Override
    public Job_Category getJobCategory(int job_categoryId) {
        return player_expertiseDAO.getJobCategory(job_categoryId);
    }

    @Override
    public Flowable<Job_Category> getObservableJobCategory(int job_categoryId) {
        return player_expertiseDAO.getObservableJobCategory(job_categoryId);
    }

    @Override
    public void insertNewExpertise(Player_Expertise player_expertise) {
        player_expertiseDAO.insertNewExpertise(player_expertise);
    }

    @Override
    public void deleteExpertise(Player_Expertise player_expertise) {
        player_expertiseDAO.deleteExpertise(player_expertise);
    }

    @Override
    public void updateExpertise(Player_Expertise player_expertise) {
        player_expertiseDAO.updateExpertise(player_expertise);
    }
}
