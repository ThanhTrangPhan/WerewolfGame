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
public class GameMatch implements Serializable{
    private int id;
    private String type;
    private String description;
    private String timeStarted;
    private String timeEnded;
    private String winnerSide;
    private List<Record> record;

    public GameMatch() {
    }

    public GameMatch(int id, String type, String description, String timeStarted, 
            String timeEnded, String winnerSide, List<Record> record) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.timeStarted = timeStarted;
        this.timeEnded = timeEnded;
        this.winnerSide = winnerSide;
        this.record = record;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(String timeStarted) {
        this.timeStarted = timeStarted;
    }

    public String getTimeEnded() {
        return timeEnded;
    }

    public void setTimeEnded(String timeEnded) {
        this.timeEnded = timeEnded;
    }

    public String getWinnerSide() {
        return winnerSide;
    }

    public void setWinnerSide(String winnerSide) {
        this.winnerSide = winnerSide;
    }

    public List<Record> getRecord() {
        return record;
    }

    public void setRecord(List<Record> record) {
        this.record = record;
    }

    
    
}
