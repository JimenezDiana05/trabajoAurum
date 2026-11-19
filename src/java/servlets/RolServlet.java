/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import dao.rolDAO;
import modelo.rol;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "RolServlet", urlPatterns = {"/api/roles"})
public class RolServlet extends BaseServlet<rol> {

    private final rolDAO dao = new rolDAO();

    @Override
    protected Class<rol> obtenerTipo() {
        return rol.class;
    }

    @Override
    protected void asignarId(rol objeto, int id) {
        objeto.setId_rol(id);
    }

    @Override
    protected boolean insertar(rol objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<rol> listar() {
        return dao.listar();
    }

    @Override
    protected rol buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(rol objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}

