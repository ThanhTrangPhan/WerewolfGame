/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverTCP.gameLogic.Config;

/**
 *
 * @author demo
 */
public interface Config {
     int SERVER_PORT = 9000;

    //Mode game for number of player
    int MODEL_12 = 1;  
    int MODEL_10 = 2;  
    int MODEL_8 = 3;  


    //Role play
    int ROLE_CODE_OF_WOLF = 1;  
    int ROLE_CODE_OF_SEER = 2;  
    int ROLE_CODE_OF_VILLAGER = 3;  
    int ROLE_CODE_OF_WITCH = 4; 
    int ROLE_CODE_OF_HUNTSMAN = 5;  
    int ROLE_CODE_OF_BODYGUARD = 6; 
    int ROLE_CODE_OF_CUPID = 7; 


//    游戏过程状态码
    int GAME_STATUS_OVER_WOLF = -1;
    int GAME_STATUS_OVER_GOOD = 1;
    int GAME_STATUS_PROCESS= 0;


    /**
     *  Time 
     */
    //Time to speak
    int TIME_GAME_SPEAK = 1000*20;

    //At morning
    int TIME_GAME_VOTE = 1000*10;

    //At night
    int TIME_GAME_DARK = 1000*30;

    //Time for wolf vote kill 1 person
    int TIME_GAME_WOLF = 1000*10;

    //Time for witch 
    int TIME_GAME_WITCH = 1000*20;


}
