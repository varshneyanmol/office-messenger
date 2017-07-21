package com.app.client.chatWindowGUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.app.client.Client;
import com.app.client.group.Group;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class MainChatWindow extends JFrame {
	private final int BROADCAST_GROUP_ID = 0;
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private Client client;

	private JTabbedPane chatAreaPane;
	private JTabbedPane optionsAreaPane;
	private ChatPanel broadcastChat;

	private BroadcastPanel broadcastPanel;
	private GroupsPanel groupsPanel;

	private ArrayList<Group> groups;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String broadcastIdentifier = config.getString("broadcast-identifier");
	private String groupIdentifier = config.getString("group-identifier");
	private String logoutIdentifier = config.getString("logout-identifier");

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

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);
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

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
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

		broadcastPanel = new BroadcastPanel();
		groupsPanel = new GroupsPanel();

		optionsAreaPane.add("Broadcast", broadcastPanel.getPanel());
		optionsAreaPane.add("Groups", groupsPanel.getPanel());

		broadcastChat = new ChatPanel(client);
		ArrayList<Integer> group1Members = new ArrayList<Integer>();
		group1Members.add(111);
		group1Members.add(222);
		group1Members.add(333);
		Group group1 = new Group("Funky", group1Members, client);

		ArrayList<Integer> group2Members = new ArrayList<Integer>();
		group1Members.add(111);
		group1Members.add(222);
		group1Members.add(555);
		Group group2 = new Group("hellalalala", group2Members, client);

		JPanel broadcastChatPanel = broadcastChat.getPanel();

		addPanel("Broadcast", broadcastChatPanel);
		addPanel(group1.getName(), group1.chatPanel());
		addPanel(group2.getName(), group2.chatPanel());

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

	private void logout(Client client) {
		/**
		 * sends a msg like: "/x/clientID"
		 */
		String message = logoutIdentifier + client.getId();
		client.send(message);
	}

	public void process(String message) {
		/**
		 * receives a message like: "/b/message" OR
		 * "/g//b/groupID/i/clientUserName/i/message"
		 */
		if (message.startsWith(broadcastIdentifier)) {
			message = message.substring(broadcastIdentifier.length(), message.length());
			broadcastChat.styleBroadcastMessage(message);

		} else if (message.startsWith(groupIdentifier)) {

		} else {
			broadcastChat.console(message, null, true);
		}
	}

	public void updateList(String client, boolean isOnline) {
		broadcastPanel.updateClient(client, isOnline);
	}

	public void updateList(String client, boolean isOnline, int groupID) {
		if (groupID == BROADCAST_GROUP_ID) {
			broadcastPanel.updateClient(client, isOnline);
		}
	}

	public void removeFromList(String clientUserName, int groupID) {
		// removes the user from group's list -> pending
	}

	private void addPanel(String panelName, JPanel panel) {
		chatAreaPane.add(panelName, panel);
	}

	public void logoutAllFromLists() {
		// clear the list of all the groups -> pending
		broadcastPanel.logoutAllFromLists();
	}
}
