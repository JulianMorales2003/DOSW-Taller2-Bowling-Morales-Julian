# ============================================================
#  PASO 5 - pruebas adicionales para subir la cobertura
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
Write-Host "PASO 5 - pruebas adicionales para subir la cobertura" -ForegroundColor Cyan
Write-Host ""

$frameTest = @'
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
 * Pruebas adicionales de Frame, agregadas despues de revisar el reporte de JaCoCo.
 */
@DisplayName("Frame - reglas por frame y casos de borde")
class FrameTest {

    @Test
    @DisplayName("Crear un frame con numero fuera de 1..10 lanza IllegalArgumentException")
    void frameNumberOutOfRange_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Frame(0));
        assertThrows(IllegalArgumentException.class, () -> new Frame(11));
    }

    @Test
    @DisplayName("Un frame abierto se completa con 2 tiros y es NORMAL")
    void openFrame_isNormalAndCompleteAfterTwoRolls() {
        Frame frame = new Frame(1);
        frame.addRoll(3);

        assertFalse(frame.isComplete());
        assertEquals(FrameType.NORMAL, frame.getType());

        frame.addRoll(4);

        assertTrue(frame.isComplete());
        assertEquals(7, frame.getPinsKnocked());
        assertEquals(1, frame.getNumber());
    }

    @Test
    @DisplayName("Agregar un tiro a un frame ya completo lanza IllegalStateException")
    void addRollToCompleteFrame_throwsIllegalState() {
        Frame frame = new Frame(4);
        frame.addRoll(10);

        assertThrows(IllegalStateException.class, () -> frame.addRoll(0));
    }

    @Test
    @DisplayName("El frame 10 es de tipo TENTH aunque tenga strike")
    void tenthFrame_isTypeTenth() {
        Frame frame = new Frame(10);
        frame.addRoll(10);

        assertTrue(frame.isStrike());
        assertEquals(FrameType.TENTH, frame.getType());
    }

    @Test
    @DisplayName("Frame 10: strike y luego 3 -> el tercer tiro no puede superar 7")
    void tenthFrameStrikeThenThree_bonusCannotExceedStandingPins() {
        Frame frame = new Frame(10);
        frame.addRoll(10);
        frame.addRoll(3);

        assertThrows(IllegalArgumentException.class, () -> frame.addRoll(8));
        assertDoesNotThrow(() -> frame.addRoll(7));
        assertTrue(frame.isComplete());
    }

    @Test
    @DisplayName("Frame 10: strike, 0 y 10 es valido (spare en los tiros bonus)")
    void tenthFrameStrikeZeroTen_isValid() {
        Frame frame = new Frame(10);

        assertDoesNotThrow(() -> {
            frame.addRoll(10);
            frame.addRoll(0);
            frame.addRoll(10);
        });
        assertEquals(20, frame.getPinsKnocked());
    }

    @Test
    @DisplayName("Frame 10: spare y bonus de 10 es valido")
    void tenthFrameSpareThenStrike_isValid() {
        Frame frame = new Frame(10);
        frame.addRoll(3);
        frame.addRoll(7);

        assertTrue(frame.isSpare());
        assertFalse(frame.isComplete());
        assertDoesNotThrow(() -> frame.addRoll(10));
        assertTrue(frame.isComplete());
    }

    @Test
    @DisplayName("Frame 10 abierto: se completa con 2 tiros y no admite un tercero")
    void tenthFrameOpen_completesWithTwoRolls() {
        Frame frame = new Frame(10);
        frame.addRoll(3);
        frame.addRoll(4);

        assertTrue(frame.isComplete());
        assertThrows(IllegalStateException.class, () -> frame.addRoll(1));
    }

    @Test
    @DisplayName("getRolls() devuelve una copia que no se puede modificar")
    void getRolls_returnsUnmodifiableCopy() {
        Frame frame = new Frame(2);
        frame.addRoll(5);
        List<Integer> rolls = frame.getRolls();

        assertThrows(UnsupportedOperationException.class, () -> rolls.add(3));
    }
}
'@
Write-Utf8 "src/test/java/edu/eci/dosw/bowling/FrameTest.java" $frameTest

$edgeTest = @'
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
'@
Write-Utf8 "src/test/java/edu/eci/dosw/bowling/BowlingEdgeCasesTest.java" $edgeTest

Write-Host ""
Write-Host "IMPORTANTE: antes de correr esto debiste generar la captura jacoco-antes.png" -ForegroundColor White
Write-Host "Ahora ejecuta:" -ForegroundColor White
Write-Host "  mvn clean verify" -ForegroundColor White
Write-Host "Deben pasar las 37 pruebas y la cobertura superar el 85%." -ForegroundColor White
Write-Host "Abre target/site/jacoco/index.html -> captura docs/evidence/jacoco-final.png" -ForegroundColor White
Write-Host "Luego:" -ForegroundColor White
Write-Host "  git add ." -ForegroundColor White
Write-Host "  git commit -m `"test: agrega casos de borde de Frame y del juego completo`"" -ForegroundColor White
Write-Host ""
