package main;

import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel {
    MyLogic logic;
    int size;
    int maxiter = 100;

    //Low iter colour
    int[] colour_from = new int[]{255, 255, 0};
    //High iter colour
    int[] colour_to = new int[]{255, 0, 0};

    int[] colour_difference = new int[]{colour_to[0]-colour_from[0], colour_to[1]-colour_from[1], colour_to[2]-colour_from[2]};

    int draw_state = 0;

    public class PaintThread implements Runnable {
        int _from;
        int _to;
        Graphics _g;
        boolean[] _complete;
        int _index;

        public PaintThread(int from, int to, Graphics g, boolean[] complete, int index){
            _from = from;
            _to = to;
            _g = g;
            _complete = complete;
            _index = index;
        }

        @Override
        public void run() {
            for (int y = _from; y < _to; y++) {
                for (int x = 0; x < size; x++) {
                    _g.setColor(new Color(logic.data[(y*size)+x], logic.data[(y*size)+x + logic.arr_size], logic.data[(y*size)+x + logic.arr_size*2]));
                    _g.drawRect(x, y, 10, 10);
                }
            }

            _complete[_index] = true;
        }
    }

    @Override
    public void paint(Graphics g){
        draw_state = 1;
        System.out.print("Drawing: ");

        /* Painting cant be threaded
        boolean threaded = false;

        if (threaded) {
            Thread[] threads = new Thread[Runtime.getRuntime().availableProcessors()];
            boolean[] complete = new boolean[threads.length];

            //Divides pixels between threads

            for (int thread = 0; thread < threads.length; thread++) {
                threads[thread] = new Thread(new PaintThread((int) (size / threads.length) * thread, (int) (size / threads.length) * (thread + 1), g, complete, thread));
                threads[thread].start();
            }

            //Waits for all threads to complete
            boolean all_complete = false;

            while (all_complete == false) {
                all_complete = true;
                for (int thread = 0; thread < Runtime.getRuntime().availableProcessors(); thread++) {
                    try {
                        if (complete[thread] == false) {
                            all_complete = false;
                        }
                    } catch (NullPointerException e) {
                        all_complete = false;
                    }
                }
            }
        }
        */

        for (int y = 0; y < size; y+=logic.lower_res[logic.lower_res_index]) {
            for (int x = 0; x < size; x+=logic.lower_res[logic.lower_res_index]) {
                // Adjusted for using full byte
                g.setColor(new Color(logic.data[(y*size)+x] + 128,
                        logic.data[(y*size)+x + logic.arr_size] + 128,
                        logic.data[(y*size)+x + logic.arr_size*2] + 128));
                g.fillRect(x, y, logic.lower_res[logic.lower_res_index], logic.lower_res[logic.lower_res_index]);
            }
        }

        System.out.println("Done");
        draw_state = 2;
    }

    public MyPanel(int in_size) {
        setFocusable(true);
        requestFocusInWindow();
        size = in_size;

        logic = new MyLogic(in_size, this, colour_difference, colour_from);
        logic.Calculate();
    }
}
