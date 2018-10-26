package com.project.jhunting1.Database;

import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

public interface IJob_ApplicationDataSource {
    Flowable<Job_Applications> getObservableJobApplicationById(int job_application_id);
    Job_Applications getJobApplicationById(int job_application_id);
    public abstract Flowable<Job_Applications> getObservableJobApplicationByPlayerForJob(int player_id, int job_id);
    public abstract List<Job_Applications> getAllJobApplications();
    Flowable<List<Job_Applications>> getObservableJobApplicationByPlayerId(int player_id);
    Flowable<List<Job_Applications>> getObservableJobApplicationByJobId(int job_id);
    public abstract Flowable<List<Job_Applications>> getObservableJobApplicationByNpc(int npc_id);
    Flowable<List<Jobs>> getObservableJobsByNPC(int npc_id);
    List<Job_Applications> getJobApplicationByPlayerId(int player_id);
    List<Job_Applications> getJobApplicationByJobId(int job_id);
    List<Jobs> getJobsByNPC(int npc_id);
    void insertJobApplication(Job_Applications job_applications);
    void deleteJobApplication(Job_Applications job_applications);
    void deleteJob(Jobs job);
    public abstract void deleteAllJobApplications(List<Job_Applications> applications);
    void updateNpc(NPC npc);
    public abstract void updateJobApplication(Job_Applications job_applications);
    void updatePlayerExpertise(List<Player_Expertise> player_expertise);
    public abstract Player getPlayer(int player_id);
    public abstract Flowable<Player> getObservablePlayer(int player_id);
    public abstract Jobs getJob(int job_id);
    public abstract Flowable<Jobs> getObservableJob(int job_id);
    public abstract NPC getNPC(int npc_id);
    public abstract Flowable<NPC> getObservableNPC(int npc_id);
    Player_Expertise getPlayerExpertiseForFeedback(int player_id, int categoryId);
    String getJobCategory(int category_id);
    NPC_Trustworth getTrustStatusForNpc (int npc_id);
    Contract_Duration getContractInfo(int duration_id);
    void updateNpcTrustWorth(NPC_Trustworth npc_trustworth);
    List<Job_Applications> getJobApplicationForPlayers(int player_id);
    List<Job_Applications> getJobApplicationForJob(int job_id);
    List<Job_Applications> getJobApplicationForNPC(int npc_id);
    void npcGiveFeedback(String grade, int job_application_id);
    void userGivesFeedback(String grade, int job_application_id);
}
