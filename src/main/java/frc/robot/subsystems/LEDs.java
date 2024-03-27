package frc.robot.subsystems;

import java.util.Random;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class LEDs {
    private AddressableLED LED   ;
    private AddressableLEDBuffer LEDBuffer;
    private int bogoCycle;
    // Store what the last hue of the first pixel is
    private int firstPixelHue;

    private bogo[] bogoArray;

    public enum Color {
        RED(0,255,128),
        GREEN(60,255,128),
        BLUE(130,255,128);

        int h;
        int s;
        int v;

        private Color (int h, int s, int v) {
            this.h = h;
            this.s = s;
            this.v = v;
        }
    }

    public enum bogo {
        RED1(0,255, 150, 1),
        RED2(13, 255, 150, 2),
        RED3(26, 255, 150, 3),
        ORANGE1(39, 255, 150, 4),
        ORANGE2(52, 255, 150, 5),
        ORANGE3(65, 255, 150, 6),
        YELLOW1(78, 255, 150, 7),
        YELLOW2(91, 255, 150, 8),
        YELLOW3(104, 255, 150, 9),
        GREEN1(117, 255, 150, 10),
        GREEN2(130, 255, 150, 11),
        GREEN3(143, 255, 150, 12),
        BLUE1(156, 255, 150, 13),
        BLUE2(169, 255, 150, 14);

        int h; // 0-180
        int s; // 0-255
        int v; // 0-255
        int sorting;

        private bogo (int h, int s, int v, int sorting) {
            this.h = h;
            this.s = s;
            this.v = v;
            this.sorting = sorting;
        }
    }

    public enum LEDState {
        BOGO,
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

        bogoArray = new bogo[]{
                    bogo.RED1, 
                    bogo.RED2, 
                    bogo.RED3,
                    bogo.ORANGE1,
                    bogo.ORANGE2,
                    bogo.ORANGE3,
                    bogo.YELLOW1,
                    bogo.YELLOW2,
                    bogo.YELLOW3,
                    bogo.GREEN1,
                    bogo.GREEN2,
                    bogo.GREEN3,
                    bogo.BLUE1,
                    bogo.BLUE2};
        ShuffleArray(bogoArray);

        bogoCycle = 0;
    }

    private void ShuffleArray(bogo[] ar) {
        Random random = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
          int index = random.nextInt(i + 1);
          // Simple swap
          bogo a = ar[index];
          ar[index] = ar[i];
          ar[i] = a;
        }
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
          LED.start();
    }

    public boolean BogoSort() {
        // check is sorted
        boolean isSorted = true;
        for(int i = 0; i < bogoArray.length - 1; i++) {
            if(bogoArray[i].sorting > bogoArray[i+1].sorting) {
                isSorted = false;
            }
        }

        // randomize if not sorted
        if (!isSorted) {
            ShuffleArray(bogoArray);
        }

        // apply array
        for (int i = 0; i < bogoArray.length; i++) {
            setHSV(i*2, bogoArray[i].h, bogoArray[i].s, bogoArray[i].v);
            
            if (i < bogoArray.length-1) {
                setHSV(i*2+1, bogoArray[i].h, bogoArray[i].s, bogoArray[i].v);
            }
        }

        return isSorted;
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
        LED.setData(LEDBuffer);
        LED.start();

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

        LED.setData(LEDBuffer);
        LED.start();

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
        LED.setData(LEDBuffer);
        LED.start();
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
            case BOGO:
                bogoCycle++;
                if(bogoCycle > 10) {
                    BogoSort();
                    bogoCycle = 0;
                }
                break;
            case ALLIGNED:
                setHSV(Color.GREEN.h, Color.GREEN.s, Color.GREEN.v);
                break;

            case HASPEICE:
                setHSV(Color.BLUE.h, Color.BLUE.s, Color.BLUE.v); 
                break;
            
            case NOPEICE:
                setHSV(Color.RED.h, Color.RED.s, Color.RED.v);
                //setHSV(0, 255, 128);
                break;
        }
    }
}