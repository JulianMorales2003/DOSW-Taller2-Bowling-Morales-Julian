package edu.eci.dosw.bowling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Modulo B: calculo de puntaje con bonos de spare y strike.
 */
@DisplayName("BowlingScorer - calculo de puntaje")
class BowlingScorerTest {

    // ---------- Helpers ----------

    // Jugar N tiros iguales
    private void rollMany(BowlingGame game, int times, int pins) {
        for (int i = 0; i < times; i++) {
            game.roll(pins);
        }
    }

    // Juego perfecto: 12 strikes
    private void rollPerfectGame(BowlingGame game) {
        rollMany(game, 12, 10);
    }

    // Todos spares
    private void rollAllSpares(BowlingGame game, int lastBonus) {
        for (int i = 0; i < 10; i++) {
            game.roll(5);
            game.roll(5);
        }
        game.roll(lastBonus);
    }

    // ---------- Casos ----------

    @Test
    @DisplayName("B1 - todos los tiros en 0: score() == 0")
    void gutterGame_scoresZero() {
        BowlingGame game = new BowlingGame();
        rollMany(game, 20, 0);

        assertEquals(0, game.score());
    }

    @Test
    @DisplayName("B2 - sin strikes ni spares: suma directa de los pinos")
    void noStrikesNoSpares_scoresSumOfPins() {
        BowlingGame game = new BowlingGame();
        for (int frame = 0; frame < 10; frame++) {
            game.roll(3);
            game.roll(4);
        }

        assertEquals(70, game.score());
    }

    @Test
    @DisplayName("B3 - spare en frame 1 y primer tiro del frame 2 = 3: frame 1 puntua 10 + 3 = 13")
    void spareInFirstFrame_addsNextRollAsBonus() {
        BowlingGame game = new BowlingGame();
        game.roll(4);
        game.roll(6);   // spare
        game.roll(3);   // bonus del spare
        game.roll(0);
        rollMany(game, 16, 0);

        // 13 del frame 1 + 3 del frame 2
        assertEquals(16, game.score());
    }

    @Test
    @DisplayName("B4 - strike en frame 1 y luego 4 + 3: frame 1 puntua 10 + 4 + 3 = 17")
    void strikeInFirstFrame_addsNextTwoRollsAsBonus() {
        BowlingGame game = new BowlingGame();
        game.roll(10);  // strike
        game.roll(4);
        game.roll(3);
        rollMany(game, 16, 0);

        // 17 del frame 1 + 7 del frame 2
        assertEquals(24, game.score());
    }

    @Test
    @DisplayName("B5 - dos strikes consecutivos y luego 5: el bono del primer strike es 10 + 5")
    void twoConsecutiveStrikes_firstStrikeBonusIsCorrect() {
        BowlingGame game = new BowlingGame();
        game.roll(10);
        game.roll(10);
        game.roll(5);
        game.roll(0);
        rollMany(game, 14, 0);

        // 25 del frame 1 + 15 del frame 2 + 5 del frame 3
        assertEquals(45, game.score());
    }

    @Test
    @DisplayName("B6 - todos spares y ultimo tiro 5: score() == 150")
    void allSparesWithFiveBonus_scores150() {
        BowlingGame game = new BowlingGame();
        rollAllSpares(game, 5);

        assertEquals(150, game.score());
    }

    @Test
    @DisplayName("B7 - juego perfecto (12 strikes): score() == 300")
    void perfectGame_scores300() {
        BowlingGame game = new BowlingGame();
        rollPerfectGame(game);

        assertEquals(300, game.score());
    }

    @Test
    @DisplayName("B8 - score() antes de completar el juego lanza IllegalStateException")
    void scoreBeforeGameIsComplete_throwsIllegalState() {
        BowlingGame game = new BowlingGame();
        game.roll(10);
        game.roll(4);

        assertThrows(IllegalStateException.class, game::score);
    }
}