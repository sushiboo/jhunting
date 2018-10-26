package com.project.jhunting1.Database;

import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

public interface IPlayerDataSource {

    Flowable<Player> getObservablePlayerById(int player_id);
    Player getPlayerById(int player_id);
    Flowable<List<Player>> getObservableAllPlayers();
    public abstract List<Player> getAllPlayers();
    Player getPlayerUernamePassword(String email, String password);
    List<Player_Expertise> getPlayerExpertises(int player_id);
    public abstract Flowable<List<Player>> getObservablePlayersForCategory(int category_id1, int category_id2);
    public abstract Flowable<List<Job_Category>> getObservableCategoriesForPlayer(int player_id);
    List<Job_Applications> getJobApplications(int player_id);
    long insertPlayer(Player player);
    void insertPlayerExpertises(List<Player_Expertise> player_expertises);
    public abstract Flowable<List<Player_Expertise>> getObservablePlayerExpertises(int player_id);
    void updatePlayer(Player player);
    void deletePlayer(Player player);
    public abstract void deleteAllPlayer(List<Player> players);
    void deletePlayer(int player_id);
    public Player getPlayerWithPlayerExpertiseAndJobApplications(int player_id);
}
