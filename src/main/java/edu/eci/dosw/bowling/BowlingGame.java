package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

public class BowlingGame {
    private final List<Frame> frames = new ArrayList<>();

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
    }

    public int score() {
        int totalScore = 0;
        for (Frame frame : frames) {
            totalScore += frame.getPins();
        }
        return totalScore;
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