package edu.eci.dosw.bowling;

public class BowlingGame {
    private final int[] rolls = new int[21];
    private int currentRoll = 0;

    public void roll(int pins) {
        if (pins < 0 || pins > 10) {
            throw new IllegalArgumentException("Los pinos deben estar entre 0 y 10");
        }
        if (isFinished()) {
            throw new IllegalStateException("El juego ya ha finalizado");
        }

        if (isSecondRollInStandardFrame()) {
            if (rolls[currentRoll - 1] + pins > 10) {
                throw new IllegalArgumentException("La suma de pines en un frame no puede superar 10");
            }
        }

        rolls[currentRoll++] = pins;
    }

    public int score() {
        int score = 0;
        int rollIndex = 0;

        for (int frame = 0; frame < 10; frame++) {
            if (isStrike(rollIndex)) {
                score += 10 + strikeBonus(rollIndex);
                rollIndex += 1;
            } else if (isSpare(rollIndex)) {
                score += 10 + spareBonus(rollIndex);
                rollIndex += 2;
            } else {
                score += sumOfBallsInFrame(rollIndex);
                rollIndex += 2;
            }
        }
        return score;
    }

    public boolean isFinished() {
        int rollIndex = 0;
        for (int frame = 0; frame < 9; frame++) {
            if (isStrike(rollIndex)) {
                rollIndex += 1;
            } else {
                rollIndex += 2;
            }
        }

        if (rollIndex >= currentRoll) return false;

        if (isStrike(rollIndex) || isSpare(rollIndex)) {
            return currentRoll >= rollIndex + 3;
        } else {
            return currentRoll >= rollIndex + 2;
        }
    }

    private boolean isSecondRollInStandardFrame() {
        int rollIndex = 0;
        for (int frame = 0; frame < 9; frame++) {
            if (rollIndex == currentRoll) return false;
            if (isStrike(rollIndex)) {
                rollIndex += 1;
            } else {
                if (rollIndex + 1 == currentRoll) return true;
                rollIndex += 2;
            }
        }
        return false;
    }

    private boolean isStrike(int rollIndex) {
        return rolls[rollIndex] == 10;
    }

    private boolean isSpare(int rollIndex) {
        return rolls[rollIndex] + rolls[rollIndex + 1] == 10;
    }

    private int strikeBonus(int rollIndex) {
        return rolls[rollIndex + 1] + rolls[rollIndex + 2];
    }

    private int spareBonus(int rollIndex) {
        return rolls[rollIndex + 2];
    }

    private int sumOfBallsInFrame(int rollIndex) {
        return rolls[rollIndex] + rolls[rollIndex + 1];
    }
}