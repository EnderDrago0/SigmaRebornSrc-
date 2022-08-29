package com.mentalfrostbyte.jello.util;

import java.util.ArrayList;

public class HWIDUtil {
	/*
	Copyright Krypton
	https://github.com/kkrypt0nn
	*/
	
	
	
	
	
	
	public static String getHWID() {
        String returnhwid = "";
        String hwid = System.getProperty("user.name") + System.getProperty("user.home") + System.getProperty("os.version") + System.getProperty("os.name");
        for (String s : getSubstrings(hwid)) {
            returnhwid = returnhwid + convertToString(s);
        }
        return returnhwid;
    }
	public static enum LETTERS {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;
    }
    public static String get(int letter) {
        return LETTERS.values()[letter].name();
    }
    public static ArrayList<String> getSubstrings(String s) {
        ArrayList<String> substrings = new ArrayList<String>();
        for (int i = 0; i < s.length(); i++) {
            String substring = s.substring(i, i + 1);
            substrings.add(substring);
        }
        return substrings;
    }
	
	public static int convertToString(String lette) {
        LETTERS letter = null;
        for (int i = 0; i < LETTERS.values().length; i++) {
            if (get(i).equalsIgnoreCase(lette)) {
                letter = LETTERS.values()[i];
            }
        }
        int string = -1;
        if (letter == letter.A) {
            string = 1;
        } else if (letter == letter.B) {
            string = 2;
        } else if (letter == letter.C) {
            string = 3;
        } else if (letter == letter.D) {
            string = 4;
        } else if (letter == letter.E) {
            string = 5;
        } else if (letter == letter.F) {
            string = 6;
        } else if (letter == letter.G) {
            string = 7;
        } else if (letter == letter.H) {
            string = 8;
        } else if (letter == letter.I) {
            string = 9;
        } else if (letter == letter.J) {
            string = 10;
        } else if (letter == letter.K) {
            string = 11;
        } else if (letter == letter.L) {
            string = 12;
        } else if (letter == letter.M) {
            string = 13;
        } else if (letter == letter.N) {
            string = 14;
        } else if (letter == letter.O) {
            string = 15;
        } else if (letter == letter.P) {
            string = 16;
        } else if (letter == letter.Q) {
            string = 17;
        } else if (letter == letter.R) {
            string = 18;
        } else if (letter == letter.S) {
            string = 19;
        } else if (letter == letter.T) {
            string = 20;
        } else if (letter == letter.U) {
            string = 21;
        } else if (letter == letter.V) {
            string = 22;
        } else if (letter == letter.W) {
            string = 23;
        } else if (letter == letter.X) {
            string = 24;
        } else if (letter == letter.Y) {
            string = 25;
        } else if (letter == letter.Z) {
            string = 26;
        }
        return string;
    }
}
