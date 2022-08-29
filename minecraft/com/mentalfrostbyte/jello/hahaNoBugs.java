package com.mentalfrostbyte.jello;

import com.mentalfrostbyte.jello.hud.JelloHud;

public class hahaNoBugs {
    //wtf is this
	public static boolean TimerThingThatDelaysTheBlurThingToPReventItfromCrashing;

	
    private static long lastCheck = getSystemTime();
    public static boolean hasReach(float mil) {
        return getTimePassed() >= (mil);
    }
    public void reset() {
        lastCheck = getSystemTime();
    }

    private static long getTimePassed() {
        return getSystemTime() - lastCheck;
    }

    private static long getSystemTime() {
        return System.nanoTime() / (long) (1E6);
    }
    public static void bu(float t) {
    	if(hasReach(t) && JelloHud.h) {
    		TimerThingThatDelaysTheBlurThingToPReventItfromCrashing = true;
    	}
    }
	
}
