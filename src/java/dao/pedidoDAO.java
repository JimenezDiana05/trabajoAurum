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
import modelo.pedido;
import Componentes.Encriptacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class pedidoDAO {

    // INSERTAR un registro.
    public boolean insertar(pedido objeto) {
        String sql = "INSERT INTO pedido (id_cliente, id_direccion, fecha_pedido, total) VALUES (?, ?, ?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_cliente());
                ps.setInt(2, objeto.getId_direccion());
                ps.setTimestamp(3, Timestamp.valueOf(objeto.getFecha_pedido()));

                // Encriptar el total antes de guardarlo.
                ps.setString(4, Encriptacion.encriptar(objeto.getTotal()));

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar en pedido: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<pedido> listar() {
        List<pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido ORDER BY id_pedido";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pedido: " + e.getMessage());
        }

        return lista;
    }

    // BUSCAR un registro por su ID.
    public pedido buscarPorId(int id) {
        String sql = "SELECT * FROM pedido WHERE id_pedido = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar en pedido: " + e.getMessage());
        }

        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(pedido objeto) {
        String sql = "UPDATE pedido SET id_cliente = ?, id_direccion = ?, fecha_pedido = ?, total = ? WHERE id_pedido = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_cliente());
                ps.setInt(2, objeto.getId_direccion());
                ps.setTimestamp(3, Timestamp.valueOf(objeto.getFecha_pedido()));

                // Encriptar el nuevo total antes de actualizarlo.
                ps.setString(4, Encriptacion.encriptar(objeto.getTotal()));

                ps.setInt(5, objeto.getId_pedido());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar pedido: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM pedido WHERE id_pedido = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar de pedido: " + e.getMessage());
            return false;
        }
    }

    // MAPEAR y desencriptar el total recuperado de la base de datos.
    private pedido mapear(ResultSet rs) throws SQLException {
        pedido objeto = new pedido();

        objeto.setId_pedido(rs.getInt("id_pedido"));
        objeto.setId_cliente(rs.getInt("id_cliente"));
        objeto.setId_direccion(rs.getInt("id_direccion"));

        Timestamp ts_fecha_pedido = rs.getTimestamp("fecha_pedido");
        objeto.setFecha_pedido(
            ts_fecha_pedido == null ? null : ts_fecha_pedido.toLocalDateTime()
        );

        // Recuperar el total original.
        objeto.setTotal(
            Encriptacion.desencriptar(rs.getString("total"))
        );

        return objeto;
    }
}

