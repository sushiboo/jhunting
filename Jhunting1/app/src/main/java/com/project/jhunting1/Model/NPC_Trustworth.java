package com.project.jhunting1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "NPC_Trustworth",
        foreignKeys = @ForeignKey(entity = NPC.class, parentColumns = "NPC_ID", childColumns = "NPC_ID", onDelete = CASCADE))
public class NPC_Trustworth {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "NPC_ID")
    private int npc_id;

    @ColumnInfo(name = "trust_points")
    private int trust_points;

    @ColumnInfo(name = "trust_level")
    private int trust_level;

    public NPC_Trustworth(){}

    @Ignore
    public NPC_Trustworth(int npc_id, int trust_points, int trust_level) {
        this.npc_id = npc_id;
        this.trust_points = trust_points;
        this.trust_level = trust_level;
    }

    public int getNpc_id() {
        return npc_id;
    }

    public void setNpc_id(int npc_id) {
        this.npc_id = npc_id;
    }

    public int getTrust_points() {
        return trust_points;
    }

    public void setTrust_points(int trust_points) {
        this.trust_points = trust_points;
    }

    public int getTrust_level() {
        return trust_level;
    }

    public void setTrust_level(int trust_level) {
        this.trust_level = trust_level;
    }

    @Override
    public String toString(){
        return new StringBuilder(npc_id + " , Trust Points: " + trust_points + " , Trust Level: " + trust_level).toString();
    }
}
