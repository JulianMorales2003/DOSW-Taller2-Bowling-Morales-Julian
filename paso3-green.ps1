# ============================================================
#  PASO 3 (GREEN) - motor basado en Frame con reglas del frame 10
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
Write-Host "PASO 3 (GREEN) - motor basado en Frame con reglas del frame 10" -ForegroundColor Cyan
Write-Host ""

$frameType = @'
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
'@
Write-Utf8 "src/main/java/edu/eci/dosw/bowling/FrameType.java" $frameType

$frame = @'
package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un frame del juego con sus tiros.
 * <p>
 * Los frames 1 a 9 admiten maximo 2 tiros (1 solo si es strike).
 * El frame 10 admite un tercer tiro cuando hay strike o spare.
 */
public class Frame {

    public static final int MAX_PINS = 10;
    public static final int LAST_FRAME_NUMBER = 10;

    private static final int REGULAR_MAX_ROLLS = 2;
    private static final int TENTH_MAX_ROLLS = 3;

    private final int number;
    private final List<Integer> rolls = new ArrayList<>();

    /**
     * @param number numero del frame, entre 1 y 10
     */
    public Frame(int number) {
        if (number < 1 || number > LAST_FRAME_NUMBER) {
            throw new IllegalArgumentException(
                    "El numero de frame debe estar entre 1 y " + LAST_FRAME_NUMBER + ": " + number);
        }
        this.number = number;
    }

    /**
     * Valida que una cantidad de pinos este entre 0 y 10.
     *
     * @throws IllegalArgumentException si pins &lt; 0 o pins &gt; 10
     */
    static void requireValidPins(int pins) {
        if (pins < 0 || pins > MAX_PINS) {
            throw new IllegalArgumentException(
                    "Los pinos derribados deben estar entre 0 y " + MAX_PINS + ": " + pins);
        }
    }

    /**
     * Registra un tiro en el frame.
     *
     * @throws IllegalStateException    si el frame ya esta completo
     * @throws IllegalArgumentException si los pinos estan fuera de rango o superan los pinos en pie
     */
    public void addRoll(int pins) {
        if (isComplete()) {
            throw new IllegalStateException("El frame " + number + " ya esta completo");
        }
        requireValidPins(pins);
        int standing = pinsStanding();
        if (pins > standing) {
            throw new IllegalArgumentException(
                    "En el frame " + number + " solo quedan " + standing + " pinos en pie: " + pins);
        }
        rolls.add(pins);
    }

    /**
     * Pinos que siguen en pie para el proximo tiro. Cuando un tiro deja la pista
     * limpia se vuelven a parar los 10 pinos (solo pasa en el frame 10).
     */
    private int pinsStanding() {
        int standing = MAX_PINS;
        for (int pins : rolls) {
            standing -= pins;
            if (standing == 0) {
                standing = MAX_PINS;
            }
        }
        return standing;
    }

    public boolean isComplete() {
        if (isTenth()) {
            return rolls.size() == TENTH_MAX_ROLLS
                    || (rolls.size() == REGULAR_MAX_ROLLS && !earnsBonusRoll());
        }
        return isStrike() || rolls.size() == REGULAR_MAX_ROLLS;
    }

    public boolean isStrike() {
        return !rolls.isEmpty() && rolls.get(0) == MAX_PINS;
    }

    public boolean isSpare() {
        return rolls.size() >= REGULAR_MAX_ROLLS
                && !isStrike()
                && rolls.get(0) + rolls.get(1) == MAX_PINS;
    }

    private boolean earnsBonusRoll() {
        return isStrike() || isSpare();
    }

    public boolean isTenth() {
        return number == LAST_FRAME_NUMBER;
    }

    public FrameType getType() {
        if (isTenth()) {
            return FrameType.TENTH;
        }
        if (isStrike()) {
            return FrameType.STRIKE;
        }
        if (isSpare()) {
            return FrameType.SPARE;
        }
        return FrameType.NORMAL;
    }

    /** Suma de los pinos derribados en este frame (sin bonos). */
    public int getPinsKnocked() {
        return rolls.stream().mapToInt(Integer::intValue).sum();
    }

    public int getNumber() {
        return number;
    }

    public List<Integer> getRolls() {
        return List.copyOf(rolls);
    }
}
'@
Write-Utf8 "src/main/java/edu/eci/dosw/bowling/Frame.java" $frame

$game = @'
package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de un juego de Bowling para un jugador.
 * Un juego tiene exactamente 10 frames.
 */
public class BowlingGame {

    public static final int MAX_FRAMES = Frame.LAST_FRAME_NUMBER;

    private static final int STRIKE_BONUS_ROLLS = 2;
    private static final int SPARE_BONUS_ROLLS = 1;

    private final List<Frame> frames;
    private int currentFrame;

    public BowlingGame() {
        this.frames = new ArrayList<>();
        this.currentFrame = 0;
    }

    /**
     * Registra pinos derribados.
     *
     * @throws IllegalArgumentException si pins &lt; 0, pins &gt; 10 o supera los pinos en pie del frame
     * @throws IllegalStateException    si el juego ya termino
     */
    public void roll(int pins) {
        Frame.requireValidPins(pins);
        if (isComplete()) {
            throw new IllegalStateException("El juego ya termino: no se admiten mas tiros");
        }
        Frame frame = currentFrame();
        frame.addRoll(pins);
        if (frame.isComplete()) {
            currentFrame++;
        }
    }

    private Frame currentFrame() {
        if (frames.size() == currentFrame) {
            frames.add(new Frame(currentFrame + 1));
        }
        return frames.get(currentFrame);
    }

    /**
     * Puntaje total.
     *
     * @throws IllegalStateException si el juego no esta completo
     */
    public int score() {
        if (!isComplete()) {
            throw new IllegalStateException(
                    "El juego no ha terminado: van " + currentFrame + " de " + MAX_FRAMES + " frames");
        }
        List<Integer> allRolls = frames.stream()
                .flatMap(frame -> frame.getRolls().stream())
                .toList();

        int total = 0;
        int firstRollOfFrame = 0;
        for (Frame frame : frames) {
            total += frameScore(frame, allRolls, firstRollOfFrame);
            firstRollOfFrame += frame.getRolls().size();
        }
        return total;
    }

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

    /** true cuando los 10 frames han sido completados. */
    public boolean isComplete() {
        return currentFrame == MAX_FRAMES;
    }

    public List<Frame> getFrames() {
        return List.copyOf(frames);
    }
}
'@
Write-Utf8 "src/main/java/edu/eci/dosw/bowling/BowlingGame.java" $game

Write-Host ""
Write-Host "Ahora ejecuta:" -ForegroundColor White
Write-Host "  mvn test" -ForegroundColor White
Write-Host "Deben pasar las 22 pruebas. Eso es el GREEN." -ForegroundColor White
Write-Host "Toma captura de la consola en verde -> docs/evidence/tdd-green.png" -ForegroundColor White
Write-Host "Luego:" -ForegroundColor White
Write-Host "  git add ." -ForegroundColor White
Write-Host "  git commit -m `"feat: GREEN - motor basado en frames con reglas del frame 10 y bonos`"" -ForegroundColor White
Write-Host ""
