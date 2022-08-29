package com.mentalfrostbyte.jello.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class SigMeme extends Module {

	public Random rand = new Random();
	
	public TimerUtil timer = new TimerUtil();
	public static boolean en;
	public ModeValue mode;
	public int co;
	public boolean s;
	public int ca;
	public int ci;
	public ArrayList<String> sul = new ArrayList<>(Arrays.asList("Learn your alphabet with the Sigma reborn client: Omikron, Sigma reborn, Epsilon, Alpha!",
		    "Download Sigma reborn to kick ass while listening to some badass music!",
		    "Why Sigma reborn? Cause it is the addition of pure skill and incredible intellectual abilities",
		    "Want some skills? Check out Sigma reborn client.Info!",
		    "You have been oofed by Sigma reborn oof oof",
		    "I am not racist, but I only like Sigma reborn users. so git gut noobs",
		    "Quick Quiz: I am zeus's son, who am I? Sigma reborn",
		    "Wow! My combo is Sigma reborn'n!",
		    "What should I choose? Sigma reborn or Sigma reborn?",
		    "Bigmama and Sigmama",
		    "I don't hack I just Sigma reborn",
		    "Sigma reborn client . Info is your new home",
		    "Look a divinity! He definitely must use Sigma reborn!",
		    "In need of a cute present for Christmas? Sigma reborn is all you need!",
		    "I have a good Sigma reborn config, don't blame me",
		    "Don't piss me off or you will discover the true power of Sigma reborn's inf reach",
		    "Sigma reborn never dies",
		    "Maybe I will be Sigma reborn, I am already Sigma reborn",
		    "Sigma reborn will help you! Oops, i killed you instead.",
		    "NoHaxJustSigma reborn",
		    "Do like Tenebrous, subscribe to LeakedPvP!",
		    "Did I really just forget that melody? Si sig sig sig Sigma reborn",
		    "Sigma reborn. The only client run by speakers of Breton",
		    "Order free baguettes with Sigma reborn client",
		    "Another Sigma reborn user? Awww man",
		    "Sigma reborn utility client no hax 100%",
		    "Hypixel wants to know Sigma reborn owner's location [Accept] [Deny]",
		    "I am a sig-magician, thats how I am able to do all those block game tricks",
		    "Stop it, get some help! Get Sigma reborn",
		    "Sigma reborn users belike: Hit or miss I guess I never miss!",
		    "I dont hack i just have Sigma reborn Gaming Chair",
		    "Stop Hackustation me cuz im just Sigma reborn",
		    "S. I. G. M. A. Hack with me today!",
		    "Subscribe to MentalFrostbyte on youtube and discover Jello for Sigma reborn!",
		    "Beauty is not in the face; beauty is in Jello for Sigma reborn",
		    "Imagine using anything but Sigma reborn",
		    "No hax just beta testing the anti-cheat with Sigma reborn",
		    "Don't forget to report me for Sigma reborn on the forums!",
		    "Search Sigma rebornclient , info to get the best mineman skills!",
		    "don't use Sigma reborn? ok boomer",
		    "Sigma reborn is better than Optifine",
		    "It's not Scaffold it's BlockFly in Jello for Sigma reborn!",
		    "How come a noob like you not use Sigma reborn?",
		    "A mother becomes a true grandmother the day she gets Sigma reborn 5.0",
		    "Fly faster than light, only available in Sigma reborn™",
		    "Behind every Sigma reborn user, is an incredibly cool human being. Trust me, cooler than you.",
		    "Hello Sigma reborn my old friend...",
		    "#SwitchToSigmaReborn",
		    "What? You've never downloaded Jello for Sigma reborn? You know it's the best right?",
		    "Your client sucks, just get Sigma reborn",
		    "Sigma reborn made this world a better place, killing you with it even more",
		    "Stop being a disapointment to your parents and download Sigma reborn!",
		    "After I started using Sigma reborn my dad finally came home from the gas station!",
		    "It's a bird! It's a plane! It's Jello for Sigma reborn!",
		    "you've been killed by a Sigma reborn user, rejoice!",
		    "I'm not hacking it's just my new hair dryer!",
		    "I'm not hacking it's just my 871619-B21 HP Intel Xeon 8180 2.5GHz DL380 G10 processor!",
		    "Omikron is my dad!",
		    "Report me for Sigma reborn!",
		    "Sigma reborn is the only way to play Redesky!",
		    "Sigma reborn 6.0 cures cancer!",
		    "Sigma reborn 6.0 is sexier than you!",
		    "Once I started using Sigma reborn I started getting a lot of matches on tinder!",
		    "Redesky killed by Sigma reborn",
		    "#SigmaRebornOnTop",
		    "Sigma reborn is the best client for any server!",
		    "Don't be like the guy who just died and download Sigma reborn!",
		    "I'm not hacking you're just bad!",
		    "Get Sigma reborn noob!"));
	
	private List<EntityLivingBase> targetList = new ArrayList<EntityLivingBase>();
	public SigMeme() {
        super("Sigmeme", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
    }
   
	public void onEnable(){
		en = true;
		s= true;
		//mc.thePlayer.setLastAttacker(null);)
	}
	
	public void onDisable(){
		  en = false;
		  System.out.print("e");
	}
	

  public void onUpdate()
  {
	  s = true;
	  if(!this.isToggled())
		  return;
	//  if(mc.theWorld.getLoadedEntityList().contains(mc.thePlayer) && mc.theWorld.getLoadedEntityList().size() == 1)
	//  if(timer.hasTimeElapsed(4000, true)) {
		//  mc.thePlayer.sendChatMessage(sul.get((int) (Math.random() * sults.length)));
	 // }
	  
	  if(mc.thePlayer.getLastAttacker() != null) {
		  if(!targetList.contains(mc.thePlayer.getLastAttacker()))
			  targetList.add(mc.thePlayer.getLastAttacker());
	  }
	  if(!targetList.isEmpty()) {
		  //if(targetList.get(0).isDead) {
		  if(!mc.theWorld.getLoadedEntityList().contains(targetList.get(0))) {
			  mc.thePlayer.sendChatMessage(sul.get((int) (Math.random() * sul.size())));
			  targetList.clear();
		  }
		  //}
	  }
	  
	  
	  
  }
	public void deadcheck(EntityLivingBase a) {
		if(a.isDead) {
			targetList.remove(a);
		}
	}
	
}
