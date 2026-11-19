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
import modelo.cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class clienteDAO {

    // REGISTRAR UN CLIENTE
    public boolean insertar(cliente c) {

        String sql = "INSERT INTO cliente "
                + "(nombre, apellido_paterno, apellido_materno, telefono, correo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido_paterno());
            ps.setString(3, c.getApellido_materno());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar cliente: " + e.getMessage());
            return false;
        }
    }

    // LISTAR TODOS LOS CLIENTES
    public List<cliente> listar() {

        List<cliente> lista = new ArrayList<>();

        String sql = "SELECT * FROM cliente ORDER BY id_cliente";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                cliente c = new cliente();

                c.setId_cliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido_paterno(rs.getString("apellido_paterno"));
                c.setApellido_materno(rs.getString("apellido_materno"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));

                lista.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }

        return lista;
    }

    // BUSCAR CLIENTE POR ID
    public cliente buscarPorId(int id) {

        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    cliente c = new cliente();

                    c.setId_cliente(rs.getInt("id_cliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellido_paterno(rs.getString("apellido_paterno"));
                    c.setApellido_materno(rs.getString("apellido_materno"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setCorreo(rs.getString("correo"));

                    return c;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar cliente: " + e.getMessage());
        }

        return null;
    }

    // ACTUALIZAR CLIENTE
    public boolean actualizar(cliente c) {

        String sql = "UPDATE cliente SET nombre = ?, "
                + "apellido_paterno = ?, apellido_materno = ?, "
                + "telefono = ?, correo = ? "
                + "WHERE id_cliente = ?";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido_paterno());
            ps.setString(3, c.getApellido_materno());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreo());
            ps.setInt(6, c.getId_cliente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR CLIENTE
    public boolean eliminar(int id) {

        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}
