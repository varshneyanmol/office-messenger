package com.app.client.chatWindowGUI.list;

import com.app.client.Client;

import javax.swing.*;

public class CentralFilesList extends ListWithIcons{
    ImageIcon toDownloadIcon = new ImageIcon("src/com/app/client/resources/icons/toDownload.png");
    ImageIcon downloadedIcon = new ImageIcon("src/com/app/client/resources/icons/downloaded.png");

    public CentralFilesList(Client client, ListCellRenderer<Object> cellRenderer, int cellHeight) {
        super(client, cellRenderer, cellHeight);
        setTrueStatusIcon(toDownloadIcon);
        setFalseStatusIcon(downloadedIcon);
    }

}
