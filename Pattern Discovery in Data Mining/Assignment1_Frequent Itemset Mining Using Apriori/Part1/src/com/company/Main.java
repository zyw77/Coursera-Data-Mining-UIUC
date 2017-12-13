package com.company;

import java.io.*;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        // write your code here
        String lineTxt = null;
        HashMap<String, Integer> hash = new HashMap<>(); // <category count>
        try {
            String encoding = "GBK";
            File file = new File("categories.txt");
            if (file.isFile() && file.exists()) { // if file exists
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding); // consider encoding format
                BufferedReader bufferedReader = new BufferedReader(read);
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.equals("")) {
                        continue;
                    }
                    String[] array = lineTxt.split(";");
                    for (String category : array) {
                        if (!hash.containsKey(category)) {
                            hash.put(category, 0);
                        }
                        int count = hash.get(category);
                        hash.put(category, count + 1);
                    }
                }
                read.close();
            } else {
                System.out.println("can not find file");
            }
        } catch (Exception e) {
            System.out.println("read error");
            e.printStackTrace();
        }
        try {
            File file = new File("patterns.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getName());
            BufferedWriter bw = new BufferedWriter(fw);
            for (HashMap.Entry<String, Integer> entry : hash.entrySet()) {
                if (entry.getValue() > 771) { // threshold
                    bw.write(entry.getValue() + ":" + entry.getKey() + '\n');
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
