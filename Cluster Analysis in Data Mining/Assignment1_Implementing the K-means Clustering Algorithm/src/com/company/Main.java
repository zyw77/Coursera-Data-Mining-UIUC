package com.company;

import java.io.*;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
	// write your code here
        // read file places.txt and store points in Point Array
        Point[] places = new Point[300];
        String lineTxt = null;
        try {
            String encoding = "GBK";
            File file = new File("places.txt");
            if (file.isFile() && file.exists()) { // if file exists
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding); // consider encoding format
                BufferedReader bufferedReader = new BufferedReader(read);
                int i = 0; // point id
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String[] array = lineTxt.split(",");
                    Point p = new Point(Double.valueOf(array[0]), Double.valueOf(array[1]), i, -1);
                    places[i] = p;
                    i++;
                }
                read.close();
            } else {
                System.out.println("can not find file");
            }
        } catch (Exception e) {
            System.out.println("read error");
            e.printStackTrace();
        }

        // initialization using K-mean++ Algo
        Random rand = new Random();
        int r = rand.nextInt(300); // randomly picking the a point from 300 points
        Point first = places[r];
        Point second = getFarthest(first, places);
        Point third = getFarthest(second, places);

        // K-means Algo
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Point cur : places) {
                // assign cur point to the nearest cluster
                double d = cur.getD(first);
                int c = 0;
                if (cur.getD(second) < d) {
                    d = cur.getD(second);
                    c = 1;
                }
                if (cur.getD(third) < d) {
                    d = cur.getD(third);
                    c = 2;
                }
                if (c != cur.cluster) {
                    changed = true;
                }
                cur.cluster = c;
            }
            Point[] newC = computeCentroid(places);
            first = newC[0];
            second = newC[1];
            third = newC[2];
        }

        // output
        String outputEncodedPath = "clusters.txt";
        try {
            File file = new File(outputEncodedPath);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getName());
            BufferedWriter bw = new BufferedWriter(fw);
            for (Point cur : places) {
                bw.write(cur.id + " " + cur.cluster + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Point[] computeCentroid(Point[] places) {
        // compute new centroid for each cluster
        Point[] result = new Point[3];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Point(0, 0);
        }
        int[] count = new int[3];
        for (Point cur : places) {
            int cid = cur.cluster;
            result[cid].x += cur.x;
            result[cid].y += cur.y;
            count[cid]++;
        }
        for (int i = 0; i < result.length; i++) {
            result[i].x /= count[i];
            result[i].y /= count[i];
        }
        return result;
    }

    private static Point getFarthest(Point p, Point[] places) {
        Point result = new Point();
        double maxD = 0;
        for (Point cur: places) {
            double d = p.getD(cur);
            if (d >= maxD) {
                maxD = d;
                result = cur;
            }
        }
        return result;
    }
}

class Point {
    public double x;
    public double y;
    public int id;
    public int cluster;

    public Point(double x, double y, int id, int cluster) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.cluster = cluster;
    }
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Point() {

    }

    public double getD(Point p) { // get distance between two points
        double distance = Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
        return distance;
    }
}
