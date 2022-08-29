package com.mentalfrostbyte.jello.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.Display;

import com.mentalfrostbyte.jello.alts.AltFile;
import com.mentalfrostbyte.jello.alts.AltManager;
import com.mentalfrostbyte.jello.alts.GuiAltManager;
import com.mentalfrostbyte.jello.command.CommandManager;
import com.mentalfrostbyte.jello.event.events.Event;
import com.mentalfrostbyte.jello.event.events.EventChat;
import com.mentalfrostbyte.jello.hud.JelloHud;
import com.mentalfrostbyte.jello.hud.SplashProgress;
import com.mentalfrostbyte.jello.jelloclickgui.JelloGui;
import com.mentalfrostbyte.jello.modules.Sprint;
import com.mentalfrostbyte.jello.tabgui.TabGUI;
import com.mentalfrostbyte.jello.util.ChestUtil;
import com.mentalfrostbyte.jello.util.FileManager;
import com.mentalfrostbyte.jello.util.FileUtils;
import com.mentalfrostbyte.jello.util.InventoryUtil;
import com.mentalfrostbyte.jello.util.ScriptUtils;
import com.mentalfrostbyte.jello.util.SettingsFile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.ChatComponentText;
import viamcp.ViaMCP;


public class Jello {
	public static String version = "1.15";
	
	public static boolean updateNeeded = false;
	public static ArrayList<File> scripts = new ArrayList<File>();
	public static HashMap<File, Boolean> sc = new HashMap<File, Boolean>();
	public static ArrayList<Module> mods = new ArrayList<Module>();
	private static ScaledResolution s = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
	public static TabGUI tabgui = new TabGUI();
	public static double fontScaleOffset = 1;//round((double)1600/1080, 1) * s.getScaleFactor();//2.75;
	private static JelloHud hud;
	public static Object theClient;
	public static JCore core;
    private static AltManager altManager;
	private static File directory;
    private static FileManager fileManager;
    public static GuiAltManager altmanagergui;
    public static ArrayList<String> clickguiarray = new ArrayList<String>();
    public static SettingsFile settingsFile = new SettingsFile();
    public static JelloGui jgui;
   // public static Menu menu;
    public static CommandManager commandManager = new CommandManager();
    public static ChestUtil chestUtil = new ChestUtil();
	public static InventoryUtil inventoryUtil = new InventoryUtil();
	public static double round (double value, int precision) {
	    int scale = (int) Math.pow(10, precision);
	    return (double) Math.round(value * scale) / scale;
	}
	public static void register() {
        Display.setTitle("Sigma Jello Reborn " + Jello.version);
		SplashProgress.setProgress(8, "Loading Modules");
		register(new com.mentalfrostbyte.jello.modules.ClickGUI());
		register(new com.mentalfrostbyte.jello.modules.AutoArmor());
		register(new com.mentalfrostbyte.jello.modules.AirWalk());
		register(new com.mentalfrostbyte.jello.modules.Notifications());
		register(new com.mentalfrostbyte.jello.modules.InvCleaner());
		register(new com.mentalfrostbyte.jello.modules.KillAura());
		register(new com.mentalfrostbyte.jello.modules.LongJump());
		register(new com.mentalfrostbyte.jello.modules.Compass());
		register(new com.mentalfrostbyte.jello.modules.BlockFly());
		register(new com.mentalfrostbyte.jello.modules.TabGUI());
		register(new com.mentalfrostbyte.jello.modules.CameraNoClip());
		register(new com.mentalfrostbyte.jello.modules.KeyStrokes());
		register(new com.mentalfrostbyte.jello.modules.MiniMap());
		register(new com.mentalfrostbyte.jello.modules.AntiKnockback());
		register(new com.mentalfrostbyte.jello.modules.Chams());
		register(new com.mentalfrostbyte.jello.modules.Step());
		register(new com.mentalfrostbyte.jello.modules.NoSlow());
		register(new com.mentalfrostbyte.jello.modules.ActiveMods());
		register(new com.mentalfrostbyte.jello.modules.Fly());
		register(new com.mentalfrostbyte.jello.modules.HighJump());
		register(new com.mentalfrostbyte.jello.modules.Speed());
		register(new com.mentalfrostbyte.jello.modules.ChestStealer());
		register(new com.mentalfrostbyte.jello.modules.NoFall());
		register(new com.mentalfrostbyte.jello.modules.MenuGUI());
		register(new com.mentalfrostbyte.jello.modules.ESP());
		register(new com.mentalfrostbyte.jello.modules.Animations());
		register(new com.mentalfrostbyte.jello.modules.TargetHUD());
		register(new com.mentalfrostbyte.jello.modules.Sprint());
		register(new com.mentalfrostbyte.jello.modules.AntiVoid());
		register(new com.mentalfrostbyte.jello.modules.GuiMove());
		register(new com.mentalfrostbyte.jello.modules.SigMeme());
		register(new com.mentalfrostbyte.jello.modules.TargetStrafe());
		register(new com.mentalfrostbyte.jello.modules.InfiniteAura());
		register(new com.mentalfrostbyte.jello.modules.Blink());
		register(new com.mentalfrostbyte.jello.modules.Autoclicker());
		register(new com.mentalfrostbyte.jello.modules.FastUse());
		register(new com.mentalfrostbyte.jello.modules.Criticals());
		register(new com.mentalfrostbyte.jello.modules.RenderModule());
		register(new com.mentalfrostbyte.jello.modules.Tracers());
		register(new com.mentalfrostbyte.jello.modules.ItemPhysics());
		register(new com.mentalfrostbyte.jello.modules.AntiBot());
		//register(new com.mentalfrostbyte.jello.modules.NoRotateSet());
		register(new com.mentalfrostbyte.jello.modules.Eagle());
		register(new com.mentalfrostbyte.jello.modules.AntiCheat());
	//	register(new com.mentalfrostbyte.jello.modules.MatrixShit());
		register(new com.mentalfrostbyte.jello.modules.AutoRegister());
	//	register(new com.mentalfrostbyte.jello.modules.AutoBlock());
		register(new com.mentalfrostbyte.jello.modules.WorldTime());
		register(new com.mentalfrostbyte.jello.modules.NoHurtCam());
		register(new com.mentalfrostbyte.jello.modules.AutoPot());
		register(new com.mentalfrostbyte.jello.modules.AutoTool());
	//	register(new com.mentalfrostbyte.jello.modules.Disabler());
		//register(new com.mentalfrostbyte.jello.modules.ResetVL());
		register(new com.mentalfrostbyte.jello.modules.HvHAura());
		register(new com.mentalfrostbyte.jello.modules.Regen());
		register(new com.mentalfrostbyte.jello.modules.NameProtector());
	//	register(new com.mentalfrostbyte.jello.modules.NameTags());
		register(new com.mentalfrostbyte.jello.modules.ServerCrasher());
		register(new com.mentalfrostbyte.jello.modules.AntiCrash());
		ScriptUtils.load();
		System.out.println("Modules loaded");
		SplashProgress.setProgress(9, "Loading Settings");
		/*
        try
        {
         	 ViaMCP.getInstance().start();
          
      //     Only use one of the following
          ViaMCP.getInstance().initAsyncSlider(); // For top left aligned slider
        }
        catch (Exception e)
        {
          e.printStackTrace();
       }*/
			
		Jello.fileManager = new FileManager();
		jgui = new JelloGui();
		altManager = new AltManager();
		altmanagergui = new GuiAltManager();
		hud = new JelloHud();
		core = new JCore();
		
		settingsFile.loadFiles();
		
		Jello.altManager.setupAlts();
		
		
		AltFile.load();
		SplashProgress.setProgress(10, "Starting Up");
		
		
		//check for updates
    	try {
    		List<String> lines = new ArrayList<String>();
    		String agent1 = "User-Agent";
    	    String agent2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36";
    	    String link = "https://raw.githubusercontent.com/JelloSigma/h/main/h.txt";
    	    String line;
    	    
    	   URL url = new URL(link);
    	   HttpURLConnection connection = (HttpURLConnection)url.openConnection();
           connection.addRequestProperty(agent1, agent2);
           BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
            	lines.add(line);
            }
            for(String s : lines){
            	i += s;
            }
            
            
            //check
            
            if(i.contains(version)) {
            	updateNeeded = false;
            }else {
            	updateNeeded = true;
            }
            in.close();
    	}catch(Exception e) {
    		System.out.println(e);
    	}
	}
    private static String i;
	public static void register(Module module) {
		mods.add(module);
	}

	public static ArrayList<Module> getModules() {
		return mods;
	}
	
	public static JelloHud getInGameGUI() {
		return hud;
	}
	
	public static void onKeyPressed(int keyCode) {
		for(Module module : mods) {
			if(module.getKeyCode() == keyCode) {
				module.toggle();
			}
		}
		if(keyCode == 200){
			tabgui.keyUp();
		}
		if(keyCode == 208){
			tabgui.keyDown();
		}
		if(keyCode == 203){
			tabgui.keyLeft();
		}
		if(keyCode == 205){
			tabgui.keyRight();
		}
	}
	
	public static void onUpdate() {
		for(Module module : mods) {
			module.onUpdate();
		}
	}
	
	public static void onEvent(Event e) {
		if(e instanceof EventChat) {
			commandManager.handleChat((EventChat)e);
		}
	}
	
	public static void onRender() {
		for(Module module : mods) {
			module.onRender();
		}
	}
	
	public static AltManager getAltManager() {
        return Jello.altManager;
    }
	public static FileManager getFileManager() {
        return Jello.fileManager;
    }
	public static void addChatMessage(String s) {
		core.player().addChatMessage(new ChatComponentText("§6[Jello] \247r" + s));
	}
	public static void addSilentChatMessage(String s) {
		core.player().addChatMessage(new ChatComponentText(s));
	}

	public static void sendChatMessage(String s) {
		core.player().sendChatMessage(s);
	}
	public static File getDirectory() {
        return Jello.directory;
    }
	public static boolean onSendChatMessage(String s) {// EntityPlayerSP
		
		return true;
	}
	
	public static Module getModule(final String modName) {
        for (final Module module : getModules()) {
            if (module.getName().equalsIgnoreCase(modName) || module.getName().equalsIgnoreCase(modName)) {
                return module;
            }
        }
        return null;
    }
	
	public static ArrayList<Module> getModulesInCategory(TabGUI.Cat cat)
    {
      ArrayList<Module> modsInCat = new ArrayList();
      for (Module mod : getModules()) {
    	  if(mod.jelloCat != null){
    		  if(mod.jelloCat.equals(cat)){
    			  modsInCat.add(mod);
    		  }
    	  }
      }
      return modsInCat;
    }
	
}
