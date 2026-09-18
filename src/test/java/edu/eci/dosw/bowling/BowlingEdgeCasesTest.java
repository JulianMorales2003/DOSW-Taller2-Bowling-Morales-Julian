package edu.eci.dosw.bowling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Casos de borde adicionales del juego completo, agregados despues de revisar JaCoCo.
 */
@DisplayName("Bowling - casos de borde adicionales")
class BowlingEdgeCasesTest {

    private final BowlingScorer scorer = new BowlingScorer();

    private void rollAll(BowlingGame game, int... rolls) {
        for (int pins : rolls) {
            game.roll(pins);
        }
    }

    @Test
    @DisplayName("Partida mixta de referencia: score() == 167")
    void mixedGame_scores167() {
        BowlingGame game = new BowlingGame();
        rollAll(game, 10, 7, 3, 9, 0, 10, 0, 8, 8, 2, 0, 6, 10, 10, 10, 8, 1);

        assertEquals(List.of(20, 19, 9, 18, 8, 10, 6, 30, 28, 19), scorer.scoreByFrame(game.getFrames()));
        assertEquals(167, game.score());
    }

    @Test
    @DisplayName("Strike en el frame 9 toma su bono de los tiros del frame 10")
    void strikeInNinthFrame_takesBonusFromTenthFrame() {
        BowlingGame game = new BowlingGame();
        rollAll(game, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        rollAll(game, 10, 10, 10, 10);

        assertEquals(60, game.score());
    }

    @Test
    @DisplayName("Frame 10 abierto: el tiro 21 lanza IllegalStateException")
    void openTenthFrame_rejectsExtraRoll() {
        BowlingGame game = new BowlingGame();
        for (int i = 0; i < 20; i++) {
            game.roll(2);
        }

        assertThrows(IllegalStateException.class, () -> game.roll(2));
    }

    @Test
    @DisplayName("score() en un juego nuevo lanza IllegalStateException")
    void scoreOnNewGame_throwsIllegalState() {
        BowlingGame game = new BowlingGame();

        assertThrows(IllegalStateException.class, game::score);
    }

    @Test
    @DisplayName("El scorer con un juego en curso cuenta solo los bonos ya lanzados")
    void scorerWithGameInProgress_countsOnlyRolledBonus() {
        BowlingGame game = new BowlingGame();
        rollAll(game, 10, 4);

        assertEquals(List.of(14, 4), scorer.scoreByFrame(game.getFrames()));
        assertEquals(18, scorer.calculate(game.getFrames()));
    }

    @Test
    @DisplayName("getFrames() devuelve una copia que no se puede modificar")
    void getFrames_returnsUnmodifiableCopy() {
        BowlingGame game = new BowlingGame();
        game.roll(3);
        List<Frame> frames = game.getFrames();

        assertThrows(UnsupportedOperationException.class, () -> frames.add(new Frame(2)));
    }
}