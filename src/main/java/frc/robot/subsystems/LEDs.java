package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class LEDs {
    private AddressableLED LED   ;
    private AddressableLEDBuffer LEDBuffer;
    // Store what the last hue of the first pixel is
    private int firstPixelHue;

    public enum Color {
        RED(255,0,0),
        GREEN(0,255,0),
        BLUE(0,0,255);

        int r;
        int g;
        int b;

        private Color (int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    public enum LEDState {
        ALLIGNED,
        HASPEICE,
        NOPEICE;
    }

    public LEDState currentState;

    public LEDs() {
        LED = new AddressableLED(0);
        LEDBuffer = new AddressableLEDBuffer(27);
    
        LED.setLength(LEDBuffer.getLength());

        currentState = LEDState.NOPEICE;
    
        LED.setData(LEDBuffer);
        LED.start();
    }


    /**
     * makes the leds do a rainbow pattern
     */
    public void Rainbow() {
        for (var i = 0; i < LEDBuffer.getLength(); i++) {
            final var hue = (firstPixelHue + (i * 180 / LEDBuffer.getLength())) % 180;
            LEDBuffer.setHSV(i, hue, 255, 128);
        }
    
          firstPixelHue += 3;
          firstPixelHue %= 180;

          LED.setData(LEDBuffer);
    }


    /**
     * setting one of the leds a certian color in HS
     * @param i which led
     * @param hue color [0-180 degrees]
     * @param saturation how much of the color [0-255]
     * @param value the brightness of the color [0-255]
     */
    public void setHSV(int i, int hue, int saturation, int value) {
        LEDBuffer.setHSV(i, hue, saturation, value);
    }


    /**
     * setting all of the leds a certian color in HS
     * @param hue color [0-180 degrees]
     * @param saturation how much of the color [0-255]
     * @param value the brightness of the color [0-255]
     */
    public void setHSV(int hue, int saturation, int value) {
        for (int i = 0; i < getBufferLength(); i++) {
            LEDBuffer.setHSV(i, hue, saturation, value);
        }
    }


    /**
     * adressing one led at a time with a color RGB
     * @param i what led
     * @param red how much red is in the led [0-255]
     * @param green how much green is in the led [0-255]
     * @param blue how much blue is in the led [0-255]
     */
    public void setRGB(int i, int red, int green, int blue) {
        LEDBuffer.setRGB(i, red, green, blue);
    }


    /**
     * gives every led the same color in RGB
     * @param red how much red is in the led [0-255]
     * @param green how much green is in the led [0-255]
     * @param blue how much blue is in the led [0-255]
     */
    public void setRGB(int red, int green, int blue) {
        for (int i = 0; i < getBufferLength(); i++) {
            LEDBuffer.setRGB(i, red, green, blue);
        }
    }


    /**
     * 
     * @return number of leds in buffer
     */
    public int getBufferLength() {
        return LEDBuffer.getLength();
    }


    /**
     * gives the leds imformation
     */
    public void sendData() {
        LED.setData(LEDBuffer);
    }
    /**
     * sets the leds state
     * @param state
     */
    public void setState(LEDState state) {
        currentState = state;
    }
    /**
     * gets the current leds state
     * @return currentState
     */
    public LEDState getState() {
        return currentState;
    }

    public void periodic() {
        switch (currentState) {
            case ALLIGNED:
                setRGB(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b);
                break;

            case HASPEICE:
                setRGB(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b); 
                break;
            
            case NOPEICE:
                Rainbow();
                //setHSV(120, 255, 125);
                //setRGB(Color.RED.r, Color.RED.g, Color.RED.b);
                break;
        }
    }
}