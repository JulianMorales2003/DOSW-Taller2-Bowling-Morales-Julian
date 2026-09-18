# ============================================================
#  PASO 4 (REFACTOR) - extraer el calculo de puntaje a BowlingScorer
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
Write-Host "PASO 4 (REFACTOR) - extraer el calculo de puntaje a BowlingScorer" -ForegroundColor Cyan
Write-Host ""

$scorer = @'
package edu.eci.dosw.bowling;

import java.util.ArrayList;
import java.util.List;

/**
 * Calcula el puntaje de una lista de frames aplicando los bonos de spare y strike.
 * No tiene estado: solo recibe frames y devuelve numeros.
 */
public class BowlingScorer {

    private static final int STRIKE_BONUS_ROLLS = 2;
    private static final int SPARE_BONUS_ROLLS = 1;

    /** Puntaje total de los frames. */
    public int calculate(List<Frame> frames) {
        return scoreByFrame(frames).stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Puntaje individual de cada frame (no acumulado), en orden.
     * Si faltan tiros de bono (juego en curso), el bono cuenta solo lo ya lanzado.
     */
    public List<Integer> scoreByFrame(List<Frame> frames) {
        List<Integer> allRolls = frames.stream()
                .flatMap(frame -> frame.getRolls().stream())
                .toList();

        List<Integer> scores = new ArrayList<>();
        int firstRollOfFrame = 0;
        for (Frame frame : frames) {
            scores.add(frameScore(frame, allRolls, firstRollOfFrame));
            firstRollOfFrame += frame.getRolls().size();
        }
        return List.copyOf(scores);
    }

    /*
     * El frame 10 no necesita un caso especial: sus tiros extra ya estan en la
     * lista, asi que "10 + los dos siguientes" o "10 + el siguiente" dan
     * exactamente la suma de sus propios tiros.
     */
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
}
'@
Write-Utf8 "src/main/java/edu/eci/dosw/bowling/BowlingScorer.java" $scorer

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

    private final List<Frame> frames;
    private final BowlingScorer scorer;
    private int currentFrame;

    public BowlingGame() {
        this.frames = new ArrayList<>();
        this.scorer = new BowlingScorer();
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
        return scorer.calculate(frames);
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
Write-Host "Las mismas 22 pruebas siguen en verde: el comportamiento no cambio." -ForegroundColor White
Write-Host "Luego:" -ForegroundColor White
Write-Host "  git add ." -ForegroundColor White
Write-Host "  git commit -m `"refactor: extrae el calculo de puntaje a BowlingScorer`"" -ForegroundColor White
Write-Host ""
