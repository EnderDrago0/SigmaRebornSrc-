package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.events.callables.EventCancellable;

import net.minecraft.entity.EntityLivingBase;

public class EventRenderEntity extends EventCancellable{
	private boolean canceled;
	private EntityLivingBase entity;

	public EventRenderEntity(EntityLivingBase entity) {
		this.entity = entity;
	}
	
    public EntityLivingBase getEntity() {
		return entity;
	}

	public void setEntity(EntityLivingBase entity) {
		this.entity = entity;
	}

	@Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public void setCancelled(boolean state) {
        this.canceled = state;
    }

}
