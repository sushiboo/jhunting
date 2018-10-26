package com.project.jhunting1.Local;

import com.project.jhunting1.DAO.NPC_DAO;
import com.project.jhunting1.Database.INPCDataSource;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;

import java.util.List;

import io.reactivex.Flowable;

public class NPCDataSource implements INPCDataSource{

    private NPC_DAO npc_dao;
    private static NPCDataSource mInstance;

    public NPCDataSource(NPC_DAO npc_dao){
        this.npc_dao = npc_dao;
    }

    public static NPCDataSource getInstance(NPC_DAO npc_dao) {
        if(mInstance == null){
            mInstance = new NPCDataSource(npc_dao);
        }
        return mInstance;
    }

    @Override
    public Flowable<NPC> getObservableNPCById(int npc_id) {
        return npc_dao.getObservableNPCById(npc_id);
    }

    @Override
    public NPC getNPCById(int npc_id) {
        return npc_dao.getNPCById(npc_id);
    }

    @Override
    public Flowable<List<NPC>> getObservableAllNpc() {
        return npc_dao.getObservableAllNpc();
    }

    @Override
    public List<NPC> getAllNpc() {
        return npc_dao.getAllNpc();
    }

    @Override
    public NPC getNPCUernamePassword(String email, String password) {
        return npc_dao.getNPCUernamePassword(email, password);
    }

    @Override
    public List<Jobs> getJobsForNpc(int npc_id) {
        return npc_dao.getJobsForNpc(npc_id);
    }

    @Override
    public Jobs getJob(int job_id) {
        return npc_dao.getJob(job_id);
    }

    @Override
    public void insertNpcTrustWorth(NPC_Trustworth npc_trustworth) {
        npc_dao.insertNpcTrustWorth(npc_trustworth);
    }

    @Override
    public long insertNPC(NPC npc) {
        return npc_dao.insertNPC(npc);
    }

    @Override
    public List<NPC_Trustworth> getAllNpcTrustworth() {
        return npc_dao.getAllNpcTrustworth();
    }

    @Override
    public NPC_Trustworth getNpcTrustworth(int npcId) {
        return npc_dao.getNpcTrustworth(npcId);
    }

    @Override
    public Flowable<NPC_Trustworth> getObservableNpcTrustworth(int npcId) {
        return npc_dao.getObservableNpcTrustworth(npcId);
    }

    @Override
    public void updateNPC(NPC npc) {
        npc_dao.updateNPC(npc);
    }

    @Override
    public void deleteNPC(NPC npc) {
        npc_dao.deleteNPC(npc);
    }

    @Override
    public void deleteNPC(int npc_id) {
        npc_dao.deleteNPC(npc_id);
    }

    @Override
    public NPC getNpcWithJobs(int npc_id) {
        return npc_dao.getNpcWithJobs(npc_id);
    }
}
