package com.project.jhunting1.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.project.jhunting1.Model.Contract_Duration;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.NPC_Trustworth;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.Model.Player_Expertise;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class Job_ApplicationDAO {

    @Query("Select * from Job_Applications where job_application_id=:job_application_id")
    public abstract Flowable<Job_Applications> getObservableJobApplicationById(int job_application_id);

    @Query("Select * from Job_Applications where job_application_id=:job_application_id")
    public abstract Job_Applications getJobApplicationById(int job_application_id);

    @Query("Select * from Job_Applications where player_id =:player_id AND job_id=:job_id")
    public abstract Flowable<Job_Applications> getObservableJobApplicationByPlayerForJob(int player_id, int job_id);

    @Query("Select * from Job_Applications ")
    public abstract List<Job_Applications> getAllJobApplications();

    @Query("Select * from Job_Applications where player_id =:player_id")
    public abstract Flowable<List<Job_Applications>> getObservableJobApplicationByPlayerId(int player_id);

    @Query("Select * from Job_Applications where job_id=:job_id")
    public abstract Flowable<List<Job_Applications>> getObservableJobApplicationByJobId(int job_id);

    @Query("Select * from Job_Applications ja inner join Jobs j on j.job_id == ja.Job_Id where j.npc_id =:npc_id")
    public abstract Flowable<List<Job_Applications>> getObservableJobApplicationByNpc(int npc_id);

    @Query("Select * from Jobs where npc_id=:npc_id")
    public abstract Flowable<List<Jobs>> getObservableJobsByNPC(int npc_id);

    @Query("Select * from job_applications where player_id =:player_id")
    public abstract List<Job_Applications> getJobApplicationByPlayerId(int player_id);

    @Query("Select * from job_applications where job_id=:job_id")
    public abstract List<Job_Applications> getJobApplicationByJobId(int job_id);

    @Query("Select * from Jobs where npc_id=:npc_id")
    public abstract List<Jobs> getJobsByNPC(int npc_id);

    @Insert
    public abstract void insertJobApplication(Job_Applications job_applications);

    @Delete
    public abstract void deleteJobApplication(Job_Applications job_applications);

    @Delete
    public abstract void deleteJob(Jobs job);

    @Delete
    public abstract void deleteAllJobApplications(List<Job_Applications> applications);

    @Update
    public abstract void updateNpc(NPC npc);

    @Update
    public abstract void updateJobApplication(Job_Applications job_applications);

    @Update
    public abstract void updatePlayerExpertise(List<Player_Expertise> player_expertise);

    @Query("Select * from Player where Player_Id=:player_id")
    public abstract Player getPlayer(int player_id);

    @Query("Select * from Player where Player_Id=:player_id")
    public abstract Flowable<Player> getObservablePlayer(int player_id);

    @Query("Select * from Jobs where Job_Id=:job_id")
    public abstract Jobs getJob(int job_id);

    @Query("Select * from Jobs where Job_Id=:job_id")
    public abstract Flowable<Jobs> getObservableJob(int job_id);

    @Query("Select * from NPC where NPC_ID=:npc_id")
    public abstract NPC getNPC(int npc_id);

    @Query("Select * from NPC where NPC_ID=:npc_id")
    public abstract Flowable<NPC> getObservableNPC(int npc_id);

    @Query("Select * from Player_Expertise where Player_Id=:player_id AND category_id =:category_id")
    public abstract Player_Expertise getPlayerExpertiseForFeedback(int player_id, int category_id);

    @Query("Select category from Job_Category where JC_ID =:category_id")
    public abstract String getJobCategory(int category_id);

    @Query("Select * from NPC_Trustworth where NPC_ID =:npc_id")
    public abstract NPC_Trustworth getTrustStatusForNpc (int npc_id);

    @Query("Select * from Contract_Duration where duration_id =:duration_id")
    public abstract Contract_Duration getContractInfo(int duration_id);

    @Update
    public abstract void updateNpcTrustWorth(NPC_Trustworth npc_trustworth);

    public List<Job_Applications> getJobApplicationForPlayers(int player_id){
        List<Job_Applications> job_applications = getJobApplicationByPlayerId(player_id);
        for(int i=0; i<job_applications.size(); i++){
            Job_Applications current_job_application = job_applications.get(i);
            Jobs job = getJob(current_job_application.getJob_id());
            NPC npc = getNPC(job.getNpc_id());
            job.setNpc(npc);
            job_applications.get(i).setPlayer(getPlayer(player_id));
            job_applications.get(i).setJobs(job);
        }
        return job_applications;
    }

    public List<Job_Applications> getJobApplicationForJob(int job_id){
        List<Job_Applications> job_applications = getJobApplicationByJobId(job_id);
        for(int i=0; i<job_applications.size(); i++){
            Job_Applications current_job_application = job_applications.get(i);
            Player player = getPlayer(current_job_application.getPlayer_id());
            NPC npc = getNPC(getJob(job_id).getNpc_id());
            Jobs job = getJob(current_job_application.getJob_id());
            job.setNpc(npc);
            job_applications.get(i).setPlayer(player);
            job_applications.get(i).setJobs(job);
        }
        return job_applications;
    }

    public List<Job_Applications> getJobApplicationForNPC(int npc_id){
        List<Jobs> jobs = getJobsByNPC(npc_id);
        List<Job_Applications> allApplicationForNPC = new ArrayList<Job_Applications>();
        for(int i=0; i< jobs.size(); i++){
            Jobs currentJob = jobs.get(i);
            List<Job_Applications> job_applications = getJobApplicationForJob(currentJob.getJob_id());
            allApplicationForNPC.addAll(job_applications);
        }
        return allApplicationForNPC;
    }

    public void npcGiveFeedback(String grade, int job_application_id){
        List<Player_Expertise> expertiseList = new ArrayList<Player_Expertise>();
        Job_Applications job_applications = getJobApplicationById(job_application_id);
        job_applications.setJobs(getJob(job_applications.getJob_id()));
        job_applications.setPlayer(getPlayer(job_applications.getPlayer_id()));
        Jobs job = getJob(job_applications.getJob_id());
        int jobCategoryId1 = job.getJob_category1();
        int jobCategoryId2 = job.getJob_category2();
        int contractDurationId = job.getContractDurationId();
        Contract_Duration contract_duration = getContractInfo(contractDurationId);
        Player_Expertise player_expertise1 = getPlayerExpertiseForFeedback(job_applications.getPlayer_id(), jobCategoryId1);
        Player_Expertise player_expertise2 = getPlayerExpertiseForFeedback(job_applications.getPlayer_id(), jobCategoryId2);
        int exp_points1 = player_expertise1.getExp_points();
        int exp_points2 = player_expertise2.getExp_points();
        int goodScore = contract_duration.getGoodScore();
        int satisfactoryScore = contract_duration.getSatisfactoryScore();
        int notBadScore = contract_duration.getNotBadScore();
        if (grade.equals("Good")){
            exp_points1 += goodScore;
            exp_points2 += goodScore;
        } else if (grade.equals("Satisfactory")){
            exp_points1 += satisfactoryScore;
            exp_points2 += satisfactoryScore;
        } else {
            exp_points1 += notBadScore;
            exp_points2 += notBadScore;
        }
        player_expertise1.setExp_points(exp_points1);
        player_expertise2.setExp_points(exp_points2);

        if (exp_points1 > 100){
            player_expertise1.setLevel(2);
        }else if(exp_points1 > 300){
            player_expertise1.setLevel(3);
        }else if(exp_points1 > 600){
            player_expertise1.setLevel(4);
        }else if(exp_points1 > 1000){
            player_expertise1.setLevel(5);
        }else{
            player_expertise1.setLevel(player_expertise1.getLevel());
        }

        if (exp_points2 > 100){
            player_expertise2.setLevel(2);
        }else if(exp_points2 > 300){
            player_expertise2.setLevel(3);
        }else if(exp_points2 > 600){
            player_expertise2.setLevel(4);
        }else if(exp_points2 > 1000){
            player_expertise2.setLevel(5);
        }else{
            player_expertise2.setLevel(player_expertise2.getLevel());
        }

        expertiseList.add(player_expertise1);
        expertiseList.add(player_expertise2);
        updatePlayerExpertise(expertiseList);
    }

    public void userGivesFeedback(String grade, int job_application_id){
        Job_Applications job_applications = getJobApplicationById(job_application_id);
        Jobs job = getJob(job_applications.getJob_id());
        Player player = getPlayer(job_applications.getPlayer_id());
        job_applications.setJobs(job);
        job_applications.setPlayer(player);
        NPC_Trustworth npc_trustworth = getTrustStatusForNpc(job.getNpc_id());
        int trust_points = npc_trustworth.getTrust_points();
        if (grade.equals("Good")){
            trust_points += 100;
        } else if (grade.equals("Satisfactory")){
            trust_points += 70;
        } else {
            trust_points += 40;
        }
        npc_trustworth.setTrust_points(trust_points);
        if (trust_points > 100){
            npc_trustworth.setTrust_level(2);
        }else if(trust_points > 300){
            npc_trustworth.setTrust_level(3);
        }else if(trust_points > 600){
            npc_trustworth.setTrust_level(4);
        }else if(trust_points > 1000){
            npc_trustworth.setTrust_level(5);
        }else{
            npc_trustworth.setTrust_level(npc_trustworth.getTrust_level());
        }
        updateNpcTrustWorth(npc_trustworth);
    }
}
