//  PROYECTO: Urban Caps
//  ARCHIVO: UsuarioDAO.java
//  PAQUETE : com.urbancaps.modelo.dao
//  DESCRIPCIÓN: Clase DAO que gestiona todas las operaciones de base de datos para la tabla 'usuarios'.
//  También maneja la autenticación del login.

//  CRUD Usuarios:
//  listar() : obtiene todos los usuarios
//  obtenerPorId() : obtiene un usuario por su ID
//  obtenerPorEmail() : busca un usuario por su email (LOGIN)
//  insertar() : crea un nuevo usuario
//  actualizar(): modifica un usuario existente
//  eliminar(): desactiva un usuario
//  emailExiste() : verifica si un email ya está registrado

// Paquete donde vive esta clase
package com.urbancaps.modelo.dao;

// Importo Conexion para manejar la conexión a la BD
import com.urbancaps.util.Conexion;

// Importo la clase Encriptador para cifrar contraseñas al crear usuarios
import com.urbancaps.util.Encriptador;

// Importo la clase Usuario Bean porque los métodos trabajan con esos objetos
import com.urbancaps.modelo.bean.Usuario;

// Importo las clases JDBC para comunicarme con MySQL
import java.sql.Connection;    // Representa la conexión activa a MySQL
import java.sql.PreparedStatement; // Permite ejecutar consultas SQL seguras
import java.sql.ResultSet;     // Almacena los resultados de un SELECT
import java.sql.SQLException;  // Maneja errores de base de datos

// Importo la clase Timestamp para manejar fechas que vienen de MySQL
import java.sql.Timestamp;

// Importo las clases ArrayList y List para devolver listas de usuarios
import java.util.ArrayList;
import java.util.List;

// Declara la clase pública
public class UsuarioDAO {

    // MÉTODO 1: listar()
    // Aqui obtengo todos los Usuarios de la base de datos.
    // Usando un JOIN con la tabla roles para traer el nombre del rol en lugar del número id_rol.
    // Retorna una lista List<Usuario> con todos los usuarios registrados
    
    public List<Usuario> listar() {

        List<Usuario> lista = new ArrayList<>();

        // Uso JOIN para traer el nombre del rol en la misma consulta, así evito hacer una segunda consulta para cada usuario.
        // u.*  trae todos los campos de la tabla usuarios
        // r.nombre_rol trae el nombre del rol de la tabla roles
        
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.email, "
                   + "u.password, u.id_rol, r.nombre_rol, u.estado, "
                   + "u.fecha_registro "
                   + "FROM usuarios u "
                   + "INNER JOIN roles r ON u.id_rol = r.id_rol "
                   + "ORDER BY u.id_usuario ASC";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setIdRol(rs.getInt("id_rol"));
                u.setNombreRol(rs.getString("nombre_rol"));
                u.setEstado(rs.getInt("estado"));

                // Convierte el Timestamp de MySQL a java.util.Date
                // getTimestamp() lee columnas de tipo DATETIME de MySQL
                
                Timestamp ts = rs.getTimestamp("fecha_registro");
                if (ts != null) {
                    u.setFechaRegistro(new java.util.Date(ts.getTime()));
                }

                lista.add(u);
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al listar usuarios: " + e.getMessage());
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
    // Busca y retorna un solo usuario por su ID, lo uso al cargar los datos en el formulario de edición.
    // Parámetro: idUsuario  ID del usuario a buscar
    // Retorna  : objeto Usuario null si no existe

    public Usuario obtenerPorId(int idUsuario) {
        
        Usuario u = null;
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.email, "
                   + "u.password, u.id_rol, r.nombre_rol, u.estado, "
                   + "u.fecha_registro "
                   + "FROM usuarios u "
                   + "INNER JOIN roles r ON u.id_rol = r.id_rol "
                   + "WHERE u.id_usuario = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setIdRol(rs.getInt("id_rol"));
                u.setNombreRol(rs.getString("nombre_rol"));
                u.setEstado(rs.getInt("estado"));

                Timestamp ts = rs.getTimestamp("fecha_registro");
                if (ts != null) {
                    u.setFechaRegistro(new java.util.Date(ts.getTime()));
                }
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al obtener usuario por ID: " + e.getMessage());
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

        return u;
    }

    // MÉTODO 3: obtenerPorEmail()
    // Busca un usuario por su correo electrónico.
    // Este es el método más importante para el LOGIN:
    // cuando alguien intenta entrar al sistema, buscamos su usuario por email y luego verificamos su contraseña con el Encriptador.
    // Parámetro: email → correo que escribió el usuario en el login
    // Retorna  : objeto Usuario si existe | null si no se encontró
    public Usuario obtenerPorEmail(String email) {

        Usuario u = null;
        
        // Se busca por email en la tabla usuarios, también traemos el nombre del rol con JOIN
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.email, "
                   + "u.password, u.id_rol, r.nombre_rol, u.estado, "
                   + "u.fecha_registro "
                   + "FROM usuarios u "
                   + "INNER JOIN roles r ON u.id_rol = r.id_rol "
                   + "WHERE u.email = ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            // Se asigna el email como parámetro de búsqueda
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setEmail(rs.getString("email"));

                // Guarda el hash BCrypt de la contraseña, el LoginServlet lo usará para verificar con Encriptador
                u.setPassword(rs.getString("password"));
                u.setIdRol(rs.getInt("id_rol"));
                u.setNombreRol(rs.getString("nombre_rol"));
                u.setEstado(rs.getInt("estado"));

                Timestamp ts = rs.getTimestamp("fecha_registro");
                if (ts != null) {
                    u.setFechaRegistro(new java.util.Date(ts.getTime()));
                }
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al obtener usuario por email: " + e.getMessage());
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

        return u;
    }
    
    // MÉTODO 4: insertar()
    // Crea un nuevo usuario en la base de datos.
    // Dato a tener en cuenta: la contraseña se encripta con BCrypt antes de guardarla. Nunca se guarda en texto plano.
    // Parámetro: usuario apunta a objeto con los datos del formulario
    // Retorna: true si se insertó correctamente  false si falló
    public boolean insertar(Usuario usuario) {

        // No incluimos ni el id_usuario ni la fecha_registro debido a que MySQL los genera automaticamente a medida que vamos creando usuarios.
        String sql = "INSERT INTO usuarios "
                   + "(nombre, apellido, email, password, id_rol, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getEmail());

            // Encripta la contraseña antes de guardarla en la Bases de Datos.
            String hashPassword = Encriptador.cifrar(usuario.getPassword());
            ps.setString(4, hashPassword);

            ps.setInt(5, usuario.getIdRol());
            ps.setInt(6, usuario.getEstado());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al insertar usuario: " + e.getMessage());
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
    // Modifica los datos de un usuario existente.
    // Si el campo password viene vacío significa que el admin no quiere cambiar la contraseña, así que la dejamos igual.
    // Si viene con valor, la encriptamos y la actualizamos.
    // Parámetro: usuario apunta al objeto con los datos actualizados
    public boolean actualizar(Usuario usuario) {

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();

            // Verificamos si el admin quiere cambiar la contraseña o no.
            // Si el password llega vacío  no actualizamos la contraseña.
            // Si el password llega con valor  la encriptamos y actualizamos.
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {

                // Actualización sin cambiar contraseña
                String sql = "UPDATE usuarios "
                           + "SET nombre = ?, apellido = ?, email = ?, "
                           + "id_rol = ?, estado = ? "
                           + "WHERE id_usuario = ?";

                ps = conexion.prepareStatement(sql);
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getApellido());
                ps.setString(3, usuario.getEmail());
                ps.setInt(4, usuario.getIdRol());
                ps.setInt(5, usuario.getEstado());
                ps.setInt(6, usuario.getIdUsuario());

            } else {

                // Actualización con cambio de contraseña
                // Encriptamos la nueva contraseña antes de guardarla
                String sql = "UPDATE usuarios "
                           + "SET nombre = ?, apellido = ?, email = ?, "
                           + "password = ?, id_rol = ?, estado = ? "
                           + "WHERE id_usuario = ?";

                ps = conexion.prepareStatement(sql);
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getApellido());
                ps.setString(3, usuario.getEmail());
                ps.setString(4, Encriptador.cifrar(usuario.getPassword()));
                ps.setInt(5, usuario.getIdRol());
                ps.setInt(6, usuario.getEstado());
                ps.setInt(7, usuario.getIdUsuario());
            }

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al actualizar usuario: " + e.getMessage());
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
    // Se desactiva un usuario cambiando su estado a 0 (Inactivo), no se borra físicamente para conservar el historial.
    // Un usuario inactivo no puede iniciar sesión en el sistema.
    // Parámetro: idUsuario es igual al ID del usuario a desactivar en Base de Datos
    // Retorna: true si se desactivó, false si falló
    public boolean eliminar(int idUsuario) {

        // Borrado lógico: estado = 0 en lugar de DELETE
        String sql = "UPDATE usuarios SET estado = 0 "
                   + "WHERE id_usuario = ?";

        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("✘ Error al eliminar usuario: " + e.getMessage());
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

    // MÉTODO 7: emailExiste()
    // Verifica si un email ya está registrado en la base de datos.
    // Se usa al crear o editar un usuario para evitar duplicados, ya que el email debe ser único en todo el sistema.
    // Parámetro email es igual a el email a verificar
    // Parámetro idUsuario es igual ID del usuario actual (al editar)
    // Retorna: true si el email YA existe, false si está disponible
    public boolean emailExiste(String email, int idUsuario) {

        // Busca si existe algún usuario con ese email que sea diferente al usuario que estamos editando.
        // Esto permite que al editar un usuario pueda conservar su propio email sin que marque error.
        String sql = "SELECT COUNT(*) FROM usuarios "
                   + "WHERE email = ? AND id_usuario != ?";

        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = Conexion.getConexion();
            ps = conexion.prepareStatement(sql);
            ps.setString(1, email);

            ps.setInt(2, idUsuario);

            rs = ps.executeQuery();

            if (rs.next()) {
                
                // COUNT(*) retorna cuántos registros existen con ese email, si es mayor a 0 significa que el email ya está en uso.
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("✘ Error al verificar email: " + e.getMessage());
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

        return false;
    }
}