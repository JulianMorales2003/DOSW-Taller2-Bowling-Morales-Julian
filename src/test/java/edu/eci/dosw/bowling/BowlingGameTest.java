package edu.eci.dosw.bowling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BowlingGameTest {
    private BowlingGame game;

    @BeforeEach
    void setUp() {
        game = new BowlingGame();
    }

    @Test
    @DisplayName("A2: roll(-1) lanza IllegalArgumentException")
    void testA2_rollNegative() {
        assertThrows(IllegalArgumentException.class, () -> game.roll(-1));
    }

    @Test
    @DisplayName("A3: roll(11) lanza IllegalArgumentException")
    void testA3_rollMoreThanTen() {
        assertThrows(IllegalArgumentException.class, () -> game.roll(11));
    }

    @Test
    @DisplayName("A4: Suma de tiros en un frame mayor a 10 lanza IllegalArgumentException")
    void testA4_frameSumMoreThanTen() {
        game.roll(6);
        assertThrows(IllegalArgumentException.class, () -> game.roll(5));
    }
}