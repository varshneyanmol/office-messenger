package com.app.client;

import java.util.ResourceBundle;

public class CentralManager {
    private static CentralManager centralManager = null;
    private Client client = null;

    private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
    private String updateListIdentifier = config.getString("update-list-identifier");
    private String identityIdentifier = config.getString("identity-identifier");

    private CentralManager(Client client) {
        this.client = client;
    }

    public static CentralManager getCentralManager(Client client) {
        if (centralManager == null) {
            centralManager = new CentralManager(client);
        }
        return centralManager;
    }

    public void process(String message) {
        if (message.startsWith(updateListIdentifier)) {
            message = message.substring(updateListIdentifier.length(), message.length());
            processUpdateTableMessage(message);
        }
    }

    private void processUpdateTableMessage(String message) {
        /**
         * receives a message like: "anmol_e1,fileName1,time1/i/sug_e2,fileName2,time2/i/"
         */
        String[] arr = message.split(identityIdentifier);
        for (int i = 0; i < arr.length; i++) {
            String[] arrSplit = arr[i].split(",");
            if (isFileDownloaded(arrSplit[1])) {
                client.getCentralWindow().addFile(false, arrSplit[0], arrSplit[1], arrSplit[2]);
            } else {
                client.getCentralWindow().addFile(true, arrSplit[0], arrSplit[1], arrSplit[2]);
            }
        }
    }

    private boolean isFileDownloaded(String fileName) {
        FileManager fileManager = client.getFileManager();
        return fileManager.isCentralFileDownloaded(fileName);
    }
}
