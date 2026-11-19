/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import dao.envioDAO;
import modelo.envio;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "EnvioServlet", urlPatterns = {"/api/envios"})
public class EnvioServlet extends BaseServlet<envio> {

    private final envioDAO dao = new envioDAO();

    @Override
    protected Class<envio> obtenerTipo() {
        return envio.class;
    }

    @Override
    protected void asignarId(envio objeto, int id) {
        objeto.setId_envio(id);
    }

    @Override
    protected boolean insertar(envio objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<envio> listar() {
        return dao.listar();
    }

    @Override
    protected envio buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(envio objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}

