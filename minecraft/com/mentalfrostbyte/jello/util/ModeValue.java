package com.mentalfrostbyte.jello.util;

import java.util.Arrays;
import java.util.List;

public class ModeValue extends Value{

	
	public int index;
	public List<String> modes;
	public ModeValue(String name, String defaultMode, String... modes) {
		super(name);
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
	}
	public String getMode() {
		//System.out.println("....................................................." + modes.get(index));
		return modes.get(index);
	}
	public boolean is(String mode) {
		return index == modes.indexOf(mode);
	}
	public void cycle() {
		if(index < modes.size() - 1) {
			index++;
		}else {
			index = 0;
		}
	}
	public void setMode(String mode) {
		//if(modes.contains(mode)) {
		//	if(modes.get(index) != mode) {
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
				cycleWithCheck(mode);
		//	}
		//}
	}
	private void cycleWithCheck(String mode) {
	//	if(modes.contains(mode)) {
			if(modes.indexOf(mode) != index) {
				System.out.println("goasg" + index + mode);
				cycle();
			}else {
				System.out.println("ITS THE RIGHT ONEEEEEEEEEEEEEEEEEEEEEEE");
			}
		}
	//}
}
