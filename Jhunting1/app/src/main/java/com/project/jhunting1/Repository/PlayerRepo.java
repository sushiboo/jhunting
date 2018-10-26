package com.project.jhunting1.Repository;

import com.project.jhunting1.Database.IPlayerDataSource;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

public class PlayerRepo implements IPlayerDataSource{

    private IPlayerDataSource localDataSource;
    private static PlayerRepo mInstance;

    public PlayerRepo(IPlayerDataSource localDataSource){
        this.localDataSource = localDataSource;
    }

    public static PlayerRepo getInstance(IPlayerDataSource localDataSource) {
        if(mInstance == null){
            mInstance = new PlayerRepo(localDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<Player> getObservablePlayerById(int player_id) {
        return localDataSource.getObservablePlayerById(player_id);
    }

    @Override
    public Player getPlayerById(int player_id) {
        return localDataSource.getPlayerById(player_id);
    }

    @Override
    public Flowable<List<Player>> getObservableAllPlayers() {
        return localDataSource.getObservableAllPlayers();
    }

    @Override
    public List<Player> getAllPlayers() {
        return localDataSource.getAllPlayers();
    }

    @Override
    public Player getPlayerUernamePassword(String email, String password) {
        return localDataSource.getPlayerUernamePassword(email,password);
    }

    @Override
    public List<Player_Expertise> getPlayerExpertises(int player_id) {
        return localDataSource.getPlayerExpertises(player_id);
    }

    @Override
    public Flowable<List<Player>> getObservablePlayersForCategory(int category_id1, int category_id2) {
        return localDataSource.getObservablePlayersForCategory(category_id1, category_id2);
    }

    @Override
    public Flowable<List<Job_Category>> getObservableCategoriesForPlayer(int player_id) {
        return localDataSource.getObservableCategoriesForPlayer(player_id);
    }

    @Override
    public List<Job_Applications> getJobApplications(int player_id) {
        return localDataSource.getJobApplications(player_id);
    }

    @Override
    public long insertPlayer(Player player) {
        return localDataSource.insertPlayer(player);
    }

    @Override
    public void insertPlayerExpertises(List<Player_Expertise> player_expertises) {
        localDataSource.insertPlayerExpertises(player_expertises);
    }

    @Override
    public Flowable<List<Player_Expertise>> getObservablePlayerExpertises(int player_id) {
        return localDataSource.getObservablePlayerExpertises(player_id);
    }

    @Override
    public void updatePlayer(Player player) {
        localDataSource.updatePlayer(player);
    }

    @Override
    public void deletePlayer(Player player) {
        localDataSource.deletePlayer(player);
    }

    @Override
    public void deleteAllPlayer(List<Player> players) {
        localDataSource.deleteAllPlayer(players);
    }

    @Override
    public void deletePlayer(int player_id) {
        localDataSource.deletePlayer(player_id);
    }

    @Override
    public Player getPlayerWithPlayerExpertiseAndJobApplications(int player_id) {
        return localDataSource.getPlayerWithPlayerExpertiseAndJobApplications(player_id);
    }


}
