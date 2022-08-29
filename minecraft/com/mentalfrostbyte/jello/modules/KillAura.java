package com.mentalfrostbyte.jello.modules;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventPreMotionUpdates;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.event.events.WorldChangeEvent;
import com.mentalfrostbyte.jello.event.types.EventType2;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.AimUtil;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ColorUtil;
import com.mentalfrostbyte.jello.util.MathUtil;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.RenderingUtil;
import com.mentalfrostbyte.jello.util.RotationUtil;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class KillAura extends Module {

	private static boolean players = true;
	private static boolean monsters = false;
	private static boolean animals = false;
	private static boolean neutrals = false;
	private static boolean invisibles = false;
	private Random rand = new Random();
	public static Entity target;
	//private List<EntityLivingBase> entities = new ArrayList<EntityLivingBase>();
	private NumberValue range;
	private NumberValue aps;
    private BooleanValue autoblock;
    private BooleanValue pl;
    private BooleanValue gm;
    private BooleanValue an;
    private BooleanValue mo;
    private BooleanValue team;
    private BooleanValue rotations;
    public static boolean enabled;
	public static boolean attacking = false;
	public static EntityLivingBase closestE;
	public static boolean blocking;
	private float lastX;
	private float lastY;
	private ModeValue mode = new ModeValue("Mode", "Single", "Single", "Switch");
	private final ModeValue rotationMode = new ModeValue("Rotation Mode", "Custom", "Custom", "Smooth", "Sin Wave", "Down", "Derp", "None");//	private final ModeValue rotationMode = new ModeValue("Rotation Mode", "Custom", "Custom", "Custom Simple", "Custom Advanced", "Smooth", "Sin Wave", "Down", "Derp", "None");
	private NumberValue predict = new NumberValue("Predict", 6, 1, 30, 0.1);
	private BooleanValue predictedPosition = new BooleanValue("predictedPosition", true);
	private NumberValue random = new NumberValue("Randomness", 9, 1, 40, 0.1);
	private NumberValue maxRotation = new NumberValue("MaxRotation", 180, 1, 180, 0.1);
	private NumberValue minRotation = new NumberValue("MinRotation", 180, 1, 180, 0.1);
	private final NumberValue minYawRotation = new NumberValue("Min Yaw Rot", 180, 1, 180, 0.1);
    private final NumberValue maxYawRotation = new NumberValue("Max Yaw Rot", 180, 1, 180, 0.1);
    private final NumberValue minPitchRotation = new NumberValue("Min Pitch Rot", 180, 1, 180, 0.1);
    private final NumberValue maxPitchRotation = new NumberValue("Max Pitch Rot", 180, 1, 180, 0.1);
    private final NumberValue rotationRange = new NumberValue("Rotation Range", 6, 0, 12, 0.1);
    private final BooleanValue raytrace = new BooleanValue("RayCast", false);
    private BooleanValue disableOnWorldChange = new BooleanValue("DisableOnWorldChange", true);
    private final BooleanValue strafe = new BooleanValue("Strafe", false);
    private final ModeValue targetEspMode = new ModeValue("TargetESP", "Classic", "Classic", "Rainbow", "None");
	public TimerUtil timer = new TimerUtil();
	
	
	
	public static float yaw, pitch, lastYaw, lastPitch, serverYaw, serverPitch;
    private float randomYaw, randomPitch, derpYaw;
	public KillAura() {
        super("KillAura", Keyboard.KEY_R);
        Byte CreditsToAllanForYheRotationCode;
        this.jelloCat = Jello.tabgui.cats.get(2);
     //   this.addValue(mode);
        range = new NumberValue("Range", 3.0, 1.0, 6.0, 0.1);
        addValue(range);
        aps = new NumberValue("APS", 10.0, 1.0, 20.0, 1);
        addValue(aps);
        autoblock = new BooleanValue("BlockHit", false);
        addValue(autoblock);
        pl = new BooleanValue("Attack Players", true);
        addValue(pl);
        an = new BooleanValue("Attack Mobs", false);
        addValue(an);
        //mo = new BooleanValue("Attack Passives", false);
        //addValue(mo);
        team = new BooleanValue("AttackTeam", false);
        addValue(team);
        rotations = new BooleanValue("Rotations", false);
      //  addValue(rotations);
        this.addValue(targetEspMode,rotationMode);
       // this.addValue(predict);
     //   this.addValue(predictedPosition);
        this.addValue(random);
      //  this.addValue(maxRotation);
       // this.addValue(minRotation);
        this.addValue(raytrace, strafe);
       // this.addValue(rotationRange);
        this.addValue(disableOnWorldChange);
    }
	double y;
	double p;
	float a;
	double normalise( double value,  double start,  double end ) 
	{
	   double width       = end - start   ;   // 
	   double offsetValue = value - start ;   // value relative to 0

	  return ( offsetValue - ( Math.floor( offsetValue / width ) * width ) ) + start ;
	}
	public void onEnable(){
		blocking = mc.gameSettings.keyBindUseItem.pressed;
		EventManager.register(this);
		enabled = true;
		lastYaw = mc.thePlayer.rotationYaw;
        lastPitch = mc.thePlayer.rotationPitch;
        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;
		EntityPlayer.movementYaw = null;
        sinWaveTicks = 0;
	}
	private boolean cird;
	private final List<EntityLivingBase> targetList = new ArrayList<EntityLivingBase>();
	private final List<EntityLivingBase> s = new ArrayList<EntityLivingBase>();
	public void onDisable(){
		EntityPlayer.movementYaw = null;
		unblock();
		EventManager.unregister(this);
		attacking = false;
		enabled = false;
		blocking = false;
		target = null;
	}
	@EventTarget
	public void render(EventRender3D rend) {
		//if(target != null) {
		if(!targetList.isEmpty()) {
			//Jello.addChatMessage("y");
			if(!targetEspMode.is("None")) {
				drawCircle(targetList.get(0), 0.67, targetEspMode.is("Classic") ? new Color(255, 255, 255) : ColorUtil.rainbow(2, 1) , true);
			}
		}
		//}
		//float par3 = 1;
		//double x = mc.thePlayer.getLastAttacker().lastTickPosX + (mc.thePlayer.getLastAttacker().posX - mc.thePlayer.getLastAttacker().lastTickPosX) * (double)par3 - RenderManager.renderPosX;
        //double y = mc.thePlayer.getLastAttacker().lastTickPosY + (mc.thePlayer.getLastAttacker().posY - mc.thePlayer.getLastAttacker().lastTickPosY) * (double)par3 - RenderManager.renderPosY;
        //double z = mc.thePlayer.getLastAttacker().lastTickPosZ + (mc.thePlayer.getLastAttacker().posZ - mc.thePlayer.getLastAttacker().lastTickPosZ) * (double)par3 - RenderManager.renderPosZ;
		//RenderingUtil.draw3DLine(0, mc.thePlayer.getLastAttacker().getEyeHeight(),1, -1);
	}
	TimerUtil tim = new TimerUtil();
	TimerUtil tima = new TimerUtil();
	private boolean cana;
	 private double targetPosX, targetPosY, targetPosZ;
	 
	public void onUpdate() {
		if(!targetList.isEmpty()) {
			target = targetList.get(0);
			double ping = 250;
	        ping /= 50;//predictedPosition.isEnabled()
	        if (false) {
	            final double deltaX = (target.posX - target.lastTickPosX) * 2;
	            final double deltaY = (target.posY - target.lastTickPosY) * 2;
	            final double deltaZ = (target.posZ - target.lastTickPosZ) * 2;
	            targetPosX = target.posX + deltaX * ping;
	            targetPosY = target.posY + deltaY * ping;
	            targetPosZ = target.posZ + deltaZ * ping;
	        } else {
	            targetPosX = target.posX;
	            targetPosY = target.posY;
	            targetPosZ = target.posZ;
	        }
	        
	        lastYaw = yaw;
	        lastPitch = pitch;
	        final float[] rotations = this.setClosestTarget();
	        
	        /*
	         * We can now update the rotation fields for the aura so the client
	         * can send the server the rotations we actually want to apply.
	         */
	        yaw = rotations[0];
	        pitch = rotations[1];
	        
		}
		else {
	        lastYaw = mc.thePlayer.rotationYaw;
	        lastPitch = mc.thePlayer.rotationPitch;
			sinWaveTicks = 0;
		}
		serverYaw = yaw;
        serverPitch = pitch;
        if (target == null) {
            lastYaw = mc.thePlayer.rotationYaw;
            lastPitch = mc.thePlayer.rotationPitch;
        } else {
            /*
             * Because we have found a target successfully we can grab the
             * required rotations to look and actually attack this target.
             */
            this.updateRotations();
        }
	}
	
	private void updateRotations() {
        /*
         * Update our last rotations as the current ones as we are updating
         * the current ones soon. We require the last rotations to smooth
         * out the current rotations properly based on the last rotations.
         */
        lastYaw = yaw;
        lastPitch = pitch;

        /*
         * Finally grab the required rotations to actually aim at the target.
         * We do not need to pass in any parameters as the method already grabs the settings for us.
         */
        final float[] rotations = this.rot();

        /*
         * We can now update the rotation fields for the aura so the client
         * can send the server the rotations we actually want to apply.
         */
        yaw = rotations[0];
        pitch = rotations[1];

        
        //if not raytracing set rotations so it does
        if (false) {
            if (rayTrace(lastYaw, lastPitch, rotationRange.getValue(), target)) {
                yaw = lastYaw;
                pitch = lastPitch;
            }
        }
    }
   public float[] rot() {
	    final double predictValue = 1;

        final double x = (targetPosX - (target.lastTickPosX - targetPosX) * predictValue) + 0.01 - mc.thePlayer.posX;
        final double z = (targetPosZ - (target.lastTickPosZ - targetPosZ) * predictValue) - mc.thePlayer.posZ;

        double minus = (mc.thePlayer.posY - targetPosY);

        if (minus < -1.4) minus = -1.4;
        if (minus > 0.1) minus = 0.1;

        final double y = (targetPosY - (target.lastTickPosY - targetPosY) * predictValue) + 0.4 + target.getEyeHeight() / 1.3 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) + minus;

        final double xzSqrt = MathHelper.sqrt_double(x * x + z * z);

        float yaw = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(z, x)) - 90.0F);
        float pitch = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(-Math.atan2(y, xzSqrt)));

        final double randomAmount = random.getValue();

        if (randomAmount != 0) {
            randomYaw += ((Math.random() - 0.5) * randomAmount) / 2;
            randomYaw += ((Math.random() - 0.5) * randomAmount) / 2;
            randomPitch += ((Math.random() - 0.5) * randomAmount) / 2;

            if (mc.thePlayer.ticksExisted % 5 == 0) {
                randomYaw = (float) (((Math.random() - 0.5) * randomAmount) / 2);
                randomPitch = (float) (((Math.random() - 0.5) * randomAmount) / 2);
            }

            yaw += randomYaw;
            pitch += randomPitch;
        }

        final int fps = (int) (Minecraft.getDebugFps() / 20.0F);
	   
	   
	   
	   
	   
	   
	   final float yawDistance = (float) randomBetween(180, 180);
       final float pitchDistance = (float) randomBetween(180, 180);


       final float deltaYaw = (((yaw - lastYaw) + 540) % 360) - 180;
       final float deltaPitch = pitch - lastPitch;

       final float distanceYaw = MathHelper.clamp_float(deltaYaw, -yawDistance, yawDistance) / fps * 4;
       final float distancePitch = MathHelper.clamp_float(deltaPitch, -pitchDistance, pitchDistance) / fps * 4;

       yaw = lastYaw + distanceYaw;
       pitch = lastPitch + distancePitch;
       
       
       
       final float[] rotations = new float[]{yaw, pitch};
       final float[] lastRotations = new float[]{KillAura.yaw, KillAura.pitch};

       final float[] fixedRotations = RotationUtil.getFixedRotation(rotations, lastRotations);

       yaw = fixedRotations[0];
       pitch = fixedRotations[1];

       if (this.rotationMode.is("None")) {
           yaw = mc.thePlayer.rotationYaw;
           pitch = mc.thePlayer.rotationPitch;
       }

       pitch = MathHelper.clamp_float(pitch, -90.0F, 90.0F);

       return new float[]{yaw, pitch};
   }
	@EventTarget
    public void EventMotion(EventMotion event){ 
		event.setAlwaysSend(false);
		TimerUtil tim = new TimerUtil();
		if(event.getType() == EventType2.PRE) {
			//updateRotations();
		/*
			tim.reset();
			if(this.tim.hasTimeElapsed(2000, true)) {
				bot.clear();
				Jello.addChatMessage("cleared");
			//	Jello.addChatMessage(String.valueOf(bot.size()));
			}
			*/
			targetList.clear();
	        if(!this.isToggled())
	            return;
	        if(autoblock.isEnabled()) {
				if(mc.thePlayer.getCurrentEquippedItem() != null)
					if(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword && this.attacking)
						//mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
						//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
					//	mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
						block();
			}
	        
	        
	        
	        
	        for(Entity target : mc.thePlayer.getEntityWorld().loadedEntityList) {
	        	 if (target instanceof EntityLivingBase) {
		        	if(target.getDistanceToEntity(mc.thePlayer) < range.getValue()) {
		        		EntityLivingBase entityLivingBase = (EntityLivingBase) target;
		        		if(canAtack(entityLivingBase, true))
		        			s.add(entityLivingBase);
		        		if (canAtack(entityLivingBase, false))
		        			targetList.add(entityLivingBase);
		        	}
	        	 }
	        }
			
	        
	        
			//targets = targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
			
			if(!targetList.isEmpty()) {
				blocking = true;
				//blocking
				EntityLivingBase target = targetList.get(0);
				KillAura.target = target;
				if(strafe.isEnabled()) {
					mc.thePlayer.movementYaw = serverYaw;
				}
				
				
				//atadelay
			//	if(tima.hasTimeElapsed(100, true)) {
					cana = true;
				//}
				
				
				
				
				
				if(!rayTrace(lastY, RotationUtil.faceEntity(target, 1, 1)[1], 6, target)) {
					//lastY = RotationUtil.faceEntity(target, 1, 1)[0];
				}
				
				
				//a = (float) (Math.random() * 25) - 12;
				if(timer.hasTimeElapsed((long) (1000/(aps.getValue() + Math.random())), true) && !BlockFly.ena && cana) {
					//a = (float) (Math.random() * (60) - 30);
					lastY = RotationUtil.faceEntity(target, 1, 1)[0];
					//y = getRotations(target)[1];
					//lastY = RotationUtil.faceEntity(target, a, a)[0];
					
						//mc.thePlayer.isBlocking();
						//mc.thePlayer.isUsingItem();
					if(raytrace.isEnabled()) {
						if(rayTrace(serverYaw, (float) serverPitch, 6, target) && true) {
							mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
							///mc.playerController.attackEntity(mc.thePlayer, target);
							mc.thePlayer.swingItem();
						}
						else {
							mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
						}
					}else {
						mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
						///mc.playerController.attackEntity(mc.thePlayer, target);
						mc.thePlayer.swingItem();
					}
					//mc.gameSettings.keyBindUseItem.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
					// if(autoblock.isEnabled()) 
							//if(mc.thePlayer.getCurrentEquippedItem() != null)
							//	if(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword && this.attacking)
								//	mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
					//mc.playerController.attackEntity(mc.thePlayer, target);
				}else {
					//mc.gameSettings.keyBindUseItem.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
				}
				
				double deltaX = target.posX + (target.posX - target.lastTickPosX) - mc.thePlayer.posX,
		                deltaY = target.posY - 3.5 + target.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
		                deltaZ = target.posZ + (target.posZ - target.lastTickPosZ) - mc.thePlayer.posZ,
		                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
				mc.thePlayer.setLastAttacker(target);
				blocking = true;
				attacking = true;
				if(true) {
					
					
				//	mc.thePlayer.rotationYaw = (lastY + a);
					//mc.thePlayer.rotationPitch = (float)y;
					//event.setYaw((float) lastY + a);
					//event.setPitch((float) (y));//event.setPitch((float) (RotationUtil.faceEntity(target, a, a)[1] - a * 0.2));
					event.setYaw(serverYaw);
                    event.setPitch(serverPitch);
					mc.thePlayer.renderYawOffset = serverYaw;
	                mc.thePlayer.rotationYawHead = serverYaw;
	                mc.thePlayer.renderPitchRotation = serverPitch;
				}
			//	if(y > getRotations(target)[0]) {
			//		y-= (2+ (Math.random() * (Math.random() * 5)));
			//	}else
			//		y += (2+ (Math.random() * (Math.random() * 5)));
			//	if(mc.thePlayer.posY > target.posY)
			////		y = 38;
			//	else
			//		y = 6;
			//	if(mc.thePlayer.posY < target.posY)
			//		y = -33;
			//	else
				//	y = 6;
				if(tim.hasTimeElapsed(100, true)) {
					y = getRotations(target)[1];
					tim.reset();
				}
			//	if(rayTrace((float) y, getRotations(target)[1], 4, target) && mc.thePlayer.posY > target.posY) {
			//		y = -8;
				//}
			//	if(rayTrace((float) y, getRotations(target)[1], 4, target) && mc.thePlayer.posY < target.posY) {
				////	y = -8;
				//}
				
				//mc.thePlayer.rotationYawHead = (float) lastY + a;//getRotations(target)[0]
				//mc.thePlayer.renderYawOffset = (float) getRotations(target)[0];
				//mc.thePlayer.rotationPitchHead = (float) y;
			}else {
				EntityPlayer.movementYaw = null;
				blocking = false;
				attacking = false;
				cana = false;
				sinWaveTicks = 0;
				tima.reset();
				unblock();
				//mc.gameSettings.keyBindUseItem.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			}
		}
    }
	public float[] getRotations(Entity e) {
        double deltaX = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX,
                deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ,
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
                pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        final double v = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if (deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + v);
        } else if (deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + v);
        }
        return new float[]{yaw, pitch};
    }
    
    public float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
        final Minecraft mc = Minecraft.getMinecraft();

        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float lastYaw = lastRotations[0];
        final float lastPitch = lastRotations[1];

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 1.2F;

        final float deltaYaw = yaw - lastYaw;
        final float deltaPitch = pitch - lastPitch;

        final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
        final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

        final float fixedYaw = lastYaw + fixedDeltaYaw;
        final float fixedPitch = lastPitch + fixedDeltaPitch;

        return new float[]{fixedYaw, fixedPitch};
    }
    private boolean rayTrace(final float yaw, final float pitch, final double reach, final Entity target) {
        final Vec3 vec3 = mc.thePlayer.func_174824_e(mc.timer.renderPartialTicks);
        final Vec3 vec31 = mc.thePlayer.getVectorForRotationFloat(MathHelper.clamp_float(pitch, -90.F, 90.F), yaw % 360);
        final Vec3 vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach);

        final MovingObjectPosition objectPosition = target.getEntityBoundingBox().calculateIntercept(vec3, vec32);

        return (objectPosition != null && objectPosition.hitVec != null);
    }
    @EventTarget
	public void autoBlock(EventMotion event)
	  {/*
	  
		   boolean isSword = autoblock.getValue() && mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
		   if (event.getType() == EventType2.PRE) {
			  
	        	EntityLivingBase entity;
	            if (this.entities.isEmpty()) {
	                for (Object object : Jello.core.world().loadedEntityList) {
	                    if (!(object instanceof EntityLivingBase) || !isValidHypixel(entity = (EntityLivingBase)object)) continue;
	                    if(this.isOnTeam(entity)){
	                    this.entities.add(entity);
	                    }
	                }
	            }
	            if (!this.entities.isEmpty()) {
	                double distance = Double.MAX_VALUE;
	                entity = null;
	                int i = 0;
	                while (i < this.entities.size()) {
	                    EntityLivingBase e = this.entities.get(i);
	                    if (!isValidHypixel(e)) {
	                        this.entities.remove(e);
	                    }
	                    if ((double)e.getDistanceToEntity(Jello.core.player()) < distance && (double)e.getDistanceToEntity(Jello.core.player()) < range.getValue()) {
	                        entity = e;
	                        distance = e.getDistanceToEntity(Jello.core.player());
	                    }
	                    ++i;
	                }
	                this.closestE = entity;
	            }
	        }
	        else if (isValidHypixel(closestE)) {
	        	if(this.isOnTeam(closestE)){
	            if (isSword && mc.thePlayer.getDistanceToEntity(closestE) < range.getValue()) {
	                PlayerControllerMP playerController = mc.playerController;
	                EntityPlayerSP p = mc.thePlayer;
	                }
	        	
	        	if (this.closestE != null && timer.hasTimeElapsed(100, true)) {
	        		attacking = true;
	        		if(this.autoblock.getValue()){
                		blocking = true;
                	}
               		if (mc.thePlayer.getFoodStats().getFoodLevel() > 6 && kb.getValue()){
           				mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
               	}
               		Jello.core.attack(this.closestE);
               		
               		if (mc.thePlayer.getFoodStats().getFoodLevel() > 6 && kb.getValue()){
           				mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
               		}
	                this.entities.remove(this.closestE);
	                this.closestE = null;
	                this.timer.reset();
	            }
	        }
	        }*/
		
	    }
    public float[] setClosestTarget() {
    	final double predictValue = 1;

        final double x = (targetPosX - (target.lastTickPosX - targetPosX) * predictValue) + 0.01 - mc.thePlayer.posX;
        final double z = (targetPosZ - (target.lastTickPosZ - targetPosZ) * predictValue) - mc.thePlayer.posZ;

        double minus = (mc.thePlayer.posY - targetPosY);

        if (minus < -1.4) minus = -1.4;
        if (minus > 0.1) minus = 0.1;

        final double y = (targetPosY - (target.lastTickPosY - targetPosY) * predictValue) + 0.4 + target.getEyeHeight() / 1.3 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) + minus;

        final double xzSqrt = MathHelper.sqrt_double(x * x + z * z);

        float yaw = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(z, x)) - 90.0F);
        float pitch = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(-Math.atan2(y, xzSqrt)));

        final double randomAmount = random.getValue();

        if (randomAmount != 0) {
            randomYaw += ((Math.random() - 0.5) * randomAmount) / 2;
            randomYaw += ((Math.random() - 0.5) * randomAmount) / 2;
            randomPitch += ((Math.random() - 0.5) * randomAmount) / 2;

            if (mc.thePlayer.ticksExisted % 5 == 0) {
                randomYaw = (float) (((Math.random() - 0.5) * randomAmount) / 2);
                randomPitch = (float) (((Math.random() - 0.5) * randomAmount) / 2);
            }

            yaw += randomYaw;
            pitch += randomPitch;
        }

        final int fps = (int) (Minecraft.getDebugFps() / 20.0F);

        switch (this.rotationMode.getMode()) {
            case "Custom": {
                if (180 != 180.0F && 180 != 180.0F) {
                    final float distance = (float) randomBetween(180, 180);

                    final float deltaYaw = (((yaw - lastYaw) + 540) % 360) - 180;
                    final float deltaPitch = pitch - lastPitch;

                    final float distanceYaw = MathHelper.clamp_float(deltaYaw, -distance, distance) / fps * 1;
                    final float distancePitch = MathHelper.clamp_float(deltaPitch, -distance, distance) / fps * 1;

                    yaw = MathHelper.wrapAngleTo180_float(lastYaw) + distanceYaw;
                    pitch = MathHelper.wrapAngleTo180_float(lastPitch) + distancePitch;
                }
                break;
            }

            case "Custom Simple": {
                final float yawDistance = (float) randomBetween(180, 180);
                final float pitchDistance = (float) randomBetween(180, 180);


                final float deltaYaw = (((yaw - lastYaw) + 540) % 360) - 180;
                final float deltaPitch = pitch - lastPitch;

                final float distanceYaw = MathHelper.clamp_float(deltaYaw, -yawDistance, yawDistance) / fps * 4;
                final float distancePitch = MathHelper.clamp_float(deltaPitch, -pitchDistance, pitchDistance) / fps * 4;

                yaw = lastYaw + distanceYaw;
                pitch = lastPitch + distancePitch;
                break;
            }

            case "Custom Advanced": {
                final float advancedYawDistance = (float) randomBetween(this.minYawRotation.getValue(), this.maxYawRotation.getValue());
                final float advancedPitchDistance = (float) randomBetween(this.minPitchRotation.getValue(), this.maxPitchRotation.getValue());

                final float advancedDeltaYaw = (((yaw - lastYaw) + 540) % 360) - 180;
                final float advancedDeltaPitch = pitch - lastPitch;

                final float advancedDistanceYaw = MathHelper.clamp_float(advancedDeltaYaw, -advancedYawDistance, advancedYawDistance) / fps * 4;
                final float advancedDistancePitch = MathHelper.clamp_float(advancedDeltaPitch, -advancedPitchDistance, advancedPitchDistance) / fps * 4;

                yaw = lastYaw + advancedDistanceYaw;
                pitch = lastPitch + advancedDistancePitch;
                break;
            }

            case "Smooth": {
                final float yawDelta = (float) (((((yaw - lastYaw) + 540) % 360) - 180) / (fps / 1 * (1 + Math.random())));
                final float pitchDelta = (float) ((pitch - lastPitch) / (fps / 1 * (1 + Math.random())));

                yaw = lastYaw + yawDelta;
                pitch = lastPitch + pitchDelta;

                break;
            }

            case "Down": {
                pitch = RandomUtils.nextFloat(89, 90);
                break;
            }

            case "Derp": {
                pitch = RandomUtils.nextFloat(89, 90);
                yaw = derpYaw;
                break;
            }

            case "Sin Wave": {
                final float halal = (float) (Math.abs(Math.sin((sinWaveTicks + Math.random() * 0.001) / 10)) * 180);

                final float sinWaveYaw = MathHelper.clamp_float((((yaw - lastYaw) + 540) % 360) - 180, -halal, halal) / fps;
                final float sinWavePitch = MathHelper.clamp_float(pitch - lastPitch, -halal, halal) / fps / fps;

                yaw = lastYaw + sinWaveYaw;
                pitch = lastPitch + sinWavePitch;

                sinWaveTicks++;
                break;
            }
        }

        final float[] rotations = new float[]{yaw, pitch};
        final float[] lastRotations = new float[]{KillAura.yaw, KillAura.pitch};

        final float[] fixedRotations = RotationUtil.getFixedRotation(rotations, lastRotations);

        yaw = fixedRotations[0];
        pitch = fixedRotations[1];

        if (this.rotationMode.is("None")) {
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
        }

        pitch = MathHelper.clamp_float(pitch, -90.0F, 90.0F);

        return new float[]{yaw, pitch};
		}
    @EventTarget
    public void onPacket(EventReceivePacket e){
    	if(e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C05PacketPlayerLook || e.getPacket() instanceof C06PacketPlayerPosLook) {
    		mc.thePlayer.sendQueue.addToSendQueue(new C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, getRotations(targetList.get(0))[0], getRotations(targetList.get(0))[1], mc.thePlayer.onGround));
    	}
    /*	if(attacking){
    		
    		
       	 double 	posX 		= closestE.posX - mc.thePlayer.posX,
   	  				posZ 		= closestE.posZ - mc.thePlayer.posZ,
   	  				posY 		= (closestE.posY - 3.5 + Jello.core.findClosestEntity().getEyeHeight() - mc.thePlayer.posY + (mc.thePlayer.getEyeHeight())),
   	  				helper 		= MathHelper.sqrt_double((posX * posX) + (posZ * posZ));
   	  		float 	newYaw 		= (float) Math.toDegrees(-Math.atan(posX / posZ)),
   	  				newPitch 	= (float) -Math.toDegrees(Math.atan(posY / helper));
   	  		
   	  		if ((posZ < 0) && (posX < 0)) {
   	  			newYaw = (float) (90 + Math.toDegrees(Math.atan(posZ / posX)));
   	  		} else if ((posZ < 0) && (posX > 0)) {
   	  			newYaw = (float) (-90 + Math.toDegrees(Math.atan(posZ / posX)));
   	  		}
   	  	if(event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C05PacketPlayerLook || event.getPacket() instanceof C06PacketPlayerPosLook) {
   	  		event.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, progressYaw, progressPitch, mc.thePlayer.onGround));
   	 
   	  		yaw = newYaw;
   	  		pitch = newPitch;
   	  		
   	  	}
   	  		}else{
   	  			progressYaw = (float)this.normalise(mc.thePlayer.rotationYaw, -180, 180);
   	  			yaw = (float)this.normalise(mc.thePlayer.rotationYaw, -180, 180);
   	  			progressPitch = mc.thePlayer.rotationPitch;
   				pitch = mc.thePlayer.rotationPitch;
   	  		}*/
    }
    
    private boolean isOnTeam(EntityLivingBase entity){
    	String namewithcolorcode = Jello.core.player().getDisplayName().getUnformattedText();
    	String colorcode = namewithcolorcode.replace(Jello.core.player().getName(), "");
    	String enamewithcolorcode = entity.getDisplayName().getUnformattedText();//.getUnformattedText();
    	String ecolorcode = enamewithcolorcode.replace(entity.getName(), "");
        return (team.getValue() ? !(colorcode != "" && colorcode.equalsIgnoreCase(ecolorcode)):true)/*&& (!FriendManager.isFriend(entity.getName())) && (Team.isEnabled ? !(colorcode != "" && colorcode.equalsIgnoreCase(ecolorcode)):true*/;
   
    }
    
    public boolean isValidHypixel(EntityLivingBase entity) {
    	 return entity != null &&  
    			 entity != Jello.core.player() && 
    			 ((entity instanceof EntityOtherPlayerMP && pl.getValue()) || 
    					 (entity instanceof EntityAnimal && an.getValue()) || 
    					 entity instanceof EntityMob && an.getValue()) && 
    			 entity.getDistanceToEntity(mc.thePlayer) <= range.getValue() && 
    			 (!entity.isInvisible()) && /*(entity.ticksExisted > 10) &&*/ 
    			 (double)entity.getDistanceToEntity(Jello.core.player()) <= (!Jello.core.player().canEntityBeSeen(entity) ? 3.0 : 
    				 this.range.getValue());// && (team.getValue() ? !(colorcode != "" && colorcode.equalsIgnoreCase(ecolorcode)):true)/*&& (!FriendManager.isFriend(entity.getName())) && (Team.isEnabled ? !(colorcode != "" && colorcode.equalsIgnoreCase(ecolorcode)):true*/;
    }
    
    public boolean isEntityInFov(EntityLivingBase entity, double angle) {
        double angleDifference = this.getAngleDifference(Jello.core.player().rotationYaw, AimUtil.getRotations(entity)[0]);
        if (!(angleDifference > 0.0 && angleDifference < angle || - (angle *= 0.5) < angleDifference && angleDifference < 0.0)) {
            return false;
        }
        return true;
    }
    
    public static float getAngleDifference(float direction, float rotationYaw) {
        float phi = Math.abs(rotationYaw - direction) % 360.0f;
        float distance = phi > 180.0f ? 360.0f - phi : phi;
        return distance;
    }
	public boolean canAtack(EntityLivingBase entityLivingBase, boolean blocking) {
		if (entityLivingBase == mc.thePlayer || entityLivingBase.isDead || entityLivingBase.getHealth() == 0 || entityLivingBase instanceof EntityArmorStand)
            return false;
		if(entityLivingBase.isOnSameTeam(mc.thePlayer) && !team.isEnabled())
			return false;
		if (entityLivingBase instanceof EntityPlayer && !pl.isEnabled())
            return false;
		 if ((entityLivingBase instanceof EntityMob || entityLivingBase instanceof EntityAmbientCreature || entityLivingBase instanceof EntityWaterMob) && !an.isEnabled())
	            return false;
		 if(entityLivingBase.isInvisible())
			 return true;
		 if(AntiBot.bot.contains(entityLivingBase) && Jello.getModule("AntiBot").isToggled())
			 return false;
		 if(entityLivingBase instanceof EntityVillager){
			 return false;
		 }
		return true;
	}
	 private void drawCircle(final Entity entity, final double rad, final Color color, final boolean shade) {
	        GL11.glPushMatrix();
	        GL11.glDisable(3553);
	        GL11.glEnable(2848);
	        GL11.glEnable(2832);
	        GL11.glEnable(3042);
	        GL11.glBlendFunc(770, 771);
	        GL11.glHint(3154, 4354);
	        GL11.glHint(3155, 4354);
	        GL11.glHint(3153, 4354);
	        GL11.glDepthMask(false);
	        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
	        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
	        GlStateManager.disableCull();
	        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

	        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosX;
	        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosY) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
	        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosZ;

	        Color c;

	        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64.F) {
	            final double vecX = x + rad * Math.cos(i);
	            final double vecZ = z + rad * Math.sin(i);

	            c = color;

	            if (shade) {
	                GL11.glColor4f(c.getRed() / 255.F, c.getGreen() / 255.F, c.getBlue() / 255.F, 0);
	                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
	                GL11.glColor4f(c.getRed() / 255.F, c.getGreen() / 255.F, c.getBlue() / 255.F, 0.35F);//GL11.glColor4f(c.getRed() / 255.F, c.getGreen() / 255.F, c.getBlue() / 255.F, 0.65F);
	                
	            }
	            GL11.glVertex3d(vecX, y, vecZ);
	        }

	        GL11.glEnd();
	        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
	        GL11.glDepthMask(true);
	        GL11.glEnable(2929);
	        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
	        GlStateManager.enableCull();
	        GL11.glDisable(2848);
	        GL11.glDisable(2848);
	        GL11.glEnable(2832);
	        GL11.glEnable(3553);
	        GL11.glPopMatrix();
	        GL11.glColor3f(255, 255, 255);
	        //Jello.addChatMessage("dtawing");
	    }
	 private float sinWaveTicks;
	
	 	public double randomBetween(final double min, final double max) {
	        return min + (MathUtil.RANDOM.nextDouble() * (max - min));
	    }
	 	@EventTarget
	 	public void onWorldChange(WorldChangeEvent e) {
	 		if(disableOnWorldChange.isEnabled()) {
		 		Jello.addChatMessage("Disabled KilAura due to world change");
		 		this.toggle();
	 		}
	 	}
	 	private boolean b;
	 	private void block() {
	        sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
	        mc.gameSettings.keyBindUseItem.pressed = true;
	        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
	        b = true;
	    }

	    private void unblock() {
	        if (b) {
	            mc.gameSettings.keyBindUseItem.pressed = false;
	           // mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
	            b = false;
	        }
	    }
	    public void sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
	        if (!(mc.playerController.currentGameType == WorldSettings.GameType.SPECTATOR)) {
	            mc.playerController.syncCurrentPlayItem();
	            int i = itemStackIn.stackSize;
	            ItemStack itemstack = itemStackIn.useItemRightClick(worldIn,
	                    playerIn);

	            if (itemstack != itemStackIn || itemstack.stackSize != i) {
	                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = itemstack;

	                if (itemstack.stackSize == 0) {
	                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
	                }

	            }
	        }
	    }
	    
	 	
}
