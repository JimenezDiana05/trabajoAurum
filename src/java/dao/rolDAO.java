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
import modelo.rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class rolDAO {

    // INSERTAR un registro. El ID se genera automaticamente en PostgreSQL.
    public boolean insertar(rol objeto) {
        String sql = "INSERT INTO rol (tipo, permisos) VALUES (?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, objeto.getTipo());
                ps.setString(2, objeto.getPermisos());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar en rol: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<rol> listar() {
        List<rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM rol ORDER BY id_rol";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar rol: " + e.getMessage());
        }
        return lista;
    }

    // BUSCAR un registro por su ID.
    public rol buscarPorId(int id) {
        String sql = "SELECT * FROM rol WHERE id_rol = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar en rol: " + e.getMessage());
        }
        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(rol objeto) {
        String sql = "UPDATE rol SET tipo = ?, permisos = ? WHERE id_rol = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, objeto.getTipo());
                ps.setString(2, objeto.getPermisos());
                ps.setInt(3, objeto.getId_rol());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar rol: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM rol WHERE id_rol = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar de rol: " + e.getMessage());
            return false;
        }
    }

    private rol mapear(ResultSet rs) throws SQLException {
        rol objeto = new rol();
        objeto.setId_rol(rs.getInt("id_rol"));
        objeto.setTipo(rs.getString("tipo"));
        objeto.setPermisos(rs.getString("permisos"));
        return objeto;
    }
}