package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de un juego de Bowling para un jugador.
 * Un juego tiene exactamente 10 frames.
 */
public class BowlingGame {

    public static final int MAX_FRAMES = Frame.LAST_FRAME_NUMBER;

    private static final int STRIKE_BONUS_ROLLS = 2;
    private static final int SPARE_BONUS_ROLLS = 1;

    private final List<Frame> frames;
    private int currentFrame;

    public BowlingGame() {
        this.frames = new ArrayList<>();
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
        List<Integer> allRolls = frames.stream()
                .flatMap(frame -> frame.getRolls().stream())
                .toList();

        int total = 0;
        int firstRollOfFrame = 0;
        for (Frame frame : frames) {
            total += frameScore(frame, allRolls, firstRollOfFrame);
            firstRollOfFrame += frame.getRolls().size();
        }
        return total;
    }

    private int frameScore(Frame frame, List<Integer> allRolls, int firstRoll) {
        if (frame.isStrike()) {
            return Frame.MAX_PINS + bonus(allRolls, firstRoll + 1, STRIKE_BONUS_ROLLS);
        }
        if (frame.isSpare()) {
            return Frame.MAX_PINS + bonus(allRolls, firstRoll + 2, SPARE_BONUS_ROLLS);
        }
        return frame.getPinsKnocked();
    }

    private int bonus(List<Integer> allRolls, int from, int count) {
        int total = 0;
        for (int i = from; i < from + count && i < allRolls.size(); i++) {
            total += allRolls.get(i);
        }
        return total;
    }

    /** true cuando los 10 frames han sido completados. */
    public boolean isComplete() {
        return currentFrame == MAX_FRAMES;
    }

    public List<Frame> getFrames() {
        return List.copyOf(frames);
    }
}