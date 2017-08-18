package com.app.client.test;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) {

        System.out.println(System.getProperty("user.dir"));



















//        String fileName = "ha.doop.tar.gz";
//        String[] arr = fileName.split("[.]");
//        if (arr.length == 0) {
//            System.out.println("Could not split File Name");
//            return;
//        }
//        for (int i = 0; i < arr.length; i++) {
//            System.out.println("arr[" + i + "]: " + arr[i]);
//        }
//        String temp = "";
//        int i = 0;
//        File file = null;
//        String fileCat = null;
//        do {
//            System.out.println("temp: " + temp);
//            temp = temp + arr[i];
//            Path path = Paths.get(temp);
//            try {
//                fileCat = Files.probeContentType(path);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println("FILE CAT NULL");
//            if (fileCat == null)
//                temp += ".";
//            i++;
//        } while (fileCat == null);
//        System.out.println("temp outside: " + temp);
//        temp = temp.substring(0, temp.lastIndexOf("."));
//        System.out.println("temp formatted: " + temp);
//
//        String ext = fileName.replaceFirst(temp, "");
//        System.out.println("ext: " + ext);
//        System.out.println(ext.length());

    }
}
