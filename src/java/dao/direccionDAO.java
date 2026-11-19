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
import modelo.direccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class direccionDAO {

    // INSERTAR un registro. 
    public boolean insertar(direccion objeto) {
        String sql = "INSERT INTO direccion (id_cliente, calle, numero, colonia, cp, ciudad, estado, referencias) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_cliente());
                ps.setString(2, objeto.getCalle());
                ps.setString(3, objeto.getNumero());
                ps.setString(4, objeto.getColonia());
                ps.setString(5, objeto.getCp());
                ps.setString(6, objeto.getCiudad());
                ps.setString(7, objeto.getEstado());
                ps.setString(8, objeto.getReferencias());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar en direccion: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<direccion> listar() {
        List<direccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM direccion ORDER BY id_direccion";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar direccion: " + e.getMessage());
        }
        return lista;
    }

    // BUSCAR un registro por su ID.
    public direccion buscarPorId(int id) {
        String sql = "SELECT * FROM direccion WHERE id_direccion = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar en direccion: " + e.getMessage());
        }
        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(direccion objeto) {
        String sql = "UPDATE direccion SET id_cliente = ?, calle = ?, numero = ?, colonia = ?, cp = ?, ciudad = ?, estado = ?, referencias = ? WHERE id_direccion = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_cliente());
                ps.setString(2, objeto.getCalle());
                ps.setString(3, objeto.getNumero());
                ps.setString(4, objeto.getColonia());
                ps.setString(5, objeto.getCp());
                ps.setString(6, objeto.getCiudad());
                ps.setString(7, objeto.getEstado());
                ps.setString(8, objeto.getReferencias());
                ps.setInt(9, objeto.getId_direccion());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar direccion: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM direccion WHERE id_direccion = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar de direccion: " + e.getMessage());
            return false;
        }
    }

    private direccion mapear(ResultSet rs) throws SQLException {
        direccion objeto = new direccion();
        objeto.setId_direccion(rs.getInt("id_direccion"));
        objeto.setId_cliente(rs.getInt("id_cliente"));
        objeto.setCalle(rs.getString("calle"));
        objeto.setNumero(rs.getString("numero"));
        objeto.setColonia(rs.getString("colonia"));
        objeto.setCp(rs.getString("cp"));
        objeto.setCiudad(rs.getString("ciudad"));
        objeto.setEstado(rs.getString("estado"));
        objeto.setReferencias(rs.getString("referencias"));
        return objeto;
    }
}
