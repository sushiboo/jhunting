package com.project.jhunting1.Local;

import com.project.jhunting1.DAO.Job_ApplicationDAO;
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

public class Job_ApplicationDataSource implements IJob_ApplicationDataSource{

    private Job_ApplicationDAO job_applicationDAO;
    private static Job_ApplicationDataSource mInstance;

    public Job_ApplicationDataSource(Job_ApplicationDAO job_applicationDAO){
        this.job_applicationDAO = job_applicationDAO;
    }

    public static Job_ApplicationDataSource getInstance(Job_ApplicationDAO job_applicationDAO) {
        if(mInstance == null){
            mInstance = new Job_ApplicationDataSource(job_applicationDAO);
        }
        return mInstance;
    }

    @Override
    public Flowable<Job_Applications> getObservableJobApplicationById(int job_application_id) {
        return job_applicationDAO.getObservableJobApplicationById(job_application_id);
    }

    @Override
    public Job_Applications getJobApplicationById(int job_application_id) {
        return job_applicationDAO.getJobApplicationById(job_application_id);
    }

    @Override
    public Flowable<Job_Applications> getObservableJobApplicationByPlayerForJob(int player_id, int job_id) {
        return job_applicationDAO.getObservableJobApplicationByPlayerForJob(player_id, job_id);
    }

    @Override
    public List<Job_Applications> getAllJobApplications() {
        return job_applicationDAO.getAllJobApplications();
    }

    @Override
    public Flowable<List<Job_Applications>> getObservableJobApplicationByPlayerId(int player_id) {
        return job_applicationDAO.getObservableJobApplicationByPlayerId(player_id);
    }

    @Override
    public Flowable<List<Job_Applications>> getObservableJobApplicationByJobId(int job_id) {
        return job_applicationDAO.getObservableJobApplicationByJobId(job_id);
    }

    @Override
    public Flowable<List<Job_Applications>> getObservableJobApplicationByNpc(int npc_id) {
        return job_applicationDAO.getObservableJobApplicationByNpc(npc_id);
    }

    @Override
    public Flowable<List<Jobs>> getObservableJobsByNPC(int npc_id) {
        return job_applicationDAO.getObservableJobsByNPC(npc_id);
    }

    @Override
    public List<Job_Applications> getJobApplicationByPlayerId(int player_id) {
        return job_applicationDAO.getJobApplicationByPlayerId(player_id);
    }

    @Override
    public List<Job_Applications> getJobApplicationByJobId(int job_id) {
        return job_applicationDAO.getJobApplicationByJobId(job_id);
    }

    @Override
    public List<Jobs> getJobsByNPC(int npc_id) {
        return job_applicationDAO.getJobsByNPC(npc_id);
    }

    @Override
    public void insertJobApplication(Job_Applications job_applications) {
        job_applicationDAO.insertJobApplication(job_applications);
    }

    @Override
    public void deleteJobApplication(Job_Applications job_applications) {
        job_applicationDAO.deleteJobApplication(job_applications);
    }

    @Override
    public void deleteJob(Jobs job) {
        job_applicationDAO.deleteJob(job);
    }

    @Override
    public void deleteAllJobApplications(List<Job_Applications> applications) {
        job_applicationDAO.deleteAllJobApplications(applications);
    }

    @Override
    public void updateNpc(NPC npc) {
        job_applicationDAO.updateNpc(npc);
    }

    @Override
    public void updateJobApplication(Job_Applications job_applications) {
        job_applicationDAO.updateJobApplication(job_applications);
    }

    @Override
    public void updatePlayerExpertise(List<Player_Expertise> player_expertises) {
        job_applicationDAO.updatePlayerExpertise(player_expertises);
    }

    @Override
    public Player getPlayer(int player_id) {
        return job_applicationDAO.getPlayer(player_id);
    }

    @Override
    public Flowable<Player> getObservablePlayer(int player_id) {
        return job_applicationDAO.getObservablePlayer(player_id);
    }

    @Override
    public Jobs getJob(int job_id) {
        return job_applicationDAO.getJob(job_id);
    }

    @Override
    public Flowable<Jobs> getObservableJob(int job_id) {
        return job_applicationDAO.getObservableJob(job_id);
    }

    @Override
    public NPC getNPC(int npc_id) {
        return job_applicationDAO.getNPC(npc_id);
    }

    @Override
    public Flowable<NPC> getObservableNPC(int npc_id) {
        return job_applicationDAO.getObservableNPC(npc_id);
    }

    @Override
    public Player_Expertise getPlayerExpertiseForFeedback(int player_id, int categoryId) {
        return job_applicationDAO.getPlayerExpertiseForFeedback(player_id, categoryId);
    }

    @Override
    public String getJobCategory(int category_id) {
        return job_applicationDAO.getJobCategory(category_id);
    }

    @Override
    public NPC_Trustworth getTrustStatusForNpc(int npc_id) {
        return job_applicationDAO.getTrustStatusForNpc(npc_id);
    }

    @Override
    public Contract_Duration getContractInfo(int duration_id) {
        return job_applicationDAO.getContractInfo(duration_id);
    }

    @Override
    public void updateNpcTrustWorth(NPC_Trustworth npc_trustworth) {
        job_applicationDAO.updateNpcTrustWorth(npc_trustworth);
    }

    @Override
    public List<Job_Applications> getJobApplicationForPlayers(int player_id) {
        return job_applicationDAO.getJobApplicationForPlayers(player_id);
    }

    @Override
    public List<Job_Applications> getJobApplicationForJob(int job_id) {
        return job_applicationDAO.getJobApplicationForJob(job_id);
    }

    @Override
    public List<Job_Applications> getJobApplicationForNPC(int npc_id) {
        return job_applicationDAO.getJobApplicationForNPC(npc_id);
    }

    @Override
    public void npcGiveFeedback(String grade, int job_application_id) {
        job_applicationDAO.npcGiveFeedback(grade, job_application_id);
    }

    @Override
    public void userGivesFeedback(String grade, int job_application_id) {
        job_applicationDAO.userGivesFeedback(grade, job_application_id);
    }
}
