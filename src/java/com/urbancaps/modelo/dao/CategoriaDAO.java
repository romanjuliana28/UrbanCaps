//  PROYECTO  : Urban Caps
//  ARCHIVO: CategoriaDAO.java
//  PAQUETE: com.urbancaps.modelo.dao
//  DESCRIPCIÓN: Clase DAO que gestiona las operaciones de base de datos para la tabla 'categorias'. las categorías se usan en el formulario de inventario, Catálogo y en el header del website.

//  CRUD Categoria:
// listar() : obtiene todas las categorías
// listarActivas(): obtiene solo categorías activas
// obtenerPorId() : obtiene una categoría por su ID
// insertar(): crea una nueva categoría
// actualizar() : modifica una categoría existente
// eliminar(): desactiva una categoría

// Paquete donde vive esta clase
package com.urbancaps.modelo.dao;

// Importo Conexion para manejar la conexión a la BD
import com.urbancaps.util.Conexion;

import com.urbancaps.modelo.bean.Categoria;

// Importo las clases JDBC para comunicarme con MySQL
import java.sql.Connection;    // Representa la conexión activa a MySQL
import java.sql.PreparedStatement; // Permite ejecutar consultas SQL seguras
import java.sql.ResultSet;     // Almacena los resultados de un SELECT
import java.sql.SQLException;  // Maneja errores de base de datos

// Importo las clases ArrayList y List para devolver listas de usuarios
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    // MÉTODO 1: listar()
    // Obtiene TODAS las categorías de la base de datos.
    // Se usa para llenar los selectores del formulario de iInventario y Catálogo en el panel de administración.

    public List<Categoria> listar() {

        List<Categoria> lista = new ArrayList<>();

        // Traemos todas las categorías ordenadas por nombre
        String sql = "SELECT id_categoria, nombre_categoria, "
                   + "descripcion, estado "
                   + "FROM categorias "
                   + "ORDER BY nombre_categoria ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setEstado(rs.getInt("estado"));
                lista.add(c);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar categorías: " + e.getMessage());
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

    // MÉTODO 2: listarActivas()

    // Obtiene SOLO las categorías con estado = 1 (Activas).
    // Se usa en el HEADER del website para mostrar solo las categorías vigentes al cliente.

    public List<Categoria> listarActivas() {

        List<Categoria> lista = new ArrayList<>();

        // Filtramos por estado = 1 para traer solo las activas
        String sql = "SELECT id_categoria, nombre_categoria, "
                   + "descripcion, estado "
                   + "FROM categorias "
                   + "WHERE estado = 1 "
                   + "ORDER BY nombre_categoria ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setEstado(rs.getInt("estado"));
                lista.add(c);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar categorías activas: " + e.getMessage());
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

    // Busca y retorna una sola categoría por su ID.
    // Se usa al cargar los datos en el formulario de edición.

    public Categoria obtenerPorId(int idCategoria) {

        Categoria c = null;

        String sql = "SELECT id_categoria, nombre_categoria, "
                   + "descripcion, estado "
                   + "FROM categorias "
                   + "WHERE id_categoria = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idCategoria);
            rs = ps.executeQuery();

            if (rs.next()) {
                c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al obtener categoría por ID: " + e.getMessage());
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

        return c;
    }

    // MÉTODO 4: insertar()
    
    // Crea una nueva categoría en la base de datos.
    // Ejemplo: "Snapback", "Trucker", "Bucket Hat".

    public boolean insertar(Categoria categoria) {

        String sql = "INSERT INTO categorias "
                   + "(nombre_categoria, descripcion, estado) "
                   + "VALUES (?, ?, ?)";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setString(1, categoria.getNombreCategoria());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getEstado());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al insertar categoría: " + e.getMessage());
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

    // Modifica los datos de una categoría existente.

    public boolean actualizar(Categoria categoria) {

        String sql = "UPDATE categorias "
                   + "SET nombre_categoria = ?, descripcion = ?, estado = ? "
                   + "WHERE id_categoria = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setString(1, categoria.getNombreCategoria());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getEstado());
            ps.setInt(4, categoria.getIdCategoria());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al actualizar categoría: " + e.getMessage());
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
    // Desactiva una categoría cambiando su estado a 0.
    // No se borra físicamente porque el inventario y el catálogo pueden tener productos asociados a esta categoría.

    public boolean eliminar(int idCategoria) {

        // Borrado lógico: estado = 0 en lugar de DELETE
        String sql = "UPDATE categorias SET estado = 0 "
                   + "WHERE id_categoria = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idCategoria);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al eliminar categoría: " + e.getMessage());
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
}