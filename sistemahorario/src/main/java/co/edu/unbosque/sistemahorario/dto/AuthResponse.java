package co.edu.unbosque.sistemahorario.dto;

/**
 * Respuesta del login ({@code POST /api/auth/login}).
 * <p>
 * Lleva el token JWT y los datos que el front necesita para decidir qué vista mostrar:
 * id, rol ({@code ADMIN}, {@code PROFESOR} o {@code ESTUDIANTE}), nombre y correo del usuario.
 * Si el usuario es un estudiante, también lleva su carrera y su semestre; para los demás roles
 * esos dos campos quedan en {@code null}.
 */
public class AuthResponse {
    private long id;
    private String token;
    private String rol;
    private String nombre;
    private String correo;
    /**
     * Carrera del estudiante (por ejemplo `ING_SISTEMAS`). Solo se llena si el usuario es estudiante.
     */
    private String carrera;
    /**
     * Semestre del estudiante. Solo se llena si el usuario es estudiante.
     */
    private Short semestre;

    public AuthResponse(long id, String token, String rol, String nombre, String correo) {
        this.id = id;
        this.token = token;
        this.rol = rol;
        this.nombre = nombre;
        this.correo = correo;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public Short getSemestre() { return semestre; }
    public void setSemestre(Short semestre) { this.semestre = semestre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
