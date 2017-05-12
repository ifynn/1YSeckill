package com.fynn.oyseckill.widget.dialog;

import android.graphics.Color;

/**
 * Created by fynn on 16/6/18.
 */
public class SheetItem {

    private String text;
    private float size = 14;
    private int color = Color.parseColor("#4F4F4F");

    public SheetItem() {

    }

    public SheetItem(String text) {
        this.text = text;
    }

    public SheetItem(String text, float size) {
        this.text = text;
        this.size = size;
    }

    public SheetItem(String text, int color) {
        this.text = text;
        this.color = color;
    }

    public SheetItem(String text, float size, int color) {
        this.text = text;
        this.size = size;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public SheetItem setText(String text) {
        this.text = text;
        return this;
    }

    public float getSize() {
        return size;
    }

    public SheetItem setSize(float size) {
        this.size = size;
        return this;
    }

    public int getColor() {
        return color;
    }

    public SheetItem setColor(int color) {
        this.color = color;
        return this;
    }
}
