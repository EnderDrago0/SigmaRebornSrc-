package com.mentalfrostbyte.jello.util;

public class Vec2f {
    public float x;
	public float y;

    public Vec2f() {
        this(0, 0);
    }

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
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
