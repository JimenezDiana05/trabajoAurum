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

    private static final String URL =
            "jdbc:postgresql://localhost:5434/PwJoyeriaAurum";

    private static final String USUARIO = "postgres";

    
    private static final String CONTRASENA = "2005";

    private static final HikariDataSource DATA_SOURCE = crearDataSource();
        
    private static HikariDataSource crearDataSource() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(URL);
        config.setUsername(USUARIO);
        config.setPassword(CONTRASENA);
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
