package edu.eci.dosw.bowling;

public class BowlingGame {
    public void roll(int pins) {
        if (pins < 0 || pins > 10) {
            throw new IllegalArgumentException("El numero de pinos debe estar entre 0 y 10");
        }
    }
}