package navalstrikep;

public class Usuario {

    private String nombre;
    private String clave;
    private int puntos;
    private static final int MAX_LOGS = 10;
    private final String[] ultimosJuegos = new String[MAX_LOGS];
    private int cantidadLogs = 0;

    public Usuario(String nombre, String clave) {
        setNombre(nombre);
        setClave(clave);
        this.puntos = 0;
    }

    public String getNombre() { return nombre; }
    public String getClave() { return clave; }

    public void setNombre(String nombre) {
        if (nombre == null) nombre = "";
        this.nombre = nombre.trim();
    }

    public void setClave(String clave) {
        if (clave == null) clave = "";
        this.clave = clave;
    }
    public int getPuntos() { return puntos; }

    public void sumarPuntos(int cantidad) {
        puntos += cantidad;
        if (puntos < 0) puntos = 0; 
    }

    public void agregarLog(String descripcion) {
        if (descripcion == null) descripcion = "";
        descripcion = descripcion.trim();
        if (descripcion.isEmpty()) descripcion = "Partida registrada.";

        for (int i = MAX_LOGS - 1; i > 0; i--) {
            ultimosJuegos[i] = ultimosJuegos[i - 1];
        }
        ultimosJuegos[0] = descripcion;

        if (cantidadLogs < MAX_LOGS) cantidadLogs++;
    }

    public int getCantidadLogs() { return cantidadLogs; }

    public String getLog(int index) {
        if (index < 0 || index >= MAX_LOGS) return null;
        return ultimosJuegos[index];
    }

    public String[] getUltimosJuegos() { return ultimosJuegos; }
}
