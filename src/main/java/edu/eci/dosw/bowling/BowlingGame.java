package edu.eci.dosw.bowling;

public class BowlingGame {
    public void roll(int pins) {
        if (pins < 0) {
            throw new IllegalArgumentException("Los pinos no pueden ser negativos");
        }
    }
}
