package com.app.client.chatWindowGUI.list;

import com.app.client.Client;

import javax.swing.*;
import java.util.List;

public class RegisteredClientsList extends ListWithIcons {

    ImageIcon onlineClientIcon = new ImageIcon("src/com/app/client/resources/icons/onlineClient.png");
    ImageIcon offlineClientIcon = new ImageIcon("src/com/app/client/resources/icons/offlineClient.png");

    public RegisteredClientsList(Client client) {
        super(client);
        setTrueStatusIcon(onlineClientIcon);
        setFalseStatusIcon(offlineClientIcon);
    }

    public void logoutAllFromLists() {
        System.out.println("inside logoutAllFromLists()");
        DefaultListModel<ListEntryItem> listModel = getListModel();
        for (int i = 0; i < listModel.getSize(); i++) {
            ListEntryItem item = listModel.get(0);
            if (item.getStatus()) {
                toggleStatus(item);
            } else {
                /**
                 * Lists are being displayed in a manner like all the logged in
                 * clients will be displayed first then all the logged out
                 * clients. SO, if the first logged out client is found in the
                 * list then it is guaranteed that no other elements below it
                 * will be logged in. So it does not need to reverse their status
                 * and It will simply return.
                 */
                return;
            }
        }
    }

    public String getSelectedUserNames() {
        String members = "";
        List<ListEntryItem> selectedItems = getSelectedValues();
        if (!selectedItems.isEmpty()) {
            if (selectedItems.size() == 1 && selectedItems.get(0).getText().equals(getClient().getUserName())) {
                return members;
            }
            boolean flag = true;
            for (int i = 0; i < selectedItems.size(); i++) {
                ListEntryItem item = selectedItems.get(i);
                if (item.getText().equals(getClient().getUserName())) {
                    flag = false;
                }
                members = members + item.getText() + ",";
            }
            if (flag) {
                members = getClient().getUserName() + "," + members;
            }
        }
        return members;
    }

}
