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
import modelo.categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class categoriaDAO {

    // INSERTAR un registro. El ID se genera automaticamente en PostgreSQL.
    public boolean insertar(categoria objeto) {
        String sql = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, objeto.getNombre());
                ps.setString(2, objeto.getDescripcion());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar en categoria: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<categoria> listar() {
        List<categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria ORDER BY id_categoria";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar categoria: " + e.getMessage());
        }
        return lista;
    }

    // BUSCAR un registro por su ID.
    public categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categoria WHERE id_categoria = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar en categoria: " + e.getMessage());
        }
        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(categoria objeto) {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id_categoria = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, objeto.getNombre());
                ps.setString(2, objeto.getDescripcion());
                ps.setInt(3, objeto.getId_categoria());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar categoria: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar de categoria: " + e.getMessage());
            return false;
        }
    }

    private categoria mapear(ResultSet rs) throws SQLException {
        categoria objeto = new categoria();
        objeto.setId_categoria(rs.getInt("id_categoria"));
        objeto.setNombre(rs.getString("nombre"));
        objeto.setDescripcion(rs.getString("descripcion"));
        return objeto;
    }
}
