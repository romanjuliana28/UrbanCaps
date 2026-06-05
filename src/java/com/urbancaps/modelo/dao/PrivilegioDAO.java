//  PROYECTO  : Urban Caps
//  ARCHIVO   : PrivilegioDAO.java
//  PAQUETE   : com.urbancaps.modelo.dao
//  DESCRIPCIÓN: Clase DAO que gestiona todas las operaciones de base de datos para la tabla 'privilegios' y también la tabla 'roles_privilegios'.

//  CRUD Privilegio:
//  listar() : obtiene todos los privilegios
//  obtenerPorId() : obtiene un privilegio por ID
//  insertar(): crea un nuevo privilegio
//  actualizar(): modifica un privilegio existente
//  eliminar() : desactiva un privilegio
//  listarPorRol() : obtiene privilegios de un rol
//  actualizarPrivilegiosRol() : actualiza permisos de un rol


// Paquete donde vive esta clase
package com.urbancaps.modelo.dao;

// Importo Conexion para abrira la conexión a la BD
import com.urbancaps.util.Conexion;

// Importo Privilegio porque los métodos trabajan con esos objetos
import com.urbancaps.modelo.bean.Privilegio;

// Importo las clases JDBC para comunicarme con MySQL
import java.sql.Connection;    // Representa la conexión activa a MySQL
import java.sql.PreparedStatement; // Permite ejecutar consultas SQL seguras
import java.sql.ResultSet;     // Almacena los resultados de un SELECT
import java.sql.SQLException;  // Maneja errores de base de datos

// Importo las clases ArrayList y List para devolver listas de privilegios
import java.util.ArrayList;
import java.util.List;

public class PrivilegioDAO {

    // MÉTODO 1: listar()

    // Obtiene TODOS los privilegios de la base de datos, se usa para mostrar la tabla de privilegios en el panel admin y también para mostrar los checkboxes al asignar privilegios a un rol.
   // Retorna una lista List<Privilegio> con todos los privilegios registrados.

    public List<Privilegio> listar() {

        // Lista vacía que iremos llenando con cada privilegio
        List<Privilegio> lista = new ArrayList<>();

        // Traemos todos los privilegios ordenados por módulo y luego por nombre para que queden agrupados visualmente  en pantalla: todos los de ROLES juntos, todos los de USUARIOS juntos, etc.
        String sql = "SELECT id_privilegio, nombre, descripcion, modulo, estado "
                   + "FROM privilegios "
                   + "ORDER BY modulo ASC, nombre ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            // Por cada fila del resultado creamos un objeto Privilegio
            while (rs.next()) {
                Privilegio p = new Privilegio();
                p.setIdPrivilegio(rs.getInt("id_privilegio"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setModulo(rs.getString("modulo"));
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar privilegios: " + e.getMessage());
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

    // MÉTODO 2: obtenerPorId()
    
    // Busca y retorna un solo privilegio por su ID, lo uso al cargar los datos en el formulario de edición.
    // Parámetro: idPrivilegio  ID del Privilegio a buscar
    // Retorna  : objeto Privilegio null si no exise.
    
    public Privilegio obtenerPorId(int idPrivilegio) {

        Privilegio p = null;

        String sql = "SELECT id_privilegio, nombre, descripcion, modulo, estado "
                   + "FROM privilegios "
                   + "WHERE id_privilegio = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            // Reemplazamos el ? con el ID del privilegio buscado
            ps.setInt(1, idPrivilegio);
            rs = ps.executeQuery();

            if (rs.next()) {
                p = new Privilegio();
                p.setIdPrivilegio(rs.getInt("id_privilegio"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setModulo(rs.getString("modulo"));
                p.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al obtener privilegio por ID: " + e.getMessage());
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

    // MÉTODO 3: insertar()

    // Crea un nuevo privilegio en la base de datos.
    // Se usa cuando el admin registra una nueva acción disponible en el sistema.
    // Parámetro: privilegio apunta a objeto con los datos del formulario
    // Retorna  : true si se insertó correctamente | false si falló

    public boolean insertar(Privilegio privilegio) {

        // No incluimos id_privilegio debido a que MySQL lo genera automaticamente.
        String sql = "INSERT INTO privilegios (nombre, descripcion, modulo, estado) "
                   + "VALUES (?, ?, ?, ?)";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setString(1, privilegio.getNombre());
            ps.setString(2, privilegio.getDescripcion());
            ps.setString(3, privilegio.getModulo());
            ps.setInt(4, privilegio.getEstado());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al insertar privilegio: " + e.getMessage());
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

    // MÉTODO 4: actualizar()

    // Modifica los datos de un privilegio existente.
    // Se usa cuando el admin edita un privilegio y guarda cambios.
    // Parámetro: privilegio a`punta a objeto con los datos actualizados
    // Retorna  : true si se actualizó correctamente | false si falló

    public boolean actualizar(Privilegio privilegio) {

        String sql = "UPDATE privilegios "
                   + "SET nombre = ?, descripcion = ?, modulo = ?, estado = ? "
                   + "WHERE id_privilegio = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setString(1, privilegio.getNombre());
            ps.setString(2, privilegio.getDescripcion());
            ps.setString(3, privilegio.getModulo());
            ps.setInt(4, privilegio.getEstado());
            ps.setInt(5, privilegio.getIdPrivilegio());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al actualizar privilegio: " + e.getMessage());
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

    // MÉTODO 5: eliminar()

    // Desactiva un privilegio cambiando su estado a 0, no se borra físicamente para conservar el historial.
    // Parámetro: idPrivilegio apunta a  ID del privilegio a desactivar
    // Retorna  : true si se desactivó | false si falló

    public boolean eliminar(int idPrivilegio) {

        // Borrado lógico: cambiamos estado a 0 en lugar de DELETE
        String sql = "UPDATE privilegios SET estado = 0 "
                   + "WHERE id_privilegio = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idPrivilegio);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al eliminar privilegio: " + e.getMessage());
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

    // MÉTODO 6: listarPorRol()

    // Obtiene la lista de IDs de privilegios asignados a un rol.
    // Se usa en la pantalla de asignación de privilegios para saber qué checkboxes deben aparecer marcados.
    // Parámetro: idRol apunta a  ID del rol que queremos consultar, esta retorna  : List<Integer> con los IDs de los privilegios que tiene asignados ese rol.

    public List<Integer> listarPorRol(int idRol) {

        // Lista de IDs de privilegios asignados al rol
        List<Integer> idsPrivilegios = new ArrayList<>();

        // Consultamos la tabla puente roles_privilegios para saber qué privilegios tiene asignados este rol
        String sql = "SELECT id_privilegio FROM roles_privilegios "
                   + "WHERE id_rol = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idRol);
            rs = ps.executeQuery();

            // Guardamos cada ID de privilegio en la lista
            while (rs.next()) {
                idsPrivilegios.add(rs.getInt("id_privilegio"));
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar privilegios por rol: " + e.getMessage());
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

        return idsPrivilegios;
    }

    // MÉTODO 7: actualizarPrivilegiosRol()

    // Actualiza los privilegios asignados a un rol.
    // Se usa cuando el admin marca o desmarca checkboxes en la pantalla de asignación de privilegios.
    // El proceso es:
    // 1. Borramos TODOS los privilegios actuales del rol
    // 2. Insertamos los nuevos privilegios seleccionados
    // Parámetro idRol apunta a rol al que se actualizan privilegios
    // Parámetro idsPrivilegios apunta a  lista de IDs seleccionados en pantalla
    // Retorna: true si todo salió bien | false si hubo error

    public boolean actualizarPrivilegiosRol(int idRol, List<Integer> idsPrivilegios) {

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            conexion.setAutoCommit(false);

            // PASO 1: Borramos todos los privilegios actuales del rol
            // Usamos DELETE real aquí porque roles_privilegios es una tabla puente, no tiene datos importantes propios.
            String sqlDelete = "DELETE FROM roles_privilegios WHERE id_rol = ?";
            ps = conexion.prepareStatement(sqlDelete);
            ps.setInt(1, idRol);
            ps.executeUpdate();
            ps.close();

            // PASO 2: Insertamos los nuevos privilegios seleccionados
            // Recorremos la lista de IDs que llegó del formulario
            if (idsPrivilegios != null && !idsPrivilegios.isEmpty()) {

                String sqlInsert = "INSERT INTO roles_privilegios "
                                 + "(id_rol, id_privilegio) VALUES (?, ?)";
                ps = conexion.prepareStatement(sqlInsert);

                for (int idPrivilegio : idsPrivilegios) {
                    // Para cada privilegio seleccionado creamos un registro en la tabla puente
                    ps.setInt(1, idRol);
                    ps.setInt(2, idPrivilegio);
                    ps.executeUpdate();
                }
            }

            // Confirmamos la transacción: ambos pasos se guardan juntos
            conexion.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("✘ Error al actualizar privilegios del rol: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conexion != null) conexion.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;

        } finally {
            try {
                if (ps != null) ps.close();
                if (conexion != null) conexion.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Conexion.cerrarConexion(conexion);
        }
    }
}