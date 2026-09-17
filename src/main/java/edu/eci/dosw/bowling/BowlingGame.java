package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

public class BowlingGame {
    private final List<Frame> frames = new ArrayList<>();
    private final int[] rolls = new int[21];
    private int currentRoll = 0;

    public BowlingGame() {
        frames.add(new Frame());
    }

    public void roll(int pins) {
        if (isFinished()) {
            throw new IllegalStateException("El juego ya ha finalizado");
        }

        if (pins < 0 || pins > 10) {
            throw new IllegalArgumentException("El numero de pinos debe estar entre 0 y 10");
        }

        Frame currentFrame = getCurrentFrame();
        currentFrame.addRoll(pins);
        rolls[currentRoll++] = pins;
    }

    public int score() {
        int score = 0;
        int rollIndex = 0;
        for (int frame = 0; frame < 10; frame++) {
            if (isStrike(rollIndex)) {
                score += 10 + rolls[rollIndex + 1] + rolls[rollIndex + 2];
                rollIndex++;
            } else if (isSpare(rollIndex)) {
                score += 10 + rolls[rollIndex + 2];
                rollIndex += 2;
            } else {
                score += rolls[rollIndex] + rolls[rollIndex + 1];
                rollIndex += 2;
            }
        }
        return score;
    }

    private boolean isStrike(int rollIndex) {
        return rolls[rollIndex] == 10;
    }

    private boolean isSpare(int rollIndex) {
        return rolls[rollIndex] + rolls[rollIndex + 1] == 10;
    }

    public boolean isFinished() {
        return frames.size() == 10 && frames.get(9).isComplete();
    }

    private Frame getCurrentFrame() {
        Frame current = frames.get(frames.size() - 1);
        if (current.isComplete() && frames.size() < 10) {
            current = new Frame();
            frames.add(current);
        }
        return current;
    }
}