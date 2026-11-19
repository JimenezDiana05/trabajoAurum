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
import modelo.producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class productoDAO {

    // INSERTAR un registro. El ID se genera automaticamente en PostgreSQL.
    public boolean insertar(producto objeto) {
        String sql = "INSERT INTO producto (id_categoria, nombre, descripcion, precio, peso, material, kilates, stock, imagen) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_categoria());
                ps.setString(2, objeto.getNombre());
                ps.setString(3, objeto.getDescripcion());
                ps.setBigDecimal(4, objeto.getPrecio());
                ps.setBigDecimal(5, objeto.getPeso());
                ps.setString(6, objeto.getMaterial());
                ps.setBigDecimal(7, objeto.getKilates());
                ps.setInt(8, objeto.getStock());
                ps.setString(9, objeto.getImagen());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar en producto: " + e.getMessage());
            return false;
        }
    }

    // LISTAR todos los registros.
    public List<producto> listar() {
        List<producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto ORDER BY id_producto";

        try (Connection con = conexion.conectar()) {
            if (con == null) return lista;
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar producto: " + e.getMessage());
        }
        return lista;
    }

    // BUSCAR un registro por su ID.
    public producto buscarPorId(int id) {
        String sql = "SELECT * FROM producto WHERE id_producto = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar en producto: " + e.getMessage());
        }
        return null;
    }

    // ACTUALIZAR un registro existente.
    public boolean actualizar(producto objeto) {
        String sql = "UPDATE producto SET id_categoria = ?, nombre = ?, descripcion = ?, precio = ?, peso = ?, material = ?, kilates = ?, stock = ?, imagen = ? WHERE id_producto = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, objeto.getId_categoria());
                ps.setString(2, objeto.getNombre());
                ps.setString(3, objeto.getDescripcion());
                ps.setBigDecimal(4, objeto.getPrecio());
                ps.setBigDecimal(5, objeto.getPeso());
                ps.setString(6, objeto.getMaterial());
                ps.setBigDecimal(7, objeto.getKilates());
                ps.setInt(8, objeto.getStock());
                ps.setString(9, objeto.getImagen());
                ps.setInt(10, objeto.getId_producto());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR un registro por su ID.
    public boolean eliminar(int id) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";

        try (Connection con = conexion.conectar()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar de producto: " + e.getMessage());
            return false;
        }
    }

    private producto mapear(ResultSet rs) throws SQLException {
        producto objeto = new producto();
        objeto.setId_producto(rs.getInt("id_producto"));
        objeto.setId_categoria(rs.getInt("id_categoria"));
        objeto.setNombre(rs.getString("nombre"));
        objeto.setDescripcion(rs.getString("descripcion"));
        objeto.setPrecio(rs.getBigDecimal("precio"));
        objeto.setPeso(rs.getBigDecimal("peso"));
        objeto.setMaterial(rs.getString("material"));
        objeto.setKilates(rs.getBigDecimal("kilates"));
        objeto.setStock(rs.getInt("stock"));
        objeto.setImagen(rs.getString("imagen"));
        return objeto;
    }
}
