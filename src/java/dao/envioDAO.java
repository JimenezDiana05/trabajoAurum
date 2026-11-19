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
import modelo.envio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class envioDAO {

    // INSERTAR un registro. El ID se genera automaticamente en PostgreSQL.
    public boolean insertar(envio objeto) {
        String sql = "INSERT INTO envio (id_pedido, id_direccion, fecha_envio, empresa, num_pedido, fecha_entrega) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_pedido());
                ps.setInt(2, objeto.getId_direccion());
                ps.setTimestamp(3, objeto.getFecha_envio() == null ? null : Timestamp.valueOf(objeto.getFecha_envio()));
                ps.setString(4, objeto.getEmpresa());
                ps.setString(5, objeto.getNum_pedido());
                ps.setTimestamp(6, objeto.getFecha_entrega() == null ? null : Timestamp.valueOf(objeto.getFecha_entrega()));
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar en envio: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<envio> listar() {
        List<envio> lista = new ArrayList<>();
        String sql = "SELECT * FROM envio ORDER BY id_envio";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar envio: " + e.getMessage());
        }
        return lista;
    }

    // BUSCAR un registro por su ID.
    public envio buscarPorId(int id) {
        String sql = "SELECT * FROM envio WHERE id_envio = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar en envio: " + e.getMessage());
        }
        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(envio objeto) {
        String sql = "UPDATE envio SET id_pedido = ?, id_direccion = ?, fecha_envio = ?, empresa = ?, num_pedido = ?, fecha_entrega = ? WHERE id_envio = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_pedido());
                ps.setInt(2, objeto.getId_direccion());
                ps.setTimestamp(3, objeto.getFecha_envio() == null ? null : Timestamp.valueOf(objeto.getFecha_envio()));
                ps.setString(4, objeto.getEmpresa());
                ps.setString(5, objeto.getNum_pedido());
                ps.setTimestamp(6, objeto.getFecha_entrega() == null ? null : Timestamp.valueOf(objeto.getFecha_entrega()));
                ps.setInt(7, objeto.getId_envio());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar envio: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM envio WHERE id_envio = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar de envio: " + e.getMessage());
            return false;
        }
    }

    private envio mapear(ResultSet rs) throws SQLException {
        envio objeto = new envio();
        objeto.setId_envio(rs.getInt("id_envio"));
        objeto.setId_pedido(rs.getInt("id_pedido"));
        objeto.setId_direccion(rs.getInt("id_direccion"));
        Timestamp ts_fecha_envio = rs.getTimestamp("fecha_envio");
        objeto.setFecha_envio(ts_fecha_envio == null ? null : ts_fecha_envio.toLocalDateTime());
        objeto.setEmpresa(rs.getString("empresa"));
        objeto.setNum_pedido(rs.getString("num_pedido"));
        Timestamp ts_fecha_entrega = rs.getTimestamp("fecha_entrega");
        objeto.setFecha_entrega(ts_fecha_entrega == null ? null : ts_fecha_entrega.toLocalDateTime());
        return objeto;
    }
}