package navalstrikep;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JuegoLogica {

    public static class DisparoResultado {
        public boolean valido;
        public String mensaje;
        public boolean finJuego;
        public String ganador;
        public boolean fueImpacto;
        public boolean fueAgua;
        public int filaDisparo;
        public int colDisparo;
        public boolean hundido;
        public String barcoHundido;
        public int[] celdasBarcoHundido;
    }

    public static class ColocacionResultado {
        public boolean valido;
        public String mensaje;
        public boolean pasoDeFase;
        public boolean inicioJuego;
        public boolean cambioATurnoJ2;
    }

    private enum Fase { COLOCANDO_J1, COLOCANDO_J2, JUGANDO, TERMINADO }
    private Fase fase = Fase.COLOCANDO_J1;

    private static final int TAM = 8;
    private final Random rnd = new Random();

    private final Usuario j1;
    private final Usuario j2;
    private boolean turnoJ1 = true;

    private final char[][] mapa1 = new char[TAM][TAM];
    private final char[][] mapa2 = new char[TAM][TAM];

    private String dificultad = "NORMAL";
    private boolean modoTutorial = true;

    private final List<Barco> barcosPendientes = new ArrayList<>();
    private int idxBarco = 0;
    private boolean orientacionHorizontal = true;

    private static abstract class Barco {
        private final String nombre;
        private final int largo;
        private final char letra;

        public Barco(String nombre, int largo, char letra) {
            this.nombre = nombre;
            this.largo = largo;
            this.letra = letra;
        }
        public String getNombre() { return nombre; }
        public int getLargo() { return largo; }
        public char getLetra() { return letra; }
    }

    private static class Portaviones extends Barco { public Portaviones(){ super("Portaviones",5,'P'); } }
    private static class Acorazado  extends Barco { public Acorazado(){  super("Acorazado",4,'A');  } }
    private static class Submarino  extends Barco { public Submarino(){  super("Submarino",3,'S');  } }
    private static class Destructor extends Barco { public Destructor(){ super("Destructor",2,'D'); } }

    private static class BarcoInstancia {
        Barco tipo;
        int[] celdas;                
        boolean horizontal;          
        boolean[] partesGolpeadas;   

        BarcoInstancia(Barco tipo, int[] celdas, boolean horizontal) {
            this.tipo = tipo;
            this.celdas = celdas;
            this.horizontal = horizontal;
            this.partesGolpeadas = new boolean[tipo.getLargo()];
        }

        int partesDestruidas() {
            int n = 0;
            for (boolean b : partesGolpeadas) if (b) n++;
            return n;
        }

        boolean estaHundido() { return partesDestruidas() >= tipo.getLargo(); }
    }

    
    public static class BarcoVista {
        public String codigo;   
        public int fila;
        public int col;
        public int largo;
        public boolean horizontal;

        public BarcoVista(String codigo, int fila, int col, int largo, boolean horizontal) {
            this.codigo = codigo;
            this.fila = fila;
            this.col = col;
            this.largo = largo;
            this.horizontal = horizontal;
        }
    }

    private String codigoPorNombreBarco(String nombre) {
        if (nombre == null) return null;
        return switch (nombre) {
            case "Portaviones" -> "PA";
            case "Acorazado" -> "AZ";
            case "Submarino" -> "SM";
            case "Destructor" -> "DT";
            default -> null;
        };
    }

    private List<BarcoInstancia> listaDefensor() {
        return turnoJ1 ? barcosJ2 : barcosJ1;
    }

    public List<BarcoVista> getBarcosDefensorVista() {
        List<BarcoVista> out = new ArrayList<>();
        if (!modoTutorial) return out;

        for (BarcoInstancia bi : listaDefensor()) {
            if (bi == null || bi.celdas == null || bi.celdas.length == 0) continue;

           
            int filaIni = 99, colIni = 99;
            for (int pos : bi.celdas) {
                int f = pos / TAM;
                int c = pos % TAM;
                if (bi.horizontal) {
                    
                    if (c < colIni) { colIni = c; filaIni = f; }
                } else {
                   
                    if (f < filaIni) { filaIni = f; colIni = c; }
                }
            }

            String codigo = codigoPorNombreBarco(bi.tipo.getNombre());
            out.add(new BarcoVista(codigo, filaIni, colIni, bi.tipo.getLargo(), bi.horizontal));
        }
        return out;
    }

private final List<BarcoInstancia> barcosJ1 = new ArrayList<>();
    private final List<BarcoInstancia> barcosJ2 = new ArrayList<>();

    public JuegoLogica(Usuario j1, Usuario j2) {
        this.j1 = j1;
        this.j2 = j2;
        inicializarMapa(mapa1);
        inicializarMapa(mapa2);
        cargarBarcosPendientesPorDificultad();
    }

    public void setConfig(String dificultad, String modo) {
        if (dificultad != null) this.dificultad = dificultad.trim().toUpperCase();
        if (modo != null) this.modoTutorial = modo.trim().equalsIgnoreCase("TUTORIAL");

        barcosJ1.clear();
        barcosJ2.clear();

        inicializarMapa(mapa1);
        inicializarMapa(mapa2);

        fase = Fase.COLOCANDO_J1;
        turnoJ1 = true;
        orientacionHorizontal = true;
        idxBarco = 0;

        cargarBarcosPendientesPorDificultad();
    }

    public boolean isModoTutorial() { return modoTutorial; }

    public int calcularPuntos() {
        return 3;
    }

    public boolean estaEnColocacion() { return fase==Fase.COLOCANDO_J1 || fase==Fase.COLOCANDO_J2; }

    public boolean isOrientacionHorizontal() { return orientacionHorizontal; }
    public void girarOrientacion() { orientacionHorizontal = !orientacionHorizontal; }

    public String getColocadorActual() {
        if (fase==Fase.COLOCANDO_J1) return j1.getNombre();
        if (fase==Fase.COLOCANDO_J2) return j2.getNombre();
        return "";
    }

    public String getAtacante() { return turnoJ1 ? j1.getNombre() : j2.getNombre(); }
    public String getDefensor() { return turnoJ1 ? j2.getNombre() : j1.getNombre(); }

    public String getNombreBarcoActual() {
        if (!estaEnColocacion()) return "";
        if (idxBarco < 0 || idxBarco >= barcosPendientes.size()) return "";
        return barcosPendientes.get(idxBarco).getNombre();
    }

    public int getLargoBarcoActual() {
        if (!estaEnColocacion()) return 0;
        if (idxBarco < 0 || idxBarco >= barcosPendientes.size()) return 0;
        return barcosPendientes.get(idxBarco).getLargo();
    }

    public int getLargoPorCodigo(String codigo) {
        if (codigo == null) return 0;
        return switch (codigo.trim().toUpperCase()) {
            case "PA" -> 5;
            case "AZ" -> 4;
            case "SM" -> 3;
            case "DT" -> 2;
            default -> 0;
        };
    }

    public char[][] getMapaDefensorActual() {
        if (fase != Fase.JUGANDO) return null;
        return turnoJ1 ? mapa2 : mapa1;
    }

    public void pasarTurno() {
        if (fase != Fase.JUGANDO) return;
        turnoJ1 = !turnoJ1;
    }

    public int getBarcosRestantesDefensor() {
        if (fase != Fase.JUGANDO) return 0;
        List<BarcoInstancia> def = turnoJ1 ? barcosJ2 : barcosJ1;
        int vivos = 0;
        for (BarcoInstancia b : def) if (!b.estaHundido()) vivos++;
        return vivos;
    }

    public ColocacionResultado colocarBarcoEn(int fila, int col) {
        ColocacionResultado res = new ColocacionResultado();
        res.valido = false;
        res.pasoDeFase = false;
        res.inicioJuego = false;
        res.cambioATurnoJ2 = false;

        if (!estaEnColocacion()) { res.mensaje="No estás en colocación."; return res; }
        if (!enRango(fila,col)) { res.mensaje="Coordenadas inválidas."; return res; }
        if (idxBarco < 0 || idxBarco >= barcosPendientes.size()) { res.mensaje="No hay barcos pendientes."; return res; }

        Barco b = barcosPendientes.get(idxBarco);

        char[][] mapa = (fase==Fase.COLOCANDO_J1) ? mapa1 : mapa2;
        List<BarcoInstancia> lista = (fase==Fase.COLOCANDO_J1) ? barcosJ1 : barcosJ2;

        if (!cabeEnTablero(b.getLargo(), fila, col, orientacionHorizontal)) {
            res.mensaje="No cabe ahí. Probá otra casilla o girá.";
            return res;
        }
        if (!espacioLibreRecursivo(mapa, fila, col, b.getLargo(), orientacionHorizontal, 0)) {
            res.mensaje="Choca con otro barco.";
            return res;
        }

        int[] celdas = new int[b.getLargo()];
        colocarRecursivo(mapa, fila, col, b.getLargo(), orientacionHorizontal, b.getLetra(), 0, celdas);
        lista.add(new BarcoInstancia(b, celdas, orientacionHorizontal));

        res.valido = true;
        res.mensaje = "Colocado: " + b.getNombre() + " (" + b.getLargo() + ")";

        idxBarco++;
        if (idxBarco >= barcosPendientes.size()) {
            idxBarco = 0;
            res.pasoDeFase = true;

            if (fase==Fase.COLOCANDO_J1) {
                fase = Fase.COLOCANDO_J2;
                orientacionHorizontal = true;
                res.cambioATurnoJ2 = true;
                res.mensaje = "Colocación terminada (Jugador 1). Cambio de turno: ahora coloca " + j2.getNombre();
            } else {
                fase = Fase.JUGANDO;
                turnoJ1 = true;
                res.inicioJuego = true;
                res.mensaje = "¡Colocación terminada! Inicia el juego. Ataca: " + getAtacante();
            }
        }

        return res;
    }

    public DisparoResultado disparar(int fila, int col) {
        DisparoResultado res = new DisparoResultado();
        res.valido = false;
        res.finJuego = false;
        res.ganador = null;

        res.fueImpacto = false;
        res.fueAgua = false;
        res.filaDisparo = fila;
        res.colDisparo = col;

        res.hundido = false;
        res.barcoHundido = null;
        res.celdasBarcoHundido = null;

        if (fase != Fase.JUGANDO) { res.mensaje="Aún no se puede disparar."; return res; }
        if (!enRango(fila,col)) { res.mensaje="Coordenadas inválidas."; return res; }

        char[][] mapaDef = turnoJ1 ? mapa2 : mapa1;
        List<BarcoInstancia> barcosDef = turnoJ1 ? barcosJ2 : barcosJ1;

        char real = mapaDef[fila][col];
        String atacante = getAtacante();
        String coord = coordTexto(fila, col);

        if (real == '~') {
            res.valido = true;
            res.fueAgua = true;
            res.mensaje = atacante + " atacó " + coord + " → AGUA";
            return res;
        }

        
        int code = fila * TAM + col;
        BarcoInstancia b = buscarBarcoPorCelda(barcosDef, code);
        if (b == null) {
            
            res.valido = true;
            res.fueImpacto = true;
            res.mensaje = atacante + " atacó " + coord + " → IMPACTO";
            regenerarDefensorSiempre(mapaDef, barcosDef);
            return res;
        }

        int idxParte = indiceParteEnBarco(b.celdas, code, 0);
        if (idxParte >= 0 && idxParte < b.partesGolpeadas.length && b.partesGolpeadas[idxParte]) {
            res.mensaje = "Esa parte del barco ya fue explotada. Elegí otra parte.";
            return res;
        }

        if (idxParte >= 0 && idxParte < b.partesGolpeadas.length) {
            b.partesGolpeadas[idxParte] = true;
        }

        res.valido = true;
        res.fueImpacto = true;
        res.mensaje = atacante + " atacó " + coord + " → IMPACTO";

        if (b.estaHundido()) {
            res.hundido = true;
            res.barcoHundido = nombrePorLetra(b.tipo.getLetra());
            res.celdasBarcoHundido = copiar(b.celdas);

            borrarBarcoDelMapa(mapaDef, b.celdas, 0);
            barcosDef.remove(b);

            res.mensaje += " | ¡HUNDISTE " + res.barcoHundido + "!";
        }

        if (barcosDef.isEmpty()) {
            res.finJuego = true;
            res.ganador = atacante;
            fase = Fase.TERMINADO;
            return res;
        }

        regenerarDefensorSiempre(mapaDef, barcosDef);

        return res;
    }

    private void regenerarDefensorSiempre(char[][] mapaDef, List<BarcoInstancia> barcosDef) {
        inicializarMapa(mapaDef);

        for (BarcoInstancia barco : barcosDef) {
            boolean colocado = false;
            int intentos = 0;
            boolean horizontal = barco.horizontal;

            while (!colocado && intentos < 800) {
                intentos++;
                int fila = rnd.nextInt(TAM);
                int col  = rnd.nextInt(TAM);

                if (!cabeEnTablero(barco.tipo.getLargo(), fila, col, horizontal)) continue;
                if (!espacioLibreRecursivo(mapaDef, fila, col, barco.tipo.getLargo(), horizontal, 0)) continue;

                int[] nuevas = new int[barco.tipo.getLargo()];
                colocarRecursivo(mapaDef, fila, col, barco.tipo.getLargo(), horizontal, barco.tipo.getLetra(), 0, nuevas);

                barco.celdas = nuevas;
                colocado = true;
            }

            if (!colocado) {
                outer:
                for (int r=0;r<TAM;r++) {
                    for (int c=0;c<TAM;c++) {
                        if (!cabeEnTablero(barco.tipo.getLargo(), r, c, horizontal)) continue;
                        if (!espacioLibreRecursivo(mapaDef, r, c, barco.tipo.getLargo(), horizontal, 0)) continue;

                        int[] nuevas = new int[barco.tipo.getLargo()];
                        colocarRecursivo(mapaDef, r, c, barco.tipo.getLargo(), horizontal, barco.tipo.getLetra(), 0, nuevas);
                        barco.celdas = nuevas;
                        break outer;
                    }
                }
            }
        }
    }

    private BarcoInstancia buscarBarcoPorCelda(List<BarcoInstancia> lista, int celda) {
        for (BarcoInstancia b : lista) {
            if (contieneCelda(b.celdas, celda, 0)) return b;
        }
        return null;
    }

    private boolean contieneCelda(int[] arr, int celda, int i) {
        if (arr == null) return false;
        if (i >= arr.length) return false;
        if (arr[i] == celda) return true;
        return contieneCelda(arr, celda, i+1);
    }

    private int indiceParteEnBarco(int[] arr, int celda, int i) {
        if (arr == null) return -1;
        if (i >= arr.length) return -1;
        if (arr[i] == celda) return i;
        return indiceParteEnBarco(arr, celda, i+1);
    }

  
    private boolean cabeEnTablero(int largo, int fila, int col, boolean horizontal) {
        if (horizontal) return col + largo - 1 < TAM;
        return fila + largo - 1 < TAM;
    }

    private boolean espacioLibreRecursivo(char[][] t, int fila, int col, int largo, boolean horizontal, int i) {
        if (i >= largo) return true;
        int rr = horizontal ? fila : fila + i;
        int cc = horizontal ? col + i : col;
        if (!enRango(rr,cc)) return false;
        if (t[rr][cc] != '~') return false;
        return espacioLibreRecursivo(t, fila, col, largo, horizontal, i+1);
    }

    private void colocarRecursivo(char[][] t, int fila, int col, int largo, boolean horizontal, char letra, int i, int[] outCells) {
        if (i >= largo) return;
        int rr = horizontal ? fila : fila + i;
        int cc = horizontal ? col + i : col;
        t[rr][cc] = letra;
        if (outCells != null && i < outCells.length) outCells[i] = rr * TAM + cc;
        colocarRecursivo(t, fila, col, largo, horizontal, letra, i+1, outCells);
    }

    private void borrarBarcoDelMapa(char[][] mapa, int[] celdas, int i) {
        if (mapa == null || celdas == null) return;
        if (i >= celdas.length) return;
        int code = celdas[i];
        int r = code / TAM;
        int c = code % TAM;
        if (enRango(r,c)) mapa[r][c] = '~';
        borrarBarcoDelMapa(mapa, celdas, i+1);
    }

    private int[] copiar(int[] a) {
        if (a == null) return null;
        int[] b = new int[a.length];
        for (int i=0;i<a.length;i++) b[i] = a[i];
        return b;
    }

    private void inicializarMapa(char[][] t) {
        for (int r=0;r<TAM;r++) for (int c=0;c<TAM;c++) t[r][c] = '~';
    }

    private boolean enRango(int fila, int col) {
        return fila>=0 && fila<TAM && col>=0 && col<TAM;
    }

    private String coordTexto(int fila, int col) {
        String[] letras = {"A","B","C","D","E","F","G","H"};
        return letras[col] + (fila+1);
    }

    private String nombrePorLetra(char letra) {
        return switch (letra) {
            case 'P' -> "PORTAVIONES";
            case 'A' -> "ACORAZADO";
            case 'S' -> "SUBMARINO";
            case 'D' -> "DESTRUCTOR";
            default -> "BARCO";
        };
    }

    private void cargarBarcosPendientesPorDificultad() {
        barcosPendientes.clear();
        switch (dificultad) {
            case "EASY" -> {
                barcosPendientes.add(new Portaviones());
                barcosPendientes.add(new Acorazado());
                barcosPendientes.add(new Submarino());
                barcosPendientes.add(new Destructor());
                barcosPendientes.add(new Destructor());
            }
            case "EXPERT" -> {
                barcosPendientes.add(new Portaviones());
                barcosPendientes.add(new Acorazado());
            }
            case "GENIUS" -> {
                barcosPendientes.add(new Portaviones());
            }
            default -> {
                barcosPendientes.add(new Portaviones());
                barcosPendientes.add(new Acorazado());
                barcosPendientes.add(new Submarino());
                barcosPendientes.add(new Destructor());
            }
        }
        idxBarco = 0;
        orientacionHorizontal = true;
    }

    public Usuario getJugadorEnTurno() {
        if (estaEnColocacion()) {
            return (fase == Fase.COLOCANDO_J1) ? j1 : j2;
        }
     
        return turnoJ1 ? j1 : j2;
    }

    public Usuario getOtroJugador(Usuario u) {
        return (u != null && u.equals(j1)) ? j2 : j1;
    }

    public void finalizarPorRetiro(Usuario ganador, Usuario seRinde) {
        if (ganador == null || seRinde == null) return;

        ganador.sumarPuntos(3);

        String log = seRinde.getNombre()
                + " se retiró del juego dejando como ganador a "
                + ganador.getNombre() + ".";

        ganador.agregarLog(log);
        seRinde.agregarLog(log);

        fase = Fase.TERMINADO;
    }


}
