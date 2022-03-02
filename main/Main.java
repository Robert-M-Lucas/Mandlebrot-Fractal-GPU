package main;

import javax.swing.*;

public class Main {


    public static void main(String[] args) {
        int screenshot_res_multiplier = 15;

        int size = 1000;


        System.out.println(((Integer) Runtime.getRuntime().availableProcessors()).toString() + " threads available");
        System.out.println(((Integer) size) + " x " + ((Integer) size) + " resolution");


        MyWindow my_window = new MyWindow(size, screenshot_res_multiplier);
        my_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        my_window.setSize(size, size+39);
        my_window.setResizable(false);
        my_window.setVisible(true);
    }
}
