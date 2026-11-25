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
import modelo.pago;
import Componentes.Encriptacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class pagoDAO {

    // INSERTAR un registro.
    public boolean insertar(pago objeto) {
        String sql = "INSERT INTO pago (id_pedido, fecha_pago, monto, tipo) VALUES (?, ?, ?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_pedido());
                ps.setTimestamp(2, Timestamp.valueOf(objeto.getFecha_pago()));

                // Encriptar el monto antes de guardarlo.
                ps.setString(3, Encriptacion.encriptar(objeto.getMonto()));

                ps.setString(4, objeto.getTipo());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar en pago: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<pago> listar() {
        List<pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pago ORDER BY id_pago";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pago: " + e.getMessage());
        }

        return lista;
    }

    // BUSCAR un registro por su ID.
    public pago buscarPorId(int id) {
        String sql = "SELECT * FROM pago WHERE id_pago = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar en pago: " + e.getMessage());
        }

        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(pago objeto) {
        String sql = "UPDATE pago SET id_pedido = ?, fecha_pago = ?, monto = ?, tipo = ? WHERE id_pago = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_pedido());
                ps.setTimestamp(2, Timestamp.valueOf(objeto.getFecha_pago()));

                // Encriptar el nuevo monto antes de actualizarlo.
                ps.setString(3, Encriptacion.encriptar(objeto.getMonto()));

                ps.setString(4, objeto.getTipo());
                ps.setInt(5, objeto.getId_pago());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar pago: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM pago WHERE id_pago = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar de pago: " + e.getMessage());
            return false;
        }
    }

    // MAPEAR y desencriptar el monto recuperado de la base de datos.
    private pago mapear(ResultSet rs) throws SQLException {
        pago objeto = new pago();

        objeto.setId_pago(rs.getInt("id_pago"));
        objeto.setId_pedido(rs.getInt("id_pedido"));

        Timestamp ts_fecha_pago = rs.getTimestamp("fecha_pago");
        objeto.setFecha_pago(
            ts_fecha_pago == null ? null : ts_fecha_pago.toLocalDateTime()
        );

        // Recuperar el monto original.
        objeto.setMonto(
            Encriptacion.desencriptar(rs.getString("monto"))
        );

        objeto.setTipo(rs.getString("tipo"));

        return objeto;
    }
}

