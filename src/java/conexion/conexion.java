/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

/**
 *
 * @author jimen
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class conexion {

    private static final String URL =
            "jdbc:postgresql://localhost:5434/PwJoyeriaAurum";

    private static final String USUARIO = "postgres";
    private static final String CONTRASENA = "TU_CONTRASEÑA";

    public static Connection conectar() {

        try {
            Connection conexion = DriverManager.getConnection(
                    URL, USUARIO, CONTRASENA
            );

            System.out.println("Conexion exitosa a PostgreSQL");
            return conexion;

        } catch (SQLException e) {
            System.out.println("Error al conectar con PostgreSQL");
            e.printStackTrace();
            return null;
        }
    }
}