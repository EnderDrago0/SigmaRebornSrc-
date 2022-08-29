package net.minecraft.client.renderer.entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mentalfrostbyte.jello.main.Jello;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderEntityItem extends Render
{
    private final RenderItem field_177080_a;
    private Random field_177079_e = new Random();
    

    public RenderEntityItem(RenderManager p_i46167_1_, RenderItem p_i46167_2_)
    {
        super(p_i46167_1_);
        this.field_177080_a = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int func_177077_a(EntityItem p_177077_1_, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_)
    {
        ItemStack var10 = p_177077_1_.getEntityItem();
        Item var11 = var10.getItem();

        if (var11 == null)
        {
            return 0;
        }
        else
        {
            boolean var12 = p_177077_9_.isAmbientOcclusionEnabled();
            int var13 = this.func_177078_a(var10);
            float var14 = 0.25F;
            float var15 = MathHelper.sin(((float)p_177077_1_.func_174872_o() + p_177077_8_) / 10.0F + p_177077_1_.hoverStart) * 0.1F + 0.1F;
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + var15 + 0.25F, (float)p_177077_6_);
            float var16;

            if (var12 || this.renderManager.options != null && this.renderManager.options.fancyGraphics)
            {
                var16 = (((float)p_177077_1_.func_174872_o() + p_177077_8_) / 20.0F + p_177077_1_.hoverStart) * (180F / (float)Math.PI);
                GlStateManager.rotate(var16, 0.0F, 1.0F, 0.0F);
            }

            if (!var12)
            {
                var16 = -0.0F * (float)(var13 - 1) * 0.5F;
                float var17 = -0.0F * (float)(var13 - 1) * 0.5F;
                float var18 = -0.046875F * (float)(var13 - 1) * 0.5F;
                GlStateManager.translate(var16, var17, var18);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return var13;
        }
    }

    private int func_177078_a(ItemStack p_177078_1_)
    {
        byte var2 = 1;

        if (p_177078_1_.stackSize > 48)
        {
            var2 = 5;
        }
        else if (p_177078_1_.stackSize > 32)
        {
            var2 = 4;
        }
        else if (p_177078_1_.stackSize > 16)
        {
            var2 = 3;
        }
        else if (p_177078_1_.stackSize > 1)
        {
            var2 = 2;
        }

        return var2;
    }

    public static long tick;
    private static double rotation = 0.0D;
    private static Random random = new Random();
    
    public void func_177075_a(EntityItem p_177075_1_, double p_177075_2_, double p_177075_4_, double p_177075_6_, float p_177075_8_, float p_177075_9_)
    {
    	if(Jello.getModule("ItemPhysics").isToggled()) {
    		if (!p_177075_1_.onGround) {
                rotation *= 1.005f;
                p_177075_1_.rotationPitch += rotation;
            }
    		
    		 Minecraft mc = Minecraft.getMinecraft();

             //ItemPhysics pro rendering
             rotation = 2;
             if (!mc.inGameHasFocus) rotation = 0;

             ItemStack itemstack = p_177075_1_.getEntityItem();
             int i = itemstack != null && itemstack.getItem() != null ? Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata() : 187;
             random.setSeed(i);

             Minecraft.getMinecraft().getTextureManager().bindTexture(getEntityTexture(p_177075_1_));
             Minecraft.getMinecraft().getTextureManager().getTexture(getEntityTexture(p_177075_1_))
                     .func_174936_b(false, false);

             GlStateManager.enableRescaleNormal();
             GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
             GlStateManager.enableBlend();
             RenderHelper.enableStandardItemLighting();
             GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
             GlStateManager.pushMatrix();
             IBakedModel ibakedmodel = mc.getRenderItem().getItemModelMesher().getItemModel(itemstack);
             boolean flag1 = ibakedmodel.isGui3d();
             boolean is3D = ibakedmodel.isGui3d();
             int j = func_177078_a(itemstack);

             GlStateManager.translate((float) p_177075_2_, (float) p_177075_4_, (float) p_177075_6_);

             if (ibakedmodel.isGui3d()) GlStateManager.scale(0.5F, 0.5F, 0.5F);

             GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
             GL11.glRotatef(((EntityItem) p_177075_1_).rotationYaw, 0.0F, 0.0F, 1.0F);

             GlStateManager.translate(0, 0, is3D ? -0.08 : -0.04);

             //Handle Rotations
             if (is3D || mc.getRenderManager().options != null) {
                 if (is3D) {
                     if (!((EntityItem) p_177075_1_).onGround) {
                         ((EntityItem) p_177075_1_).rotationPitch += rotation;
                     }
                 } else {
                     if (!Double.isNaN(((EntityItem) p_177075_1_).posX) && !Double.isNaN(((EntityItem) p_177075_1_).posY) && !Double.isNaN(((EntityItem) p_177075_1_).posZ) && ((EntityItem) p_177075_1_).worldObj != null) {
                         if (((EntityItem) p_177075_1_).onGround) {
                             ((EntityItem) p_177075_1_).rotationPitch = 0;
                         } else {
                             ((EntityItem) p_177075_1_).rotationPitch += rotation;
                         }
                     }
                 }

                 GlStateManager.rotate(((EntityItem) p_177075_1_).rotationPitch, 1, 0, 0.0F);
             }

             GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
             for (int k = 0; k < j; k++) {
                 GlStateManager.pushMatrix();
                 if (flag1) {
                     if (k > 0) {
                         float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                         float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                         float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                         GlStateManager.translate(f7, f9, f6);
                     }

                     mc.getRenderItem().func_180454_a(itemstack, ibakedmodel);
                     GlStateManager.popMatrix();
                 } else {
                     mc.getRenderItem().func_180454_a(itemstack, ibakedmodel);
                     GlStateManager.popMatrix();
                     GlStateManager.translate(0.0F, 0.0F, 0.05375F);
                 }
             }

             GlStateManager.popMatrix();
             GlStateManager.disableRescaleNormal();
             GlStateManager.disableBlend();
             Minecraft.getMinecraft().getTextureManager().bindTexture(getEntityTexture(p_177075_1_));
             Minecraft.getMinecraft().getTextureManager().getTexture(getEntityTexture(p_177075_1_)).func_174935_a();
    		
    		
    	}else {
	        ItemStack var10 = p_177075_1_.getEntityItem();
	        this.field_177079_e.setSeed(187L);
	        boolean var11 = false;
	
	        if (this.bindEntityTexture(p_177075_1_))
	        {
	            this.renderManager.renderEngine.getTexture(this.func_177076_a(p_177075_1_)).func_174936_b(false, false);
	            var11 = true;
	        }
	
	        GlStateManager.enableRescaleNormal();
	        GlStateManager.alphaFunc(516, 0.1F);
	        GlStateManager.enableBlend();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        GlStateManager.pushMatrix();
	        IBakedModel var12 = this.field_177080_a.getItemModelMesher().getItemModel(var10);
	        int var13 = this.func_177077_a(p_177075_1_, p_177075_2_, p_177075_4_, p_177075_6_, p_177075_9_, var12);
	
	        for (int var14 = 0; var14 < var13; ++var14)
	        {
	            if (var12.isAmbientOcclusionEnabled())
	            {
	                GlStateManager.pushMatrix();
	
	                if (var14 > 0)
	                {
	                    float var15 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
	                    float var16 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
	                    float var17 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
	                    GlStateManager.translate(var15, var16, var17);
	                }
	
	                GlStateManager.scale(0.5F, 0.5F, 0.5F);
	                this.field_177080_a.func_180454_a(var10, var12);
	                GlStateManager.popMatrix();
	            }
	            else
	            {
	                this.field_177080_a.func_180454_a(var10, var12);
	                GlStateManager.translate(0.0F, 0.0F, 0.046875F);
	            }
	        }
	
	        GlStateManager.popMatrix();
	        GlStateManager.disableRescaleNormal();
	        GlStateManager.disableBlend();
	        this.bindEntityTexture(p_177075_1_);
	
	        if (var11)
	        {
	            this.renderManager.renderEngine.getTexture(this.func_177076_a(p_177075_1_)).func_174935_a();
	        }
	
	        super.doRender(p_177075_1_, p_177075_2_, p_177075_4_, p_177075_6_, p_177075_8_, p_177075_9_);
    	}
    }

    protected ResourceLocation func_177076_a(EntityItem p_177076_1_)
    {
        return TextureMap.locationBlocksTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return this.func_177076_a((EntityItem)p_110775_1_);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        this.func_177075_a((EntityItem)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
