package serverTCP.Controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Model.GameMatch;
import Model.Group;
import Model.GroupMember;
import Model.IPAddress;
import Model.MatchRequest;
import Model.ObjectWrapper;
import Model.Player;
import Model.PlayerStat;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author demo
 */
public class ServerCtr_tcp extends ClientCtr {

    private ServerMainFrm view;
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private IPAddress myAddress = new IPAddress("localhost", 8998);  //default server host and port
    private LinkedHashMap<Integer, Integer> playerOnline;
    private LinkedHashMap<Player, PlayerStat> scoreboard;
    private LinkedHashMap<String, List<Player>> listParticipantInARoom;

    private List<MatchRequest> listMatchRequest;
    private static int number = 1;

    public ServerCtr_tcp(ServerMainFrm view) {
        super();
        myProcess = new ArrayList<ServerProcessing>();
        playerOnline = new LinkedHashMap<>();
        listMatchRequest = new ArrayList<>();
        listParticipantInARoom = new LinkedHashMap<>();
        this.view = view;
        openServer();
        openConnectionRMI(0);
    }

    public ServerCtr_tcp(ServerMainFrm view, int serverPort) {
        super();
        myProcess = new ArrayList<ServerProcessing>();
        this.view = view;
        myAddress.setPort(serverPort);
        openServer();
        openConnectionRMI(0);
    }

    private void openServer() {
        try {
            myServer = new ServerSocket(myAddress.getPort());
            myListening = new ServerListening();
            myListening.start();
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showServerInfor(myAddress);
            view.showMessage("TCP server is running at the port " + myAddress.getPort() + "...");
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

    public void stopServer() {
        try {
            for (ServerProcessing sp : myProcess) {
                sp.stop();
            }
            myListening.stop();
            myServer.close();
            view.showMessage("TCP server is stopped!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean openConnectionRMI(int no) {
        try {

            init();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // update the online Player 
    public void publicListOnline() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.PLAYER_ONLINE, playerOnline);
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
        data = new ObjectWrapper(ObjectWrapper.GLOBAL_SCOREBOARD, scoreboard);
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }

//    public void publicListResquestMatch() {
//        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.INVITE_MATCH, listMatchRequest);
//        for (ServerProcessing sp : myProcess) {
//            sp.sendData(data);
//        }
//    }
    public void publicClientNumber() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER, myProcess.size());
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }

    public void sendMatchRequest(MatchRequest mr, int noPlayer) {
        for (ServerProcessing sp : myProcess) {
            if (sp.getNo() == noPlayer) {
                ObjectWrapper data = new ObjectWrapper(ObjectWrapper.INVITE_MATCH, mr);
                sp.sendData(data);
                break;
            }
        }
    }

    public void sendUpdateRoom(int noPlayer, List<Player> lp) {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.UPDATE_ROOM_PLAY, lp);
        for (ServerProcessing sp : myProcess) {
            if (sp.getNo() == noPlayer) {
                sp.sendData(data);
                break;
            }
        }
    }

    public void startTheGame(int playerID) {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.REPLY_START_GAME, "play the game");
        for (ServerProcessing sp : myProcess) {
            if (sp.getNo() == playerOnline.get(playerID)) {
                sp.sendData(data);
                break;
            }
        }
    }

    public void checkoutRoomPlay(Player sender, String roomID) {
        // delete all match request from the sender that logged out
        ArrayList<Player> listUpdate = new ArrayList<>();
        List<MatchRequest> removeMatchRequests = new ArrayList<>(listMatchRequest);
        for (MatchRequest matchRequest : listMatchRequest) {
            if (matchRequest.getSender().getId() == sender.getId()) {
                listUpdate.add(matchRequest.getReceiver());
                removeMatchRequests.remove(matchRequest);
            }
        }
        listMatchRequest = removeMatchRequests;
        if (!listUpdate.isEmpty()) {
            for (Player player : listUpdate) {
                for (ServerProcessing sp : myProcess) {
                    if (sp.getNo() == playerOnline.get(player.getId())) {
                        sp.sendData(new ObjectWrapper(ObjectWrapper.REMOVE_MATCH_REQUEST, sender));
                        break;
                    }
                }
            }

        }
        removeMatchRequests.clear();
        listUpdate.clear();

        // delete participant that logged out
        if (roomID.equals("")) {
            return;
        }
        for (Player players : listParticipantInARoom.get(roomID)) {
            if (players.getId() == sender.getId()) {
                listParticipantInARoom.get(roomID).remove(players);
                break;
            }
        }
        if (listParticipantInARoom.get(roomID).isEmpty()) {
            return;
        }
        for (Player players : listParticipantInARoom.get(roomID)) {
            int n = playerOnline.get(players.getId());
            System.out.println(players.getName());
            sendUpdateRoom(n, listParticipantInARoom.get(roomID));

        }

    }

    /**
     * The class to listen the connections from client, avoiding the blocking of
     * accept connection
     *
     */
    class ServerListening extends Thread {

        public ServerListening() {
            super();
        }

        public void run() {
            view.showMessage("server is listening... ");
            try {
                while (true) {
                    Socket clientSocket = myServer.accept();
                    ServerProcessing sp = new ServerProcessing(clientSocket);
                    sp.start();
                    myProcess.add(sp);
                    view.showMessage("Number of client connecting to the server: " + myProcess.size());

                    publicClientNumber();
                    publicListOnline();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The class to treat the requirement from client
     *
     */
    class ServerProcessing extends Thread implements Serializable {

        private Socket mySocket;
        private int no = number++;
        //private ObjectInputStream ois;
        //private ObjectOutputStream oos;

        public ServerProcessing(Socket s) {
            super();
            mySocket = s;
        }

        public int getNo() {
            return no;
        }

        public void sendData(Object obj) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
                oos.writeObject(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Player pl = new Player();
            String roomPlay = "";
            try {
                while (true) {
                    ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
                    boolean ok = false;
                    Object o = ois.readObject();

                    if (o instanceof ObjectWrapper) {
                        ObjectWrapper data = (ObjectWrapper) o;
                        //System.out.println(data.getPerformative());
                        switch (data.getPerformative()) {
                            case ObjectWrapper.PLAYER_ONLINE:
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.PLAYER_ONLINE, playerOnline));
                                //publicListOnline();
                                break;

                            case ObjectWrapper.LOGIN:
                                pl = (Player) data.getData();
                                String res = checkLogin(pl);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN, res));
                                if (!res.equals("false")) {
                                    playerOnline.put(Integer.parseInt(res), no);
                                    pl.setId(Integer.parseInt(res));
                                    publicListOnline();

                                }
                                System.out.println(res + "  port: " + no);

                                break;

                            case ObjectWrapper.REGISTER:
                                pl = (Player) data.getData();
                                res = (String) register(pl);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_REGISTER, res));
                                if (!res.equals("false")) {
                                    playerOnline.put(Integer.parseInt(res), no);
                                    pl.setId(Integer.parseInt(res));
                                    publicListOnline();
                                    System.out.println("System player online: " + playerOnline.size());
                                }

                                break;
                            case ObjectWrapper.CANCLE_MEMBER:
                                GroupMember memberID = (GroupMember) data.getData();
                                ok = cancelMember(memberID);
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_CANCEL_MEMBER, "ok"));
                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_CANCEL_MEMBER, "false"));
                                }
                                break;
                            case ObjectWrapper.SEARCH_GROUP_JOINED:
                                int id = (int) data.getData();
                                ArrayList<Group> ress = searchGroupJoined(id);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_GROUP, ress));
                                break;
                            case ObjectWrapper.FRIEND_LIST:
                                id = (int) data.getData();
                                ArrayList<Player> fl = SearchFriendList(id);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_FRIEND_LIST, fl));
                                break;
                            case ObjectWrapper.SEARCH_PLAYER_ID:
                                id = (int) data.getData();
                                System.out.println(id);
                                Player player = searchPlayers(id);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_PLAYER_ID, player));
                                break;
                            case ObjectWrapper.SEARCH_PLAYER_NAME:
                                String key = (String) data.getData();
                                List<Player> listPlayers = searchPlayers(key);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_PLAYER_NAME, listPlayers));
                                break;

                            case ObjectWrapper.STATISTIC:
                                id = (int) data.getData();
                                PlayerStat ps = getPlayerStat(id);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_STATISTIC, ps));
                                break;
                            case ObjectWrapper.GLOBAL_SCOREBOARD:
                                LinkedHashMap<Player, PlayerStat> hm = getScoreboard();
                                scoreboard = hm;
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.GLOBAL_SCOREBOARD, hm));
                                break;
                            case ObjectWrapper.LOAD_FRIEND_REQUEST:
                                pl = (Player) data.getData();
                                HashMap<Player, String> req = showFriendRequestPending(pl);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOAD_FRIEND_REQUEST, req));
                                break;
                            case ObjectWrapper.ACCEPT_FRIEND_REQUEST:
                                ArrayList<Player> friend = (ArrayList<Player>) data.getData();
                                ok = updateStatusFriend(friend.get(0), friend.get(1));
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "accepted"));
                                    if (playerOnline.containsKey(friend.get(1).getId())) {
                                        int port = playerOnline.get(friend.get(1).getId());
                                        for (ServerProcessing sp : myProcess) {
                                            if (sp.getNo() == port) {
                                                sp.sendData(new ObjectWrapper(ObjectWrapper.RECEIVE_SIGNAL, 2));
                                            }
                                        }
                                    }
                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "false"));
                                }
                                break;
                            case ObjectWrapper.REJECT_FRIEND_REQUEST:
                                friend = (ArrayList<Player>) data.getData();
                                ok = cancelFrined(friend.get(0), friend.get(1));
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "rejected"));

                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "false"));
                                }
                                break;

                            case ObjectWrapper.SEND_FRIEND_REQUEST:
                                friend = (ArrayList<Player>) data.getData();
                                ok = addFriend(friend.get(0), friend.get(1));
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEND_FRIEND_REQUEST, "ok"));

                                    if (playerOnline.containsKey(friend.get(1).getId())) {
                                        int port = playerOnline.get(friend.get(1).getId());
                                        for (ServerProcessing sp : myProcess) {
                                            if (sp.getNo() == port) {
                                                System.out.println("Friend request receiver: " + port);
                                                sp.sendData(new ObjectWrapper(ObjectWrapper.RECEIVE_SIGNAL, 1));
                                                break;
                                            }
                                        }
                                    }

                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEND_FRIEND_REQUEST, "false"));
                                }
                                break;

                            case ObjectWrapper.CREATE_A_ROOM:
                                ArrayList<Object> game = (ArrayList<Object>) data.getData();
                                List<Player> listParticipant = new ArrayList<>();
                                listParticipant.add((Player) game.get(1));
                                listParticipantInARoom.put(((GameMatch) game.get(0)).getDescription(), listParticipant);
                                roomPlay = ((GameMatch) game.get(0)).getDescription();
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_CREATE_ROOM, "ok"));
                                System.out.println("Create a room");
                                break;

                            case ObjectWrapper.INVITE_MATCH:
                                MatchRequest mr = (MatchRequest) data.getData();

                                ok = true;
                                System.out.println("Recievermatch request: " + mr.getReceiver().getId() + mr.getReceiver().getName());
                                for (MatchRequest i : listMatchRequest) {
                                    if (i.getRoomID().equals(mr.getRoomID()) && i.getSender().getName().equals(mr.getSender().getName())
                                            && i.getReceiver().getName().equals(mr.getReceiver().getName())) {
                                        ok = false;
                                    }
                                }
                                System.out.println(ok);
                                if (!ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_INVITE_MATCH, "exist"));
                                } else {
                                    listMatchRequest.add(mr);
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_INVITE_MATCH, "ok"));
                                    int spID = playerOnline.get(mr.getReceiver().getId());
                                    System.out.println(spID + " is the port of reciever");
                                    sendMatchRequest(mr, spID);
                                }
                                //System.out.println("1111222 " + listMatchRequest.size());
                                break;
                            case ObjectWrapper.ACCEPT_MATCH_REQUEST:
                                mr = (MatchRequest) data.getData();
                                if (listParticipantInARoom.containsKey(mr.getRoomID())) {
                                    listParticipantInARoom.get(mr.getRoomID()).add(mr.getReceiver());
                                    listMatchRequest.remove(mr);
                                    int n = playerOnline.get(mr.getReceiver().getId());
                                    //sendUpdateRoom(n, listPaticipantInARoom.get(mr.getRoomID()));
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.UPDATE_ROOM_PLAY, listParticipantInARoom.get(mr.getRoomID())));
                                    roomPlay = mr.getRoomID();
                                    for (Player players : listParticipantInARoom.get(mr.getRoomID())) {
                                        if (players != mr.getReceiver()) {
                                            n = playerOnline.get(players.getId());
                                            sendUpdateRoom(n, listParticipantInARoom.get(mr.getRoomID()));
                                        }

                                    }
                                }
                                break;
                            case ObjectWrapper.REJECT_MATCH_REQUEST:
                                mr = (MatchRequest) data.getData();
                                listMatchRequest.remove(mr);
                                break;

                            case ObjectWrapper.QUIT_OLD_ROOM:
                                ArrayList<Object> arr = (ArrayList<Object>) data.getData();

                                for (Player players : listParticipantInARoom.get((String) arr.get(0))) {
                                    if (players.getId() == ((Player) arr.get(1)).getId()) {
                                        listParticipantInARoom.get((String) arr.get(0)).remove(players);
                                        break;
                                    }
                                }
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.UPDATE_ROOM_PLAY, "ok"));
                                for (Player players : listParticipantInARoom.get((String) arr.get(0))) {
                                    int n = playerOnline.get(players.getId());
                                    System.out.println(players.getName());
                                    sendUpdateRoom(n, listParticipantInARoom.get((String) arr.get(0)));

                                }
                                break;

                            case ObjectWrapper.START_GAME:
                                String room = (String) data.getData();
                                if (listParticipantInARoom.containsKey(room)) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_START_GAME, null));
                                    for (int i = 0; i < listParticipantInARoom.get(room).size(); ++i) {

                                        startTheGame(listParticipantInARoom.get(room).get(i).getId());
                                    }
                                }
                                break;

                        }

                    }

                }
            } catch (EOFException | SocketException e) {
                //e.printStackTrace();        
                checkoutRoomPlay(pl, roomPlay);
                System.out.println(playerOnline.size());
                playerOnline.remove(pl.getId());
                myProcess.remove(this);
                view.showMessage("Number of client connecting to the server: " + myProcess.size());
                publicClientNumber();
                publicListOnline();

                try {
                    mySocket.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
