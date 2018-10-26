package com.project.jhunting1.Local;

import com.project.jhunting1.DAO.Admin_DAO;
import com.project.jhunting1.Database.IAdminDataSource;
import com.project.jhunting1.Model.Admin;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.Player;

import io.reactivex.Flowable;

public class AdminDataSource implements IAdminDataSource{

    private Admin_DAO admin_dao;
    private static AdminDataSource mInstance;

    public AdminDataSource(Admin_DAO admin_dao){
        this.admin_dao = admin_dao;
    }

    public static AdminDataSource getInstance(Admin_DAO admin_dao){
        if(mInstance == null){
            mInstance = new AdminDataSource(admin_dao);
        }
        return mInstance;
    }

    @Override
    public Flowable<NPC> getAdminUsernamePassword(String username, String password) {
        return this.admin_dao.getAdminUsernamePassword(username, password);
    }

    @Override
    public void addNewAdmin(Admin admin) {
        this.admin_dao.addNewAdmin(admin);
    }

    @Override
    public void deleteNPC(NPC npc) {
        this.admin_dao.deleteNPC(npc);
    }

    @Override
    public void updateNPC(NPC npc) {
        this.admin_dao.updateNPC(npc);
    }

    @Override
    public void deletePlayer(Player player) {
        this.admin_dao.deletePlayer(player);
    }

    @Override
    public void updatePlayer(Player player) {
        this.admin_dao.updatePlayer(player);
    }

    @Override
    public void updateAdmin(Admin admin) {
        this.admin_dao.updateAdmin(admin);
    }
}
