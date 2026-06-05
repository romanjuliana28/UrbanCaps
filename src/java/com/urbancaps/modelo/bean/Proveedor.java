package com.urbancaps.modelo.bean;

import java.io.Serializable;

public class Proveedor implements Serializable {

    private int idProveedor;
    private String nombre;
    private String apellidos;
    private String contacto;
    private String direccion;
    private String correo;
    private int estado;

    public Proveedor() {
    }

    public Proveedor(int idProveedor, String nombre, String apellidos,
                     String contacto, String direccion,
                     String correo, int estado) {
        this.idProveedor = idProveedor;
        this.nombre      = nombre;
        this.apellidos   = apellidos;
        this.contacto    = contacto;
        this.direccion   = direccion;
        this.correo      = correo;
        this.estado      = estado;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Proveedor{"
                + "idProveedor=" + idProveedor
                + ", nombre='"    + nombre    + '\''
                + ", apellidos='" + apellidos + '\''
                + ", contacto='"  + contacto  + '\''
                + ", direccion='" + direccion + '\''
                + ", correo='"    + correo    + '\''
                + ", estado="     + estado
                + '}';
    }
}