package com.mentalfrostbyte.jello.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.event.types.EventType2;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.AStarCustomPathFinder;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.RenderingUtil;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mentalfrostbyte.jello.util.Vec3;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import java.awt.Color;
public class InfiniteAura extends Module {

	private double dashDistance = 5;
    private ArrayList<Vec3> path = new ArrayList<>();
    private List<Vec3>[] test = new ArrayList[50];
    private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
    private TimerUtil cps = new TimerUtil();
    public static TimerUtil timer = new TimerUtil();
    public static boolean canReach;
    private NumberValue aps = new NumberValue("APS", 1, 1, 10, 1);
    private NumberValue range = new NumberValue("Range", 100, 1, 1000, 1);
    private ArrayList<Vec3> lastPos = new ArrayList<>();
    
	public InfiniteAura() {
        super("InfiniteAura", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(2);
        this.addValue(range, aps);
    }
   
	public void onEnable(){
		EventManager.register(this);
		timer.reset();
    	targets.clear();
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}
	
    @Override
	public void onUpdate(){ 
    	if(!this.isToggled())
    		return;
    	//String mode = ((Options) settings.get(MODE).getValue()).getSelected();
    //    this.setSuffix(mode);
        int maxtTargets = 1;
     //   if (event instanceof EventUpdate) {
   //        EventUpdate em = (EventUpdate) event;
            int delayValue = (20 / 4 * 50);//cps
          //  double hypixelTimer = ((Number) settings.get(TIMER).getValue()).doubleValue()*1000;
         //   if (em.isPre()) {
               
                targets = getTargets();

                if (cps.hasTimeElapsed(delayValue, false))
                    if (targets.size() > 0) {
                        test = new ArrayList[50];
                        for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
                            EntityLivingBase T = targets.get(i);
                            Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                            Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                            
                            path = computePath(topFrom, to);
                            test[i] = path;
                            for (Vec3 pathElm : path) {
                            	
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                            }

                            mc.thePlayer.swingItem();
                            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(targets.get(0), Action.ATTACK));
                            Collections.reverse(path);
                            for (Vec3 pathElm : path) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                            }
                        }
                        cps.reset();
                    }
          //  }
    //    }
    }
			
	  //  }
	
    @EventTarget
    public void onre(EventRender3D e) {
    	 if (!targets.isEmpty()) {
             if (targets.size() > 0) {
                 for (int i = 0; i < (targets.size() > 1 ? 1 : targets.size()); i++) {
                     int color = 0xff42b9f5;
                  //   drawESP(targets.get(i), color);
                 }

             }
         }
         if (!path.isEmpty()) {
             for (int i = 0; i < targets.size(); i++) {
                 try {
                     if (test != null)
                         for (Vec3 pos : test[i]) {
                             if (pos != null)
                                 drawPath(pos);
                         }
                 } catch (Exception es) {

                 }
             }

             if (cps.hasTimeElapsed(1000 ,false)) {
                 test = new ArrayList[50];
                 path.clear();
             }
         }
     }
    boolean validEntity(EntityLivingBase entity) {
    //    float range = ((Number) settings.get(RANGE).getValue()).floatValue();
     //   boolean players = (Boolean) settings.get(PLAYERS).getValue();
      //  boolean animals = (Boolean) settings.get(ANIMALS).getValue();

        if ((mc.thePlayer.isEntityAlive())
                && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= range.getValue()) {
                if (AntiBot.bot.contains(entity)) {
                    return false;
                }
                if (entity.isPlayerSleeping()) {
                    return false;
                }
                if (entity instanceof EntityPlayer) {
                    if (true) {

                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive()
                                && player.getHealth() == 0.0) {
                            return false;
                        }
                    //    else if (TeamUtils.isTeam(mc.thePlayer, player)
                    //            && (Boolean) settings.get(TEAMS).getValue()) {
                   //         return false;
                   //     }
                  //      else if (player.isInvisible()
                  //              && !(Boolean) settings.get(INVISIBLES)
                   //             .getValue()) {
                   //         return false;
                  //      }
                    //    else if (FriendManager.isFriend(player.getName())) {
                    //        return false;
                      //  }
                        else
                            return true;
                    }
                } else {
                    if (!entity.isEntityAlive()) {

                        return false;
                    }
                }

                if (entity instanceof EntityMob && true) {//if (entity instanceof EntityMob && animals) {

                    return true;
                }
                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)
                        && true) {//&& animals) {
                    if (entity.getName().equals("Villager")) {
                        return false;
                    }
                    return true;
                }
            }
        }

        return false;
    }
	private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        if (!canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }
        AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();

        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<Vec3>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }

    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }
    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();
//        targets.clear();
        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (validEntity(entity)) {
                    targets.add(entity);
                }
            }
        }
        targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) * 1000 - o2.getDistanceToEntity(mc.thePlayer) * 1000));
        return targets;
    }
    public void drawESP(Entity entity, int color) {
        double x = entity.lastTickPosX
                + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;

        double y = entity.lastTickPosY
                + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;

        double z = entity.lastTickPosZ
                + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX);
        double height = Math.abs(entity.boundingBox.maxY - entity.boundingBox.minY);
        Vec3 vec = new Vec3(x - width / 2, y, z - width / 2);
        Vec3 vec2 = new Vec3(x + width / 2, y + height, z + width / 2);
        RenderingUtil.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderingUtil.glColor(color);
        RenderingUtil.drawBoundingBox(new AxisAlignedBB(
                vec.getX() - RenderManager.renderPosX, vec.getY() - RenderManager.renderPosY, vec.getZ() - RenderManager.renderPosZ,
                vec2.getX() - RenderManager.renderPosX, vec2.getY() - RenderManager.renderPosY, vec2.getZ() - RenderManager.renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderingUtil.post3D();
    }

    public void drawPath(Vec3 vec) {
        double x = vec.getX() - RenderManager.renderPosX;
        double y = vec.getY() - RenderManager.renderPosY;
        double z = vec.getZ() - RenderManager.renderPosZ;
        double width = 0.3;
        double height = mc.thePlayer.getEyeHeight();
        RenderingUtil.pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        int colors[] = {getColor(Color.black), getColor(Color.white)};
        for (int i = 0; i < 2; i++) {
            RenderingUtil.glColor(colors[i]);
            GL11.glLineWidth(3 - i * 2);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glEnd();
        }

        RenderingUtil.post3D();
    }
    public int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }
}
