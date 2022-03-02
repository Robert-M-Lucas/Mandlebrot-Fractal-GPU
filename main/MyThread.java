package main;

import java.awt.*;
import java.awt.color.ICC_Profile;

// OLD THREADED IMPLEMENTED

public class MyThread implements Runnable{
    private int from;
    private int to;
    private int size;
    private Color[] data;
    private int maxiter;
    private double mod;
    private int[] offset;
    private int thread;
    private Boolean[] complete;
    private double half_size;
    private  int[] colour_difference;
    private  int[] colour_from;

    public MyThread(int _from, int _to, int _size, int _maxiter, Color[] _data, int[] _offset, double _mod, int _thread, Boolean[] _complete, int[] _colour_difference,
                    int[] _colour_from){
        data = _data;
        maxiter = _maxiter;
        from = _from;
        to = _to;
        size = _size;
        mod = _mod;
        offset = _offset;
        thread = _thread;
        complete = _complete;
        half_size = size/2.0;
        colour_difference = _colour_difference;
        colour_from = _colour_from;

    }

    private void GetIterCount(double ca, double cb, int pixel) {
        double za = ca;
        double zb = cb;
        for (int n = 0; n < maxiter; n++){
            if ((za * za) + (zb * zb) > 4.0) {
                int r = (int) (colour_from[0] + (colour_difference[0]*(n/(float) maxiter)));
                int gr = (int) (colour_from[1] + (colour_difference[1]*(n/(float) maxiter)));
                int b = (int) (colour_from[2] + (colour_difference[2]*(n/(float) maxiter)));
                data[pixel] = new Color(r, gr, b);
                return;
            }

            za = Math.abs(za);
            zb = Math.abs(zb);

            double _za = (za*za) - (zb*zb) + ca;
            double _zb = (2*za*zb) + cb;
            za = _za;
            zb = _zb;
        }

        data[pixel] = Color.BLACK;
        return;
    }

    public void run(){
        for (int pixel = from; pixel < to; pixel++){
            this.GetIterCount(((pixel%size) - (half_size) + offset[0]) * mod, ((pixel/size) - (half_size) + offset[1]) * mod, pixel);
        }
        //Thread finished
        complete[thread] = true;
    }
}
