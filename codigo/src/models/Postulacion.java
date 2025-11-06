package models;

public class Postulacion {
    private Usuario usuario;
    private String rolDeseado;
    private String estado;

    public Postulacion(Usuario usuario, String rolDeseado) {
        this.usuario = usuario;
        this.rolDeseado = rolDeseado;
        this.estado = "PENDIENTE";
    }

    public Usuario getUsuario() { return usuario; }
    public String getRolDeseado() { return rolDeseado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
