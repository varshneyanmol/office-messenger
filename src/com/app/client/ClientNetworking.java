package com.app.client;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ClientNetworking {
    private DatagramSocket socket;
    private InetAddress serverIP;
    private int serverPort;
    private Thread send;
    private Client client;

    ResourceBundle config = ResourceBundle.getBundle("com.app.config");
    private final int MAX_PACKET_SIZE = Integer.parseInt(config.getString("max-packet-size"));
    private final String fileSendIdentifier = config.getString("file-send-identifier");
    private final String identityIdentifier = config.getString("identity-identifier");

    public ClientNetworking(InetAddress serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public boolean openConnection() {

        try {
            socket = new DatagramSocket(); // Opens a sockets at any
            // available port
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public DatagramPacket receive() {
        byte[] data = new byte[MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }

    public void send(byte[] data) {
        send = new Thread("Send_Client") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, serverPort);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    public void close() {
        new Thread() {
            public void run() {
                synchronized (socket) {
                    socket.close();
                }
            }
        };
    }

    public void sendFile(File selectedFile, String message) {
        send = new Thread("send file") {
            public void run() {
                String fileCat = getFileCat(selectedFile);
                String filePath;
                byte[] byteArr;

                if (fileCat.equalsIgnoreCase("text")) {
                    filePath = client.getDbPath() + "/Media/Sent/Documents";
                    byteArr = readFile(selectedFile, filePath, true);

                } else if (fileCat.equalsIgnoreCase("image")) {
                    filePath = client.getDbPath() + "/Media/Sent/Images";
                    byteArr = readFile(selectedFile, filePath, false);

                } else if (fileCat.equalsIgnoreCase("video")) {
                    filePath = client.getDbPath() + "/Media/Sent/Videos";
                    byteArr = readFile(selectedFile, filePath, false);

                } else if (fileCat.equalsIgnoreCase("audio")) {
                    filePath = client.getDbPath() + "/Media/Sent/Audios";
                    byteArr = readFile(selectedFile, filePath, false);

                } else {
                    filePath = client.getDbPath() + "/Media/Sent/Documents";
                    byteArr = readFile(selectedFile, filePath, false);
                }

                Socket tcpSocket = null;
                DataOutputStream dos = null;

                try {
                    /**
                     * writing the file to the socket
                     */
                    tcpSocket = new Socket(serverIP, serverPort);
                    dos = new DataOutputStream(tcpSocket.getOutputStream());
                    dos.writeUTF(message);
                    /**
                     * used <code>byteArr.length</code> because in case file is read through character stream,
                     * <code>byteArr.length</code> will be twice of <code>selectedFile.length</code>
                     */
                    dos.writeLong((long) byteArr.length);
                    dos.write(byteArr, 0, byteArr.length);
                    dos.flush();

                } catch (IOException e) {
                    System.out.println("could not open TCP socket");
                    e.printStackTrace();
                } finally {
                }
                try {
                    dos.close();
                    tcpSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    private String getFileCat(File selectedFile) {
        String mimeType = null;
        try {
            Path path = selectedFile.toPath();
            mimeType = Files.probeContentType(path);
            if (mimeType == null) {
                String pathStr = path.toString();
                String newPathStr = pathStr.substring(0, pathStr.lastIndexOf("."));
                Path newPath = Paths.get(newPathStr);
                mimeType = Files.probeContentType(newPath);
            }

        } catch (IOException e) {
            System.out.println("Could not probe the content type of the file...");
            e.printStackTrace();
        }

        String fileCat = mimeType.split("/")[0];
        return fileCat;
    }

    /**
     * @param selectedFile
     * @param filePath
     * @param useCharacterStream
     * @return byteArr
     * reads <code>selectedFile</code> from client chosen location and copies the file into client's OfficeMessenger Database. And returns a byteArr
     */
    private byte[] readFile(File selectedFile, String filePath, boolean useCharacterStream) {
        byte[] byteArr = null;
        File _filePath = new File(filePath);
        if (!_filePath.exists()) {
            _filePath.mkdirs();
        }

        if (useCharacterStream) {
//            System.out.println("USING CHARACTER STREAM FOR FILE: " + selectedFile.getName());
            FileReader fr = null;
            BufferedReader br = null;
            FileWriter fw = null;
            BufferedWriter bw = null;

            char[] charArr = new char[(int) selectedFile.length()];
            try {
                fr = new FileReader(selectedFile);
                br = new BufferedReader(fr);
                br.read(charArr, 0, charArr.length);

                /**
                 * copying the file into sender's database in its machine
                 */
                fw = new FileWriter(_filePath.getAbsolutePath() + "/" + selectedFile.getName());
                bw = new BufferedWriter(fw);
                bw.write(charArr, 0, charArr.length);
                bw.flush();
                byteArr = char2ByteArray(charArr);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bw.close();
                    fw.close();
                    br.close();
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return byteArr;
        }

        System.out.println("USING BYTE STREAM FOR FILE: " + selectedFile.getName());
        byteArr = new byte[(int) selectedFile.length()];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fis = new FileInputStream(selectedFile);
            bis = new BufferedInputStream(fis);
            bis.read(byteArr, 0, byteArr.length);

            /**
             * copying the file into sender's database in its machine
             */
            fos = new FileOutputStream(_filePath.getAbsolutePath() + "/" + selectedFile.getName());
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
                bis.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return byteArr;
    }

    private byte[] char2ByteArray(char[] chars) {
        int length = chars.length;
        byte[] result = new byte[length * 2];
        int i = 0;
        for (int j = 0; j < chars.length; j++) {
            result[i++] = (byte) ((chars[j] & 0xFF00) >> 8);
            result[i++] = (byte) ((chars[j] & 0x00FF));
        }
        return result;
    }


    public void receiveFile(byte[] byteArr, String serverFileName, String fileName) {
        send = new Thread("send client") {
            public void run() {
                Socket tcpSocket = null;
                DataOutputStream dos = null;
                DataInputStream dis = null;

                String message = fileSendIdentifier + serverFileName;
                try {
                    tcpSocket = new Socket(serverIP, serverPort);
                    dos = new DataOutputStream(tcpSocket.getOutputStream());
                    dos.writeUTF(message);

                    dis = new DataInputStream(tcpSocket.getInputStream());
                    dis.readFully(byteArr, 0, byteArr.length);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        dos.close();
                        dis.close();
                        tcpSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                client.getFileManager().storeToClient(fileName, byteArr);
            }
        };
        send.start();
    }

    public void setClient(Client client) {
        this.client = client;
    }
}