package com.app.client;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class LoginClientWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldUserName;
	private JPasswordField textFieldPassword;

	public LoginClientWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Login Client");
		setSize(320, 250);
		setLocationRelativeTo(null);
		setResizable(false);
		getContentPane().setLayout(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createRegisterClientWindow();
			}
		});
		btnRegister.setBounds(210, 12, 80, 25);
		getContentPane().add(btnRegister);

		JLabel lblUserName = new JLabel("User name");
		lblUserName.setBounds(20, 60, 90, 25);
		getContentPane().add(lblUserName);

		textFieldUserName = new JTextField();
		textFieldUserName.setBounds(130, 60, 160, 25);
		getContentPane().add(textFieldUserName);
		textFieldUserName.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(20, 120, 70, 25);
		getContentPane().add(lblPassword);

		textFieldPassword = new JPasswordField();
		textFieldPassword.setBounds(130, 120, 160, 25);
		getContentPane().add(textFieldPassword);
		textFieldPassword.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userName = textFieldUserName.getText();
				String password = String.copyValueOf(textFieldPassword.getPassword());

				if (!(userName.equals("") || password.equals(""))) {
					/*
					 * if (validate(userName, password)) { // main Chat window }
					 * else { showInvalidDialog("Invalid username or password."
					 * ); }
					 */
					new Client().loginClient(userName, password);
					new ClientWindow();
					dispose();
				}
			}
		});
		btnLogin.setBounds(100, 185, 120, 25);
		getContentPane().add(btnLogin);
		setVisible(true);
	}

	private void createRegisterClientWindow() {
		this.setVisible(false);
		new RegisterClientWindow(this);
	}

	/*
	 * private boolean validate(String username, String password) { boolean
	 * result = false;
	 *//**
		 * Code to validate user name and password
		 *//*
		 * 
		 * return result; }
		 */
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
		invalidDialog.setSize(250, 100);
		invalidDialog.setLocationRelativeTo(null);
		invalidDialog.setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new LoginClientWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
