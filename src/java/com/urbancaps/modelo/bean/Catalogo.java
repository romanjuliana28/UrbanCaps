//  PROYECTO  : Urban Caps
//  ARCHIVO   : Catalogo.java
//  PAQUETE   : com.urbancaps.modelo.bean
//  DESCRIPCIÓN: Clase bean que representa la tabla 'catalogo' de la base de datos. Cada objeto Catalogo equivale a un registro de esa tabla.

//  TABLA EN BD:
//  catalogo (id_catalogo, id_inventario, id_categoria, titulo, descripcion, precio_venta, imagen, stock, estado)

//  ¿Para qué sirve esta clase en el proyecto?
//  - Representa cada producto que el administrador publica en el website para que los clientes puedan verlo y comprarlo.
//  - Se alimenta desde el módulo de Inventario: solo se pueden publicar productos que ya existen en el inventario.
//  - Los registros con estado=1 son los que aparecen visibles en el website público de Urban Caps.
//  - Contiene el botón de WhatsApp que conecta al cliente con un asesor de ventas directamente.

// Indica en qué paquete vive esta clase dentro del proyecto.
package com.urbancaps.modelo.bean;

// Implementamos Serializable para que los objetos Catalogo puedan guardarse en la sesión HTTP si es necesario.
import java.io.Serializable;

// Declara la clase pública con el mismo nombre del archivo.
public class Catalogo implements Serializable {

    // ATRIBUTOS DE LA CLASE

    // Cada atributo corresponde exactamente a una columna de la tabla 'catalogo' en la base de datos.
    // Los atributos extra (nombreProducto, nombreCategoria) se llenan desde el DAO mediante JOIN para mostrar nombres legibles en pantalla en lugar de números.

    // Corresponde a la columna id_catalogo (INT, PK, AUTO_INCREMENT)
    // Identificador único de cada producto publicado en el catálogo.
    private int idCatalogo;

    // Corresponde a la columna id_inventario (INT, FK → inventario)
    // Número del producto del inventario que se está publicando.
    // Un producto del inventario solo puede publicarse una vez.
    private int idInventario;

    // Atributo extra: nombre del producto del inventario.
    // NO existe en la tabla 'catalogo' de la BD.
    // Se llena con un JOIN a la tabla inventario en el DAO.
    // Sirve para mostrar de qué producto del inventario viene este registro del catálogo en el panel de administración.
    private String nombreProducto;

    // Corresponde a la columna id_categoria (INT, FK → categorias)
    // Número de la categoría a la que pertenece este producto.
    // Define en qué sección del header del website aparece.
    private int idCategoria;

    // Atributo extra: nombre de la categoría para mostrar en pantalla.
    // No existe en la tabla 'catalogo' de la BD.
    // Se llena con un JOIN a la tabla categorias en el DAO.
    // Ejemplo: "Snapback", "Plana", "Trucker"
    private String nombreCategoria;

    // Corresponde a la columna titulo (VARCHAR 150)
    // Título del producto tal como lo ve el cliente en el website.
    // Ejemplo: "Gorra Snapback Premium New York"
    // Puede ser diferente al nombre en inventario (más comercial).
    private String titulo;

    // Corresponde a la columna descripcion (TEXT)
    // Descripción detallada del producto para el cliente.
    // Aparece en la tarjeta del producto en el website.
    // Ejemplo: "Gorra snapback de alta calidad con bordado..."
    private String descripcion;

    // Corresponde a la columna precio_venta (DECIMAL 10,2)
    // Precio al que se vende el producto al cliente.
    // Es diferente al precio_compra del inventario.
    // Usamos double para manejar valores decimales.
    private double precioVenta;

    // Corresponde a la columna imagen (VARCHAR 255)
    // Ruta de la imagen del producto para mostrar en el website.
    // Ejemplo: "gorras/snapback-ny-001.jpg"
    // Se guarda solo la ruta relativa, no la imagen completa.
    // Puede ser null si el producto no tiene imagen aún.
    private String imagen;

    // Corresponde a la columna stock (INT)
    // Número de unidades disponibles para vender al cliente.
    // Se actualiza cuando se confirma una venta.
    private int stock;

    // Corresponde a la columna estado (TINYINT 1)
    // 1 = Visible en el website (el cliente puede verlo)
    // 0 = Oculto (el administrador lo desactivó temporalmente)
    private int estado;

    // CONSTRUCTORES

    // CONSTRUCTOR VACÍO (sin parámetros)
    // Lo usamos cuando creamos un Catalogo vacío y luego lo llenamos campo a campo con los datos del formulario.
    // Ejemplo de uso: Catalogo c = new Catalogo();
    public Catalogo() {
    }

    // CONSTRUCTOR COMPLETO (con todos los parámetros)
    // Lo usamos en el DAO cuando leemos un registro de la BD y lo convertimos directamente en un objeto Catalogo.
    // Incluye los campos extra para los nombres de las relaciones.
  
    // Ejemplo de uso:
    //   Catalogo c = new Catalogo(1, 3, "Gorra NY Yankees", 1, "Snapback","Gorra Snapback Premium", "Descripcion...", 65000.0, "gorras/ny-001.jpg", 20, 1);
    public Catalogo(int idCatalogo, int idInventario,
                    String nombreProducto, int idCategoria,
                    String nombreCategoria, String titulo,
                    String descripcion, double precioVenta,
                    String imagen, int stock, int estado) {
        this.idCatalogo      = idCatalogo;
        this.idInventario    = idInventario;
        this.nombreProducto  = nombreProducto;
        this.idCategoria     = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.titulo          = titulo;
        this.descripcion     = descripcion;
        this.precioVenta     = precioVenta;
        this.imagen          = imagen;
        this.stock           = stock;
        this.estado          = estado;
    }

    // GETTERS Y SETTERS

    // GETTER de idCatalogo
    // Retorna el identificador único del producto en catálogo
    // Uso: int id = catalogo.getIdCatalogo();
    public int getIdCatalogo() {
        return idCatalogo;
    }

    // SETTER de idCatalogo
    // Permite asignar el identificador del producto
    // Uso: catalogo.setIdCatalogo(1);
    public void setIdCatalogo(int idCatalogo) {
        this.idCatalogo = idCatalogo;
    }

    // GETTER de idInventario
    // Retorna el número del producto del inventario asociado
    // Uso: int idInv = catalogo.getIdInventario();
    public int getIdInventario() {
        return idInventario;
    }

    // SETTER de idInventario
    // Permite asignar el producto del inventario
    // Uso: catalogo.setIdInventario(3);
    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    // GETTER de nombreProducto
    // Retorna el nombre del producto del inventario
    // Este valor viene del JOIN con la tabla inventario en el DAO
    // Uso: String prod = catalogo.getNombreProducto();
    public String getNombreProducto() {
        return nombreProducto;
    }

    // SETTER de nombreProducto
    // Permite asignar el nombre del producto del inventario
    // Uso: catalogo.setNombreProducto("Gorra NY Yankees");
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    // GETTER de idCategoria
    // Retorna el número de la categoría del producto
    // Uso: int idCat = catalogo.getIdCategoria();
    public int getIdCategoria() {
        return idCategoria;
    }

    // SETTER de idCategoria
    // Permite asignar la categoría del producto
    // Uso: catalogo.setIdCategoria(1);
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    // GETTER de nombreCategoria
    // Retorna el nombre legible de la categoría
    // Este valor viene del JOIN con la tabla categorias en el DAO
    // Uso: String cat = catalogo.getNombreCategoria();
    public String getNombreCategoria() {
        return nombreCategoria;
    }

    // SETTER de nombreCategoria
    // Permite asignar el nombre de la categoría
    // Uso: catalogo.setNombreCategoria("Snapback");
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    // GETTER de titulo
    // Retorna el título comercial del producto en el website
    // Uso: String titulo = catalogo.getTitulo();
    public String getTitulo() {
        return titulo;
    }

    // SETTER de titulo
    // Permite asignar el título del producto
    // Uso: catalogo.setTitulo("Gorra Snapback Premium New York");
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // GETTER de descripcion
    // Retorna la descripción detallada del producto
    // Uso: String desc = catalogo.getDescripcion();
    public String getDescripcion() {
        return descripcion;
    }

    // SETTER de descripcion
    // Permite asignar la descripción del producto
    // Uso: catalogo.setDescripcion("Gorra de alta calidad...");
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // GETTER de precioVenta
    // Retorna el precio de venta al público
    // Uso: double precio = catalogo.getPrecioVenta();
    public double getPrecioVenta() {
        return precioVenta;
    }

    // SETTER de precioVenta
    // Permite asignar el precio de venta
    // Uso: catalogo.setPrecioVenta(65000.0);
    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    // GETTER de imagen
    // Retorna la ruta de la imagen del producto
    // Puede retornar null si el producto no tiene imagen
    // Uso: String img = catalogo.getImagen();
    public String getImagen() {
        return imagen;
    }

    // SETTER de imagen
    // Permite asignar la ruta de la imagen
    // Uso: catalogo.setImagen("gorras/snapback-ny-001.jpg");
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    // GETTER de stock
    // Retorna las unidades disponibles para vender
    // Uso: int stock = catalogo.getStock();
    public int getStock() {
        return stock;
    }

    // SETTER de stock
    // Permite asignar el stock disponible
    // Uso: catalogo.setStock(20);
    public void setStock(int stock) {
        this.stock = stock;
    }

    // GETTER de estado
    // Retorna el estado del producto (1 = Visible, 0 = Oculto)
    // Uso: int estado = catalogo.getEstado();
    public int getEstado() {
        return estado;
    }

    // SETTER de estado
    // Permite asignar el estado del producto
    // Uso: catalogo.setEstado(1);
    public void setEstado(int estado) {
        this.estado = estado;
    }

    // MÉTODO toString()

    // Permite imprimir un objeto Catalogo con System.out.println() mostrando todos sus datos. Útil para depuración.

    // Ejemplo:
    //   System.out.println(catalogo);
    //   - Catalogo{idCatalogo=1, titulo='Gorra Snapback Premium', nombreCategoria='Snapback', precioVenta=65000.0, stock=20, estado=1}

    @Override
    public String toString() {
        return "Catalogo{"
                + "idCatalogo="       + idCatalogo
                + ", titulo='"          + titulo          + '\''
                + ", nombreProducto='"  + nombreProducto  + '\''
                + ", nombreCategoria='" + nombreCategoria + '\''
                + ", precioVenta="      + precioVenta
                + ", stock="            + stock
                + ", imagen='"          + imagen          + '\''
                + ", estado="           + estado
                + '}';
    }
}