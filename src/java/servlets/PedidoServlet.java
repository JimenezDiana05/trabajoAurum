/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import dao.pedidoDAO;
import modelo.pedido;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "PedidoServlet", urlPatterns = {"/api/pedidos"})
public class PedidoServlet extends BaseServlet<pedido> {

    private final pedidoDAO dao = new pedidoDAO();

    @Override
    protected Class<pedido> obtenerTipo() {
        return pedido.class;
    }

    @Override
    protected void asignarId(pedido objeto, int id) {
        objeto.setId_pedido(id);
    }

    @Override
    protected boolean insertar(pedido objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<pedido> listar() {
        return dao.listar();
    }

    @Override
    protected pedido buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(pedido objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}

