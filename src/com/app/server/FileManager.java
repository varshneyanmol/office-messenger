package com.app.server;

import com.app.server.hibernate.dao.ServerDao;
import com.app.server.hibernate.model.*;
import com.app.server.util.Util;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

public class FileManager {
    private static FileManager fileManager = null;
    private Server server;
    private final String FILE_PATH_MEDIA = "/home/hduser/OfficeMessenger/Server/Media/";
    private final String FILE_PATH_CENTRAL = "/home/hduser/OfficeMessenger/Server/Central/";
    private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
    private String identityIdentifier = config.getString("identity-identifier");
    private String broadcastIdentifier = config.getString("broadcast-identifier");
    private String groupIdentifier = config.getString("group-identifier");
    private String privateChatIdentifier = config.getString("private-chat-identifier");
    private String centralIdentifier = config.getString("central-identifier");
    private String tcpIdentifier = config.getString("tcp-identifier");

    private FileManager(Server server) {
        this.server = server;
    }

    public static FileManager getFileManager(Server server) {
        if (fileManager == null) {
            fileManager = new FileManager(server);
        }
        return fileManager;
    }

    public void manage(File file) {

    }

    public void process(String message, byte[] byteArr) {
        if (message.startsWith(privateChatIdentifier)) {
            /**
             * receives a msg like: "/p/id/i/senderID/i/fileName"
             */

            message = message.substring(privateChatIdentifier.length(), message.length());
            String[] arr = message.split(identityIdentifier);
            String privateChatID = arr[0];
            String senderID = arr[1];
            String fileName = arr[2];
            managePrivateChatFile(privateChatID, senderID, fileName, byteArr);

        } else if (message.startsWith(groupIdentifier)) {
            /**
             * receives a msg like: "/g/groupID/i/senderID/i/fileName"
             */

            message = message.substring(groupIdentifier.length(), message.length());
            String[] arr = message.split(identityIdentifier);
            int groupID = Integer.parseInt(arr[0]);
            String senderID = arr[1];
            String fileName = arr[2];
            manageGroupFile(groupID, senderID, fileName, byteArr);

        } else if (message.startsWith(broadcastIdentifier)) {
            /**
             * receives a msg like: "/b/clientID/i/fileName"
             */

            message = message.substring(broadcastIdentifier.length(), message.length());
            String[] arr = message.split(identityIdentifier);
            String senderID = arr[0];
            String fileName = arr[1];
            manageGroupFile(server.getBroadcastGroupID(), senderID, fileName, byteArr);

        } else if (message.startsWith(centralIdentifier)) {
            /**
             * receives a msg like: "/w/clientID/i/fileName"
             */

            message = message.substring(centralIdentifier.length(), message.length());
            String[] arr = message.split(identityIdentifier);
            String senderID = arr[0];
            String fileName = arr[1];
            manageCentralFile(senderID, fileName, byteArr);
        }
    }

    private void manageCentralFile(String senderID, String fileName, byte[] byteArr) {
        int newCopyCount = ServerDao.getNewCopyCountFromCentral(senderID, fileName);
        if (newCopyCount != 0) {
            String[] fileNameSplit = Util.splitFileName(fileName);
            fileName = Util.modifyCentralFileName(fileNameSplit[0], fileNameSplit[1], newCopyCount);
        }

        String serverFileName = Util.getServerCentralFileName(fileName, senderID);
        storeToServer(FILE_PATH_CENTRAL, serverFileName, byteArr);

        RegisteredClient sender = ServerDao.fetchClient(senderID);
        ServerDao.saveCentral(new Central(fileName, sender, new Date()));
    }


    private void manageGroupFile(int groupID, String senderID, String fileName, byte[] byteArr) {
        System.out.println("GROUP CHAT FILE");
        System.out.println("IDs: " + groupID + " : " + senderID + " : " + fileName);

        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String serverFileName = groupID + "_" + new Date().getTime() + extension;

        storeToServer(FILE_PATH_MEDIA, serverFileName, byteArr);

        Group group = saveGroupFile(groupID, senderID, fileName, serverFileName);
        if (group == null) {
            System.out.println("GROUP RETURNED NULL");
            return;
        }
        Set<RegisteredClient> members = group.getMembers();
        RegisteredClient sender = ServerDao.fetchClient(senderID);
        /**
         * sends a msg to logged in members like: "/t//g/groupID/i/senderUserName/i/fileName/i/fileSize/i/serverFileName"
         */
        String message = tcpIdentifier + groupIdentifier + groupID + identityIdentifier + sender.getUserName() + identityIdentifier + fileName
                + identityIdentifier + byteArr.length + identityIdentifier + serverFileName;
        System.out.println("MESSAGE SENT: " + message);


        Iterator<RegisteredClient> iterator = members.iterator();
        System.out.println("TOTAL CLIENTS TO SEND FILE TO: " + members.size());
        while (iterator.hasNext()) {
            RegisteredClient member = iterator.next();
            LoggedInClient loggedInClient = server.getLoggedInClient(member.getId());
            if (loggedInClient != null) {
                server.send(message, loggedInClient.getIp(), loggedInClient.getPort());
            } else {
                // client is not logged in
//                PendingGroupMessage pendingGroupMessage = ServerDao.fetchPendingGroupMessage(member, group, true);
//                if (pendingGroupMessage == null) {
//                    System.out.println("pendingGroupMessage FETCHED NULL");
//                    // pending group does not exist
//                    pendingGroupMessage = new PendingGroupMessage(member, group, messageDB);
//                    pendingGroupMessage.setTotalPending(1);
//                    ServerDao.savePendingGroupMessage(pendingGroupMessage);
//                } else {
//                    pendingGroupMessage.toString();
//                    ServerDao.updatePendingGroupMessage(pendingGroupMessage, messageDB,
//                            pendingGroupMessage.getTotalPending() + 1);
//                }
            }
        }


    }

    private Group saveGroupFile(int groupID, String senderID, String fileName, String serverFileName) {
        RegisteredClient sender = ServerDao.fetchClient(senderID);
        Group group = server.getGroupManager().getGroup(groupID);
        if (group == null) {
            System.out.println("GROUP FETCHED NULL");
            return null;
        }
        Message parent = ServerDao.fetchParent(group);
        String message = tcpIdentifier + serverFileName + identityIdentifier + fileName;
        GroupMessage messageDB = new GroupMessage(message, new Date(), sender, group);
        messageDB.setParent(parent);
        if (ServerDao.saveGroupMessage(messageDB))
            System.out.println("GROUP FILE SAVED");
        else
            System.out.println("GROUP FILE NOT SAVED");

        return group;
    }

    private void managePrivateChatFile(String privateChatID, String senderID, String fileName,
                                       byte[] byteArr) {

        System.out.println("PRIVATE CHAT FILE");
        System.out.println("IDs: " + privateChatID + " : " + senderID + " : " + fileName);

        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String serverFileName = privateChatID + "_" + new Date().getTime() + extension;

        storeToServer(FILE_PATH_MEDIA, serverFileName, byteArr);

        RegisteredClient receiver = savePrivateFile(privateChatID, senderID, fileName, serverFileName);
        if (receiver == null) {
            System.out.println("RECEIVER RETURNED NULL");
            return;
        }

        LoggedInClient receiverLoggedIn = server.getLoggedInClient(receiver.getId());
        if (receiverLoggedIn != null) {
            /**
             * sends a msg like: "/t//p/privateChatID/i/fileName/i/fileSize/i/serverFileName"
             */
            String message = tcpIdentifier + privateChatIdentifier + privateChatID + identityIdentifier + fileName
                    + identityIdentifier + byteArr.length + identityIdentifier + serverFileName;
            System.out.println("MESSAGE SENT: " + message);

            server.send(message, receiverLoggedIn.getIp(), receiverLoggedIn.getPort());

        } else {
            // receiver is not logged in -> pending
        }
    }

    private void storeToServer(String filePathStr, String serverFileName, byte[] byteArr) {
        File filePath = new File(filePathStr);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File file = null;
        String fileCat = null;
        try {
            file = new File(filePathStr + serverFileName);
            Path path = file.toPath();
            String fileType = Files.probeContentType(path);
            if (fileType == null) {
                String pathStr = path.toString();
                String newPathStr = pathStr.substring(0, pathStr.lastIndexOf("."));
                Path newPath = Paths.get(newPathStr);
                fileType = Files.probeContentType(newPath);
            }

            fileCat = fileType.split("/")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileCat.equalsIgnoreCase("text")) {
            saveFile(file, byteArr, true);
        } else {
            saveFile(file, byteArr, false);
        }

    }

    private void saveFile(File file, byte[] byteArr, boolean useCharacterStream) {

        if (useCharacterStream) {
            System.out.println("USING CHARACTER STREAM FOR FILE: " + file.getName());
            char[] charArr = byte2CharArr(byteArr);
            FileWriter fw = null;
            BufferedWriter bw = null;
            try {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(charArr, 0, charArr.length);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        System.out.println("USING BYTE STREAM FOR FILE: " + file.getName());
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(byteArr, 0, byteArr.length);
            bos.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private RegisteredClient savePrivateFile(String privateChatID, String senderID, String fileName, String serverFileName) {
        RegisteredClient sender = ServerDao.fetchClient(senderID);
        PrivateChat privateChat = ServerDao.fetchPrivateChat(privateChatID);
        if (privateChat == null) {
            System.out.println("PRIVATE CHAT FETCHED NULL");
            return null;
        }
        RegisteredClient receiver;
        if (sender.getId().equals(privateChat.getClient1().getId()))
            receiver = privateChat.getClient2();
        else
            receiver = privateChat.getClient1();

        Message parent = ServerDao.fetchParent(privateChat);
        String message = tcpIdentifier + serverFileName + identityIdentifier + fileName;
        PrivateMessage messageDB = new PrivateMessage(message, new Date(), sender, privateChat);
        messageDB.setParent(parent);

        if (ServerDao.savePrivateMessage(messageDB))
            System.out.println("PRIVATE FILE SAVED");
        else
            System.out.println("PRIVATE FILE NOT SAVED");

        return receiver;
    }

    public void send(String message, Socket tcpSocket) {
        System.out.println("INSIDE SEND METHOD ON SERVER");
        String serverFileName = message;

        File file = null;
        String fileCat = null;
        try {
            file = new File(FILE_PATH_MEDIA + "/" + serverFileName);
            Path path = file.toPath();
            String fileType = Files.probeContentType(path);
            if (fileType == null) {
                String pathStr = path.toString();
                String newPathStr = pathStr.substring(0, pathStr.lastIndexOf("."));
                Path newPath = Paths.get(newPathStr);
                fileType = Files.probeContentType(newPath);
            }
            fileCat = fileType.split("/")[0];

        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] byteArr;
        if (fileCat.equalsIgnoreCase("text"))
            byteArr = readFile(file, true);
        else
            byteArr = readFile(file, false);

        DataOutputStream dos = null;

        try {
            dos = new DataOutputStream(tcpSocket.getOutputStream());
            dos.write(byteArr, 0, byteArr.length);
            dos.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
                tcpSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] readFile(File file, boolean useCharacterStream) {
        byte[] byteArr = null;

        if (useCharacterStream) {
            System.out.println("USING CHARACTER STREAM FOR FILE: " + file.getName());
            FileReader fr = null;
            BufferedReader br = null;

            char[] charArr = new char[(int) file.length()];
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                br.read(charArr, 0, charArr.length);

                byteArr = char2ByteArray(charArr);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return byteArr;
        }

        System.out.println("USING BYTE STREAM FOR FILE: " + file.getName());
        byteArr = new byte[(int) file.length()];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            bis.read(byteArr, 0, byteArr.length);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return byteArr;
    }

    private byte[] char2ByteArray(char[] chars) {
        int length = chars.length;
        byte[] result = new byte[length * 2];
        int i = 0;
        for (int j = 0; j < chars.length; j++) {
            result[i++] = (byte) ((chars[j] & 0xFF00) >> 8);
            result[i++] = (byte) ((chars[j] & 0x00FF));
        }
        return result;
    }

    private static char[] byte2CharArr(byte[] byteArr) {
        char[] charArr = new char[byteArr.length / 2];
        for (int i = 0; i < charArr.length; i++) {
            charArr[i] = (char) (((byteArr[i * 2] & 0xff) << 8) + (byteArr[i * 2 + 1] & 0xff));
        }
        return charArr;
    }

    public void createDirectoryStructure() {
        String userHome = com.app.server.util.Util.getHomeDir();
        String appDir = userHome + "/Igen/Server";
        String arr[] = new String[2];
        arr[0] = appDir + "/Media";
        arr[1] = appDir + "/Central";

        for (int i = 0; i < arr.length; i++) {
            File file = new File(arr[i]);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }
}
