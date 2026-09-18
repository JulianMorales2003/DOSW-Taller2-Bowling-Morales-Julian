package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de un juego de Bowling para un jugador.
 * Un juego tiene exactamente 10 frames.
 */
public class BowlingGame {

    public static final int MAX_FRAMES = Frame.LAST_FRAME_NUMBER;

    private final List<Frame> frames;
    private final BowlingScorer scorer;
    private int currentFrame;

    public BowlingGame() {
        this.frames = new ArrayList<>();
        this.scorer = new BowlingScorer();
        this.currentFrame = 0;
    }

    /**
     * Registra pinos derribados.
     *
     * @throws IllegalArgumentException si pins &lt; 0, pins &gt; 10 o supera los pinos en pie del frame
     * @throws IllegalStateException    si el juego ya termino
     */
    public void roll(int pins) {
        Frame.requireValidPins(pins);
        if (isComplete()) {
            throw new IllegalStateException("El juego ya termino: no se admiten mas tiros");
        }
        Frame frame = currentFrame();
        frame.addRoll(pins);
        if (frame.isComplete()) {
            currentFrame++;
        }
    }

    private Frame currentFrame() {
        if (frames.size() == currentFrame) {
            frames.add(new Frame(currentFrame + 1));
        }
        return frames.get(currentFrame);
    }

    /**
     * Puntaje total.
     *
     * @throws IllegalStateException si el juego no esta completo
     */
    public int score() {
        if (!isComplete()) {
            throw new IllegalStateException(
                    "El juego no ha terminado: van " + currentFrame + " de " + MAX_FRAMES + " frames");
        }
        return scorer.calculate(frames);
    }

    /** true cuando los 10 frames han sido completados. */
    public boolean isComplete() {
        return currentFrame == MAX_FRAMES;
    }

    public List<Frame> getFrames() {
        return List.copyOf(frames);
    }
}