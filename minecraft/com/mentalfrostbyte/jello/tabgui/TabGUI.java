package com.mentalfrostbyte.jello.tabgui;

import java.util.Arrays;
import java.util.List;

import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;

public class TabGUI {

	public int currentCategory;
	public int currentModule;
	public boolean viewingCats = true;
	public float seenTrans;
	public boolean showModules;
	
	
	
	public void keyRight(){
		if(viewingCats){
			if(!showModules){
			showModules = true;
			}else{
				if(Jello.getModulesInCategory(cats.get(currentCategory)).size() != 0)
				Jello.getModulesInCategory(cats.get(currentCategory)).get(cats.get(currentCategory).selectedIndex).toggle();
			}
		}
	}
	
	public void keyLeft(){
		if(viewingCats){
			if(showModules){
			showModules = false;
			}
		}
	}
	
	public void keyDown(){
		if(viewingCats){
			if(!showModules){
			if(currentCategory < cats.size()-1){
				currentCategory++;
			}else{
				currentCategory = 0;
			}
		}else{
			if(cats.get(currentCategory).selectedIndex < Jello.getModulesInCategory(cats.get(currentCategory)).size()-1){
			cats.get(currentCategory).selectedIndex++;	
			}else{
				cats.get(currentCategory).selectedIndex = 0;
			}
		}
		}
	}
	
	public void keyUp(){
		if(viewingCats){
			if(!showModules){
			if(currentCategory > 0){
				currentCategory--;
			}else{
				currentCategory = cats.size()-1;
			}
			}else{
				if(cats.get(currentCategory).selectedIndex > 0){
				cats.get(currentCategory).selectedIndex--;	
				}else{
					cats.get(currentCategory).selectedIndex = Jello.getModulesInCategory(cats.get(currentCategory)).size()-1;
				}
			}
		}
	}
	
	public List<Cat> cats = Arrays.asList(Cat.MOVEMENT, Cat.PLAYER, Cat.COMBAT, Cat.ITEM, Cat.RENDER, Cat.WORLD, Cat.MISC);
	
	public enum Cat{
		MOVEMENT("Movement", "MovementCat1.png", 0),
		PLAYER("Player", "PlayerCat1.png", 1),
		COMBAT("Combat", "CombatCat1.png", 2),
		ITEM("Item", "ItemCat1.png", 3),
		RENDER("Render", "RenderCat1.png", 4),
		WORLD("World", "WorldCat1.png", 5),
		MISC("Misc", "ScriptsCat1.png", 6);
	//	SCRIPTS("Scripts", "ScriptsCat1.png", 7);//needs icons^
		
		public String location;
		public int index;
		public String name;
		public int selectedIndex;
		public float selectedTrans;
		public float lastSelectedTrans;
		//public float trans;
		public float seenTrans;

		private Cat(String n, String name, final int index) {
			this.name = n;
            this.location = name;
            this.index = index;
        }
	}
	
}
