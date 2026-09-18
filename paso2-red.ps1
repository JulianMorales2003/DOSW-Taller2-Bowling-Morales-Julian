# ============================================================
#  PASO 2 (RED) - pruebas A1-A8, C1-C6 y B1-B8
#  Ejecutar desde la RAIZ del repo DOSW-Taller2-Bowling-Morales-Julian
# ============================================================

if (-not (Test-Path ".git")) {
    Write-Host "ERROR: no estas en la raiz del repositorio. Haz 'cd' a la carpeta del repo." -ForegroundColor Red
    exit 1
}

function Write-Utf8($RelPath, $Content) {
    $full = Join-Path (Get-Location) $RelPath
    $dir = Split-Path -Parent $full
    if ($dir -and -not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [System.IO.File]::WriteAllText($full, $Content, (New-Object System.Text.UTF8Encoding $false))
    Write-Host "  escrito: $RelPath" -ForegroundColor Green
}

Write-Host ""
Write-Host "PASO 2 (RED) - pruebas A1-A8, C1-C6 y B1-B8" -ForegroundColor Cyan
Write-Host ""

$gameTest = @'
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
'@
Write-Utf8 "src/test/java/edu/eci/dosw/bowling/BowlingGameTest.java" $gameTest

$scorerTest = @'
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
'@
Write-Utf8 "src/test/java/edu/eci/dosw/bowling/BowlingScorerTest.java" $scorerTest

Write-Host ""
Write-Host "Ahora ejecuta:" -ForegroundColor White
Write-Host "  mvn test" -ForegroundColor White
Write-Host "DEBE FALLAR (no existen getFrames, getType ni isComplete). Eso es el RED." -ForegroundColor White
Write-Host "Toma captura de la consola en rojo -> docs/evidence/tdd-red.png" -ForegroundColor White
Write-Host "Luego:" -ForegroundColor White
Write-Host "  git add ." -ForegroundColor White
Write-Host "  git commit -m `"test: RED - casos A1, A6-A8, modulo C completo y modulo B con numeracion del enunciado`"" -ForegroundColor White
Write-Host ""
