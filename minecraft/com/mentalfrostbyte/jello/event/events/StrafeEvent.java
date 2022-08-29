package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.events.callables.EventCancellable;

import net.minecraft.client.Minecraft;

public class StrafeEvent extends EventCancellable{

	 private float forward, strafe, friction;

	 private Minecraft mc = Minecraft.getMinecraft();
	 
	    public void setSpeedPartialStrafe(float friction, final float strafe) {
	        final float remainder = 1 - strafe;

	        if (forward != 0 && this.strafe != 0) {
	            friction *= 0.91;
	        }

	        if (mc.thePlayer.onGround) {
	            setSpeed(friction);
	        } else {
	            mc.thePlayer.motionX *= strafe;
	            mc.thePlayer.motionZ *= strafe;
	            setFriction(friction * remainder);
	        }
	    }

	    public void setSpeed(final float speed, final double motionMultiplier) {
	        setFriction(getForward() != 0 && getStrafe() != 0 ? speed * 0.99F : speed);
	        mc.thePlayer.motionX *= motionMultiplier;
	        mc.thePlayer.motionZ *= motionMultiplier;
	    }

	    public void setSpeed(final float speed) {
	        setFriction(getForward() != 0 && getStrafe() != 0 ? speed * 0.99F : speed);
	        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
	    }
	
}
