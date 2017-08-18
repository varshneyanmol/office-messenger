package com.app.client.chatWindowGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.app.client.Client;
import com.app.client.chat.Broadcast;
import com.app.client.chat.Group;
import com.app.client.chat.PrivateChat;

public class MainChatWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private Client client;

	private JSplitPane splitPane;
	private JTabbedPane chatAreaPane;
	private JTabbedPane optionsAreaPane;
	private Broadcast broadcast;

	private BroadcastPanel broadcastPanel;
	private GroupsPanel groupsPanel;

	private ArrayList<Group> groups;
	private ArrayList<PrivateChat> privateChats;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String broadcastIdentifier = config.getString("broadcast-identifier");
	private String groupIdentifier = config.getString("group-identifier");
	private String logoutIdentifier = config.getString("logout-identifier");
	private String identityIdentifier = config.getString("identity-identifier");
	private String chatFormIdentifier = config.getString("chat-form-identifier");

	public MainChatWindow(Client client) {
		this.client = client;
		this.groups = new ArrayList<Group>();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(client.getUserName());
		int windowWidth = 700;
		int windowHeight = 550;
		Dimension windowSize = new Dimension(windowWidth, windowHeight);
		setSize(windowSize);
		setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout(client);
				dispose();
				System.exit(0);
			}
		});
		mnFile.add(exit);
		JMenuItem formGroup = new JMenuItem("Form new group");
		formGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fromGroup();
			}
		});
		mnFile.add(formGroup);

		JMenuItem central = new JMenuItem("Central");
		central.addActionListener(e -> {
			client.central();
		});
		mnFile.add(central);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		JMenuItem hotKeys = new JMenuItem("Hot Keys");
		hotKeys.addActionListener(e -> {
			client.hotKeys();
		});
		mnSettings.add(hotKeys);

		JMenuItem logout = new JMenuItem("Logout");
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout(client);
			}
		});
		mnSettings.add(logout);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setContinuousLayout(true);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		splitPane.setDividerLocation((int) (windowWidth / 3.5));
		splitPane.setDividerSize(2);
		contentPane.add(splitPane, gbc_splitPane);

		optionsAreaPane = new JTabbedPane();
		chatAreaPane = new JTabbedPane();
		chatAreaPane.setBackground(Color.BLUE);

		broadcastPanel = new BroadcastPanel(client);
		groupsPanel = new GroupsPanel(client);

		optionsAreaPane.add("Broadcast", broadcastPanel.getPanel());
		optionsAreaPane.add("Groups", groupsPanel.getPanel());

		broadcast = Broadcast.getBroadcast(client);
		privateChats = new ArrayList<PrivateChat>();
		addPanel(broadcast.getNAME(), broadcast.getPanel());
		splitPane.setLeftComponent(optionsAreaPane);
		splitPane.setRightComponent(chatAreaPane);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				logout(client);
			}
		});

		setVisible(true);
	}

	public Client getClient() {
		return client;
	}

	private void fromGroup() {
		showGroupFormDialog();
	}

	private void showGroupFormDialog() {
		JDialog formGroupDialog = new JDialog(this, "Form new group", true);

		JButton btnOk = new JButton("Form");
		JLabel lblDialog = new JLabel("Group Name");
		JTextField txtName = new JTextField();

		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String groupName = txtName.getText();
				if (!groupName.equals("")) {

					/**
					 * returns a string like:
					 * "client1Username,client1Username,....,"
					 */
					String members = broadcastPanel.getSelectedValues();
					if (!members.equals("")) {
						/**
						 * sends a msg like:
						 * "/g//f/groupName/i/clientID/i/member1UserName,member2UserName,..,"
						 */
						if (client.getId() != null) { // condition of logged out
														// client
							String message = groupIdentifier + chatFormIdentifier + groupName + identityIdentifier
									+ client.getId() + identityIdentifier + members;
							client.send(message);
						}
					}
					formGroupDialog.dispose();
				}
			}
		});

		formGroupDialog.getContentPane().setLayout(new GridLayout(3, 1, 5, 5));
		formGroupDialog.getContentPane().add(lblDialog);
		formGroupDialog.getContentPane().add(txtName);
		formGroupDialog.getContentPane().add(btnOk);
		formGroupDialog.setSize(300, 100);
		formGroupDialog.setLocationRelativeTo(null);
		formGroupDialog.setVisible(true);
	}

	private void logout(Client client) {
		/**
		 * sends a msg like: "/x/clientID"
		 */
		String message = logoutIdentifier + client.getId();
		client.send(message);
	}

	public void process(String message) {
		/**
		 * receives a message like: "/b/clientUserName/i/message" OR
		 * "/g//b/groupID/i/clientUserName/i/message"
		 */
		if (message.startsWith(broadcastIdentifier)) {
			message = message.substring(broadcastIdentifier.length(), message.length());
			String[] arr = message.split(identityIdentifier);
			broadcast.getChatPanel().console(arr[1], arr[0]);

		} else if (message.startsWith(groupIdentifier)) {

		} else {
			chatAreaPane.setSelectedIndex(0);
			broadcast.getChatPanel().console(message, null);
		}
	}

	public void updateList(String client, boolean isOnline) {
		broadcastPanel.updateClient(client, isOnline);
	}

	public void updateList(String client, boolean isOnline, int groupID) {
		if (groupID == broadcast.getBROADCAST_ID()) {
			broadcastPanel.updateClient(client, isOnline);
		}
	}

	public void removeFromList(String clientUserName, int groupID) {
		// removes the user from group's list -> pending
	}

	private void addPanel(String panelName, JPanel panel) {
		chatAreaPane.add(panelName, panel);
		int index = chatAreaPane.indexOfTab(panelName);

		JPanel panelTitle = new JPanel();
		panelTitle.setOpaque(false);
		panelTitle.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 0));

		JLabel title = new JLabel(panelName);
		Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize());
		title.setFont(titleFont);

		JButton close = new JButton();
		close.setIcon(new ImageIcon("src/com/app/client/resources/icons/close-inactive.png"));
		close.setBorder(BorderFactory.createEmptyBorder());
		close.setContentAreaFilled(false);

		panelTitle.add(title);
		panelTitle.add(close);

		chatAreaPane.setTabComponentAt(index, panelTitle);
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				close.setIcon(new ImageIcon("src/com/app/client/resources/icons/close-active.png"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				close.setIcon(new ImageIcon("src/com/app/client/resources/icons/close-inactive.png"));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int index = chatAreaPane.indexOfTab(title.getText());
				System.out.println("index: " + index);
			}
		});
	}

	public void logoutAllFromLists() {
		// clear the list of all the groups -> pending
		broadcastPanel.logoutAllFromLists();
	}

	public void addGroup(int groupID, String groupName, String creatorUserName, String[] members) {
		Group group = new Group(groupID, groupName, creatorUserName, new ArrayList<>(Arrays.asList(members)), client);
		groups.add(group);
		addPanel(groupName, group.getPanel());
	}

	public void processGroupMessage(int groupID, String senderUserName, String message) {
		Group group = getGroupByID(groupID);
		if (group == null) {
			return;
		}
		// message = privateChat.getReceiverUserName() + ": " + message;
		group.getChatPanel().console(message, senderUserName);
	}

	private Group getGroupByID(int groupID) {
		Group group = null;

		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getId() == groupID) {
				group = groups.get(i);
				break;
			}
		}

		return group;
	}

	public void addPrivateChat(String key, String receiverID, String receiverUserName) {
		PrivateChat privateChat = new PrivateChat(key, receiverID, receiverUserName, client);
		privateChats.add(privateChat);
		addPanel(privateChat.getReceiverUserName(), privateChat.getPanel());
	}

	public void processPrivateChatMessage(String privateChatID, String message) {
		PrivateChat privateChat = getPrivateChat(privateChatID);
		if (privateChat == null) {
			return;
		}
		// message = privateChat.getReceiverUserName() + ": " + message;
		privateChat.getChatPanel().console(message, privateChat.getReceiverUserName());
	}

	private PrivateChat getPrivateChat(String privateChatID) {
		PrivateChat privateChat = null;

		for (int i = 0; i < privateChats.size(); i++) {
			if (privateChats.get(i).getId().equals(privateChatID)) {
				privateChat = privateChats.get(i);
				break;
			}
		}

		return privateChat;
	}
}
