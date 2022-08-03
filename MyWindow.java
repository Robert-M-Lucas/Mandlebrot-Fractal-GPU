package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyWindow extends JFrame {
    public MyWindow(int size, int screenshot_res_multiplier){
        super();

        MyPanel panel = new MyPanel(size);

        setContentPane(panel);

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'p') {
                    System.out.println("\nScreenshot");
                    BufferedImage bufferedImage = new BufferedImage(size*screenshot_res_multiplier, size*screenshot_res_multiplier, BufferedImage.TYPE_INT_RGB);

                    //Saves and modifies variables to zoom in
                    int[] save_ints = new int[]{panel.maxiter, panel.logic.offset[0], panel.logic.offset[1], panel.logic.arr_size};
                    double mod_save = panel.logic.mod;

                    panel.size = panel.size * screenshot_res_multiplier;
                    panel.logic.size = size * screenshot_res_multiplier;
                    panel.maxiter = panel.maxiter * 2;
                    panel.logic.offset[0] = panel.logic.offset[0]*screenshot_res_multiplier;
                    panel.logic.offset[1] = panel.logic.offset[1]*screenshot_res_multiplier;
                    panel.logic.mod = panel.logic.mod * (1.0/screenshot_res_multiplier);
                    panel.logic.data = new byte[(size * screenshot_res_multiplier) * (size * screenshot_res_multiplier) * 3];
                    panel.logic.arr_size = panel.logic.size * panel.logic.size;
                    panel.logic.Calculate(false);

                    // Create a graphics which can be used to draw into the buffered image
                    Graphics2D g2d = bufferedImage.createGraphics();
                    panel.paint(g2d);
                    g2d.dispose();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
                    Date date = new Date();
                    File file = new File(formatter.format(date)+".png");
                    try {
                        ImageIO.write(bufferedImage, "png", file);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    System.out.println(file.getAbsolutePath());

                    //Resets variables
                    System.out.println("Reloading after screenshot...");
                    panel.maxiter = save_ints[0];
                    panel.logic.data = new byte[size * size * 3];
                    panel.size = size;
                    panel.logic.size = size;
                    panel.logic.offset[0] = save_ints[1];
                    panel.logic.offset[1] = save_ints[2];
                    panel.logic.arr_size = save_ints[3];
                    panel.logic.mod = mod_save;
                }
                panel.logic.Calculate();

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 5) {
                    System.out.println("\nZoom");
                    panel.logic.enlarge();
                }
                else if (e.getButton() == 1) {
                    System.out.println("\nMove");
                    panel.logic.calc_offset(e.getX()/(double) size, e.getY()/(double) size);
                }
                else if (e.getButton() == 3) {
                    System.out.println("\nContrast");
                    panel.maxiter = panel.maxiter*2;
                }
                else if (e.getButton() == 4) {
                    System.out.println("\nZoom out");
                    panel.logic.zoom_out();
                }
                panel.logic.Calculate();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}
