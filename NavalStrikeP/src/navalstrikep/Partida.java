package navalstrikep;

public class Partida {

    private Usuario jugador1;
    private Usuario jugador2;
    private Usuario ganador;

    private String dificultad;
    private boolean fueRetiro;

    public Partida(Usuario j1, Usuario j2, String dificultad) {
        this.jugador1 = j1;
        this.jugador2 = j2;
        this.dificultad = dificultad;
        this.fueRetiro = false;
    }

    public Usuario getJugador1() { return jugador1; }
    public Usuario getJugador2() { return jugador2; }
    public Usuario getGanador() { return ganador; }

    public void setGanador(Usuario ganador) {
        this.ganador = ganador;
    }

    public void setFueRetiro(boolean fueRetiro) {
        this.fueRetiro = fueRetiro;
    }

    public boolean isFueRetiro() {
        return fueRetiro;
    }

    public String getDificultad() {
        return dificultad;
    }

    public String generarDescripcion() {

        if (ganador == null) {
            return "Partida sin finalizar.";
        }

        Usuario perdedor = (ganador == jugador1) ? jugador2 : jugador1;

        if (fueRetiro) {
            return perdedor.getNombre() + " se retiró dejando como ganador a "
                    + ganador.getNombre() + " en modo " + dificultad + ".";
        } else {
            return ganador.getNombre() + " hundió todos los barcos de "
                    + perdedor.getNombre() + " en modo " + dificultad + ".";
        }
    }
}
