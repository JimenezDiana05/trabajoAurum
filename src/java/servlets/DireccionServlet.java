/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;


import dao.direccionDAO;
import modelo.direccion;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "DireccionServlet", urlPatterns = {"/api/direcciones"})
public class DireccionServlet extends BaseServlet<direccion> {

    private final direccionDAO dao = new direccionDAO();

    @Override
    protected Class<direccion> obtenerTipo() {
        return direccion.class;
    }

    @Override
    protected void asignarId(direccion objeto, int id) {
        objeto.setId_direccion(id);
    }

    @Override
    protected boolean insertar(direccion objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<direccion> listar() {
        return dao.listar();
    }

    @Override
    protected direccion buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(direccion objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}

