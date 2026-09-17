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

    @Test
    @DisplayName("A5: Lanzar un tiro cuando el juego termino lanza IllegalStateException")
    void testA5_gameAlreadyFinished() {
        for (int i = 0; i < 20; i++) {
            game.roll(0);
        }
        assertThrows(IllegalStateException.class, () -> game.roll(0));
    }

    @Test
    @DisplayName("B1: Calcular puntaje para una partida abierta")
    void testB1_openGameScore() {
        for (int i = 0; i < 10; i++) {
            game.roll(1);
            game.roll(2);
        }
        assertEquals(30, game.score());
    }

    @Test
    @DisplayName("B2: Un spare otorga bono del siguiente lanzamiento")
    void testB2_spareScore() {
        game.roll(5);
        game.roll(5);
        game.roll(3);
        for (int i = 0; i < 17; i++) {
            game.roll(0);
        }
        assertEquals(16, game.score());
    }

    @Test
    @DisplayName("B3: Un strike otorga bono de los dos siguientes lanzamientos")
    void testB3_strikeScore() {
        game.roll(10);
        game.roll(3);
        game.roll(4);
        for (int i = 0; i < 16; i++) {
            game.roll(0);
        }
        assertEquals(24, game.score());
    }

    @Test
    @DisplayName("B4: Juego perfecto obtiene una puntuacion de 300")
    void testB4_perfectGame() {
        for (int i = 0; i < 12; i++) {
            game.roll(10);
        }
        assertEquals(300, game.score());
    }
}
