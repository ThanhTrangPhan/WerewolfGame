/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.GroupDAO;
import Controller.GroupMemberDAO;
import Model.Group;
import Model.GroupMember;
import Model.Player;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demo
 */
public class main {

    public static void main(String[] args) {
        GroupDAO a = new GroupDAO();
        GroupMemberDAO b = new GroupMemberDAO();
        List<Group> l = a.searchGroup("a");
        List<GroupMember> k = b.searchMember("k", 2);
        
        for(Group i: l){
            System.out.println(i.getId()+" "+ i.getName());
        }
        for(GroupMember m:k){
            System.out.println(m.getId()+" "+m.getPlayer().getName());
        }
        
    }
}
