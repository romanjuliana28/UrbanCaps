//  PROYECTO: Urban Caps
//  ARCHIVO: InventarioDAO.java
//  PAQUETE: com.urbancaps.modelo.dao
//  DESCRIPCIÓN: Clase DAO que gestiona todas las operaciones de base de datos para la tabla 'inventario'.

//  CRUD Inventario:
//  - listar(): obtiene todos los productos del inventario
//  - listarActivos(): obtiene solo productos activos
//  - obtenerPorId(): obtiene un producto por su ID
//  - insertar() : registra un nuevo producto
//  - actualizar(): modifica un producto existente
//  - eliminar(): desactiva un producto del inventario
//  - buscar(): busca productos por nombre


// Paquete donde vive esta clase
package com.urbancaps.modelo.dao;

// Importamos Conexion para manejar la conexión a la BD
import com.urbancaps.util.Conexion;

// Importamos Inventario porque los métodos trabajan con esos objetos
import com.urbancaps.modelo.bean.Inventario;

// Importo las clases JDBC para comunicarme con MySQL
import java.sql.Connection;    // Representa la conexión activa a MySQL
import java.sql.PreparedStatement; // Permite ejecutar consultas SQL seguras
import java.sql.ResultSet;     // Almacena los resultados de un SELECT
import java.sql.SQLException;  // Maneja errores de base de datos

// Para manejar la fecha de ingreso que viene de MySQL
import java.sql.Date;

// Importo las clases ArrayList y List para devolver listas de Inventario
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    // MÉTODO 1: listar()

    // Obtiene TODOS los productos del inventario.
    // Usa JOIN con proveedores y categorias para traer los nombres legibles en lugar de solo los números ID.
    // Retorna: List<Inventario> con todos los productos

    public List<Inventario> listar() {

        List<Inventario> lista = new ArrayList<>();

        // JOIN doble:
        // INNER JOIN proveedores: para traer el nombre del proveedor
        // INNER JOIN categorias:  para traer el nombre de la categoría
        // Así en una sola consulta traemos toda la información necesaria para mostrar en la tabla del panel de administración.
        String sql = "SELECT i.id_inventario, i.id_proveedor, "
                   + "p.nombre AS nombre_proveedor, "
                   + "i.id_categoria, c.nombre_categoria, "
                   + "i.nombre_producto, i.cantidad, "
                   + "i.precio_compra, i.fecha_ingreso, i.estado "
                   + "FROM inventario i "
                   + "INNER JOIN proveedores p ON i.id_proveedor = p.id_proveedor "
                   + "INNER JOIN categorias c  ON i.id_categoria = c.id_categoria "
                   + "ORDER BY i.id_inventario ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Inventario inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario"));
                inv.setIdProveedor(rs.getInt("id_proveedor"));

                // El alias "nombre_proveedor" viene del JOIN con proveedores
                // En el SQL escribimos: p.nombre AS nombre_proveedor
                inv.setNombreProveedor(rs.getString("nombre_proveedor"));
                inv.setIdCategoria(rs.getInt("id_categoria"));

                // El alias "nombre_categoria" viene del JOIN con categorias
                inv.setNombreCategoria(rs.getString("nombre_categoria"));
                inv.setNombreProducto(rs.getString("nombre_producto"));
                inv.setCantidad(rs.getInt("cantidad"));
                inv.setPrecioCompra(rs.getDouble("precio_compra"));

                // Leemos la fecha de tipo DATE desde MySQL y la convertimos a java.util.Date para nuestro bean
                Date fechaSql = rs.getDate("fecha_ingreso");
                if (fechaSql != null) {
                    inv.setFechaIngreso(new java.util.Date(fechaSql.getTime()));
                }

                inv.setEstado(rs.getInt("estado"));
                lista.add(inv);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar inventario: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Conexion.cerrarConexion(conexion);
        }

        return lista;
    }

    // MÉTODO 2: listarActivos()

    // Obtiene SOLO los productos con estado = 1 (Activos).
    // Se usa en el formulario del Catálogo para mostrar únicamente los productos disponibles para publicar en el website. No tendría sentido publicar un producto que ya fue dado de baja del inventario.
    // Retorna: List<Inventario> solo con productos activos

    public List<Inventario> listarActivos() {

        List<Inventario> lista = new ArrayList<>();

        // Misma consulta que listar() pero con filtro estado = 1
        String sql = "SELECT i.id_inventario, i.id_proveedor, "
                   + "p.nombre AS nombre_proveedor, "
                   + "i.id_categoria, c.nombre_categoria, "
                   + "i.nombre_producto, i.cantidad, "
                   + "i.precio_compra, i.fecha_ingreso, i.estado "
                   + "FROM inventario i "
                   + "INNER JOIN proveedores p ON i.id_proveedor = p.id_proveedor "
                   + "INNER JOIN categorias c  ON i.id_categoria = c.id_categoria "
                   + "WHERE i.estado = 1 "
                   + "ORDER BY i.nombre_producto ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Inventario inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario"));
                inv.setIdProveedor(rs.getInt("id_proveedor"));
                inv.setNombreProveedor(rs.getString("nombre_proveedor"));
                inv.setIdCategoria(rs.getInt("id_categoria"));
                inv.setNombreCategoria(rs.getString("nombre_categoria"));
                inv.setNombreProducto(rs.getString("nombre_producto"));
                inv.setCantidad(rs.getInt("cantidad"));
                inv.setPrecioCompra(rs.getDouble("precio_compra"));

                Date fechaSql = rs.getDate("fecha_ingreso");
                if (fechaSql != null) {
                    inv.setFechaIngreso(new java.util.Date(fechaSql.getTime()));
                }

                inv.setEstado(rs.getInt("estado"));
                lista.add(inv);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar inventario activo: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Conexion.cerrarConexion(conexion);
        }

        return lista;
    }

    // MÉTODO 3: obtenerPorId()
    // Busca y retorna UN solo producto del inventario por su ID.
    // Se usa al cargar los datos en el formulario de edición.
    // Parámetro: idInventario apunta a ID del producto a buscar
    // Retorna : objeto Inventario | null si no existe

    public Inventario obtenerPorId(int idInventario) {

        Inventario inv = null;

        String sql = "SELECT i.id_inventario, i.id_proveedor, "
                   + "p.nombre AS nombre_proveedor, "
                   + "i.id_categoria, c.nombre_categoria, "
                   + "i.nombre_producto, i.cantidad, "
                   + "i.precio_compra, i.fecha_ingreso, i.estado "
                   + "FROM inventario i "
                   + "INNER JOIN proveedores p ON i.id_proveedor = p.id_proveedor "
                   + "INNER JOIN categorias c  ON i.id_categoria = c.id_categoria "
                   + "WHERE i.id_inventario = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idInventario);
            rs = ps.executeQuery();

            if (rs.next()) {
                inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario"));
                inv.setIdProveedor(rs.getInt("id_proveedor"));
                inv.setNombreProveedor(rs.getString("nombre_proveedor"));
                inv.setIdCategoria(rs.getInt("id_categoria"));
                inv.setNombreCategoria(rs.getString("nombre_categoria"));
                inv.setNombreProducto(rs.getString("nombre_producto"));
                inv.setCantidad(rs.getInt("cantidad"));
                inv.setPrecioCompra(rs.getDouble("precio_compra"));

                Date fechaSql = rs.getDate("fecha_ingreso");
                if (fechaSql != null) {
                    inv.setFechaIngreso(new java.util.Date(fechaSql.getTime()));
                }

                inv.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al obtener producto por ID: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Conexion.cerrarConexion(conexion);
        }

        return inv;
    }

    // MÉTODO 4: insertar()

    // Registra un nuevo producto en el inventario.
    // Se usa cuando el admin llena el formulario de nuevo producto y hace clic en Guardar.
    // Parámetro: inventario apunta a objeto con los datos del formulario
    // Retorna : true si se insertó correctamente | false si falló

    public boolean insertar(Inventario inventario) {

        // No incluimos id_inventario porque es AUTO_INCREMENT
        String sql = "INSERT INTO inventario "
                   + "(id_proveedor, id_categoria, nombre_producto, "
                   + "cantidad, precio_compra, fecha_ingreso, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setInt(1, inventario.getIdProveedor());
            ps.setInt(2, inventario.getIdCategoria());
            ps.setString(3, inventario.getNombreProducto());
            ps.setInt(4, inventario.getCantidad());
            ps.setDouble(5, inventario.getPrecioCompra());

            // Convertimos java.util.Date a java.sql.Date porque MySQL espera el formato de fecha SQL no el formato de fecha de Java
            ps.setDate(6, new Date(inventario.getFechaIngreso().getTime()));
            ps.setInt(7, inventario.getEstado());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al insertar en inventario: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Conexion.cerrarConexion(conexion);
        }
    }

    // MÉTODO 5: actualizar()

    // Modifica los datos de un producto existente en inventario.
    // Se usa cuando el admin edita un producto y guarda cambios.
    // Parámetro: inventario apunta a  objeto con los datos actualizados
    // Retorna: true si se actualizó correctamente | false si falló

    public boolean actualizar(Inventario inventario) {

        String sql = "UPDATE inventario "
                   + "SET id_proveedor = ?, id_categoria = ?, "
                   + "nombre_producto = ?, cantidad = ?, "
                   + "precio_compra = ?, fecha_ingreso = ?, estado = ? "
                   + "WHERE id_inventario = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setInt(1, inventario.getIdProveedor());
            ps.setInt(2, inventario.getIdCategoria());
            ps.setString(3, inventario.getNombreProducto());
            ps.setInt(4, inventario.getCantidad());
            ps.setDouble(5, inventario.getPrecioCompra());
            ps.setDate(6, new Date(inventario.getFechaIngreso().getTime()));
            ps.setInt(7, inventario.getEstado());
            ps.setInt(8, inventario.getIdInventario());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al actualizar inventario: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Conexion.cerrarConexion(conexion);
        }
    }

    // MÉTODO 6: eliminar()

    // Desactiva un producto del inventario cambiando estado a 0.
    // No se borra físicamente porque el catálogo puede tener productos referenciando este inventario.
    // Parámetro: idInventario apunta a ID del producto a desactivar
    // Retorna : true si se desactivó | false si falló

    public boolean eliminar(int idInventario) {

        // Borrado lógico: estado = 0 en lugar de DELETE
        String sql = "UPDATE inventario SET estado = 0 "
                   + "WHERE id_inventario = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idInventario);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Conexion.cerrarConexion(conexion);
        }
    }

    // MÉTODO 7: buscar()
    // Busca productos del inventario por nombre.
    // Se usa en el buscador del panel de administración para filtrar productos mientras el admin escribe en el campo  de búsqueda.
    // Parámetro: texto apunta a lo que el admin escribe en el buscador
    // Retorna: List<Inventario> con los productos que coincidan

    public List<Inventario> buscar(String texto) {

        List<Inventario> lista = new ArrayList<>();

        // LIKE '%texto%' busca el texto en cualquier parte del nombre
        // Por ejemplo buscar "york" encontrará "Gorra New York Yankees"
        // El % significa "cualquier cosa antes o después del texto"
        
        String sql = "SELECT i.id_inventario, i.id_proveedor, "
                   + "p.nombre AS nombre_proveedor, "
                   + "i.id_categoria, c.nombre_categoria, "
                   + "i.nombre_producto, i.cantidad, "
                   + "i.precio_compra, i.fecha_ingreso, i.estado "
                   + "FROM inventario i "
                   + "INNER JOIN proveedores p ON i.id_proveedor = p.id_proveedor "
                   + "INNER JOIN categorias c  ON i.id_categoria = c.id_categoria "
                   + "WHERE i.nombre_producto LIKE ? "
                   + "ORDER BY i.nombre_producto ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            // Agregamos los % alrededor del texto de búsqueda para que LIKE busque en cualquier posición del nombre
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Inventario inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario"));
                inv.setIdProveedor(rs.getInt("id_proveedor"));
                inv.setNombreProveedor(rs.getString("nombre_proveedor"));
                inv.setIdCategoria(rs.getInt("id_categoria"));
                inv.setNombreCategoria(rs.getString("nombre_categoria"));
                inv.setNombreProducto(rs.getString("nombre_producto"));
                inv.setCantidad(rs.getInt("cantidad"));
                inv.setPrecioCompra(rs.getDouble("precio_compra"));

                Date fechaSql = rs.getDate("fecha_ingreso");
                if (fechaSql != null) {
                    inv.setFechaIngreso(new java.util.Date(fechaSql.getTime()));
                }

                inv.setEstado(rs.getInt("estado"));
                lista.add(inv);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al buscar en inventario: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Conexion.cerrarConexion(conexion);
        }

        return lista;
    }
}