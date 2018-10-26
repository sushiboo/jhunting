package com.project.jhunting1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {@ForeignKey(entity = Player.class, parentColumns = "Player_Id", childColumns = "player_id"),
        @ForeignKey(entity = Jobs.class, parentColumns = "Job_Id", childColumns = "job_id", onDelete = CASCADE)})
public class Job_Applications {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "job_application_id")
    private int job_application_id;

    @ColumnInfo(name = "job_id")
    private int job_id;

    @ColumnInfo(name = "player_id")
    private int player_id;

    @ColumnInfo(name = "status")
    private String status;


    @ColumnInfo(name="NPC_Feedback")
    private String npc_feedback;

    @ColumnInfo(name="Player_Feedback")
    private String player_feedback;


    @Ignore
    private Player player;

    @Ignore
    private Jobs job;

    public Job_Applications() {
    }

    @Ignore
    public Job_Applications(int job_application_id) {
        this.job_application_id = job_application_id;
    }

    @Ignore
    public Job_Applications(int job_id, int player_id, String status) {
        this.job_id = job_id;
        this.player_id = player_id;
        this.status = status;
    }

    public int getJob_application_id() {
        return job_application_id;
    }

    public void setJob_application_id(int job_application_id) {
        this.job_application_id = job_application_id;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public int getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Jobs getJobs() {
        return job;
    }

    public void setJobs(Jobs jobs) {
        this.job = jobs;
    }

    public String getNpc_feedback() {
        return npc_feedback;
    }

    public void setNpc_feedback(String npc_feedback) {
        this.npc_feedback = npc_feedback;
    }

    public String getPlayer_feedback() {
        return player_feedback;
    }

    public void setPlayer_feedback(String player_feedback) {
        this.player_feedback = player_feedback;
    }

    public Jobs getJob() {
        return job;
    }

    public void setJob(Jobs job) {
        this.job = job;
    }

    public String toString(){
        if (player != null && job !=null) {
            return new StringBuilder("Player: " + player.getName()).append("\n Job: ").append(job.getJob_title()).append("\n Status: " + status + "\n").toString();
        }else {
            return status.toString();
        }
       // return new StringBuilder(status).toString();
    }
}
