package com.project.jhunting1.Repository;


import com.project.jhunting1.Database.INPCDataSource;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;

import java.util.List;

import io.reactivex.Flowable;

public class NPCRepo implements INPCDataSource{

    private INPCDataSource localDataSource;
    private static NPCRepo mInstance;

    public NPCRepo(INPCDataSource localDatasource){
        this.localDataSource = localDatasource;
    }

    public static NPCRepo getInstance(INPCDataSource localDataSource) {
        if (mInstance == null){
            mInstance = new NPCRepo(localDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<NPC> getObservableNPCById(int npc_id) {
        return localDataSource.getObservableNPCById(npc_id);
    }

    @Override
    public NPC getNPCById(int npc_id) {
        return localDataSource.getNPCById(npc_id);
    }

    @Override
    public Flowable<List<NPC>> getObservableAllNpc() {
        return localDataSource.getObservableAllNpc();
    }

    @Override
    public List<NPC> getAllNpc() {
        return localDataSource.getAllNpc();
    }

    @Override
    public NPC getNPCUernamePassword(String email, String password) {
        return localDataSource.getNPCUernamePassword(email,password);
    }

    @Override
    public List<Jobs> getJobsForNpc(int npc_id) {
        return localDataSource.getJobsForNpc(npc_id);
    }

    @Override
    public Jobs getJob(int job_id) {
        return localDataSource.getJob(job_id);
    }

    @Override
    public void insertNpcTrustWorth(NPC_Trustworth npc_trustworth) {
        localDataSource.insertNpcTrustWorth(npc_trustworth);
    }

    @Override
    public long insertNPC(NPC npc) {
        return localDataSource.insertNPC(npc);
    }

    @Override
    public List<NPC_Trustworth> getAllNpcTrustworth() {
        return localDataSource.getAllNpcTrustworth();
    }

    @Override
    public NPC_Trustworth getNpcTrustworth(int npcId) {
        return localDataSource.getNpcTrustworth(npcId);
    }

    @Override
    public Flowable<NPC_Trustworth> getObservableNpcTrustworth(int npcId) {
        return localDataSource.getObservableNpcTrustworth(npcId);
    }

    @Override
    public void updateNPC(NPC npc) {
        localDataSource.updateNPC(npc);
    }

    @Override
    public void deleteNPC(NPC npc) {
        localDataSource.deleteNPC(npc);
    }

    @Override
    public void deleteNPC(int npc_id) {
        localDataSource.deleteNPC(npc_id);
    }

    @Override
    public NPC getNpcWithJobs(int npc_id) {
        return localDataSource.getNpcWithJobs(npc_id);
    }
}
