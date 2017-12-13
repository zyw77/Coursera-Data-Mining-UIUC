package com.company;

import java.io.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        int[] T = new int[301];
        int[] C = new int[301];
        read(T, "input data/partitions.txt"); // relative path

        double[] NMI = new double[5];
        double[] Jaccard = new double[5];
        for (int i = 1; i <= 5; i++) {
            String name = "input data/clustering_" + i + ".txt"; // relative path
            read(C, name);
            NMI[i - 1] = NMIAlgo(T, C);
            Jaccard[i - 1] = JaccardAlgo(T, C);
        }
        write(NMI, Jaccard);
        return;
    }
    private static double JaccardAlgo(int[] T, int[] C) {
        double result = 0;
        int[][] table = new int[5][3];
        for (int i = 1; i <= 300; i++) {
            int cI = C[i];
            int tI = T[i];
            table[cI][tI]++;
        }
        int TP = 0;
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                int count = table[i][j];
                if (count != 0 || count != 1) {
                    TP += count * (count - 1) / 2;
                }
            }
        }
        int FN = -TP;
        int[] m = new int[3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < table.length; j++) {
                m[i] += table[j][i];
            }
            if (m[i] != 0 || m[i] != 1) {
                FN += m[i] * (m[i] - 1) / 2;
            }
        }
        int FP = -TP;
        int[] n = new int[5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                n[i] += table[i][j];
            }
            if (n[i] != 0 || n[i] != 1) {
                FP += n[i] * (n[i] - 1) / 2;
            }
        }
        result = (double)TP / (TP + FN + FP); // cast!
        return result;
    }
    private static double NMIAlgo(int[] T, int[] C) {
        double[] tP = new double[5];
        double[] cP = new double[5];
        double[][] p = new double[5][5];
        for (int i = 1; i <= 300; i++) {
            int tI = T[i];
            int cI = C[i];
            tP[tI]++;
            cP[cI]++;
            p[tI][cI]++;
        }
        for (int i = 0; i < 5; i++) {
            tP[i] /= 300;
            cP[i] /= 300;
            for (int j = 0; j < 5; j++) {
                p[j][i] /= 300;
            }
        }
        double HT = getEntropy(tP);
        double HC = getEntropy(cP);
        double MI = getMI(tP, cP, p);
        double NMI = MI / Math.sqrt(HC * HT);
        return NMI;
    }
    private static double getEntropy(double[] p) {
        double result = 0;
        for (int i = 0; i < p.length; i++) {
            if (p[i] != 0) {
                result -= p[i] * Math.log(p[i]);
            }
            // lim 0 * log0 = 0
        }
        return result;
    }
    private static double getMI(double[] tP, double[] cP, double[][] p) {
        double result = 0;
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < p[0].length; j++) {
                if (p[i][j] != 0) {
                    result += p[i][j] * Math.log(p[i][j] / (tP[i] * cP[j]));
                }
            }
        }
        return result;
    }
    private static void read(int[] A, String fileName) {
        String lineTxt = null;
        try {
            String encoding = "GBK";
            File file = new File(fileName);
            if (file.isFile() && file.exists()) { // if file exists
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding); // consider encoding format
                BufferedReader bufferedReader = new BufferedReader(read);
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String[] array = lineTxt.split(" ");
                    int index = Integer.valueOf(array[0]);
                    int t = Integer.valueOf(array[1]);
                    A[index] = t;
                }
                read.close();
            } else {
                System.out.println("can not find file");
            }
        } catch (Exception e) {
            System.out.println("read error");
            e.printStackTrace();
        }
    }
    private static void write(double[] NMI, double[] Jaccard) {
        String outputEncodedPath = "scores.txt";
        try {
            File file = new File(outputEncodedPath);

            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getName());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < 5; i++) {
                bw.write(NMI[i] + " " + Jaccard[i] + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
