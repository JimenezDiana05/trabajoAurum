/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import dao.productoDAO;
import modelo.producto;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/api/productos"})
public class ProductoServlet extends BaseServlet<producto> {

    private final productoDAO dao = new productoDAO();

    @Override
    protected Class<producto> obtenerTipo() {
        return producto.class;
    }

    @Override
    protected void asignarId(producto objeto, int id) {
        objeto.setId_producto(id);
    }

    @Override
    protected boolean insertar(producto objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<producto> listar() {
        return dao.listar();
    }

    @Override
    protected producto buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(producto objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}
