package com.mentalfrostbyte.jello.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ColorManager {
	public List<ColorObject> getColorObjectList() {
        return colorObjectList;
    }

    private List<ColorObject> colorObjectList = new CopyOnWriteArrayList<>();

    public ColorObject getHudColor() {
        return hudColor;
    }

    public ColorObject hudColor = new ColorObject(0, 192, 255);

    public ColorObject getXhairColor() {
        return ch;
    }
    public ColorObject getESPColor() {
        return esp;
    }

    public static ColorObject ch = new ColorObject(255, 0, 0);
    public static ColorObject esp = new ColorObject(0, 192, 255, 100);

    public ColorManager() {
        colorObjectList.add(hudColor);
        colorObjectList.add(ch);
        colorObjectList.add(esp);
    }
}
