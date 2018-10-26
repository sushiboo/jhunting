package com.project.jhunting1.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.project.jhunting1.Model.Job_Category;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class NPC_DAO {

    @Query("SELECT * from NPC where NPC_ID=:npc_id")
    public abstract Flowable<NPC> getObservableNPCById(int npc_id);

    @Query("SELECT * from NPC where NPC_ID=:npc_id")
    public abstract NPC getNPCById(int npc_id);

    @Query("Select * from NPC")
    public abstract Flowable<List<NPC>> getObservableAllNpc();

    @Query("Select * from NPC")
    public abstract List<NPC> getAllNpc();

    @Query("Select * from NPC where email=:email AND password=:password")
    public abstract NPC getNPCUernamePassword(String email, String password);


    @Query("Select * from Jobs where npc_id =:npc_id")
    public abstract List<Jobs> getJobsForNpc(int npc_id);

    @Query("Select * from Jobs where Job_Id =:job_id")
    public abstract Jobs getJob(int job_id);

    @Insert
    public abstract void insertNpcTrustWorth(NPC_Trustworth npc_trustworth);

    @Query("Select * from NPC_Trustworth")
    public abstract List<NPC_Trustworth> getAllNpcTrustworth();

    @Query("Select * from NPC_Trustworth where NPC_ID =:npcId")
    public abstract NPC_Trustworth getNpcTrustworth(int npcId);

    @Query("Select * from NPC_Trustworth where NPC_ID =:npcId")
    public abstract Flowable<NPC_Trustworth> getObservableNpcTrustworth(int npcId);


    @Insert
    public abstract long insertNPC(NPC npc);

    @Update
    public abstract void updateNPC(NPC npc);

    @Delete
    public abstract void deleteNPC(NPC npc);

    @Query("DELETE FROM NPC where NPC_ID=:npc_id")
    public abstract void deleteNPC(int npc_id);

    public NPC getNpcWithJobs(int npc_id){
        NPC npc = getNPCById(npc_id);
        List<Jobs> allJobs = getJobsForNpc(npc_id);
        for(int i = 0; i < allJobs.size(); i++){
            allJobs.get(i).setNpc(npc);
        }
        npc.setJobsList(allJobs);
        return npc;
    }

}
