package com.app.client;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.app.client.chatWindowGUI.MainChatWindow;

import java.awt.Font;

public class RegisterClientWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldName;
	private JTextField textFieldDesignation;
	private JTextField textFieldServerIP;
	private JTextField textFieldServerPort;
	private JTextField textFieldEmployeeID;
	private JPasswordField textFieldPassword;
	private JPasswordField textFieldRePassword;

	private LoginClientWindow loginClientWindow;

	public RegisterClientWindow(LoginClientWindow loginClientWindow) {
		this.loginClientWindow = loginClientWindow;
		generateWindow();
	}

	private void generateWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Register Client");
		setSize(440, 410);
		setLocationRelativeTo(null);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		textFieldName = new JTextField();
		textFieldName.setFont(new Font("SansSerif", Font.PLAIN, 14));
		textFieldName.setText("Anmol");
		textFieldName.setBounds(195, 20, 200, 25);
		contentPane.add(textFieldName);
		textFieldName.setColumns(10);

		textFieldEmployeeID = new JTextField();
		textFieldEmployeeID.setFont(new Font("SansSerif", Font.PLAIN, 14));
		textFieldEmployeeID.setText("111");
		textFieldEmployeeID.setBounds(195, 60, 200, 25);
		contentPane.add(textFieldEmployeeID);
		textFieldEmployeeID.setColumns(10);

		textFieldDesignation = new JTextField();
		textFieldDesignation.setFont(new Font("SansSerif", Font.PLAIN, 14));
		textFieldDesignation.setText("DEVELOPER");
		textFieldDesignation.setBounds(195, 100, 200, 25);
		contentPane.add(textFieldDesignation);
		textFieldDesignation.setColumns(10);

		textFieldServerIP = new JTextField();
		textFieldServerIP.setFont(new Font("SansSerif", Font.PLAIN, 14));
		textFieldServerIP.setText("127.0.0.1");
		textFieldServerIP.setBounds(195, 140, 200, 25);
		contentPane.add(textFieldServerIP);
		textFieldServerIP.setColumns(10);

		textFieldServerPort = new JTextField();
		textFieldServerPort.setFont(new Font("SansSerif", Font.PLAIN, 14));
		textFieldServerPort.setText("7890");
		textFieldServerPort.setBounds(195, 180, 200, 25);
		contentPane.add(textFieldServerPort);
		textFieldServerPort.setColumns(10);

		textFieldPassword = new JPasswordField();
		textFieldPassword.setText("anaconda");
		textFieldPassword.setBounds(195, 220, 200, 25);
		contentPane.add(textFieldPassword);
		textFieldPassword.setColumns(10);

		textFieldRePassword = new JPasswordField();
		textFieldRePassword.setText("anaconda");
		textFieldRePassword.setBounds(195, 260, 200, 25);
		contentPane.add(textFieldRePassword);
		textFieldRePassword.setColumns(10);

		JLabel lblName = new JLabel("Name");
		lblName.setBounds(35, 20, 70, 25);
		contentPane.add(lblName);

		JLabel lblEmployeeId = new JLabel("Employee ID");
		lblEmployeeId.setBounds(35, 60, 111, 25);
		contentPane.add(lblEmployeeId);

		JLabel lblDesignation = new JLabel("Designation");
		lblDesignation.setBounds(35, 100, 114, 25);
		contentPane.add(lblDesignation);

		JLabel lblServerIP = new JLabel("Server IP");
		lblServerIP.setBounds(35, 140, 114, 25);
		contentPane.add(lblServerIP);

		JLabel lblServerPort = new JLabel("Server Port");
		lblServerPort.setBounds(35, 180, 114, 25);
		contentPane.add(lblServerPort);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(35, 220, 70, 25);
		contentPane.add(lblPassword);

		JLabel lblRePassword = new JLabel("Repeat Password");
		lblRePassword.setBounds(35, 260, 130, 25);
		contentPane.add(lblRePassword);

		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = textFieldName.getText();
				String employeeID = textFieldEmployeeID.getText();
				String designation = textFieldDesignation.getText();
				String serverIP = textFieldServerIP.getText();
				String serverPort = textFieldServerPort.getText();
				String password = String.copyValueOf(textFieldPassword.getPassword());
				String rePassword = String.copyValueOf(textFieldRePassword.getPassword());

				if (!(name.equals("") || employeeID.equals("") || designation.equals("") || serverIP.equals("")
						|| serverPort.equals("") || password.equals("") || rePassword.equals(""))) {
					if (!password.equals(rePassword)) {
						showInvalidDialog("Passwords did not match.");

					} else {
						try {
							registerClient(name, employeeID, designation, serverIP, serverPort, password);
						} catch (NumberFormatException | UnknownHostException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		btnRegister.setBounds(135, 330, 120, 30);
		contentPane.add(btnRegister);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				loginClientWindow.setVisible(true);
			}
		});

		setVisible(true);
	}

	private void registerClient(String name, String employeeID, String designation, String serverIP, String serverPort,
			String password) throws NumberFormatException, UnknownHostException {

		if (validateValues(employeeID, serverIP, serverPort)) {
			/**
			 * Code to Register the client
			 */
			Client client = new Client(Integer.parseInt(employeeID), name, designation, InetAddress.getByName(serverIP),
					Integer.parseInt(serverPort));
			client.registerClient(password);
			System.out.println("Registered");

			this.dispose();
			loginClientWindow.dispose();

		} else {
			showInvalidDialog("Invalid Values.");
		}
	}

	private void showInvalidDialog(String message) {
		JDialog invalidDialog = new JDialog(this, "Invalid Values", true);
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				invalidDialog.dispose();
			}
		});

		JLabel lblDialog = new JLabel(message);
		invalidDialog.getContentPane().setLayout(new FlowLayout());
		invalidDialog.getContentPane().add(lblDialog);
		invalidDialog.getContentPane().add(btnOk);
		invalidDialog.setSize(200, 100);
		invalidDialog.setLocationRelativeTo(null);
		invalidDialog.setVisible(true);
	}

	private boolean validateValues(String employeeID, String serverIP, String serverPort) {
		boolean result = false;
		/**
		 * Logic to validate Employee ID, Server IP and Server port
		 */
		if (employeeID.matches("[0-9]+")) {
			if (serverIP.matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+")) {
				if (serverPort.matches("[0-9]+")) {
					result = true;
				}
			}
		}
		return result;
	}

}
