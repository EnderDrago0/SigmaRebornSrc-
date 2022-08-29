package com.mentalfrostbyte.jello.event.events;

import net.minecraft.entity.Entity;

public class EventAttack implements Event{
	
	public Entity target;
	public EventAttack(Entity tar) {
		this.target = tar;
	}
	public Entity getEntity() {
		return target;
	}
}
