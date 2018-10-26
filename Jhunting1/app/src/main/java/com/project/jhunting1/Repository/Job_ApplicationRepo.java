package com.project.jhunting1.Repository;

import com.project.jhunting1.Database.IJob_ApplicationDataSource;
import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.List;

import io.reactivex.Flowable;

public class Job_ApplicationRepo implements IJob_ApplicationDataSource {

    private IJob_ApplicationDataSource localDataSource;
    private static Job_ApplicationRepo mInstance;

    public Job_ApplicationRepo(IJob_ApplicationDataSource localDataSource){
        this.localDataSource = localDataSource;
    }

    public static Job_ApplicationRepo getInstance(IJob_ApplicationDataSource localDataSource) {
        if(mInstance == null){
            mInstance = new Job_ApplicationRepo(localDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<Job_Applications> getObservableJobApplicationById(int job_application_id) {
        return localDataSource.getObservableJobApplicationById(job_application_id);
    }

    @Override
    public Job_Applications getJobApplicationById(int job_application_id) {
        return localDataSource.getJobApplicationById(job_application_id);
    }

    @Override
    public Flowable<Job_Applications> getObservableJobApplicationByPlayerForJob(int player_id, int job_id) {
        return localDataSource.getObservableJobApplicationByPlayerForJob(player_id, job_id);
    }

    @Override
    public List<Job_Applications> getAllJobApplications() {
        return localDataSource.getAllJobApplications();
    }

    @Override
    public Flowable<List<Job_Applications>> getObservableJobApplicationByPlayerId(int player_id) {
        return localDataSource.getObservableJobApplicationByPlayerId(player_id);
    }

    @Override
    public Flowable<List<Job_Applications>> getObservableJobApplicationByJobId(int job_id) {
        return localDataSource.getObservableJobApplicationByJobId(job_id);
    }

    @Override
    public Flowable<List<Job_Applications>> getObservableJobApplicationByNpc(int npc_id) {
        return localDataSource.getObservableJobApplicationByNpc(npc_id);
    }

    @Override
    public Flowable<List<Jobs>> getObservableJobsByNPC(int npc_id) {
        return localDataSource.getObservableJobsByNPC(npc_id);
    }

    @Override
    public List<Job_Applications> getJobApplicationByPlayerId(int player_id) {
        return localDataSource.getJobApplicationByPlayerId(player_id);
    }

    @Override
    public List<Job_Applications> getJobApplicationByJobId(int job_id) {
        return localDataSource.getJobApplicationByJobId(job_id);
    }

    @Override
    public List<Jobs> getJobsByNPC(int npc_id) {
        return localDataSource.getJobsByNPC(npc_id);
    }

    @Override
    public void insertJobApplication(Job_Applications job_applications) {
        localDataSource.insertJobApplication(job_applications);
    }

    @Override
    public void deleteJobApplication(Job_Applications job_applications) {
        localDataSource.deleteJobApplication(job_applications);
    }

    @Override
    public void deleteJob(Jobs job) {
        localDataSource.deleteJob(job);
    }

    @Override
    public void deleteAllJobApplications(List<Job_Applications> applications) {
        localDataSource.deleteAllJobApplications(applications);
    }

    @Override
    public void updateNpc(NPC npc) {
        localDataSource.updateNpc(npc);
    }

    @Override
    public void updateJobApplication(Job_Applications job_applications) {
        localDataSource.updateJobApplication(job_applications);
    }

    @Override
    public void updatePlayerExpertise(List<Player_Expertise> player_expertises) {
        localDataSource.updatePlayerExpertise(player_expertises);
    }

    @Override
    public Player getPlayer(int player_id) {
        return localDataSource.getPlayer(player_id);
    }

    @Override
    public Flowable<Player> getObservablePlayer(int player_id) {
        return localDataSource.getObservablePlayer(player_id);
    }

    @Override
    public Jobs getJob(int job_id) {
        return localDataSource.getJob(job_id);
    }

    @Override
    public Flowable<Jobs> getObservableJob(int job_id) {
        return localDataSource.getObservableJob(job_id);
    }

    @Override
    public NPC getNPC(int npc_id) {
        return localDataSource.getNPC(npc_id);
    }

    @Override
    public Flowable<NPC> getObservableNPC(int npc_id) {
        return localDataSource.getObservableNPC(npc_id);
    }

    @Override
    public Player_Expertise getPlayerExpertiseForFeedback(int player_id, int categoryId) {
        return localDataSource.getPlayerExpertiseForFeedback(player_id, categoryId);
    }

    @Override
    public String getJobCategory(int category_id) {
        return localDataSource.getJobCategory(category_id);
    }

    @Override
    public NPC_Trustworth getTrustStatusForNpc(int npc_id) {
        return localDataSource.getTrustStatusForNpc(npc_id);
    }

    @Override
    public Contract_Duration getContractInfo(int duration_id) {
        return localDataSource.getContractInfo(duration_id);
    }

    @Override
    public void updateNpcTrustWorth(NPC_Trustworth npc_trustworth) {
        localDataSource.updateNpcTrustWorth(npc_trustworth);
    }

    @Override
    public List<Job_Applications> getJobApplicationForPlayers(int player_id) {
        return localDataSource.getJobApplicationForPlayers(player_id);
    }

    @Override
    public List<Job_Applications> getJobApplicationForJob(int job_id) {
        return localDataSource.getJobApplicationForJob(job_id);
    }

    @Override
    public List<Job_Applications> getJobApplicationForNPC(int npc_id) {
        return localDataSource.getJobApplicationForNPC(npc_id);
    }

    @Override
    public void npcGiveFeedback(String grade, int job_application_id) {
        localDataSource.npcGiveFeedback(grade, job_application_id);
    }

    @Override
    public void userGivesFeedback(String grade, int job_application_id) {
        localDataSource.userGivesFeedback(grade, job_application_id);
    }
}
