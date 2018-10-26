package com.project.jhunting1.Local;

import com.project.jhunting1.DAO.PlayerDAO;
import com.project.jhunting1.Database.IPlayerDataSource;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

public class PlayerDataSource implements IPlayerDataSource{

    private PlayerDAO playerDAO;
    private static PlayerDataSource mInstance;

    public PlayerDataSource(PlayerDAO playerDAO){
        this.playerDAO = playerDAO;
    }

    public static PlayerDataSource getInstance(PlayerDAO playerDAO) {
        if (mInstance == null){
            mInstance = new PlayerDataSource(playerDAO);
        }
        return mInstance;
    }

    @Override
    public Flowable<Player> getObservablePlayerById(int player_id) {
        return playerDAO.getObservablePlayerById(player_id);
    }

    @Override
    public Player getPlayerById(int player_id) {
        return playerDAO.getPlayerById(player_id);
    }

    @Override
    public Flowable<List<Player>> getObservableAllPlayers() {
        return playerDAO.getObservableAllPlayers();
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerDAO.getAllPlayers();
    }

    @Override
    public Player getPlayerUernamePassword(String email, String password) {
        return playerDAO.getPlayerUernamePassword(email, password);
    }

    @Override
    public List<Player_Expertise> getPlayerExpertises(int player_id) {
        return playerDAO.getPlayerExpertises(player_id);
    }

    @Override
    public Flowable<List<Player>> getObservablePlayersForCategory(int category_id1, int category_id2) {
        return playerDAO.getObservablePlayersForCategory(category_id1, category_id2);
    }

    @Override
    public Flowable<List<Job_Category>> getObservableCategoriesForPlayer(int player_id) {
        return playerDAO.getObservableCategoriesForPlayer(player_id);
    }

    @Override
    public List<Job_Applications> getJobApplications(int player_id) {
        return playerDAO.getJobApplications(player_id);
    }

    @Override
    public long insertPlayer(Player player) {
        return playerDAO.insertPlayer(player);
    }

    @Override
    public void insertPlayerExpertises(List<Player_Expertise> player_expertises) {
        playerDAO.insertPlayerExpertises(player_expertises);
    }

    @Override
    public Flowable<List<Player_Expertise>> getObservablePlayerExpertises(int player_id) {
        return playerDAO.getObservablePlayerExpertises(player_id);
    }

    @Override
    public void updatePlayer(Player player) {
        playerDAO.updatePlayer(player);
    }

    @Override
    public void deletePlayer(Player player) {
        playerDAO.deletePlayer(player);
    }

    @Override
    public void deleteAllPlayer(List<Player> players) {
        playerDAO.deleteAllPlayer(players);
    }

    @Override
    public void deletePlayer(int player_id) {
        playerDAO.deletePlayer(player_id);
    }

    public Player getPlayerWithPlayerExpertiseAndJobApplications(int player_id) {
        return playerDAO.getPlayerWithPlayerExpertiseAndJobApplications(player_id);
    }
}
