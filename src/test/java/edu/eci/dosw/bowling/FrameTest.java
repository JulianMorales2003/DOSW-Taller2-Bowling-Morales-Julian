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