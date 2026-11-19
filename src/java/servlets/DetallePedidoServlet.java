/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import dao.detalle_pedidoDAO;
import modelo.detalle_pedido;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "DetallePedidoServlet", urlPatterns = {"/api/detalles-pedido"})
public class DetallePedidoServlet extends BaseServlet<detalle_pedido> {

    private final detalle_pedidoDAO dao = new detalle_pedidoDAO();

    @Override
    protected Class<detalle_pedido> obtenerTipo() {
        return detalle_pedido.class;
    }

    @Override
    protected void asignarId(detalle_pedido objeto, int id) {
        objeto.setId_detalle(id);
    }

    @Override
    protected boolean insertar(detalle_pedido objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<detalle_pedido> listar() {
        return dao.listar();
    }

    @Override
    protected detalle_pedido buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(detalle_pedido objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}
