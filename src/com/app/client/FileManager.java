package com.app.client;

import com.app.client.util.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class FileManager {
    private Client client;

    private static FileManager fileManager = null;
    private final String CENTRAL_PATH;

    private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
    private String identityIdentifier = config.getString("identity-identifier");
    private String broadcastIdentifier = config.getString("broadcast-identifier");
    private String groupIdentifier = config.getString("group-identifier");
    private String privateChatIdentifier = config.getString("private-chat-identifier");
    private String tcpIdentifier = config.getString("tcp-identifier");

    private FileManager(Client client) {

        this.client = client;
        CENTRAL_PATH = client.getDbPath() + "/Central/";
    }

    public static FileManager getFileManager(Client client) {
        if (fileManager == null) {
            fileManager = new FileManager(client);
        }
        return fileManager;
    }

    public void process(String message) {
        String fileName = null;
        String serverFileName = null;
        long fileSize = 1L;

        if (message.startsWith(privateChatIdentifier)) {
            /**
             * receives a msg like: "/p/privateChatID/i/fileName/i/fileSize/i/serverFileName"
             */
            String[] arr = message.split(privateChatIdentifier + "|" + identityIdentifier);
            String privateChatID = arr[1];
            fileName = arr[2];
            fileSize = Long.parseLong(arr[3]);
            serverFileName = arr[4];

        } else if (message.startsWith(groupIdentifier)) {
            /**
             * receives a msg to logged in members like: "/g/groupID/i/senderUserName/i/fileName/i/fileSize/i/serverFileName"
             */
            String[] arr = message.split(groupIdentifier + "|" + identityIdentifier);
            int groupID = Integer.parseInt(arr[1]);
            String senderUserName = arr[2];
            fileName = arr[3];
            fileSize = Long.parseLong(arr[4]);
            serverFileName = arr[5];
        }

        byte[] byteArr = new byte[(int) fileSize];
        client.receiveFile(byteArr, serverFileName, fileName);
        //storeToClient(fileName, byteArr);
    }

    public void storeToClient(String fileName, byte[] byteArr) {
        System.out.println("INSIDE storeToClient() METHOD");
        String filePathStr = client.getDbPath() + "/Media/Received/";
        File filePath = new File(filePathStr);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        //  File file;
        String fileCat = null;

        try {
            Path path = Paths.get(filePath.getAbsolutePath() + "/" + fileName);
            String fileType = Files.probeContentType(path);
            if (fileType == null) {
                Path _path = Paths.get(filePath.getAbsolutePath() + "/" + fileName.substring(0, fileName.lastIndexOf(".")));
                fileType = Files.probeContentType(_path);
            }
            fileCat = fileType.split("/")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileCat.equalsIgnoreCase("text")) {
            filePathStr = filePathStr + "Documents/";
            saveFile(filePathStr, fileName, byteArr, true);

        } else if (fileCat.equalsIgnoreCase("image")) {
            filePathStr = filePathStr + "Images/";
            saveFile(filePathStr, fileName, byteArr, false);

        } else if (fileCat.equalsIgnoreCase("video")) {
            filePathStr = filePathStr + "Videos/";
            saveFile(filePathStr, fileName, byteArr, false);

        } else if (fileCat.equalsIgnoreCase("audio")) {
            filePathStr = filePathStr + "Audios/";
            saveFile(filePathStr, fileName, byteArr, false);

        } else {
            filePathStr = filePathStr + "Documents/";
            saveFile(filePathStr, fileName, byteArr, false);

        }


    }

    private void saveFile(String filePathStr, String fileName, byte[] byteArr, boolean useCharacterStream) {
        File filePath = new File(filePathStr);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        File file = new File(filePath.getAbsolutePath() + "/" + fileName);

        if (useCharacterStream) {
            System.out.println("USING CHARACTER STREAM FOR FILE: " + file.getName());
            char[] charArr = byte2CharArr(byteArr);
            FileWriter fw = null;
            BufferedWriter bw = null;
            try {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                System.out.println("WRITTEN: " + new String(charArr));
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

    private static char[] byte2CharArr(byte[] byteArr) {
        char[] charArr = new char[byteArr.length / 2];
        for (int i = 0; i < charArr.length; i++) {
            charArr[i] = (char) (((byteArr[i * 2] & 0xff) << 8) + (byteArr[i * 2 + 1] & 0xff));
        }
        return charArr;
    }

    public boolean isCentralFileDownloaded(String fileName) {
        File centralRepo = new File(CENTRAL_PATH + "Downloads/");
        if (!centralRepo.exists()) {
            centralRepo.mkdirs();
        }

        File file = new File(CENTRAL_PATH + fileName);
        return file.exists();
    }

    public void createDirectoryStructure() {
        String userHome = Util.getHomeDir();
        String appDir = userHome + "/Igen/Client_" + client.getUserName();
        String arr[] = new String[5];
        arr[0] = appDir + "/Media/Sent";
        arr[1] = appDir + "/Media/Received";
        arr[2] = appDir + "/Central/Uploads";
        arr[3] = appDir + "/Central/Downloads";
        arr[4] = appDir + "/Database";

        for (int i = 0; i < arr.length; i++) {
            File file = new File(arr[i]);
                if (!file.exists()) {
                    file.mkdirs();
                }

        }
    }
}