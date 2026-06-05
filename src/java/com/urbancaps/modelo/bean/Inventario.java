package com.urbancaps.modelo.bean;

import java.io.Serializable;
import java.util.Date;

public class Inventario implements Serializable {

    private int idInventario;
    private int idProveedor;
    private String nombreProveedor;
    private int idCategoria;
    private String nombreCategoria;
    private String nombreProducto;
    private int cantidad;
    private double precioCompra;
    private Date fechaIngreso;
    private int estado;
    
    public Inventario() {
    }

    public Inventario(int idInventario, int idProveedor,
                      String nombreProveedor, int idCategoria,
                      String nombreCategoria, String nombreProducto,
                      int cantidad, double precioCompra,
                      Date fechaIngreso, int estado) {
        this.idInventario    = idInventario;
        this.idProveedor     = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.idCategoria     = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.nombreProducto  = nombreProducto;
        this.cantidad        = cantidad;
        this.precioCompra    = precioCompra;
        this.fechaIngreso    = fechaIngreso;
        this.estado          = estado;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Inventario{"
                + "idInventario="     + idInventario
                + ", nombreProducto='"  + nombreProducto  + '\''
                + ", nombreProveedor='" + nombreProveedor + '\''
                + ", nombreCategoria='" + nombreCategoria + '\''
                + ", cantidad="         + cantidad
                + ", precioCompra="     + precioCompra
                + ", fechaIngreso="     + fechaIngreso
                + ", estado="           + estado
                + '}';
    }
}