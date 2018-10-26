package com.project.jhunting1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "Jobs", foreignKeys = {@ForeignKey(entity = NPC.class, parentColumns = "NPC_ID", childColumns = "npc_id", onDelete = CASCADE)
        , @ForeignKey(entity = Contract_Duration.class, parentColumns = "duration_id", childColumns = "contract_duration_id")})
public class Jobs {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Job_Id")
    private int job_id;

    @ColumnInfo(name = "npc_id")
    private int npc_id;

    @ColumnInfo(name = "job_title")
    private String job_title;

    @ColumnInfo(name = "job_description")
    private String job_description;

    @ColumnInfo(name = "job_category1")
    private int job_category1;

    @ColumnInfo(name = "job_category2")
    private int job_category2;

    @ColumnInfo(name = "contract_duration_id")
    private int contractDurationId;

    @Ignore
    private Job_Category object_job_category1;

    @Ignore
    private Job_Category object_job_category2;

    @Ignore
    private Contract_Duration object_contract_duration;

    @Ignore
    private NPC npc;

    public Jobs() {
    }

    @Ignore
    public Jobs(int job_id) {
        this.job_id = job_id;
    }

    @Ignore
    public Jobs(int npc_id, String job_title, String job_description, int contractDurationId, int job_category1, int job_category2) {
        this.npc_id = npc_id;
        this.job_title=job_title;
        this.job_description = job_description;
        this.contractDurationId = contractDurationId;
        this.job_category1 = job_category1;
        this.job_category2 = job_category2;
    }

    @Ignore
    public Jobs(String job_title) {
        this.job_title=job_title;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public int getNpc_id() {
        return npc_id;
    }

    public void setNpc_id(int npc_id) {
        this.npc_id = npc_id;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public String getJob_description() {
        return job_description;
    }

    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }

    public int getJob_category1() {
        return job_category1;
    }

    public void setJob_category1(int job_category1) {
        this.job_category1 = job_category1;
    }

    public int getJob_category2() {
        return job_category2;
    }

    public void setJob_category2(int job_category2) {
        this.job_category2 = job_category2;
    }

    public Job_Category getObject_job_category1() {
        return object_job_category1;
    }

    public void setObject_job_category1(Job_Category object_job_category1) {
        this.object_job_category1 = object_job_category1;
    }

    public Job_Category getObject_job_category2() {
        return object_job_category2;
    }

    public void setObject_job_category2(Job_Category object_job_category2) {
        this.object_job_category2 = object_job_category2;
    }

    public int getContractDurationId() {
        return contractDurationId;
    }

    public void setContractDurationId(int contractDurationId) {
        this.contractDurationId = contractDurationId;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public Contract_Duration getObject_contract_duration() {
        return object_contract_duration;
    }

    public void setObject_contract_duration(Contract_Duration object_contract_duration) {
        this.object_contract_duration = object_contract_duration;
    }

    @Override
    public String toString(){
        return new StringBuilder(job_title.toString()).append("\n").toString();
    }

}
