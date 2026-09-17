package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

public class BowlingGame {
    private final List<Frame> frames = new ArrayList<>();

    public BowlingGame() {
        frames.add(new Frame());
    }

    public void roll(int pins) {
        if (pins < 0 || pins > 10) {
            throw new IllegalArgumentException("El numero de pinos debe estar entre 0 y 10");
        }

        Frame currentFrame = getCurrentFrame();
        currentFrame.addRoll(pins);
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