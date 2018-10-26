package com.project.jhunting1.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.project.jhunting1.DAO.Admin_DAO;
import com.project.jhunting1.DAO.JobDAO;
import com.project.jhunting1.DAO.Job_ApplicationDAO;
import com.project.jhunting1.DAO.NPC_DAO;
import com.project.jhunting1.DAO.PlayerDAO;
import com.project.jhunting1.DAO.Player_ExpertiseDAO;
import com.project.jhunting1.Model.Admin;
import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;

@Database(entities = {Admin.class, Contract_Duration.class, Player.class, NPC.class, NPC_Trustworth.class,
        Job_Category.class, Jobs.class, Job_Applications.class, Player_Expertise.class}, version = 9)
public abstract class JhuntingDB extends RoomDatabase {

    private static JhuntingDB INSTANCE;

    public static final String DATABASE_NAME = "JhuntingDB";

    public abstract JobDAO jobDAO();
    public abstract NPC_DAO npc_dao();
    public abstract Player_ExpertiseDAO player_expertiseDAO();
    public abstract PlayerDAO playerDAO();
    public abstract Job_ApplicationDAO job_applicationDAO();

    public static JhuntingDB getINSTANCE(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), JhuntingDB.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
