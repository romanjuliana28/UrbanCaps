//  PROYECTO  : Urban Caps
//  ARCHIVO   : RolDAO.java
//  PAQUETE   : com.urbancaps.modelo.dao
//  DESCRIPCIÓN: La clase DAOes la que gestiona todas las operaciones de base de datos para la tabla 'roles'. Aquí viven todos los
//SELECT, INSERT, UPDATE de roles.
//
//  CRUD Rol:
//  listar()        : obtiene todos los roles
//  obtenerPorId()  : obtiene un rol por su ID
//  insertar()      : crea un nuevo rol
//  actualizar()    : modifica un rol existente
//  eliminar()      : desactiva un rol (no borra físicamente)

// Paquete donde vive esta clase
package com.urbancaps.modelo.dao;

// Importa la clase Conexion del paquete util para poder abrir y cerrar conexiones a la base de datos
import com.urbancaps.util.Conexion;

// Importa la clase Rol del paquete bean porque los métodos trabajan con objetos Rol
import com.urbancaps.modelo.bean.Rol;

// Importo las clases JDBC para comunicarme con MySQL
import java.sql.Connection;    // Representa la conexión activa a MySQL
import java.sql.PreparedStatement; // Permite ejecutar consultas SQL seguras
import java.sql.ResultSet;     // Almacena los resultados de un SELECT
import java.sql.SQLException;  // Maneja errores de base de datos

// Importo las clases ArrayList y List para devolver listas de usuarios
import java.util.ArrayList;
import java.util.List;

// Declara la clase pública
public class RolDAO {

    // MÉTODO 1: listar()
    // Aqui obtengo todos los Roles de la base de datos.
    // Se usa para mostrar la tabla de roles en el panel admin.
    // Retorna: List<Rol> que es igual a una lista con todos los objetos Rol, si no hay roles retorna una lista vacía (nunca null)
    public List<Rol> listar() {

        // Crea la lista vacía que iremos llenando con los roles
        List<Rol> lista = new ArrayList<>();

        // Escribe la consulta SQL que queremos ejecutar, traemos todos los roles ordenados por id_rol
        String sql = "SELECT id_rol, nombre_rol, descripcion, estado "
                   + "FROM roles "
                   + "ORDER BY id_rol ASC";
        
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Abre la conexión a la base de datos usando la clase utilitaria Conexion
            conexion = Conexion.getConexion();

            // PreparedStatement prepara la consulta SQL para ejecutars, es más seguro que Statement porque evita inyección SQL.
            ps = conexion.prepareStatement(sql);

            // Ejecuta el select y guarda los resultados en rs.
            // rs funciona como un cursor que apunta fila por fila
            rs = ps.executeQuery();

            // Recorre cada fila del resultado con el while y rs.next() avanza al siguiente registro y retorna true si existe, false si ya no hay más filas
            while (rs.next()) {

                // Por cada fila crea un objeto Rol vacío
                Rol rol = new Rol();

                // Llena el objeto con los datos de la fila actual.
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setEstado(rs.getInt("estado"));

                // Agrega el objeto Rol a la lista
                lista.add(rol);
            }

        } catch (SQLException e) {
            // Si algo falla con la consulta SQL muestra el error
            System.out.println("✘ Error al listar roles: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // El bloque finally siempre se ejecuta, haya error o no.
            // Es donde cerramos los recursos para no dejar conexiones abiertas que consuman memoria del servidor.
            try {
                if (rs != null) rs.close();       
                if (ps != null) ps.close();       
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Cerramos la conexión usando el método utilitario
            Conexion.cerrarConexion(conexion);
        }

        // Retorna la lista (puede estar vacía pero nunca es null)
        return lista;
    }

    // MÉTODO 2: obtenerPorId()
    // Busca y retorna un solo rol por su ID, lo uso al cargar los datos en el formulario de edición.
    // Parámetro: idRol ID del usuario a buscar
    // Retorna  : objeto Rol null si no existe
    public Rol obtenerPorId(int idRol) {

        // Inicializa el rol como null, si no encontramos el registro retornara null.
        Rol rol = null;

        // Consulta SQL con ? como parámetro seguro.
        // El ? se reemplazará con el valor real usando PreparedStatement, lo que evita inyección SQL (alguien que intente hackear el sistema).
        String sql = "SELECT id_rol, nombre_rol, descripcion, estado "
                   + "FROM roles "
                   + "WHERE id_rol = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setInt(1, idRol);

            rs = ps.executeQuery();

            // Si rs.next() retorna true significa que encontramos el rol
            if (rs.next()) {
                rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al obtener rol por ID: " + e.getMessage());
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

        return rol;
    }

    // MÉTODO 3: insertar()
    // Crea un nuevo rol en la base de datos.
    // Dato a tener en cuenta: la contraseña se encripta con BCrypt antes de guardarla. Nunca se guarda en texto plano.
    // Parámetro: rol apunta a objeto con los datos del formulario
    // Retorna: true si se insertó correctamente  false si falló
    public boolean insertar(Rol rol) {

        // No incluimos ni el id_rol debido a que MySQL lo genera automaticamente a medida que vamos creando roles.
        String sql = "INSERT INTO roles (nombre_rol, descripcion, estado) "
                   + "VALUES (?, ?, ?)";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setString(1, rol.getNombreRol());
            ps.setString(2, rol.getDescripcion());
            ps.setInt(3, rol.getEstado());

            // executeUpdate() ejecuta INSERT, UPDATE o DELETE, retorna el número de filas afectadas.
            // Si es mayor a 0 significa que la operación fue exitosa.
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al insertar rol: " + e.getMessage());
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

    // Modifica los datos de un rol existente en la base de datos.
    // Se usa cuando el administrador edita un rol y guarda cambios.
    // Parámetro: rol - objeto Rol con los datos actualizados
    // Retorna  : true si se actualizó correctamente | false si falló

    public boolean actualizar(Rol rol) {

        // Consulta UPDATE que modifica los campos del rol  el WHERE id_rol = ? asegura que solo se modifica el rol correcto y no todos los registros de la tabla.
        String sql = "UPDATE roles "
                   + "SET nombre_rol = ?, descripcion = ?, estado = ? "
                   + "WHERE id_rol = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            // Asignamos los valores en el mismo orden del SQL:
            // ? 1 - nombre_rol (nuevo valor)
            // ? 2 - descripcion (nuevo valor)
            // ? 3 - estado (nuevo valor)
            // ? 4 - id_rol (para el WHERE, identifica qué fila actualizar)
            ps.setString(1, rol.getNombreRol());
            ps.setString(2, rol.getDescripcion());
            ps.setInt(3, rol.getEstado());
            ps.setInt(4, rol.getIdRol());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al actualizar rol: " + e.getMessage());
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
    // Se desactiva un rol cambiando su estado a 0 (Inactivo), no se borra físicamente para conservar el historial.
    // Un rol inactivo no puede iniciar sesión en el sistema.
    // Parámetro: idRol es igual al ID del rol a desactivar en Base de Datos
    // Retorna: true si se desactivó, false si falló


    public boolean eliminar(int idRol) {

        // Hacemos UPDATE del estado a 0 en lugar de DELETE esto preserva la integridad de los datos: si un usuario tiene este rol asignado, no perdemos esa información.
        String sql = "UPDATE roles SET estado = 0 WHERE id_rol = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            // Asignamos el ID del rol a desactivar
            ps.setInt(1, idRol);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al eliminar rol: " + e.getMessage());
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