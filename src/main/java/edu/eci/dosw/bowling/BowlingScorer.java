package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

/**
 * Calcula el puntaje de una lista de frames aplicando los bonos de spare y strike.
 * No tiene estado: solo recibe frames y devuelve numeros.
 */
public class BowlingScorer {

    private static final int STRIKE_BONUS_ROLLS = 2;
    private static final int SPARE_BONUS_ROLLS = 1;

    /** Puntaje total de los frames. */
    public int calculate(List<Frame> frames) {
        return scoreByFrame(frames).stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Puntaje individual de cada frame (no acumulado), en orden.
     * Si faltan tiros de bono (juego en curso), el bono cuenta solo lo ya lanzado.
     */
    public List<Integer> scoreByFrame(List<Frame> frames) {
        List<Integer> allRolls = frames.stream()
                .flatMap(frame -> frame.getRolls().stream())
                .toList();

        List<Integer> scores = new ArrayList<>();
        int firstRollOfFrame = 0;
        for (Frame frame : frames) {
            scores.add(frameScore(frame, allRolls, firstRollOfFrame));
            firstRollOfFrame += frame.getRolls().size();
        }
        return List.copyOf(scores);
    }

    /*
     * El frame 10 no necesita un caso especial: sus tiros extra ya estan en la
     * lista, asi que "10 + los dos siguientes" o "10 + el siguiente" dan
     * exactamente la suma de sus propios tiros.
     */
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
}