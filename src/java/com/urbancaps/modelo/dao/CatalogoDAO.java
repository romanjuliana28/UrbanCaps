//  PROYECTO: Urban Caps
//  ARCHIVO: CatalogoDAO.java
//  PAQUETE: com.urbancaps.modelo.dao
//  DESCRIPCIÓN: Clase DAO que gestiona todas las operaciones de base de datos para la tabla 'catalogo'.
//  Este DAO alimenta tanto el panel de administración como el website público donde los clientes pueden ver y comprar las gorras.

// CRUD Catalogo:
//  - listar(): obtiene todos los productos del catálogo
//  - listarActivos(): obtiene solo productos visibles (para el website)
//  - listarPorCategoria(): filtra productos por categoría (header website)
//  - obtenerPorId(): obtiene un producto por su ID
//  - insertar(): publica un nuevo producto en el catálogo
//  - actualizar(): modifica un producto publicado
//  - eliminar(): oculta un producto del website
//  - buscar(): busca productos por título (buscador website)

// Paquete donde vive esta clase
package com.urbancaps.modelo.dao;

// Importamos Conexion para manejar la conexión a la BD
import com.urbancaps.util.Conexion;

// Importamos Catalogo porque los métodos trabajan con esos objetos
import com.urbancaps.modelo.bean.Catalogo;

// Importo las clases JDBC para comunicarme con MySQL
import java.sql.Connection;    // Representa la conexión activa a MySQL
import java.sql.PreparedStatement; // Permite ejecutar consultas SQL seguras
import java.sql.ResultSet;     // Almacena los resultados de un SELECT
import java.sql.SQLException;  // Maneja errores de base de datos

// Para devolver listas de productos
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {

    // MÉTODO 1: listar()

    // Obtiene TODOS los productos del catálogo incluyendo los ocultos. Se usa en el panel de administración para que el admin vea y gestione todos los productos.
    // Usa JOIN con inventario y categorias para traer los nombres legibles en lugar de solo los números ID.
    // Retorna: List<Catalogo> con todos los productos

    public List<Catalogo> listar() {

        List<Catalogo> lista = new ArrayList<>();

        // JOIN doble:
        // INNER JOIN inventario: para traer el nombre del producto del inventario del que viene este registro del catálogo
        // INNER JOIN categorias: para traer el nombre de la categoría
        String sql = "SELECT c.id_catalogo, c.id_inventario, "
                   + "i.nombre_producto, c.id_categoria, "
                   + "cat.nombre_categoria, c.titulo, "
                   + "c.descripcion, c.precio_venta, "
                   + "c.imagen, c.stock, c.estado "
                   + "FROM catalogo c "
                   + "INNER JOIN inventario i   ON c.id_inventario = i.id_inventario "
                   + "INNER JOIN categorias cat ON c.id_categoria  = cat.id_categoria "
                   + "ORDER BY c.id_catalogo ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Catalogo cat = new Catalogo();
                cat.setIdCatalogo(rs.getInt("id_catalogo"));
                cat.setIdInventario(rs.getInt("id_inventario"));

                // nombre_producto viene del JOIN con la tabla inventario
                cat.setNombreProducto(rs.getString("nombre_producto"));
                cat.setIdCategoria(rs.getInt("id_categoria"));

                // nombre_categoria viene del JOIN con la tabla categorias
                cat.setNombreCategoria(rs.getString("nombre_categoria"));
                cat.setTitulo(rs.getString("titulo"));
                cat.setDescripcion(rs.getString("descripcion"));
                cat.setPrecioVenta(rs.getDouble("precio_venta"));
                cat.setImagen(rs.getString("imagen"));
                cat.setStock(rs.getInt("stock"));
                cat.setEstado(rs.getInt("estado"));
                lista.add(cat);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar catálogo: " + e.getMessage());
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

    // Obtiene SOLO los productos con estado = 1 (Visibles).
    // Este es el método que usa el WEBSITE PÚBLICO para mostrar las gorras disponibles a los clientes.
    // Los productos con estado = 0 están ocultos y no aparecen.
    // Retorna: List<Catalogo> solo con productos visibles

    public List<Catalogo> listarActivos() {

        List<Catalogo> lista = new ArrayList<>();

        // Misma consulta que listar() pero filtrando solo visibles
        // c.estado = 1 significa que el producto está visible en el website
        String sql = "SELECT c.id_catalogo, c.id_inventario, "
                   + "i.nombre_producto, c.id_categoria, "
                   + "cat.nombre_categoria, c.titulo, "
                   + "c.descripcion, c.precio_venta, "
                   + "c.imagen, c.stock, c.estado "
                   + "FROM catalogo c "
                   + "INNER JOIN inventario i   ON c.id_inventario = i.id_inventario "
                   + "INNER JOIN categorias cat ON c.id_categoria  = cat.id_categoria "
                   + "WHERE c.estado = 1 "
                   + "ORDER BY c.id_catalogo ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Catalogo cat = new Catalogo();
                cat.setIdCatalogo(rs.getInt("id_catalogo"));
                cat.setIdInventario(rs.getInt("id_inventario"));
                cat.setNombreProducto(rs.getString("nombre_producto"));
                cat.setIdCategoria(rs.getInt("id_categoria"));
                cat.setNombreCategoria(rs.getString("nombre_categoria"));
                cat.setTitulo(rs.getString("titulo"));
                cat.setDescripcion(rs.getString("descripcion"));
                cat.setPrecioVenta(rs.getDouble("precio_venta"));
                cat.setImagen(rs.getString("imagen"));
                cat.setStock(rs.getInt("stock"));
                cat.setEstado(rs.getInt("estado"));
                lista.add(cat);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar catálogo activo: " + e.getMessage());
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

    // MÉTODO 3: listarPorCategoria()

    // Obtiene los productos visibles filtrados por categoría.
    // Se usa cuando el cliente hace clic en una categoría del HEADER del website (Snapback, Plana, Trucker, etc.) para ver solo las gorras de ese tipo.
    // Parámetro: idCategoria apunta a ID de la categoría seleccionada
    // Retorna: List<Catalogo> con los productos de esa categoría

    public List<Catalogo> listarPorCategoria(int idCategoria) {

        List<Catalogo> lista = new ArrayList<>();

        // Filtramos por categoría Y por estado = 1 (visible)
        // Solo mostramos al cliente productos activos de esa categoría
        String sql = "SELECT c.id_catalogo, c.id_inventario, "
                   + "i.nombre_producto, c.id_categoria, "
                   + "cat.nombre_categoria, c.titulo, "
                   + "c.descripcion, c.precio_venta, "
                   + "c.imagen, c.stock, c.estado "
                   + "FROM catalogo c "
                   + "INNER JOIN inventario i   ON c.id_inventario = i.id_inventario "
                   + "INNER JOIN categorias cat ON c.id_categoria  = cat.id_categoria "
                   + "WHERE c.id_categoria = ? AND c.estado = 1 "
                   + "ORDER BY c.titulo ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            // Reemplazamos el ? con el ID de la categoría seleccionada
            ps.setInt(1, idCategoria);
            rs = ps.executeQuery();

            while (rs.next()) {
                Catalogo cat = new Catalogo();
                cat.setIdCatalogo(rs.getInt("id_catalogo"));
                cat.setIdInventario(rs.getInt("id_inventario"));
                cat.setNombreProducto(rs.getString("nombre_producto"));
                cat.setIdCategoria(rs.getInt("id_categoria"));
                cat.setNombreCategoria(rs.getString("nombre_categoria"));
                cat.setTitulo(rs.getString("titulo"));
                cat.setDescripcion(rs.getString("descripcion"));
                cat.setPrecioVenta(rs.getDouble("precio_venta"));
                cat.setImagen(rs.getString("imagen"));
                cat.setStock(rs.getInt("stock"));
                cat.setEstado(rs.getInt("estado"));
                lista.add(cat);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar catálogo por categoría: " + e.getMessage());
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

    // MÉTODO 4: obtenerPorId()

    // Busca y retorna UN solo producto del catálogo por su ID.
    // Se usa al cargar los datos en el formulario de edición en el panel de administración.
    // Parámetro: idCatalogo apunta a ID del producto a buscar
    // Retorna: objeto Catalogo | null si no existe

    public Catalogo obtenerPorId(int idCatalogo) {

        Catalogo cat = null;

        String sql = "SELECT c.id_catalogo, c.id_inventario, "
                   + "i.nombre_producto, c.id_categoria, "
                   + "cat.nombre_categoria, c.titulo, "
                   + "c.descripcion, c.precio_venta, "
                   + "c.imagen, c.stock, c.estado "
                   + "FROM catalogo c "
                   + "INNER JOIN inventario i   ON c.id_inventario = i.id_inventario "
                   + "INNER JOIN categorias cat ON c.id_categoria  = cat.id_categoria "
                   + "WHERE c.id_catalogo = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idCatalogo);
            rs = ps.executeQuery();

            if (rs.next()) {
                cat = new Catalogo();
                cat.setIdCatalogo(rs.getInt("id_catalogo"));
                cat.setIdInventario(rs.getInt("id_inventario"));
                cat.setNombreProducto(rs.getString("nombre_producto"));
                cat.setIdCategoria(rs.getInt("id_categoria"));
                cat.setNombreCategoria(rs.getString("nombre_categoria"));
                cat.setTitulo(rs.getString("titulo"));
                cat.setDescripcion(rs.getString("descripcion"));
                cat.setPrecioVenta(rs.getDouble("precio_venta"));
                cat.setImagen(rs.getString("imagen"));
                cat.setStock(rs.getInt("stock"));
                cat.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al obtener producto del catálogo por ID: " + e.getMessage());
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

        return cat;
    }

    // MÉTODO 5: insertar()

    // Publica un nuevo producto en el catálogo del website.
    // Se usa cuando el admin selecciona un producto del inventario y lo publica con su título, descripción, precio e imagen.
    // Parámetro: catalogo apunta al objeto con los datos del formulario
    // Retorn : true si se publicó correctamente | false si falló

    public boolean insertar(Catalogo catalogo) {

        // No incluimos id_catalogo porque MySQL se encarga de generarlo automaticamente.
        String sql = "INSERT INTO catalogo "
                   + "(id_inventario, id_categoria, titulo, descripcion, "
                   + "precio_venta, imagen, stock, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setInt(1, catalogo.getIdInventario());
            ps.setInt(2, catalogo.getIdCategoria());
            ps.setString(3, catalogo.getTitulo());
            ps.setString(4, catalogo.getDescripcion());
            ps.setDouble(5, catalogo.getPrecioVenta());
            ps.setString(6, catalogo.getImagen());
            ps.setInt(7, catalogo.getStock());
            ps.setInt(8, catalogo.getEstado());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al insertar en catálogo: " + e.getMessage());
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

    // MÉTODO 6: actualizar()

    // Modifica los datos de un producto ya publicado.
    // Se usa cuando el admin edita el título, descripción, precio, imagen o stock de un producto del catálogo.
    // Parámetro: catalogo apunta al objeto con los datos actualizados
    // Retorna: true si se actualizó correctamente | false si falló

    public boolean actualizar(Catalogo catalogo) {

        String sql = "UPDATE catalogo "
                   + "SET id_inventario = ?, id_categoria = ?, "
                   + "titulo = ?, descripcion = ?, precio_venta = ?, "
                   + "imagen = ?, stock = ?, estado = ? "
                   + "WHERE id_catalogo = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setInt(1, catalogo.getIdInventario());
            ps.setInt(2, catalogo.getIdCategoria());
            ps.setString(3, catalogo.getTitulo());
            ps.setString(4, catalogo.getDescripcion());
            ps.setDouble(5, catalogo.getPrecioVenta());
            ps.setString(6, catalogo.getImagen());
            ps.setInt(7, catalogo.getStock());
            ps.setInt(8, catalogo.getEstado());
            ps.setInt(9, catalogo.getIdCatalogo());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al actualizar catálogo: " + e.getMessage());
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

    // MÉTODO 7: eliminar()

    // Oculta un producto del website cambiando su estado a 0.
    // El producto deja de verse en el website pero no se borra de la base de datos. El admin puede volver a activarlo.
    // Parámetro: idCatalogo apunta al ID del producto a ocultar
    // Retorna  : true si se ocultó | false si falló

    public boolean eliminar(int idCatalogo) {

        // Borrado lógico: estado = 0 oculta el producto del website
        String sql = "UPDATE catalogo SET estado = 0 "
                   + "WHERE id_catalogo = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idCatalogo);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al eliminar producto del catálogo: " + e.getMessage());
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

    // MÉTODO 8: buscar()

    // Busca productos del catálogo por título.
    // Se usa en el BUSCADOR INTELIGENTE del website público cuando el cliente escribe el nombre de una gorra en la barra de búsqueda del header.
    // Parámetro: texto apunta a lo que el cliente escribe en el buscador
    // Retorna  : List<Catalogo> con los productos que coincidan

    public List<Catalogo> buscar(String texto) {

        List<Catalogo> lista = new ArrayList<>();

        // Buscamos por título Y por descripción para que el buscador sea más inteligente: 
        // si el cliente escribe "new york"  puede encontrar gorras que lo mencionen en la descripción aunque el título sea diferente.
        // Solo mostramos productos visibles (estado = 1)
        String sql = "SELECT c.id_catalogo, c.id_inventario, "
                   + "i.nombre_producto, c.id_categoria, "
                   + "cat.nombre_categoria, c.titulo, "
                   + "c.descripcion, c.precio_venta, "
                   + "c.imagen, c.stock, c.estado "
                   + "FROM catalogo c "
                   + "INNER JOIN inventario i   ON c.id_inventario = i.id_inventario "
                   + "INNER JOIN categorias cat ON c.id_categoria  = cat.id_categoria "
                   + "WHERE (c.titulo LIKE ? OR c.descripcion LIKE ?) "
                   + "AND c.estado = 1 "
                   + "ORDER BY c.titulo ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            // Asignamos el mismo texto con % a los dos parámetros ?
            // ? 1 - busca en el título del producto
            // ? 2 - busca en la descripción del producto
            
            String parametro = "%" + texto + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            rs = ps.executeQuery();

            while (rs.next()) {
                Catalogo cat = new Catalogo();
                cat.setIdCatalogo(rs.getInt("id_catalogo"));
                cat.setIdInventario(rs.getInt("id_inventario"));
                cat.setNombreProducto(rs.getString("nombre_producto"));
                cat.setIdCategoria(rs.getInt("id_categoria"));
                cat.setNombreCategoria(rs.getString("nombre_categoria"));
                cat.setTitulo(rs.getString("titulo"));
                cat.setDescripcion(rs.getString("descripcion"));
                cat.setPrecioVenta(rs.getDouble("precio_venta"));
                cat.setImagen(rs.getString("imagen"));
                cat.setStock(rs.getInt("stock"));
                cat.setEstado(rs.getInt("estado"));
                lista.add(cat);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al buscar en catálogo: " + e.getMessage());
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