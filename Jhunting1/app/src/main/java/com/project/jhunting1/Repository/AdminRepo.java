package com.project.jhunting1.Repository;

import com.project.jhunting1.Database.IAdminDataSource;
import com.project.jhunting1.Model.Admin;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.Player;

import io.reactivex.Flowable;

public class AdminRepo implements IAdminDataSource{

    private IAdminDataSource localDataSource;
    private static AdminRepo mInstance;

    public AdminRepo(IAdminDataSource localDataSource){
        this.localDataSource = localDataSource;
    }

    public static AdminRepo getInstance(IAdminDataSource localDataSource){
        if(mInstance == null){
            mInstance = new AdminRepo(localDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<NPC> getAdminUsernamePassword(String username, String password) {
        return localDataSource.getAdminUsernamePassword(username, password);
    }

    @Override
    public void addNewAdmin(Admin admin) {
        localDataSource.addNewAdmin(admin);
    }

    @Override
    public void deleteNPC(NPC npc) {
        localDataSource.deleteNPC(npc);
    }

    @Override
    public void updateNPC(NPC npc) {
        localDataSource.updateNPC(npc);
    }

    @Override
    public void deletePlayer(Player player) {
        localDataSource.deletePlayer(player);
    }

    @Override
    public void updatePlayer(Player player) {
        localDataSource.updatePlayer(player);
    }

    @Override
    public void updateAdmin(Admin admin) {
        localDataSource.updateAdmin(admin);
    }
}
