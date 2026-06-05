//  PROYECTO  : Urban Caps
//  ARCHIVO   : Usuario.java
//  PAQUETE   : com.urbancaps.modelo.bean
//  DESCRIPCIÓN: Clase bean que representa la tabla 'usuarios' de la base de datos. Cada objeto Usuario equivale a un registro de esa tabla.

//  TABLA EN BD:
//  usuarios (id_usuario, nombre, apellido, email, password, id_rol, estado, fecha_registro)

//  ¿Para qué sirve esta clase en el proyecto?
//  - Representa a cada persona que puede iniciar sesión en el panel de administración de Urban Caps.
//  - Se usa en el login para verificar credenciales.
//  - Se usa en el módulo de usuarios para el CRUD completo.
//  - El objeto Usuario se guarda en la sesión HTTP después del login para saber quién está conectado en todo momento.

// Indica en qué paquete vive esta clase dentro del proyecto
package com.urbancaps.modelo.bean;

// Importamos Serializable para poder guardar objetos Usuario en la sesión HTTP. Es obligatorio para objetos en sesión.
import java.io.Serializable;

// Importamos Date para manejar la fecha de registro del usuario.
// java.util.Date es la clase estándar de Java para fechas.
import java.util.Date;

// Declaramos la clase pública con el mismo nombre del archivo.
// Implementa Serializable para poder guardarse en sesión HTTP.
public class Usuario implements Serializable {

    // ATRIBUTOS DE LA CLASE

    // Cada atributo corresponde exactamente a una columna de la tabla 'usuarios' en la base de datos.
    // Se declaran 'private' para protegerlos mediante encapsulamiento.

    // Corresponde a la columna id_usuario (INT, PK, AUTO_INCREMENT)
    // Identificador único de cada usuario en la base de datos
    private int idUsuario;

    // Corresponde a la columna nombre (VARCHAR 100)
    // Nombre de pila del usuario: "Julia", "Carlos", etc.
    private String nombre;

    // Corresponde a la columna apellido (VARCHAR 100)
    // Apellido del usuario: "García", "Martínez", etc.
    private String apellido;

    // Corresponde a la columna email (VARCHAR 150)
    // Correo electrónico único del usuario.
    // Este campo se usa como nombre de usuario en el LOGIN.
    private String email;

    // Corresponde a la columna password (VARCHAR 255)
    // Contraseña del usuario SIEMPRE encriptada con BCrypt.
    // Nunca se guarda ni se muestra en texto plano.
    private String password;

    // Corresponde a la columna id_rol (INT, FK - roles)
    // Almacena el número del rol asignado al usuario.
    // 1 = Administrador | 2 = Empleado
    // A través de este número se consultan los privilegios.
    private int idRol;

    // Atributo adicional: nombre del rol para mostrar en pantalla.
    // Este campo NO existe en la tabla 'usuarios' de la BD.
    // Lo llenamos mediante un JOIN en el DAO para poder mostrar "Administrador" o "Empleado" en la interfaz sin hacer una segunda consulta separada.
    private String nombreRol;

    // Corresponde a la columna estado (TINYINT 1)
    // 1 = Activo (puede iniciar sesión)
    // 0 = Inactivo (no puede iniciar sesión)
    private int estado;

    // Corresponde a la columna fecha_registro (DATETIME)
    // Se guarda automáticamente cuando se crea el usuario.
    // Usamos java.util.Date porque es compatible con los tipos de fecha que devuelve MySQL en JDBC.
    private Date fechaRegistro;

    // CONSTRUCTORES

    // CONSTRUCTOR VACÍO (sin parámetros)
    // Lo usamos cuando creamos un objeto Usuario vacío y luego lo llenamos con los datos del formulario.
    // Ejemplo de uso: Usuario u = new Usuario();
    public Usuario() {
    }

    // CONSTRUCTOR COMPLETO (con todos los parámetros)
    // Lo usamos en el DAO cuando leemos un registro de la BD y queremos convertirlo directamente en un objeto Usuario.
 
    // Ejemplo de uso en el DAO del login:
    //   Usuario u = new Usuario(1, "Julia", "García", "julia@urbancaps.com", hashBCrypt, 1, "Administrador", 1, fechaRegistro);
    public Usuario(int idUsuario, String nombre, String apellido,
                   String email, String password, int idRol,
                   String nombreRol, int estado, Date fechaRegistro) {
        this.idUsuario     = idUsuario;
        this.nombre        = nombre;
        this.apellido      = apellido;
        this.email         = email;
        this.password      = password;
        this.idRol         = idRol;
        this.nombreRol     = nombreRol;
        this.estado        = estado;
        this.fechaRegistro = fechaRegistro;
    }

    // GETTERS Y SETTERS
    // Métodos para leer (GET) y escribir (SET) cada atributo.
    // Los Servlets y JSP acceden a los datos del usuario exclusivamente a través de estos métodos.

    // GETTER de idUsuario
    // Retorna el identificador único del usuario
    // Uso: int id = usuario.getIdUsuario();
    public int getIdUsuario() {
        return idUsuario;
    }

    // SETTER de idUsuario
    // Permite asignar el identificador del usuario
    // Uso: usuario.setIdUsuario(1);
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    // GETTER de nombre
    // Retorna el nombre del usuario
    // Uso: String nombre = usuario.getNombre();
    public String getNombre() {
        return nombre;
    }

    // SETTER de nombre
    // Permite asignar el nombre del usuario
    // Uso: usuario.setNombre("Julia");
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // GETTER de apellido
    // Retorna el apellido del usuario
    // Uso: String apellido = usuario.getApellido();
    public String getApellido() {
        return apellido;
    }

    // SETTER de apellido
    // Permite asignar el apellido del usuario
    // Uso: usuario.setApellido("García");
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    // GETTER de email
    // Retorna el correo electrónico del usuario
    // Se usa como identificador en el formulario de login
    // Uso: String email = usuario.getEmail();
    public String getEmail() {
        return email;
    }

    // SETTER de email
    // Permite asignar el correo del usuario
    // Uso: usuario.setEmail("julia@urbancaps.com");
    public void setEmail(String email) {
        this.email = email;
    }

    // GETTER de password
    // Retorna el hash BCrypt de la contraseña
    // Nunca retorna la contraseña en texto plano
    // Uso: String hash = usuario.getPassword();
    public String getPassword() {
        return password;
    }

    // SETTER de password
    // Permite asignar la contraseña (siempre ya encriptada)
    // Uso: usuario.setPassword(Encriptador.cifrar("clave123"));
    public void setPassword(String password) {
        this.password = password;
    }

    // GETTER de idRol
    // Retorna el número del rol del usuario
    // Uso: int rol = usuario.getIdRol();
    public int getIdRol() {
        return idRol;
    }

    // SETTER de idRol
    // Permite asignar el rol del usuario
    // Uso: usuario.setIdRol(1);
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    // GETTER de nombreRol
    // Retorna el nombre legible del rol para mostrar en pantalla
    // Este valor viene de un JOIN con la tabla roles en el DAO
    // Uso: String rol = usuario.getNombreRol();
    public String getNombreRol() {
        return nombreRol;
    }

    // SETTER de nombreRol
    // Permite asignar el nombre del rol
    // Uso: usuario.setNombreRol("Administrador");
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    // GETTER de estado
    // Retorna el estado del usuario (1 = Activo, 0 = Inactivo)
    // Uso: int estado = usuario.getEstado();
    public int getEstado() {
        return estado;
    }

    // SETTER de estado
    // Permite asignar el estado del usuario
    // Uso: usuario.setEstado(1);
    public void setEstado(int estado) {
        this.estado = estado;
    }

    // GETTER de fechaRegistro
    // Retorna la fecha en que se registró el usuario
    // Uso: Date fecha = usuario.getFechaRegistro();
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    // SETTER de fechaRegistro
    // Permite asignar la fecha de registro del usuario
    // Uso: usuario.setFechaRegistro(new Date());
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    // MÉTODO toString()

    // Permite imprimir un objeto Usuario con System.out.println() mostrando sus datos principales.
    // IMPORTANTE: nunca se imprime el password por seguridad.

    // Ejemplo:
    //   System.out.println(usuario);
    //   - Usuario{idUsuario=1, nombre='Julia', apellido='García', email='julia@urbancaps.com', rol='Administrador', estado=1}

    @Override
    public String toString() {
        return "Usuario{"
                + "idUsuario="    + idUsuario
                + ", nombre='"    + nombre    + '\''
                + ", apellido='"  + apellido  + '\''
                + ", email='"     + email     + '\''
                + ", nombreRol='" + nombreRol + '\''
                + ", estado="     + estado
                + '}';
        // Nota: el password no se incluye aquí por seguridad.
        // Nunca debemos imprimir contraseñas aunque estén cifradas.
    }
}
