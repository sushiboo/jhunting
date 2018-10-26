package com.project.jhunting1.Database;

import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

public interface IPlayerExpertiseDataSource {

    public abstract List<Player_Expertise> getAllExpertise();
    public abstract Job_Category getJobCategory(int job_categoryId);
    public abstract Flowable<Job_Category> getObservableJobCategory(int job_categoryId);
    public abstract void insertNewExpertise(Player_Expertise player_expertise);
    public abstract void deleteExpertise(Player_Expertise player_expertise);
    public abstract void updateExpertise(Player_Expertise player_expertise);
}
