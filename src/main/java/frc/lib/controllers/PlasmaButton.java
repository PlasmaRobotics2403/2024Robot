package frc.lib.controllers;

import edu.wpi.first.wpilibj.*;

public class PlasmaButton {
	
	private final int joystickPort;
	private final byte buttonNumByte;
	
	private boolean isHeld = false;
	private boolean isToggled = false;
	
	/**
	 * Contructor for the button class
	 * 
	 * @param buttonNum - ID value of the button
	 * @param joystickPort - Joystick port that the button is on
	 * 
	 * @author Nic A
	 */
	public PlasmaButton(int buttonNum, int joystickPort) {
		this.joystickPort = joystickPort;
		this.buttonNumByte = (byte)buttonNum;
	}
	
	/**
	 * Check if button is pressed
	 * 
	 * @return True if button is pressed
	 * 
	 * @author Nic A
	 */
	public boolean isPressed(){
		return DriverStation.getStickButton(joystickPort, buttonNumByte);
	}
	
	/**
	 * Checks if button has been switched from off to on
	 * 
	 * @return True once when button is toggled on
	 * 
	 * @author Nic A
	 */
	public boolean isOffToOn(){
		if(!isHeld && isPressed()){
			isHeld = true;
			return true;
		}
		else{
			isHeld = isPressed();
			return false;
		}
	}
	
	/**
	 * Checks if button has been switched from on to off
	 * 
	 * @return True once when button is toggled off
	 * 
	 * @author Nic A
	 */
	public boolean isOnToOff(){
		if(isHeld && !isPressed()){
			isHeld = false;
			return true;
		}
		else{
			isHeld = isPressed();
			return false;
		}
	}
	
	public boolean isToggledOn(){
		if(isOnToOff()){
			isToggled = !isToggled;
		}
		return isToggled;
	}
	

}