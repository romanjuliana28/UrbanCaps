//  PROYECTO  : Urban Caps
//  ARCHIVO   : ProveedorDAO.java
//  PAQUETE   : com.urbancaps.modelo.dao
//  DESCRIPCIÓN: Clase DAO que gestiona todas las operacion de base de datos para la tabla 'proveedores'.

//  CRUD Proovedores
//  listar()          : obtiene todos los proveedores
//  listarActivos()   : obtiene solo proveedores activos
//  obtenerPorId()    : obtiene un proveedor por su ID
//  insertar()        : crea un nuevo proveedor
//  actualizar()      : modifica un proveedor existente
//  eliminar()        : desactiva un proveedor

// Paquete donde vive esta clase
package com.urbancaps.modelo.dao;

// Importa la clase Conexion para manejar la conexión a la BD
import com.urbancaps.util.Conexion;

// Importa la clase Proveedor porque los métodos trabajan con esos objetos
import com.urbancaps.modelo.bean.Proveedor;

// Importo las clases JDBC para comunicarme con MySQL
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Importo las clases ArrayList y List para devolver listas de usuarios
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {


    // MÉTODO 1: listar()

    // Obtiene TODOS los proveedores de la base de datos, se usa para mostrar la tabla completa en el panel admin.
    // Retorna: List<Proveedor> con todos los proveedores

    public List<Proveedor> listar() {

        List<Proveedor> lista = new ArrayList<>();

        // Traemos todos los proveedores ordenados por nombre
        String sql = "SELECT id_proveedor, nombre, apellidos, contacto, "
                   + "direccion, correo, estado "
                   + "FROM proveedores "
                   + "ORDER BY nombre ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            // Por cada fila creamos un objeto Proveedor y lo agregamos a la lista
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidos(rs.getString("apellidos"));
                p.setContacto(rs.getString("contacto"));
                p.setDireccion(rs.getString("direccion"));
                p.setCorreo(rs.getString("correo"));
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar proveedores: " + e.getMessage());
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

    // Obtiene SOLO los proveedores con estado = 1 (Activos).
    // Se usa en el formulario de Inventario para mostrar únicamente los proveedores disponibles en el selector.
    // No tiene sentido mostrar proveedores inactivos al registrar un nuevo producto en inventario.
    // Retorna: List<Proveedor> solo con proveedores activos

    public List<Proveedor> listarActivos() {

        List<Proveedor> lista = new ArrayList<>();

        // Filtramos por estado = 1 para traer solo los activos
        String sql = "SELECT id_proveedor, nombre, apellidos, contacto, "
                   + "direccion, correo, estado "
                   + "FROM proveedores "
                   + "WHERE estado = 1 "
                   + "ORDER BY nombre ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidos(rs.getString("apellidos"));
                p.setContacto(rs.getString("contacto"));
                p.setDireccion(rs.getString("direccion"));
                p.setCorreo(rs.getString("correo"));
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar proveedores activos: " + e.getMessage());
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

    // Busca y retorna un solo proveedor por su ID, lo uso al cargar los datos en el formulario de edición.
    // Parámetro: idproveedor  ID del proveedor a buscar
    // Retorna  : objeto Proveedor null si no existe

    public Proveedor obtenerPorId(int idProveedor) {

        Proveedor p = null;

        String sql = "SELECT id_proveedor, nombre, apellidos, contacto, "
                   + "direccion, correo, estado "
                   + "FROM proveedores "
                   + "WHERE id_proveedor = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            // Reemplazamos el ? con el ID del proveedor buscado
            ps.setInt(1, idProveedor);
            rs = ps.executeQuery();

            if (rs.next()) {
                p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidos(rs.getString("apellidos"));
                p.setContacto(rs.getString("contacto"));
                p.setDireccion(rs.getString("direccion"));
                p.setCorreo(rs.getString("correo"));
                p.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al obtener proveedor por ID: " + e.getMessage());
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

        return p;
    }

    // MÉTODO 4: insertar()

    // Crea un nuevo proveedor en la base de datos, se usa cuando el admin llena el formulario de nuevo proveedor y hace clic en Guardar.
    // Parámetro: proveedor es igual al objeto con los datos del formulario
    // Retorna  : true si se insertó correctamente | false si falló

    public boolean insertar(Proveedor proveedor) {


        String sql = "INSERT INTO proveedores "
                   + "(nombre, apellidos, contacto, direccion, correo, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getApellidos());
            ps.setString(3, proveedor.getContacto());
            ps.setString(4, proveedor.getDireccion());
            ps.setString(5, proveedor.getCorreo());
            ps.setInt(6, proveedor.getEstado());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al insertar proveedor: " + e.getMessage());
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

    // Modifica los datos de un proveedor existente.
    // Se usa cuando el admin edita un proveedor y guarda cambios.
    // Parámetro: proveedor es igual objeto con los datos actualizados
    // Retorna  : true si se actualizó correctamente | false si falló

    public boolean actualizar(Proveedor proveedor) {

        // Actualizamos todos los campos del proveedor
        // El WHERE id_proveedor = ? garantiza que solo se modifica el proveedor correcto
        String sql = "UPDATE proveedores "
                   + "SET nombre = ?, apellidos = ?, contacto = ?, "
                   + "direccion = ?, correo = ?, estado = ? "
                   + "WHERE id_proveedor = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getApellidos());
            ps.setString(3, proveedor.getContacto());
            ps.setString(4, proveedor.getDireccion());
            ps.setString(5, proveedor.getCorreo());
            ps.setInt(6, proveedor.getEstado());
            ps.setInt(7, proveedor.getIdProveedor());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al actualizar proveedor: " + e.getMessage());
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


    // ---------------------------------------------------------
    // MÉTODO 6: eliminar()

     // Se desactiva un proveedor cambiando su estado a 0 (Inactivo), no se borra físicamente para conservar el historial.
    // Un proveedor inactivo no puede iniciar sesión en el sistema.
    // Parámetro: idProveedor es igual al ID del proveedor a desactivar en Base de Datos
    // Retorna: true si se desactivó, false si falló

    public boolean eliminar(int idProveedor) {

        // Borrado lógico: estado = 0 en lugar de DELETE
        String sql = "UPDATE proveedores SET estado = 0 "
                   + "WHERE id_proveedor = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idProveedor);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al eliminar proveedor: " + e.getMessage());
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