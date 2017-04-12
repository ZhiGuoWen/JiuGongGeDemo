package com.wenzhiguo.jiugonggedemo;

/**
 * Created by dell on 2017/4/7.
 */

public class Point {
    public float x;
    public float y;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PRESSED = 1;
    public static final int STATE_ERROR = 2;
    public int state;
    public int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
