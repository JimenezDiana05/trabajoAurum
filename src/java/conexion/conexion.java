/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package conexion;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class conexion {

    // Cambiar a true para utilizar Render.
    // Cambiar a false para utilizar PostgreSQL local.
    private static final boolean USAR_RENDER = true;

    // Conexion local
    private static final String URL_LOCAL =
            "jdbc:postgresql://localhost:5434/PwJoyeriaAurum";

    private static final String USUARIO_LOCAL = "postgres";
    private static final String CONTRASENA_LOCAL = "2005";

    // Conexion Render
    private static final String URL_RENDER =
        "jdbc:postgresql://dpg-dc2vs6t9el5s73d0v9r0-a.oregon-postgres.render.com:5432/pwjoyeriaaurum?sslmode=require";

    private static final String USUARIO_RENDER = "aurumsys_app";

    // Coloca aquí la contraseña nueva de Render.
    private static final String CONTRASENA_RENDER = "QuJPGlHupTbWpuGTpHBT8ndfqHMEBCOG";

    private static final HikariDataSource DATA_SOURCE = crearDataSource();

    private static HikariDataSource crearDataSource() {

        HikariConfig config = new HikariConfig();

        if (USAR_RENDER) {
            config.setJdbcUrl(URL_RENDER);
            config.setUsername(USUARIO_RENDER);
            config.setPassword(CONTRASENA_RENDER);
        } else {
            config.setJdbcUrl(URL_LOCAL);
            config.setUsername(USUARIO_LOCAL);
            config.setPassword(CONTRASENA_LOCAL);
        }

        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setPoolName("AurumPool");

        return new HikariDataSource(config);
    }

    public static Connection conectar() {

        try {
            Connection conexion = DATA_SOURCE.getConnection();

            System.out.println("Conexion exitosa mediante HikariCP");

            return conexion;

        } catch (SQLException e) {

            System.out.println("Error al conectar con PostgreSQL");
            System.out.println("DETALLE: " + e.getMessage());

            e.printStackTrace();

            return null;
        }
    }
}

