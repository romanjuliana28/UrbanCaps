//  ARCHIVO   : TestConexion.java
//  PAQUETE   : com.urbancaps.util
//  DESCRIPCIÓN: Clase TEMPORAL para verificar que la conexión a la base de datos MySQL funciona correctamente. Una vez confirmada la conexión este archivo puede eliminarse del proyecto.

// Este archivo vive en el mismo paquete que Conexion.java
package com.urbancaps.util;

// Importamos Connection para poder recibir y verificar el objeto que nos devuelve Conexion.getConexion()
import java.sql.Connection;

// Clase de prueba con método main para ejecutarla directamente desde NetBeans sin necesitar un servidor web.
public class TestConexion {

    // El método main es el punto de entrada cuando ejecutamos esta clase directamente. NetBeans lo ejecutará al hacer/ clic derecho sobre el archivo → Run File.
    public static void main(String[] args) {

        // Mostramos un mensaje inicial para saber que el programa arrancó
        System.out.println("===========================================");
        System.out.println("  PRUEBA DE CONEXIÓN - URBAN CAPS");
        System.out.println("===========================================");
        System.out.println("Intentando conectar a la base de datos...");
        System.out.println();

        // Llamamos al método getConexion() de nuestra clase Conexion.
        // Este método intentará conectarse a MySQL y nos devolverá:
        // - Un objeto Connection activo si la conexión fue exitosa
        // - null si algo salió mal
        Connection conexion = Conexion.getConexion();

        // Verificamos si la conexión fue exitosa o no
        if (conexion != null) {

            //  La conexión funcionó correctamente
            System.out.println();
            System.out.println("===========================================");
            System.out.println("  RESULTADO: CONEXIÓN EXITOSA ✔");
            System.out.println("===========================================");
            System.out.println("  Base de datos : urban_caps");
            System.out.println("  Servidor      : localhost:3306");
            System.out.println("  Estado        : Conectado correctamente");
            System.out.println("===========================================");
            System.out.println("  El proyecto puede comunicarse con MySQL.");
            System.out.println("  Puedes continuar con el desarrollo.");
            System.out.println("===========================================");

            // Cerramos la conexión porque ya no la necesitamos.
            // Siempre hay que cerrar las conexiones que se abren.
            Conexion.cerrarConexion(conexion);

        } else {

            //  La conexión falló — mostramos guía para solucionar el problema
            System.out.println();
            System.out.println("===========================================");
            System.out.println("  RESULTADO: CONEXIÓN FALLIDA ✘");
            System.out.println("===========================================");
            System.out.println("  Posibles causas:");
            System.out.println("  1. MySQL no está corriendo en XAMPP");
            System.out.println("     → Abre XAMPP y presiona Start en MySQL");
            System.out.println();
            System.out.println("  2. La base de datos no existe");
            System.out.println("     → Ejecuta el script SQL en PHPMyAdmin");
            System.out.println();
            System.out.println("  3. Usuario o contraseña incorrectos");
            System.out.println("     → Revisa USUARIO y PASSWORD en Conexion.java");
            System.out.println();
            System.out.println("  4. El archivo mysql-connector-j.jar");
            System.out.println("     no está bien agregado en Libraries");
            System.out.println("===========================================");
        }
    }
}
