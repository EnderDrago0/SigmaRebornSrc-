package com.mentalfrostbyte.jello.event.events;

import net.minecraft.world.World;

public class WorldChangeEvent implements Event {
	public World oldWorld, newWorld;
	
	public WorldChangeEvent(World oldWorld, World newWorld) {
        this.oldWorld = oldWorld;
        this.newWorld = newWorld;
    }
	
	
}
