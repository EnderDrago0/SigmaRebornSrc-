package com.mentalfrostbyte.jello.modules;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.MovementUtil;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.PlayerUtils;
import com.mentalfrostbyte.jello.util.RenderingUtil;
import com.mentalfrostbyte.jello.util.RotationUtil;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class TargetStrafe extends Module{
	private Entity target;
    private float distance;
    private boolean direction, reset;
    
    private Point currentPoint;
    private int currentIndex = 0;
    private ArrayList<Point> points = new ArrayList<>();
    private final NumberValue lineWidth = new NumberValue("Line Width", 1.8, 0.1, 3, 0.1);
    private final NumberValue radius = new NumberValue("Distance", 1, 1, 6, 0.1);
    private final NumberValue pointAmount = new NumberValue("Points", 10, 1, 20, 1);

    private final BooleanValue pressSpaceOnly = new BooleanValue("Press Space Only", false);
    private final BooleanValue checkVoid = new BooleanValue("Check Void", false);
    

  //  public final NumberValue range = new NumberValue("Range", 3, 1, 6, 0.1);
  //  public final BooleanValue thirdPerson = new BooleanValue("Third Person", false);
 //   public final BooleanValue jumpOnly = new BooleanValue("Jump Only", false);
    public TargetStrafe() {
    	super("TargetStrafe", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(2);
        this.addValue(radius);
       // this.addValue(lineWidth, radius, pointAmount, slowdown, pressSpaceOnly, checkVoid);
	}
    @Override
    public void onEnable() {
    	EventManager.register(this);
    }
    @Override
    public void onDisable() {
    	EventManager.unregister(this);
    }
	
	

    public void editMovement(double x, double y, double z) {
        double movementSpeed = ApacheMath.sqrt(x * x + z * z) * slowdown.getValue().doubleValue();
        boolean modulesEnabled = Client.getInstance().getModuleManager()
                .getByClass(FlightModule.class).getData().isEnabled() ||
                Client.getInstance().getModuleManager()
                        .getByClass(SpeedModule.class).getData().isEnabled();

        if (this.canStrafe && MovementUtils.isMoving() && modulesEnabled && this.shouldStrafe) {
            if (!PlayerUtils.inLiquid() && !mc.thePlayer.isOnLadder()) {
                strafing = true;

                float forward = 1;
                float strafe = 0;
                float yaw = getYawTo(currentPoint);

                yaw = MovementUtils.getDirection(forward, strafe, yaw);

                x = -ApacheMath.sin(ApacheMath.toRadians(yaw)) * movementSpeed;
                z = ApacheMath.cos(ApacheMath.toRadians(yaw)) * movementSpeed;
            }
        }
        
    }
	
	private float getYaw() {
        final double x = (target.posX - (target.lastTickPosX - target.posX)) - mc.thePlayer.posX;
        final double z = (target.posZ - (target.lastTickPosZ - target.posZ)) - mc.thePlayer.posZ;

        return (float) (Math.toDegrees(Math.atan2(z, x)) - 90.0F);
    }
	

    public Block getBlock(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(offsetX, offsetY, offsetZ)).getBlock();
    }
    
    public static class Point {
        Vector2d position;
        boolean valid;

        public Point(Vector2d position) {
            this.position = position;
            this.valid = isPointValid(position);
        }

        private boolean isPointValid(Vector2d position) {
            Vec3 pointVec = new Vec3(position.x, mc.thePlayer.posY, position.y);
            IBlockState blockState = mc.theWorld.getBlockState(new BlockPos(pointVec));

            boolean canBeSeen = mc.theWorld.rayTraceBlocks(mc.thePlayer.getPositionVector(), pointVec,
                    false, true, false) == null;

            boolean isAboveVoid = isBlockUnder(position.x, position.y, 5);

            return !blockState.getBlock().isFullBlock() && canBeSeen && (!isAboveVoid || Jello.getModule("Fly").isToggled());
        }

        private boolean isBlockUnder(double posX, double posZ, double height) {
            for (int i = (int) mc.thePlayer.posY; i >= mc.thePlayer.posY - height; i--) {
                if (!(mc.theWorld.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof BlockAir)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    
    private void updatePoints() {
        points.clear();

        int size = (int) pointAmount.getValue();

        for (int i = 0; i < size; i++) {
            double cos = radius.getValue() * Math.cos(i * (Math.PI * 2) / size);
            double sin = radius.getValue() * Math.sin(i * (Math.PI * 2) / size);

            double pointX = target.posX + cos;
            double pointZ = target.posZ + sin;

            Point point = new Point(new Vector2d(pointX, pointZ));

            points.add(point);
        }
    }

    private Point getBestPoint() {
        double closest = Double.MAX_VALUE;
        Point bestPoint = null;

        for (Point point : points) {
            if (point.valid) {
                final double dist = getDistanceTo(point);
                if (dist < closest) {
                    closest = dist;
                    bestPoint = point;
                }
            }
        }

        return bestPoint;
    }

    private double getDistanceTo(Point point) {
        double xDist = point.position.x - mc.thePlayer.posX;
        double zDist = point.position.y - mc.thePlayer.posZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    private float getYawTo(Point point) {
        if (point == null) return mc.thePlayer.rotationYaw;
        double xDist = point.position.x - mc.thePlayer.posX;
        double zDist = point.position.y - mc.thePlayer.posZ;
        float rotationYaw = mc.thePlayer.rotationYaw;
        float var1 = (float) (StrictMath.atan2(zDist, xDist) * 180.0D / StrictMath.PI) - 90.0F;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }
    
    private void drawCircle(Entity entity, float lineWidth, double radius) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GL11.glPushMatrix();
       // mc.entityRenderer.func_175072_h();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);

        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX)
                * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY)
                * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ)
                * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        GL11.glPushMatrix();
        GL11.glLineWidth(lineWidth);
//        GL11.glBegin(GL11.GL_LINE_STRIP);

//        for (int i = 0; i <= 90; ++i) {
//            ColorUtils.glColor(Color.WHITE);
//            double div = pointAmount.getValue().intValue();
//
//            GL11.glVertex3d(posX + radius * ApacheMath.cos((double) i * ApacheMath.PI * 2 / div),
//                    posY, posZ + radius * ApacheMath.sin((double) i * ApacheMath.PI * 2 / div));
//        }

        Cylinder c = new Cylinder();
        GL11.glTranslated(posX, posY, posZ);
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        GlStateManager.func_179117_G();
        RenderingUtil.color(255, 2, 2, 1);
        c.draw((float) (radius + 0.25), (float) (radius + 0.25), 0.0f, 100, 0);
        c.draw((float) (radius + 0.25), (float) (radius + 0.25), 0.0f, 100, 0);

//        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    //    mc.entityRenderer.func_180436_i();
        GL11.glPopMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
    private float yaw;
    @EventTarget
    public void e(EventRender3D e) {
    	if(KillAura.target != null && KillAura.attacking)
    		mc.thePlayer.movementYaw = RotationUtil.faceEntity(KillAura.target, 44, 3)[0] + yaw;
    	else
    		mc.thePlayer.movementYaw = null;
    	yaw = (float) (radius.getValue() * 13);
    	//if(KillAura.target != null)
    		//drawCircle(KillAura.target, 2, 1);
    }
}
