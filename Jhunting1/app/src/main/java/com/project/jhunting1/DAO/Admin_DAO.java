package com.project.jhunting1.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.project.jhunting1.Model.Admin;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.Player;

import io.reactivex.Flowable;

@Dao
public interface Admin_DAO {

    @Query("Select * from Admin where username=:username AND password=:password")
    Flowable<NPC> getAdminUsernamePassword(String username, String password);

    @Insert
    void addNewAdmin(Admin admin);

    @Delete
    void deleteNPC(NPC npc);

    @Update
    void updateNPC(NPC npc);

    @Delete
    void deletePlayer(Player player);

    @Update
    void updatePlayer(Player player);

    @Update
    void updateAdmin(Admin admin);

}
