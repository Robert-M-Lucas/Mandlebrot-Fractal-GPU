package com.company;

import com.aparapi.Kernel;
import java.math.BigDecimal;

public class MyLogic {


    Thread calc_in_progress;
    // Rendered image data
    byte[] data;

    int arr_size;

    double mod;
    int size;
    int maxiter;

    int[] offset = new int[]{0, 0};

    MyPanel master;

    // Colour scale
    int[] colour_difference;
    int[] colour_from;
    int[] colour_black;

    // Lower resolutions before final render
    int[] lower_res = new int[] {50, 25, 10, 5, 1};
    int lower_res_index = 0;

    Kernel kernel;

    public MyLogic(int in_size, MyPanel _master, int[] _colour_difference, int[] _colour_from, int[] _colour_black){
        colour_difference = _colour_difference;
        master = _master;

        data = new byte[in_size*in_size * 3];
        arr_size = in_size * in_size;

        mod = (1.0/in_size) * 2.0;
        size = in_size;
        colour_from = _colour_from;
        colour_black = _colour_black;

        // GPU section
        kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId() * lower_res[lower_res_index];
                int x = i % size;
                int y = (i / size) * lower_res[lower_res_index];
                i = (y * size) + x;

                double za = ((x - (size / 2.0) + offset[0]) * mod);
                double zb = ((y - (size / 2.0) + offset[1]) * mod);
                double ca = za;
                double cb = zb;

                for( int n = 0; n < maxiter; n++){
                    if ((za * za) + (zb * zb) > 4.0 * 10) {
                        // Adjusted to use full byte
                        data[i] = (byte) ((int) (colour_from[0] + (colour_difference[0] * (n / (float) maxiter)))-128);
                        data[(arr_size) + i] = (byte) ((int) (colour_from[1] + (colour_difference[1] * (n / (float) maxiter)))-128);
                        data[(arr_size * 2) + i] = (byte) ((int) (colour_from[2] + (colour_difference[2] * (n / (float) maxiter)))-128);

                        return;
                    }

                    /* Uncomment for burning fractal
                    if (za < 0){
                        za *= -1;
                    }
                    if (zb < 0){
                        zb *= -1;
                    }
                    */

                    double _za = (za * za) - (zb * zb) + ca;
                    double _zb = (2 * za * zb) + cb;
                    za = _za;
                    zb = _zb;
                }

                data[i] = (byte) (colour_black[0]-128);
                data[(arr_size) + i] = (byte) (colour_black[1]-128);
                data[(arr_size * 2) + i] = (byte) (colour_black[2]-128);

            }
        };
    }

    public void Calculate() {
        if (calc_in_progress != null && calc_in_progress.isAlive()) {
            calc_in_progress.interrupt();
        }
        calc_in_progress = new Thread(new Runnable() {
            @Override
            public void run() {
                CalculateThread();
            }
        });
        calc_in_progress.start();
    }

    public void Calculate(boolean threaded) {
        if (threaded) { Calculate(); }
        else { CalculateThread(); }
    }

    public void CalculateThread() {
        maxiter = master.maxiter;
        System.out.print("Calculations: ");

        data = new byte[size * size * 3];
        for (int i = 0; i < lower_res.length; i++) {
            lower_res_index = i;
            kernel.execute(arr_size / (lower_res[lower_res_index] * lower_res[lower_res_index]));
            master.paintImmediately(0, 0, size, size);
        }

        /*
        Old Non-threaded implementation
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                GetIterCount((x - (size/2.0) + offset[0]) * mod, (y - (size/2.0) + offset[1]) * mod, (y*size)+x);

            }

        Threaded implementation
        Thread[] threads = new Thread[Runtime.getRuntime().availableProcessors()];
        Boolean[] complete = new Boolean[threads.length]
        Divides pixels between thread
        for (int thread = 0; thread < threads.length; thread++) {
            int from = (int) ((size*size)/(double) threads.length)*thread;
            threads[thread] = new Thread(new MyThread(from, (int) (from + (size*size)/(double) threads.length), size, maxiter, data, offset, mod, thread, complete, colour_difference, colour_from));
            threads[thread].start();
        }

        //Waits for all threads to complete
        boolean all_complete = false;

        while (all_complete == false){
            all_complete = true;
            for (int thread = 0; thread < Runtime.getRuntime().availableProcessors(); thread++) {
                try {
                    if (complete[thread] == false) {
                        all_complete = false;
                    }
                }
                catch (NullPointerException e) {
                    all_complete = false;
                }
            }
        }
        */

        System.out.println("Done");
    }

    public void enlarge() {
        mod = mod * 0.5;
        offset[0] += offset[0];
        offset[1] += offset[1];
    }

    public void zoom_out() {
        mod = mod * 2;
        offset[0] *= 0.5;
        offset[1] *= 0.5;
    }

    public void calc_offset(double x, double y) {
        offset[0] += (size) * (x - 0.5);
        offset[1] += (size) * (y - 0.5);
    }

    /*
    Non-threaded implementation
    public void GetIterCount(double ca, double cb, int pixel){
        double za = ca;
        double zb = cb;
        for (int n = 0; n < maxiter; n++){
            if ((za * za) + (zb * zb) > 4.0) {
                data[pixel] = n;
                return;
            }
            double _za = (za*za) - (zb*zb) + ca;
            double _zb = (2*za*zb) + cb;
            za = _za;
            zb = _zb;
//
        data[pixel] = maxiter;
        return;
    }
    */
}
