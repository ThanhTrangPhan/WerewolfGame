/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Model.IPAddress;
import Model.ObjectWrapper;
import View.LoginFrm;
import View.PlayerMainFrm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demo
 */
public class ClientCtr {

    private Socket mySocket;
    private LoginFrm view;
    private ClientListening myListening;                            // thread to listen the data from the server
    private ArrayList<ObjectWrapper> myFunction;                  // list of active client functions
    private IPAddress serverAddress = new IPAddress("localhost", 8998);  // default server host and port
    private List<Integer> playerOnline;

    public ClientCtr(LoginFrm view) {
        super();
        this.view = view;
        myFunction = new ArrayList<ObjectWrapper>();
        playerOnline = new ArrayList<>();
    }

    public ClientCtr(LoginFrm view, IPAddress serverAddr) {
        super();
        this.view = view;
        this.serverAddress = serverAddr;
        myFunction = new ArrayList<ObjectWrapper>();
        playerOnline = new ArrayList<>();
    }

    public boolean openConnection() {
        try {
            mySocket = new Socket(serverAddress.getHost(), serverAddress.getPort());
            myListening = new ClientListening();
            myListening.start();
            //view.showMessage("Connected to the server at host: " + serverAddress.getHost() + ", port: " + serverAddress.getPort());
        } catch (Exception e) {
            //e.printStackTrace();
            //view.showMessage("Error when connecting to the server!");
            return false;
        }
        return true;
    }

    public boolean sendData(Object obj) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
            oos.writeObject(obj);

        } catch (Exception e) {
            //e.printStackTrace();
            //view.showMessage("Error when sending data to the server!");
            return false;
        }
        return true;
    }

    /*
    public Object receiveData(){
        Object result = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
            result = ois.readObject();
        } catch (Exception e) {
            //e.printStackTrace();
            view.showMessage("Error when receiving data from the server!");
            return null;
        }
        return result;
    }*/
    public boolean closeConnection() {
        try {
            if (myListening != null) {
                myListening.stop();
            }
            if (mySocket != null) {
                mySocket.close();
//                view.showMessage("Disconnected from the server!");
            }
            myFunction.clear();
        } catch (Exception e) {
            //e.printStackTrace();
            //          view.showMessage("Error when disconnecting from the server!");
            return false;
        }
        return true;
    }

    public ArrayList<ObjectWrapper> getActiveFunction() {
        return myFunction;
    }

    class ClientListening extends Thread {

        public ClientListening() {
            super();
        }

        public void run() {
            try {
                while (true) {

                    ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                    Object obj = ois.readObject();
                    if (obj instanceof ObjectWrapper) {
                        ObjectWrapper data = (ObjectWrapper) obj;
                        if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER) {
                            //view.showMessage("Number of client connecting to the server: " + data.getData());
                        } else {
                            //for (ObjectWrapper fto : myFunction) {
                            for (int i = 0; i < myFunction.size(); ++i) {
                                if (myFunction.get(i).getPerformative() == data.getPerformative()) {
                                    switch (data.getPerformative()) {
                                        case ObjectWrapper.REPLY_SEARCH_GROUP:
                                            PlayerMainFrm pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingGroup(data);
                                            break;
                                        case ObjectWrapper.REPLY_CREATE_GROUP:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingGroup(data);
                                            break;
                                        case ObjectWrapper.REPLY_ADD_MEMBER:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingMember(data);
                                            break;
                                        case ObjectWrapper.REPLY_CANCEL_MEMBER:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingMember(data);
                                            break;
                                        case ObjectWrapper.REPLY_GROUP_JOINED:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingGroup(data);
                                            break;
                                        case ObjectWrapper.REPLY_FRIEND_LIST:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingFriendlist(data);
                                            break;

                                        case ObjectWrapper.PLAYER_ONLINE:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingOnline(data);
                                            break;
                                        case ObjectWrapper.REPLY_LOGIN:
                                            LoginFrm lgf = (LoginFrm) myFunction.get(i).getData();
                                            lgf.receivedDataProcessing(data);
                                            break;
                                        case ObjectWrapper.REPLY_REGISTER:
                                            lgf = (LoginFrm) myFunction.get(i).getData();
                                            lgf.receivedDataProcessing(data);
                                            break;
                                        case ObjectWrapper.REPLY_STATISTIC:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingStat(data);
                                            break;
                                        case ObjectWrapper.GLOBAL_SCOREBOARD:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingScoreboard(data);
                                            break;
                                        case ObjectWrapper.REPLY_LOAD_FRIEND_REQUEST:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingFriendRequestPending(data);
                                            break;
                                        case ObjectWrapper.REPLY_ANS_FRIEND_REQUEST:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingAnsFriendRequest(data);
                                            break;
                                        case ObjectWrapper.REPLY_SEND_FRIEND_REQUEST:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingSendFriendRequest(data);
                                            break;
                                        case ObjectWrapper.REPLY_SEARCH_PLAYER_ID:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingSearchPlayer(data);
                                            break;
                                        case ObjectWrapper.INVITE_MATCH:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingMatchRequest(data);
                                            break;

                                        case ObjectWrapper.REPLY_INVITE_MATCH:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingStatusMatchRequest(data);
                                            break;

                                        case ObjectWrapper.UPDATE_ROOM_PLAY:
                                            //System.out.println("Update room");
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingUpdateRoomPlay(data);
                                            break;
                                        case ObjectWrapper.REPLY_START_GAME:
                                            System.out.println("Start the gmae");
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingStartGame(data);
                                            break;
                                        case ObjectWrapper.RECEIVE_SIGNAL:
                                            System.out.println("Receive signal");
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingReceiveSignal(data);
                                            break;
                                        case ObjectWrapper.REMOVE_MATCH_REQUEST:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingRemoveMatchRequest(data);
                                            break;
                                        case ObjectWrapper.REPLY_SEARCH_PLAYER_NAME:
                                            pmf = (PlayerMainFrm) myFunction.get(i).getData();
                                            pmf.receivedDataProcessingSearchPlayerByName(data);
                                            break;

                                    }
                                }
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // view.showMessage("Error when receiving data from the server!");
                //view.resetClient();
            }
        }
    }
}
