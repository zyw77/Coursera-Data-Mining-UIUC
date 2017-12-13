package com.company;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // write your code here
        try {
            File file = new File("patterns.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getName()); // ensure to create a new file
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<String>> record = getRecord(); // table
        Map<List<String>, Integer> firstFreqSet = getFirstFreqSet(record); // F1
        generate(firstFreqSet, record);
    }
    private static void generate(Map<List<String>, Integer> firstFreqSet, List<List<String>> record) {
        Map<List<String>, Integer> prevFreqSet = firstFreqSet;
        List<List<String>> prevFreqList = new ArrayList<>();
        for (Map.Entry<List<String>, Integer> entry : firstFreqSet.entrySet()) {
            prevFreqList.add(entry.getKey()); // extract key in Map into List, easy to traverse
        }
        List<List<String>> candidate = new ArrayList<>();
        Map<List<String>, Integer> freqSet = new HashMap<>();
        while (prevFreqList.size() != 0) {
            print(prevFreqSet);
            for (int i = 0; i < prevFreqList.size(); i++) { // traverse list, two two combine together, generate new candidate set
                for (int j = i + 1; j < prevFreqList.size(); j++) {
                    // L(k-1) -> L(k)
                    // if we want to combine l1 and l2 in L(k-1)together, l1 and l2 should satisfy these two conditions:
                    // 1. differentElementNumber(l1, l2) = 1
                    // 2. l1 combines l2 => l3(with k elements), any subset with k-1 elements in l3 should belong to L(k-1)
                    List<String> l1 = prevFreqList.get(i);
                    List<String> l2 = prevFreqList.get(j);
                    if (!isDiff1(l1, l2)) { // check condition 1
                        continue;
                    }
                    List<String> newList = combine(l1, l2);
                    boolean exist = false;
                    for (List<String> existList : candidate) {
                        if (equals(newList, existList)) { // check if we have generate the same list before!
                            exist = true;
                            break;
                        }
                    }
                    if (!exist && check(newList,prevFreqSet)) {
                        candidate.add(newList);
                    }
                }
            }
            for (List<String> list : candidate) { // count number, generate new frequent set
                freqSet.put(list, 0);
                int count = 0;
                boolean exist;
                for (List<String> transaction : record) {
                    exist = true;
                    for (String s : list) {
                        if (!transaction.contains(s)) {
                            exist = false;
                            break;
                        }
                    }
                    if (exist) {
                        count++;
                    }
                }
                if (count <= 771) { // threshold
                    freqSet.remove(list);
                } else {
                    freqSet.put(list, count);
                }
            }
            // prepare for next iteration
            prevFreqSet = freqSet;
            prevFreqList = new ArrayList<>();
            for (Map.Entry<List<String>, Integer> entry : prevFreqSet.entrySet()) {
                prevFreqList.add(entry.getKey());
            }
            candidate = new ArrayList<>();
            freqSet = new HashMap<>();
        }
    }
    private static boolean check(List<String> list, Map<List<String>, Integer> prevFreq) {
        // check condition 2
        for (int i = 0; i < list.size(); i++) {
            List<String> temp = new ArrayList<>(list); // deep copy
            temp.remove(i);
            boolean flag = true;
            for (Map.Entry<List<String>, Integer> entry : prevFreq.entrySet()) {
                flag = false;
                if (equals(temp, entry.getKey())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
        }
        return true;
    }
    private static boolean equals(List<String> l1, List<String> l2) {
        for (String s : l1) {
            if (!l2.contains(s)) {
                return false;
            }
        }
        return true;
    }
    private static List<String> combine(List<String> l1, List<String> l2) {
        List<String> result = new ArrayList<>(l1);
        for (String s : l2) {
            if (!result.contains(s)) {
                result.add(s);
                break;
            }
        }
        return result;
    }
    private static boolean isDiff1(List<String> l1, List<String> l2) {
        // check condition 1
        int diff = 0;
        for (String s : l1) {
            if (!l2.contains(s)) {
                diff++;
            }
            if (diff > 1) {
                return false;
            }
        }
        return true;
    }
    private static Map<List<String>, Integer> getFirstFreqSet(List<List<String>> record) {
        Map<String, Integer> temp = new HashMap<>(); // use String as key, easy to count number
        for (List<String> list : record) {
            for (String category : list) {
                if (!temp.containsKey(category)) {
                    temp.put(category, 0);
                }
                int count = temp.get(category);
                temp.put(category, count + 1);
            }
        }
        Map<List<String>, Integer> firstFreqSet = new HashMap<>(); // use List<String> as key
        // put each string in a list, prepare for subsequent operation
        for (Map.Entry<String, Integer> entry : temp.entrySet()) {
            if (entry.getValue() > 771) { // threshold
                List<String> list = new ArrayList<>();
                list.add(entry.getKey());
                firstFreqSet.put(list, entry.getValue());
            }
        }
        return firstFreqSet;
    }
    private static List<List<String>> getRecord() {
        List<List<String>> record = new ArrayList<>();
        String lineTxt = null;
        try {
            String encoding = "GBK";
            File file = new File("categories.txt");
            if (file.isFile() && file.exists()) { // if file exists
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// consider encoding format
                BufferedReader bufferedReader = new BufferedReader(read);
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.equals("")) {
                        continue;
                    }
                    List<String> list = new ArrayList<>();
                    String[] array = lineTxt.split(";");
                    for (String category : array) {
                        list.add(category);
                    }
                    record.add(list);
                }
                read.close();
            } else {
                System.out.println("can not find file");
            }
        } catch (Exception e) {
            System.out.println("read error");
            e.printStackTrace();
        }
        return record;
    }
    private static void print(Map<List<String>, Integer> hash) {
        try {
            File file = new File("patterns.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getName(), true); // write to existing file
            BufferedWriter bw = new BufferedWriter(fw);
            for (Map.Entry<List<String>, Integer> entry : hash.entrySet()) {
                List<String> list = entry.getKey();
                bw.write(entry.getValue() + ":");
                for (int i = 0; i < list.size() - 1; i++) {
                    bw.write(list.get(i) + ";");
                }
                bw.write(list.get(list.size() - 1) + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
