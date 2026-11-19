/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import dao.pagoDAO;
import modelo.pago;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "PagoServlet", urlPatterns = {"/api/pagos"})
public class PagoServlet extends BaseServlet<pago> {

    private final pagoDAO dao = new pagoDAO();

    @Override
    protected Class<pago> obtenerTipo() {
        return pago.class;
    }

    @Override
    protected void asignarId(pago objeto, int id) {
        objeto.setId_pago(id);
    }

    @Override
    protected boolean insertar(pago objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<pago> listar() {
        return dao.listar();
    }

    @Override
    protected pago buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(pago objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}

