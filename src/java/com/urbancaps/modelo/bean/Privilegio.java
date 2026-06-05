package com.urbancaps.modelo.bean;

import java.io.Serializable;

public class Privilegio implements Serializable {

    private int idPrivilegio;
    private String nombre;
    private String descripcion;
    private String modulo;
    private int estado;

    public Privilegio() {
    }

    public Privilegio(int idPrivilegio, String nombre,
                      String descripcion, String modulo, int estado) {
        this.idPrivilegio = idPrivilegio;
        this.nombre       = nombre;
        this.descripcion  = descripcion;
        this.modulo       = modulo;
        this.estado       = estado;
    }

    public int getIdPrivilegio() {
        return idPrivilegio;
    }

    public void setIdPrivilegio(int idPrivilegio) {
        this.idPrivilegio = idPrivilegio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Privilegio{"
                + "idPrivilegio=" + idPrivilegio
                + ", nombre='"      + nombre      + '\''
                + ", descripcion='" + descripcion + '\''
                + ", modulo='"      + modulo      + '\''
                + ", estado="       + estado
                + '}';
    }
}
