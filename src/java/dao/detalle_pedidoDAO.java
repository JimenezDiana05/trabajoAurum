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
import modelo.detalle_pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class detalle_pedidoDAO {

    // INSERTAR un registro. El ID se genera automaticamente en PostgreSQL.
    public boolean insertar(detalle_pedido objeto) {
        String sql = "INSERT INTO detalle_pedido (id_pedido, id_producto, precio) VALUES (?, ?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_pedido());
                ps.setInt(2, objeto.getId_producto());
                ps.setBigDecimal(3, objeto.getPrecio());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar en detalle_pedido: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<detalle_pedido> listar() {
        List<detalle_pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_pedido ORDER BY id_detalle";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar detalle_pedido: " + e.getMessage());
        }
        return lista;
    }

    // BUSCAR un registro por su ID.
    public detalle_pedido buscarPorId(int id) {
        String sql = "SELECT * FROM detalle_pedido WHERE id_detalle = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar en detalle_pedido: " + e.getMessage());
        }
        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(detalle_pedido objeto) {
        String sql = "UPDATE detalle_pedido SET id_pedido = ?, id_producto = ?, precio = ? WHERE id_detalle = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_pedido());
                ps.setInt(2, objeto.getId_producto());
                ps.setBigDecimal(3, objeto.getPrecio());
                ps.setInt(4, objeto.getId_detalle());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar detalle_pedido: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM detalle_pedido WHERE id_detalle = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar de detalle_pedido: " + e.getMessage());
            return false;
        }
    }

    private detalle_pedido mapear(ResultSet rs) throws SQLException {
        detalle_pedido objeto = new detalle_pedido();
        objeto.setId_detalle(rs.getInt("id_detalle"));
        objeto.setId_pedido(rs.getInt("id_pedido"));
        objeto.setId_producto(rs.getInt("id_producto"));
        objeto.setPrecio(rs.getBigDecimal("precio"));
        return objeto;
    }
}