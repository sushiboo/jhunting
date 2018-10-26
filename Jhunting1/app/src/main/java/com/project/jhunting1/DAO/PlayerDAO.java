package com.project.jhunting1.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class PlayerDAO {

    @Query("SELECT * from Player where Player_Id=:player_id")
    public abstract Flowable<Player> getObservablePlayerById(int player_id);

    @Query("SELECT * from Player where Player_Id=:player_id")
    public abstract Player getPlayerById(int player_id);

    @Query("Select * from Player")
    public abstract Flowable<List<Player>> getObservableAllPlayers();

    @Query("Select * from Player")
    public abstract List<Player> getAllPlayers();

    @Query("Select * from Player where email=:email AND password=:password")
    public abstract Player getPlayerUernamePassword(String email, String password);

    @Query("Select * from Player_Expertise where Player_Id=:player_id")
    public abstract List<Player_Expertise> getPlayerExpertises(int player_id);

    @Query("Select * from Player p inner join Player_Expertise pe on p.Player_Id == pe.Player_Id where pe.category_id=:category_id1 or pe.category_id=:category_id2")
    public abstract Flowable<List<Player>> getObservablePlayersForCategory(int category_id1, int category_id2);

    @Query("Select * from Job_Category jc inner join player_expertise pe on jc.JC_ID == pe.category_id where pe.Player_Id =:player_id")
    public abstract Flowable<List<Job_Category>> getObservableCategoriesForPlayer(int player_id);

    @Query("Select * from Player_Expertise where Player_Id=:player_id")
    public abstract Flowable<List<Player_Expertise>> getObservablePlayerExpertises(int player_id);

    @Query("Select * from JOB_APPLICATIONS where player_id=:player_id")
    public abstract List<Job_Applications> getJobApplications(int player_id);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertPlayer(Player player);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPlayerExpertises(List<Player_Expertise> player_expertises);

    @Update
    public abstract void updatePlayer(Player player);

    @Delete
    public abstract void deletePlayer(Player player);

    @Delete
    public abstract void deleteAllPlayer(List<Player> players);

    @Query("DELETE FROM Player where Player_Id=:player_id")
    public abstract void deletePlayer(int player_id);

    public Player getPlayerWithPlayerExpertiseAndJobApplications(int player_id){
        Player player = getPlayerById(player_id);
        List<Job_Applications> job_applications = getJobApplications(player_id);
        List<Player_Expertise> player_expertises = getPlayerExpertises(player_id);
        player.setPlayer_expertises(player_expertises);
        player.setPlayer_Job_Applications(job_applications);
        return player;
    }


    //Turning in - Just change the Status field to confirming from npc/ confirming from player before it changes to completed




}
