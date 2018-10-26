package com.project.jhunting1.Database;

import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;

import java.util.List;

import io.reactivex.Flowable;

public interface INPCDataSource {
    Flowable<NPC> getObservableNPCById(int npc_id);
    NPC getNPCById(int npc_id);
    Flowable<List<NPC>> getObservableAllNpc();
    public abstract List<NPC> getAllNpc();
    NPC getNPCUernamePassword(String email, String password);
    List<Jobs> getJobsForNpc(int npc_id);
    Jobs getJob(int job_id);
    public abstract void insertNpcTrustWorth(NPC_Trustworth npc_trustworth);
    long insertNPC(NPC npc);
    public abstract List<NPC_Trustworth> getAllNpcTrustworth();
    public abstract NPC_Trustworth getNpcTrustworth(int npcId);
    public abstract Flowable<NPC_Trustworth> getObservableNpcTrustworth(int npcId);
    void updateNPC(NPC npc);
    void deleteNPC(NPC npc);
    void deleteNPC(int npc_id);
    NPC getNpcWithJobs(int npc_id);
}
