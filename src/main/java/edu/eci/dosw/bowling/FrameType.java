package edu.eci.dosw.bowling;

/**
 * Tipos de frame en un juego de Bowling.
 */
public enum FrameType {
    /** Frame abierto: no se derribaron los 10 pinos en los dos tiros. */
    NORMAL,
    /** Se derribaron los 10 pinos entre los dos tiros del frame. */
    SPARE,
    /** Se derribaron los 10 pinos en el primer tiro. */
    STRIKE,
    /** Decimo frame: admite hasta 3 tiros si hay strike o spare. */
    TENTH
}