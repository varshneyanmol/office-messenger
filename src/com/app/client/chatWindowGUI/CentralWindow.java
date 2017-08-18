package com.app.client.chatWindowGUI;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.app.client.Client;
import com.app.client.chatWindowGUI.list.TableWithIcons;

public class CentralWindow extends JFrame {
    private Client client;
    private JPanel contentPane;

    private JFileChooser fileChooser;
    private TableWithIcons centralFilesTable;
    private TableWithIcons searchTable = null;
    JScrollPane scrollPane;

    public CentralWindow(Client client) {
        this.client = client;
        generateWindow();
    }

    private void generateWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileChooser = new JFileChooser();
        centralFilesTable = new TableWithIcons(client);
        centralFilesTable.setTrueIcon(new ImageIcon("src/com/app/client/resources/icons/toDownload.png"));
        centralFilesTable.setFalseIcon(new ImageIcon("src/com/app/client/resources/icons/downloaded.png"));
        String[] tableCols = {"Status", "Uploaded by", "File name", "Time"};
        centralFilesTable.setColumns(tableCols);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Central");
        setSize(600, 550);
        setLocationRelativeTo(null);
        setResizable(true);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{35, 500, 60, 5};
        gbl_contentPane.rowHeights = new int[]{10, 40, 450, 40, 10};
        gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JLabel lblFiles = new JLabel("Files currently in the Central repository:");
        Font font = lblFiles.getFont();
        Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
        lblFiles.setFont(boldFont);
        GridBagConstraints gbc_lblFilesCurrentlyIn = new GridBagConstraints();
        gbc_lblFilesCurrentlyIn.insets = new Insets(0, 0, 5, 5);
        gbc_lblFilesCurrentlyIn.gridx = 1;
        gbc_lblFilesCurrentlyIn.gridy = 1;
        gbc_lblFilesCurrentlyIn.weightx = 1;
        gbc_lblFilesCurrentlyIn.weighty = 0;
        contentPane.add(lblFiles, gbc_lblFilesCurrentlyIn);

        JButton btnUpload = new JButton("upload");
        GridBagConstraints gbc_btnUpload = new GridBagConstraints();
        gbc_btnUpload.insets = new Insets(0, 0, 5, 5);
        gbc_btnUpload.gridx = 2;
        gbc_btnUpload.gridy = 1;
        gbc_btnUpload.weightx = 0;
        gbc_btnUpload.weighty = 0;
        btnUpload.addActionListener(e -> {
            browseFile();
        });
        contentPane.add(btnUpload, gbc_btnUpload);

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(centralFilesTable.getTable());
        GridBagConstraints gbc_filesList = new GridBagConstraints();
        gbc_filesList.insets = new Insets(0, 0, 5, 5);
        gbc_filesList.fill = GridBagConstraints.BOTH;
        gbc_filesList.gridx = 1;
        gbc_filesList.gridy = 2;
        gbc_filesList.gridwidth = 2;
        gbc_filesList.weightx = 1;
        gbc_filesList.weighty = 1;
        contentPane.add(scrollPane, gbc_filesList);


        JTextField searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                search(searchField.getText());
            }
        });
        GridBagConstraints gbc_search = new GridBagConstraints();
        gbc_search.insets = new Insets(0, 0, 0, 5);
        gbc_search.fill = GridBagConstraints.BOTH;
        gbc_search.gridx = 1;
        gbc_search.gridy = 3;
        gbc_search.weightx = 1;
        gbc_search.weighty = 0;
        contentPane.add(searchField, gbc_search);


        JButton btnDownload = new JButton("download");
        btnDownload.addActionListener(e -> {
            download();
        });
        GridBagConstraints gbc_btnDownload = new GridBagConstraints();
        gbc_btnDownload.insets = new Insets(0, 0, 0, 5);
        gbc_btnDownload.gridx = 2;
        gbc_btnDownload.gridy = 3;
        gbc_btnDownload.weightx = 0;
        gbc_btnDownload.weighty = 0;
        contentPane.add(btnDownload, gbc_btnDownload);
    }

    private void download() {
        String[][] arr;
        if (searchTable != null) {
            arr = searchTable.getFilesToDownload();
        } else {
            arr = centralFilesTable.getFilesToDownload();
        }
        for (int i = 0; i < arr.length; i++) {
            client.downloadCentralFile(arr[i][0], arr[i][1]);
        }
    }

    private void search(String text) {
        searchTable = centralFilesTable.search(text);
        if (searchTable != null) {
            scrollPane.setViewportView(searchTable.getTable());
        } else {
            scrollPane.setViewportView(centralFilesTable.getTable());
        }
    }

    protected void browseFile() {
        int result = fileChooser.showOpenDialog(contentPane);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            client.uploadCentralFile(selectedFile);
        }
    }

    public void addFile(boolean status, String uploaderUserName, String fileName, String uploadTimeStr) {
        Object[] row = new Object[4];
        if (status) {
            row[0] = true;

        } else {
            row[0] = false;

        }
        row[1] = uploaderUserName;
        row[2] = fileName;
        row[3] = uploadTimeStr;
        centralFilesTable.addRow(row);

    }
}
