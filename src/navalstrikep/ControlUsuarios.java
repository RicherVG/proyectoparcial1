package navalstrikep;

import java.util.ArrayList;

public class ControlUsuarios {

    private final ArrayList<Usuario> usuarios = new ArrayList<>();
    private Usuario usuarioActual;
   
    private String dificultad = "NORMAL";
    private String modo = "TUTORIAL";

  
    public String getDificultad() { return dificultad; }
    public String getModo() { return modo; }

    public void setDificultad(String dificultad) {
        if (dificultad == null) return;
        dificultad = dificultad.trim().toUpperCase();

        switch (dificultad) {
            case "EASY":
            case "NORMAL":
            case "EXPERT":
            case "GENIUS":
                this.dificultad = dificultad;
                break;
            default:
                break;
        }
    }

    public void setModo(String modo) {
        if (modo == null) return;
        modo = modo.trim().toUpperCase();

        switch (modo) {
            case "TUTORIAL":
            case "ARCADE":
                this.modo = modo;
                break;
            default:
                break;
        }
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
    public void cerrarSesion() { usuarioActual = null; }

    public String validarCredenciales(String usuario, String clave) {
        if (usuario == null || usuario.trim().isEmpty()) return "Usuario vacío.";
        if (clave == null || clave.trim().isEmpty()) return "Contraseña vacía.";

        usuario = usuario.trim();
        clave = clave.trim();

        if (!usuario.matches("[a-zA-Z0-9]+")) return "El usuario debe ser alfanumérico (solo letras y números).";
        if (!clave.matches("[a-zA-Z0-9]+")) return "La contraseña debe ser alfanumérica (solo letras y números).";

        for (Usuario u : usuarios) { 
            if (u.getNombre().equalsIgnoreCase(usuario)) {
                if (u.getClave().equals(clave)) {
                    usuarioActual = u;
                    return null; 
                }
                return "Contraseña incorrecta.";
            }
        }
        return "Ese usuario no existe.";
    }

    public boolean login(String usuario, String clave) {
        return validarCredenciales(usuario, clave) == null;
    }

    
    public String crearUsuario(String nombre, String clave) {

        if (nombre == null || clave == null)
            return "Datos inválidos.";

        nombre = nombre.trim();
        clave = clave.trim();

        if (nombre.isEmpty() || clave.isEmpty())
            return "Usuario y contraseña son obligatorios.";

        if (!nombre.matches("[a-zA-Z0-9]+") || !clave.matches("[a-zA-Z0-9]+")) {
            return "Solo se permiten letras y números.";
        }

        if (existeUsuario(nombre))
            return "Ese usuario ya existe.";

        Usuario nuevo = new Usuario(nombre, clave);
        usuarios.add(nuevo);

        usuarioActual = nuevo;

        return null;
    }

    private boolean usuarioExiste(String nombre) {
        if (nombre == null) return false;
        nombre = nombre.trim();
        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(nombre)) return true;
        }
        return false;
    }

    public boolean existeUsuario(String nombre) { return usuarioExiste(nombre); }

    public Usuario buscarUsuario(String nombre) {
        if (nombre == null) return null;
        nombre = nombre.trim();
        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(nombre)) return u;
        }
        return null;
    }

    public String modificarMisDatos(String nuevoUsuario, String nuevaClave) {
        if (usuarioActual == null) return "No hay sesión activa.";

        if (nuevoUsuario == null || nuevoUsuario.trim().isEmpty()) return "Usuario vacío.";
        if (nuevaClave == null || nuevaClave.trim().isEmpty()) return "Clave vacía.";

        nuevoUsuario = nuevoUsuario.trim();
        nuevaClave = nuevaClave.trim();

        boolean cambiaNombre = !usuarioActual.getNombre().equalsIgnoreCase(nuevoUsuario);
        if (cambiaNombre && usuarioExiste(nuevoUsuario)) return "Ese usuario ya existe.";

        usuarioActual.setNombre(nuevoUsuario);
        usuarioActual.setClave(nuevaClave);
        return null; 
    }

    public boolean eliminarMiCuenta() {
        if (usuarioActual == null) return false;
        usuarios.remove(usuarioActual);
        usuarioActual = null; 
        return true;
    }

    public String[] misUltimos10Juegos() {
        if (usuarioActual == null) return new String[0];

        String[] logs = usuarioActual.getUltimosJuegos();
        String[] out = new String[logs.length];
        for (int i = 0; i < logs.length; i++) {
            out[i] = (logs[i] == null) ? "" : logs[i];
        }
        return out;
    }

    public Usuario[] rankingJugadores() {
        Usuario[] arr = usuarios.toArray(Usuario[]::new);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j].getPuntos() < arr[j + 1].getPuntos()) {
                    Usuario tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
        return arr;
    }

    public void darPuntos(Usuario u, int puntos) {
        if (u == null) return;
        u.sumarPuntos(puntos);
    }
}
