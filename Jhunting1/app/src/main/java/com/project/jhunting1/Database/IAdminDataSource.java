package com.project.jhunting1.Database;

import com.project.jhunting1.Model.Admin;
import com.project.jhunting1.Model.NPC;
import com.project.jhunting1.Model.Player;

import io.reactivex.Flowable;

public interface IAdminDataSource {

    Flowable<NPC> getAdminUsernamePassword(String username, String password);
    void addNewAdmin(Admin admin);
    void deleteNPC(NPC npc);
    void updateNPC(NPC npc);
    void deletePlayer(Player player);
    void updatePlayer(Player player);
    void updateAdmin(Admin admin);

}
