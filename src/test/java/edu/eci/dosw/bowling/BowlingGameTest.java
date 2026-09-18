package edu.eci.dosw.bowling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Modulo A (roll) y Modulo C (isComplete) de BowlingGame.
 */
@DisplayName("BowlingGame - roll() e isComplete()")
class BowlingGameTest {

    // ---------- Helpers ----------

    private void rollMany(BowlingGame game, int times, int pins) {
        for (int i = 0; i < times; i++) {
            game.roll(pins);
        }
    }

    // ---------- Modulo A: roll() ----------

    @Test
    @DisplayName("A1 - roll(0) no lanza excepcion y el frame registra 0 pinos")
    void rollZero_registersZeroPins() {
        // Arrange
        BowlingGame game = new BowlingGame();
        // Act & Assert
        assertDoesNotThrow(() -> game.roll(0));
        assertEquals(List.of(0), game.getFrames().get(0).getRolls());
    }

    @Test
    @DisplayName("A2 - roll(-1) lanza IllegalArgumentException")
    void rollNegativePins_throwsException() {
        BowlingGame game = new BowlingGame();

        assertThrows(IllegalArgumentException.class, () -> game.roll(-1));
        assertTrue(game.getFrames().isEmpty(), "Un tiro invalido no debe modificar el juego");
    }

    @Test
    @DisplayName("A3 - roll(11) lanza IllegalArgumentException")
    void rollMoreThanTenPins_throwsException() {
        BowlingGame game = new BowlingGame();

        assertThrows(IllegalArgumentException.class, () -> game.roll(11));
        assertTrue(game.getFrames().isEmpty(), "Un tiro invalido no debe modificar el juego");
    }

    @Test
    @DisplayName("A4 - dos tiros que suman mas de 10 en un frame lanzan IllegalArgumentException")
    void twoRollsOverTenInFrame_throwsException() {
        BowlingGame game = new BowlingGame();
        game.roll(7);

        assertThrows(IllegalArgumentException.class, () -> game.roll(6));
        assertEquals(List.of(7), game.getFrames().get(0).getRolls());
    }

    @Test
    @DisplayName("A5 - roll() con el juego completo lanza IllegalStateException")
    void rollWhenGameIsComplete_throwsIllegalState() {
        BowlingGame game = new BowlingGame();
        rollMany(game, 20, 0);

        assertThrows(IllegalStateException.class, () -> game.roll(0));
    }

    @Test
    @DisplayName("A6 - roll(10) marca el frame como STRIKE y avanza al siguiente frame")
    void rollTen_marksStrikeAndAdvancesFrame() {
        BowlingGame game = new BowlingGame();

        game.roll(10);
        game.roll(3);

        List<Frame> frames = game.getFrames();
        assertEquals(FrameType.STRIKE, frames.get(0).getType());
        assertEquals(2, frames.size());
        assertEquals(List.of(3), frames.get(1).getRolls());
    }

    @Test
    @DisplayName("A7 - roll(5) + roll(5) marca el frame como SPARE")
    void rollFiveAndFive_marksSpare() {
        BowlingGame game = new BowlingGame();

        game.roll(5);
        game.roll(5);

        assertEquals(FrameType.SPARE, game.getFrames().get(0).getType());
    }

    @Test
    @DisplayName("A8 - el frame 10 con strike acepta 3 tiros")
    void tenthFrameStrike_acceptsThreeRolls() {
        BowlingGame game = new BowlingGame();
        rollMany(game, 18, 0);

        assertDoesNotThrow(() -> {
            game.roll(10);
            game.roll(10);
            game.roll(10);
        });
        assertEquals(3, game.getFrames().get(9).getRolls().size());
    }

    // ---------- Modulo C: isComplete() ----------

    @Test
    @DisplayName("C1 - isComplete() es false al inicio del juego")
    void newGame_isNotComplete() {
        BowlingGame game = new BowlingGame();

        assertFalse(game.isComplete());
    }

    @Test
    @DisplayName("C2 - isComplete() es false despues de 9 frames completos")
    void afterNineFrames_isNotComplete() {
        BowlingGame game = new BowlingGame();
        rollMany(game, 18, 1);

        assertEquals(9, game.getFrames().size());
        assertFalse(game.isComplete());
    }

    @Test
    @DisplayName("C3 - isComplete() es true con 10 frames normales completos")
    void tenOpenFrames_isComplete() {
        BowlingGame game = new BowlingGame();
        rollMany(game, 20, 1);

        assertTrue(game.isComplete());
    }

    @Test
    @DisplayName("C4 - spare en el frame 10 + tiro bonus: isComplete() es true")
    void spareInTenthFramePlusBonus_isComplete() {
        BowlingGame game = new BowlingGame();
        rollMany(game, 18, 0);
        game.roll(6);
        game.roll(4);

        assertFalse(game.isComplete(), "Tras el spare del frame 10 falta el tiro bonus");

        game.roll(3);

        assertTrue(game.isComplete());
    }

    @Test
    @DisplayName("C5 - strike en el frame 10 + 2 tiros bonus: isComplete() es true")
    void strikeInTenthFramePlusTwoBonus_isComplete() {
        BowlingGame game = new BowlingGame();
        rollMany(game, 18, 0);
        game.roll(10);
        game.roll(4);

        assertFalse(game.isComplete(), "Tras el strike del frame 10 faltan dos tiros bonus");

        game.roll(3);

        assertTrue(game.isComplete());
    }

    @Test
    @DisplayName("C6 - juego perfecto: isComplete() es true tras el strike 12")
    void perfectGame_isCompleteAfterTwelfthStrike() {
        BowlingGame game = new BowlingGame();
        rollMany(game, 11, 10);

        assertFalse(game.isComplete());

        game.roll(10);

        assertTrue(game.isComplete());
    }
}