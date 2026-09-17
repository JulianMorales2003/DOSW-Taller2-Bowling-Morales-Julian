package edu.eci.dosw.bowling;

public class Frame {
    private int firstRoll = -1;
    private int secondRoll = -1;

    public boolean isComplete() {
        return firstRoll != -1 && (isStrike() || secondRoll != -1);
    }

    public void addRoll(int pins) {
        if (firstRoll == -1) {
            firstRoll = pins;
        } else if (secondRoll == -1) {
            if (firstRoll + pins > 10) {
                throw new IllegalArgumentException("La suma de los tiros en un marco no puede superar 10");
            }
            secondRoll = pins;
        }
    }

    public boolean isStrike() {
        return firstRoll == 10;
    }

    public boolean isSpare() {
        return !isStrike() && firstRoll != -1 && secondRoll != -1 && (firstRoll + secondRoll == 10);
    }

    public int getPins() {
        int sum = 0;
        if (firstRoll != -1) sum += firstRoll;
        if (secondRoll != -1) sum += secondRoll;
        return sum;
    }

    public int getFirstRoll() { return firstRoll; }
    public int getSecondRoll() { return secondRoll; }
}