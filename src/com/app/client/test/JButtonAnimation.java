package com.app.client.test;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class JButtonAnimation extends JFrame {

	private JPanel contentPane;

	public JButtonAnimation() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPopupMenu popup1 = new JPopupMenu();
		JMenuItem pressed = new JMenuItem("pressed");
		JMenuItem unpressed = new JMenuItem("unpressed");
		popup1.add(pressed);
		popup1.add(unpressed);
		contentPane.add(popup1);

		JPopupMenu popup2 = new JPopupMenu();
		JMenuItem cut = new JMenuItem("Cut");
		JMenuItem copy = new JMenuItem("Copy");
		popup2.add(cut);
		popup2.add(copy);
		contentPane.add(popup2);

		DefaultListModel<String> model = new DefaultListModel<String>();
		JList<String> list = new JList<String>(model);
		model.addElement("hi");
		model.addElement("there");
		model.addElement("how");
		model.addElement("are");
		model.addElement("you");
		model.addElement("wish");
		model.addElement("you");
		model.addElement("luck");

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e)) {
					System.out.println("right clicked from list");
					int index = list.locationToIndex(e.getPoint());
					list.setSelectedIndex(index);
					// list.setBackground(list.getSelectionBackground());
					popup2.show(contentPane, e.getPoint().x, e.getPoint().y);
				} else if (SwingUtilities.isLeftMouseButton(e)) {
					System.out.println("left clicked from list");
					popup1.show(contentPane, e.getX(), getY());
				}
			}
		});
		contentPane.add(list);
		JButton btn = new JButton("popup");
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					System.out.println("right clicked");
					popup2.show(contentPane, e.getX(), e.getY());
				} else if (SwingUtilities.isLeftMouseButton(e)) {
					System.out.println("left clicked");
					popup1.show(contentPane, e.getX(), getY());
				}
			}
		});
		contentPane.add(btn);

		// ImageIcon offlineClientIcon = new
		// ImageIcon("src/com/app/client/resources/icons/onlineClient.png");

		// JMenuBar menuBar = new JMenuBar();
		// setJMenuBar(menuBar);
		// JMenu mnSettings = new JMenu("Settings");
		// mnSettings.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mouseClicked(MouseEvent e) {
		// System.out.println("cliffcked");
		// }
		// });
		// mnSettings.setIcon(offlineClientIcon);
		// menuBar.add(mnSettings);

		// JButton btnSend = new JButton();
		// btnSend.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mousePressed(MouseEvent e) {
		// Point point = btnSend.getLocation();
		// btnSend.setLocation(new Point(point.x + 4, point.y));
		// }
		//
		// @Override
		// public void mouseReleased(MouseEvent e) {
		// Point point = btnSend.getLocation();
		// btnSend.setLocation(new Point(point.x - 4, point.y));
		// }
		// });
		// btnSend.setIcon(new
		// ImageIcon("src/com/app/client/resources/icons/send.png"));
		// btnSend.setBorder(BorderFactory.createEmptyBorder());
		// btnSend.setContentAreaFilled(false);
		// contentPane.add(btnSend);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JButtonAnimation frame = new JButtonAnimation();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
