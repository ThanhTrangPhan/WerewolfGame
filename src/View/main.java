/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.GameMatchDAO;
import Controller.GroupDAO;
import Controller.GroupMemberDAO;
import Model.GameMatch;
import Model.Group;
import Model.GroupMember;
import Model.Player;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author demo
 */
public class main {

    public static void main(String[] args) {

//        Group group = new Group();
          GroupDAO groupDAO = new GroupDAO();
//        group.setFounderName("luckyMan");
//        group.setName("LaLaLa");
//        group.setTimeStarted(String.valueOf(java.time.LocalDateTime.now()));
//        groupDAO.createGroup(group);

        List<Group> listGroup = groupDAO.searchGroup("a");
        for (Group g : listGroup) {
            System.out.println(String.valueOf(g.getId())+"/n");
        }

        GroupMember gm = new GroupMember();
        gm.setGroup(listGroup.get(1));
        gm.setPlayer(new Player(3, "12", "12", "12", "123", new ArrayList<Player> ()));
        gm.setTimeJoined(String.valueOf(java.time.LocalDateTime.now()));
        gm.setId(8);
        GroupMemberDAO gmd = new GroupMemberDAO();
        //gmd.addMember(gm);
        //gmd.cancleMember(gm);
        List<GroupMember> l = gmd.searchMember("c",listGroup.get(0));
        for(GroupMember p:l){
            System.out.println(p.getId()+" "+p.getTimeJoined());
        }
        
        GameMatch match = new GameMatch();
        match.setDescription("Let's play with us!");
        match.setMaxPlayer(4);
        match.setType("Random");
        GameMatchDAO matchDAO= new GameMatchDAO();
        
        
    }
}
