package com.app.server;

import com.app.server.hibernate.dao.ServerDao;
import com.app.server.hibernate.model.*;
import com.app.server.tcp.RejectedTasksHandler;
import com.app.server.tcp.TCPTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable {
    private final int BROADCAST_GROUP_ID = 0;
    private boolean running = false;
    private InetAddress ip;
    private int port;

    private PingClients pingClients;
    private GroupManager groupManager;
    private PrivateChatManager privateChatManager;
    private FileManager fileManager;
    private ServerNetworking serverNetworking;
    private Thread runServer, listen, manage, tcp;

    private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
    private ResourceBundle serverConfig = ResourceBundle.getBundle("com.app.server.server_config");

    private String registerIdentifier = config.getString("register-identifier");
    private String loginIdentifier = config.getString("login-identifier");
    private String logoutIdentifier = config.getString("logout-identifier");
    private String identityIdentifier = config.getString("identity-identifier");
    private String broadcastIdentifier = config.getString("broadcast-identifier");
    private String groupIdentifier = config.getString("group-identifier");
    private String privateChatIdentifier = config.getString("private-chat-identifier");
    private String errorIdentifier = config.getString("error-identifier");
    private String updateListIdentifier = config.getString("update-list-identifier");
    private String listClientsIdentifier = config.getString("list-clients-identifier");
    private String pingIdentifier = config.getString("ping-identifier");
    private String centralIdentifier = config.getString("central-identifier");

    private final int CORE_TCP_THREADPOOL_SIZE = Integer.parseInt(serverConfig.getString("core-tcpThreadPool-size"));
    private final int MAX_TCP_THREADPOOL_SIZE = Integer.parseInt(serverConfig.getString("max-tcpThreadPool-size"));
    private final int MAX_TCP_QUEUE_SIZE = Integer.parseInt(serverConfig.getString("max-tcpQueue-size"));

    private ArrayList<LoggedInClient> loggedInClients = new ArrayList<LoggedInClient>();
    private ArrayList<RegisteredClient> registeredClients;

    public Server(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        serverNetworking = new ServerNetworking(port);
        pingClients = PingClients.getPingClients(this);
        groupManager = GroupManager.GetGroupManager(this);
        privateChatManager = PrivateChatManager.getPrivateChatManager(this);
        fileManager = FileManager.getFileManager(this);
        fileManager.createDirectoryStructure();
        startServer();
    }

    private void startServer() {
        if (serverNetworking.openSocket()) {
            runServer = new Thread(this, "RunServer");
            runServer.start();
        }
    }

    @Override
    public void run() {
        System.out.println("SERVER RUNNING ON: " + ip + ":" + port);
        running = true;
        manageClients();
        listen();
        adminCommands();
        tcp();
    }

    private void tcp() {
        tcp = new Thread("TCP") {
            @Override
            public void run() {

                ThreadPoolExecutor tcpThreadPool = new ThreadPoolExecutor(CORE_TCP_THREADPOOL_SIZE, MAX_TCP_THREADPOOL_SIZE, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(MAX_TCP_QUEUE_SIZE));
                tcpThreadPool.setRejectedExecutionHandler(new RejectedTasksHandler());
                tcpThreadPool.prestartAllCoreThreads();

                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(port);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                while (running) {
                    try {
                        System.out.println("SERVER TCP SOCKET RUNNING");
                        Socket tcpSocket = serverSocket.accept();
                        tcpThreadPool.execute(createTCPTask(tcpSocket));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                tcpThreadPool.shutdown();
            }
        };
        tcp.start();
    }

    private TCPTask createTCPTask(Socket tcpSocket) {
        return new TCPTask(tcpSocket, this);
    }

    private void adminCommands() {
        // admin commands
    }

    private void manageClients() {
        manage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    pingClients.ping();
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pingClients.check();
                }
            }
        });
        manage.start();
    }

    public void listen() {
        listen = new Thread("Listen") {
            public void run() {
                while (running) {
                    DatagramPacket packet = serverNetworking.receive();
                    process(packet);
                }
            }
        };
        listen.start();
    }

    private void process(DatagramPacket packet) {
        String message = new String(packet.getData()).trim();

        if (message.startsWith(pingIdentifier)) {
            /**
             * receives a msg like: "/z/clientID"
             */
            message = message.substring(pingIdentifier.length(), message.length());
            pingClients.add(message);

        } else if (message.startsWith(registerIdentifier)) {
            /**
             * receives a message like:
             * "/r/clientID/i/clientName/i/DEVELOPER/i/password"
             */
            message = message.substring(registerIdentifier.length(), message.length());
            registerClient(message, packet.getAddress(), packet.getPort());

        } else if (message.startsWith(loginIdentifier)) {
            /**
             * receives a message like: "/l/username/i/password"
             */
            message = message.substring(loginIdentifier.length(), message.length());
            loginClient(message, packet.getAddress(), packet.getPort());

        } else if (message.startsWith(broadcastIdentifier)) {
            /**
             * receives a message like: "/b/clientID/i/message"
             */
            prepareBroadcastMessage(message);

        } else if (message.startsWith(logoutIdentifier)) {
            /**
             * receives a msg like: "/x/clientID"
             */
            message = message.substring(logoutIdentifier.length(), message.length());
            processLogoutMessage(message, true);

        } else if (message.startsWith(privateChatIdentifier)) {
            /**
             * receives a msg like: "/p/message"
             */
            message = message.substring(privateChatIdentifier.length(), message.length());
            // processPrivateChatMessage(message);
            privateChatManager.process(message);

        } else if (message.startsWith(groupIdentifier)) {
            /**
             * receives a msg like: "/g/message"
             */
            message = message.substring(groupIdentifier.length(), message.length());
            // processGroupMessage(message);
            groupManager.process(message);

        } else if (message.startsWith(centralIdentifier)) {
            /**
             * receives a msg like: "/w/clientID"
             */
            message = message.substring(centralIdentifier.length(), message.length());
            processCentralMessage(message);
        }

    }

    private void processCentralMessage(String clientID) {
        LoggedInClient loggedInClient = getLoggedInClient(clientID);
        if (loggedInClient == null) {
            return;
        }
        String centralFileNames = getCentralFileNames();
        /**
         * sends a msg like: "/w//u/centralFileNames"
         */
        centralFileNames = centralIdentifier + updateListIdentifier + centralFileNames;
        serverNetworking.send(centralFileNames.getBytes(), loggedInClient.getIp(), loggedInClient.getPort());
    }

    private String getCentralFileNames() {
        /**
         * returns a string like: "anmol_e1,fileName1,time1/i/sug_e2,fileName2,time2/i/"
         */
        String centralFileNames = "";

        ArrayList<String> filesStr = ServerDao.fetchCentralFiles();
        for (int i = 0; i < filesStr.size(); i++) {
            String fileStr = filesStr.get(i);
            centralFileNames += fileStr + identityIdentifier;
        }
        return centralFileNames;
    }

    private boolean isClientLoggedIn(String clientID) {
        for (int i = 0; i < loggedInClients.size(); i++) {
            LoggedInClient c = loggedInClients.get(i);
            if (c.getClient().getId().equals(clientID)) {
                return true;
            }
        }
        return false;
    }

    public RegisteredClient getClientByUserName(String receiverClientUserName) {
        RegisteredClient client = null;
        client = ServerDao.fetchClientByUserName(receiverClientUserName);
        return client;
    }

    private void processLogoutMessage(String clientID, boolean sendAck) {
        LoggedInClient client = getLoggedInClient(clientID);
        if (client == null) {
            return;
        }
        loggedInClients.remove(client);

        if (sendAck) {
            /**
             * sends an ack to the logged out client like: "/x/clientUserName"
             */
            String ackToLoggedOutClient = logoutIdentifier + client.getClient().getUserName();
            serverNetworking.send(ackToLoggedOutClient.getBytes(), client.getIp(), client.getPort());
        }
        /**
         * sends an update list msg to all the logged in clients like :
         * "/u//x/clientUserName"
         */
        String msgToLoggedInClients = updateListIdentifier + logoutIdentifier + client.getClient().getUserName();
        for (int i = 0; i < loggedInClients.size(); i++) {
            LoggedInClient c = loggedInClients.get(i);
            serverNetworking.send(msgToLoggedInClients.getBytes(), c.getIp(), c.getPort());

        }
    }

    public LoggedInClient getLoggedInClient(String clientID) {
        for (int i = 0; i < loggedInClients.size(); i++) {
            LoggedInClient c = loggedInClients.get(i);
            if (c.getClient().getId().equals(clientID)) {
                return c;
            }
        }
        return null;
    }

    private void prepareBroadcastMessage(String message) {
        /**
         * prepares a msg like: "/b/clientUserName/i/message"
         */
        message = message.substring(broadcastIdentifier.length(), message.length());
        String[] arr = message.split(identityIdentifier);
        String clientID = arr[0];
        message = arr[1];

        // Message messageDB = new Message(message, new Date(), sender, );

        for (int i = 0; i < loggedInClients.size(); i++) {
            LoggedInClient c = loggedInClients.get(i);
            if (c.getClient().getId().equals(clientID)) {
                message = c.getClient().getUserName() + identityIdentifier + message;
                break;
            }
        }
        broadcast(message, true);
    }

    private void broadcast(String message, boolean attachBroadcastIdentifier) {
        if (attachBroadcastIdentifier) {
            message = broadcastIdentifier + message;
        }
        LoggedInClient client;
        for (int i = 0; i < loggedInClients.size(); i++) {
            client = loggedInClients.get(i);
            serverNetworking.send(message.getBytes(), client.getIp(), client.getPort());
        }
    }

    private void loginClient(String message, InetAddress clientIP, int clientPort) {
        /**
         * recieves a message = "username/i/password"
         */
        String[] arr = message.split(identityIdentifier);
        RegisteredClient client = ServerDao.fetchClient(arr[0], arr[1]);
        if (client != null) {
            LoggedInClient loggedInClient = new LoggedInClient(client, clientIP, clientPort);
            loggedInClients.add(loggedInClient);

            /**
             * sends an acknowledgement like: "/l/username/i/clientID"
             */
            String ackToLoggedInClient = loginIdentifier + client.getUserName() + identityIdentifier + client.getId();
            serverNetworking.send(ackToLoggedInClient.getBytes(), clientIP, clientPort);

            /**
             * sends an msg like:
             * "/u//c/loggedInClientsUserNames/i/RestClientsUsernames"
             */
            String messageToClient = updateListIdentifier + listClientsIdentifier + getAllClientsString();
            serverNetworking.send(messageToClient.getBytes(), clientIP, clientPort);

            /**
             * sends an ack like: "/u//l/username" to already logged in clients
             * but not to the new;y logged in client.
             */
            String ackToAllLoggedInClients = updateListIdentifier + loginIdentifier + client.getUserName();
            for (int i = 0; i < loggedInClients.size(); i++) {
                LoggedInClient c = loggedInClients.get(i);
                if (c.getClient() == client) {
                    continue;
                }
                serverNetworking.send(ackToAllLoggedInClients.getBytes(), c.getIp(), c.getPort());
            }

            ArrayList<Pending> pendings = ServerDao.fetchAllPending(client);
            if (pendings != null) {
                processLoginPendings(pendings, loggedInClient);
            }

        } else {
            /**
             * sends an error message like: "/e/Username or Password Incorrect"
             */
            String ackToRegisteredClient = errorIdentifier + "Username or Password Incorrect";
            serverNetworking.send(ackToRegisteredClient.getBytes(), clientIP, clientPort);
        }
    }

    private void processLoginPendings(ArrayList<Pending> pendings, LoggedInClient loggedInClient) {
        for (int i = 0; i < pendings.size(); i++) {

            Pending pending = pendings.get(i);
            if (pending instanceof PendingPrivateChatForm) {
                PendingPrivateChatForm pendingPrivateChatForm = (PendingPrivateChatForm) pending;
                privateChatManager.sendPrivateChatFormAck(pendingPrivateChatForm.getPrivateChat(), loggedInClient);

            } else if (pending instanceof PendingGroupForm) {
                PendingGroupForm pendingGroupForm = (PendingGroupForm) pending;
                groupManager.sendGroupFormAck(pendingGroupForm.getGroup(), loggedInClient);

            } else if (pending instanceof PendingPrivateMessage) {
                PendingPrivateMessage pendingPrivateMessage = (PendingPrivateMessage) pending;
                int totalPending = pendingPrivateMessage.getTotalPending();
                Message latestMessage = pendingPrivateMessage.getLatestMessage();
                LinkedList<PrivateMessage> pendingMessages = ServerDao.fetchPendingPrivateMessages(latestMessage,
                        totalPending);
                for (int k = 0; k < pendingMessages.size(); k++) {
                    PrivateMessage privateMessage = pendingMessages.get(k);
                    privateChatManager.sendMessage(privateMessage, loggedInClient);
                }

            } else if (pending instanceof PendingGroupMessage) {
                PendingGroupMessage pendingGroupMessage = (PendingGroupMessage) pending;
                int totalPending = pendingGroupMessage.getTotalPending();
                Message latestMessage = pendingGroupMessage.getLatestMessage();
                LinkedList<GroupMessage> groupMessages = ServerDao.fetchPendingGroupMessages(latestMessage,
                        totalPending);
                for (int k = 0; k < groupMessages.size(); k++) {
                    GroupMessage groupMessage = groupMessages.get(k);
                    groupManager.sendMessage(groupMessage, loggedInClient);
                }
            }
        }
    }

    private String getAllClientsString() {
        /**
         * return a string like:
         * "loggedInClientsUserNames/i/RestClientsUsernames"
         */
        String loggedInClientsStr = "";
        String restClientsStr = "";
        ArrayList<RegisteredClient> allClients = ServerDao.fetchAllClients();
        for (int i = 0; i < allClients.size(); i++) {
            RegisteredClient cr = allClients.get(i);
            boolean flag = false;

            for (int j = 0; j < loggedInClients.size(); j++) {
                LoggedInClient cl = loggedInClients.get(j);
                if (cl.getClient().getId().equals(cr.getId())) {
                    flag = true;
                    loggedInClientsStr = loggedInClientsStr + cr.getUserName() + ",";
                    break;
                }
            }
            if (!flag) {
                restClientsStr = restClientsStr + cr.getUserName() + ",";
            }
        }
        return loggedInClientsStr + identityIdentifier + restClientsStr;
    }

    private void registerClient(String message, InetAddress clientIP, int clientPort) {
        /**
         * recieves a message = "clientID/i/clientName/i/DEVELOPER/i/password"
         */

        String[] arr = message.split(identityIdentifier);
        String clientID = arr[0];
        String clientName = arr[1];
        String clientDesignation = arr[2];
        String clientPassword = arr[3];

        RegisteredClient client = ServerDao.fetchClient(clientID);

        if (client != null) {
            /**
             * sends an acknowledgement message like: "/e/ID already registered"
             * back to newly registered client
             */
            String ackToRegisteredClient = errorIdentifier + "ID already registered";
            serverNetworking.send(ackToRegisteredClient.getBytes(), clientIP, clientPort);
            return;
        }
        /**
         * this user name is generated by server and sent back to the client.
         */
        String clientUserName = clientName + "_" + clientID;

        client = new RegisteredClient(clientID, clientName, clientDesignation, clientUserName, clientPassword,
                new Date());

        ServerDao.saveClient(client);

        /**
         * sends an acknowledgement to all logged in clients like:
         * "/u//r/clientUserName/i/groupID"
         */
        String ackToLoggedInClients = updateListIdentifier + registerIdentifier + clientUserName + identityIdentifier
                + BROADCAST_GROUP_ID;

        for (int i = 0; i < loggedInClients.size(); i++) {
            LoggedInClient c = loggedInClients.get(i);
            serverNetworking.send(ackToLoggedInClients.getBytes(), c.getIp(), c.getPort());
        }

        loggedInClients.add(new LoggedInClient(client, clientIP, clientPort));
        /**
         * sends an acknowledgement message like: "/r/clientUserName" back to
         * newly registered client
         */
        String ackToRegisteredClient = registerIdentifier + clientUserName;
        /**
         * sends an msg like:
         * "/u//c/loggedInClientsUserNames/i/RestClientsUsernames" back to newly
         * registered client
         */
        String messageToClient = updateListIdentifier + listClientsIdentifier + getAllClientsString();
        System.out.println("MESSAGE TO CLIENT: " + messageToClient);
        serverNetworking.send(ackToRegisteredClient.getBytes(), clientIP, clientPort);
        serverNetworking.send(messageToClient.getBytes(), clientIP, clientPort);
    }

    public void send(String message, InetAddress clientIP, int clientPort) {
        serverNetworking.send(message.getBytes(), clientIP, clientPort);
    }

    public ArrayList<LoggedInClient> getLoggedInClients() {
        return loggedInClients;
    }

    public void logout(LoggedInClient client, boolean sendAck) {
        processLogoutMessage(client.getClient().getId(), sendAck);
    }

    public int getBroadcastGroupID() {
        return BROADCAST_GROUP_ID;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
