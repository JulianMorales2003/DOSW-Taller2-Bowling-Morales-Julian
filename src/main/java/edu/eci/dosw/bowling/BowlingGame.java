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

        if (currentRoll % 2 == 1 && rolls[currentRoll - 1] != 10 && currentRoll < 18) {
            if (rolls[currentRoll - 1] + pins > 10) {
                throw new IllegalArgumentException("La suma de pines en un frame no puede superar 10");
            }
        }

        rolls[currentRoll++] = pins;

        if (pins == 10 && currentRoll < 18 && currentRoll % 2 == 1) {
            currentRoll++;
        }
    }

    public int score() {
        int score = 0;
        int frameIndex = 0;

        for (int frame = 0; frame < 10; frame++) {
            if (isStrike(frameIndex)) {
                score += 10 + strikeBonus(frameIndex);
                frameIndex += 2;
            } else if (isSpare(frameIndex)) {
                score += 10 + spareBonus(frameIndex);
                frameIndex += 2;
            } else {
                score += sumOfBallsInFrame(frameIndex);
                frameIndex += 2;
            }
        }
        return score;
    }

    public boolean isFinished() {
        int frameIndex = 0;
        for (int frame = 0; frame < 9; frame++) {
            frameIndex += 2;
        }

        if (frameIndex >= currentRoll) return false;

        if (isStrike(frameIndex)) {
            return currentRoll >= frameIndex + 3;
        } else if (isSpare(frameIndex)) {
            return currentRoll >= frameIndex + 3;
        } else {
            return currentRoll >= frameIndex + 2;
        }
    }

    private boolean isStrike(int frameIndex) {
        return rolls[frameIndex] == 10;
    }

    private boolean isSpare(int frameIndex) {
        return rolls[frameIndex] + rolls[frameIndex + 1] == 10;
    }

    private int strikeBonus(int frameIndex) {
        int next = frameIndex + 2;
        if (rolls[next] == 10 && next < 18) {
            return 10 + rolls[next + 2];
        }
        return rolls[frameIndex + 2] + rolls[frameIndex + 3];
    }

    private int spareBonus(int frameIndex) {
        return rolls[frameIndex + 2];
    }

    private int sumOfBallsInFrame(int frameIndex) {
        return rolls[frameIndex] + rolls[frameIndex + 1];
    }
}