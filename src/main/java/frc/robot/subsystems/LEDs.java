package frc.robot.subsystems;

import java.util.Random;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class LEDs {
    private AddressableLED LED   ;
    private AddressableLEDBuffer LEDBuffer;
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
        RED1(0,255, 128, 0b111111110000000000000000),
        RED2(0, 153, 128, 0b11111111110010101100110),
        RED3(0, 51, 128, 0b111111110011001100110011),
        ORANGE1(36, 255, 128, 0b001001001111111111111111),
        ORANGE2(30, 153, 128, 0b111111111011001001100110),
        ORANGE3(29, 51, 128, 0b111111111110010111001100),
        YELLOW1(60, 255, 128, 0b111111111111111100000000),
        YELLOW2(60, 153, 128, 0b111111111111111101100110),
        YELLOW3(60, 51, 128, 0b111111111111111111001100),
        GREEN1(120, 255, 128, 0b000000001111111100000000),
        GREEN2(120, 153, 128, 0b011001101111111101100110),
        GREEN3(120, 51, 128, 0b110011001111111111001100),
        BLUE1(240, 255, 128, 0b000000000000000011111111),
        BLUE2(240, 153, 128, 0b011001100110011011111111);

        int h;
        int s;
        int v;
        int sorting;

        private bogo (int h, int s, int v, int sorting) {
            this.h = h;
            this.s = s;
            this.v = v;
            this.sorting = sorting;
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

        bogoArray = new bogo[]{bogo.RED1, 
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
        for (int i = 0; i < getBufferLength(); i++) {
            setHSV(i, bogoArray[i].h, bogoArray[i].s, bogoArray[i].v);
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