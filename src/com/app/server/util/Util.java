package com.app.server.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
    public static String[] splitFileName(String fileName) {
        String[] arr = fileName.split("[.]");
        if (arr.length == 0) {
            return null;
        }
        String fileNameWithoutExt = "";
        int i = 0;
        Path path;
        String fileCat = null;
        do {
            fileNameWithoutExt = fileNameWithoutExt + arr[i];
            path = Paths.get(fileNameWithoutExt);
            try {
                fileCat = Files.probeContentType(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fileCat == null) {
                fileNameWithoutExt += ".";
                i++;

            } else {
                break;
            }
        } while (true);
        fileNameWithoutExt = fileNameWithoutExt.substring(0, fileNameWithoutExt.lastIndexOf("."));
        String ext = fileName.replaceFirst(fileNameWithoutExt, "");
        String[] fileNameSplit = new String[2];
        fileNameSplit[0] = fileNameWithoutExt;
        fileNameSplit[1] = ext;

        return fileNameSplit;
    }

    public static String modifyCentralFileName(String fileNameWithoutExt, String ext, int copy) {
        fileNameWithoutExt = fileNameWithoutExt + " - (" + copy + ")";
        String fileName = fileNameWithoutExt + ext;
        return fileName;
    }

    public static String getServerCentralFileName(String fileName, String senderID) {
        String serverCentralFileName = senderID + "_" + fileName;
        return serverCentralFileName;
    }

    public static String getHomeDir() {
        return System.getProperty("user.home");
    }
}
