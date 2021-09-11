/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author demo
 */
public class Group implements  Serializable{
    private int id;
    private String name;
    private String timeStarted;
    private String founderName;
    private List<GroupMember> member;
    public Group(){
        
    }

    public Group(int id, String name, String timeStarted, String founderName, List<GroupMember> member) {
        this.id = id;
        this.name = name;
        this.timeStarted = timeStarted;
        this.founderName = founderName;
        this.member = member;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(String timeStarted) {
        this.timeStarted = timeStarted;
    }

    public String getFounderName() {
        return founderName;
    }

    public void setFounderName(String founderName) {
        this.founderName = founderName;
    }

    public List<GroupMember> getMember() {
        return member;
    }

    public void setMember(List<GroupMember> member) {
        this.member = member;
    }
    
    
}
