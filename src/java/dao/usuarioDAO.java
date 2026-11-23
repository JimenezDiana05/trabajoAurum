/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author jimen
 */
import conexion.conexion;
import modelo.usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class usuarioDAO {

    // INSERTAR un registro. 
    public boolean insertar(usuario objeto) {
        String sql = "INSERT INTO usuario (fecha_creacion, contrasena, id_rol, id_cliente, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(objeto.getFecha_creacion()));
                ps.setString(2, objeto.getContrasena());
                ps.setInt(3, objeto.getId_rol());
                ps.setInt(4, objeto.getId_cliente());
                ps.setString(5, objeto.getEstado());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar en usuario: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<usuario> listar() {
        List<usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY id_usuario";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuario: " + e.getMessage());
        }
        return lista;
    }

    // BUSCAR un registro por su ID.
    public usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar en usuario: " + e.getMessage());
        }
        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(usuario objeto) {
        String sql = "UPDATE usuario SET fecha_creacion = ?, contrasena = ?, id_rol = ?, id_cliente = ?, estado = ? WHERE id_usuario = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(objeto.getFecha_creacion()));
                ps.setString(2, objeto.getContrasena());
                ps.setInt(3, objeto.getId_rol());
                ps.setInt(4, objeto.getId_cliente());
                ps.setString(5, objeto.getEstado());
                ps.setInt(6, objeto.getId_usuario());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar de usuario: " + e.getMessage());
            return false;
        }
    }
    // BUSCAR USUARIO POR CORREO ELECTRONICO
public usuario buscarPorCorreo(String correo) {
    String sql = "SELECT u.* "
            + "FROM usuario u "
            + "INNER JOIN cliente c ON u.id_cliente = c.id_cliente "
            + "WHERE LOWER(TRIM(c.correo)) = LOWER(TRIM(?))";

    try (Connection con = conexion.conectar()) {
        if (con == null) {
            return null;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al buscar usuario por correo: " + e.getMessage());
    }

    return null;
}

    private usuario mapear(ResultSet rs) throws SQLException {
        usuario objeto = new usuario();
        objeto.setId_usuario(rs.getInt("id_usuario"));
        Timestamp ts_fecha_creacion = rs.getTimestamp("fecha_creacion");
        objeto.setFecha_creacion(ts_fecha_creacion == null ? null : ts_fecha_creacion.toLocalDateTime());
        objeto.setContrasena(rs.getString("contrasena"));
        objeto.setId_rol(rs.getInt("id_rol"));
        objeto.setId_cliente(rs.getInt("id_cliente"));
        objeto.setEstado(rs.getString("estado"));
        return objeto;
    }
}
