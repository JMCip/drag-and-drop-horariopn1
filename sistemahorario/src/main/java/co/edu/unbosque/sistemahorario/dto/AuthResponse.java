package co.edu.unbosque.sistemahorario.dto;

public class AuthResponse {
    private long id;
    private String token;
    private String rol;
    private String nombre;
    private String correo;

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
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
