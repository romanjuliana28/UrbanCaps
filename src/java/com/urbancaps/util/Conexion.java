package com.urbancaps.util;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;

public class Conexion {

    private static final String URL =
        "jdbc:mysql://localhost:3306/urban_caps"
        + "?useSSL=false"
        + "&serverTimezone=UTC"
        + "&allowPublicKeyRetrieval=true";

    private static final String USUARIO = "root";

    private static final String PASSWORD = "";

    public static Connection getConexion() {

        Connection conexion = null;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);

            System.out.println("✔ Conexión exitosa a la base de datos urban_caps");

        } catch (ClassNotFoundException e) {

            System.out.println("✘ Error: Driver de MySQL no encontrado.");
            System.out.println("  Verifica que mysql-connector-j.jar esté en Libraries.");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("✘ Error al conectar con la base de datos.");
            System.out.println("  Verifica que XAMPP/MySQL esté corriendo.");
            System.out.println("  Mensaje del error: " + e.getMessage());
            e.printStackTrace();
        }

        return conexion;
    }

    public static void cerrarConexion(Connection conexion) {

        if (conexion != null) {
            try {

                conexion.close();

                System.out.println("✔ Conexión cerrada correctamente.");

            } catch (SQLException e) {

                System.out.println("✘ Error al cerrar la conexión.");
                e.printStackTrace();
            }
        }
    }
}