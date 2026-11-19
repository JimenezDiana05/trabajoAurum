/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import dao.categoriaDAO;
import modelo.categoria;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "CategoriaServlet", urlPatterns = {"/api/categorias"})
public class CategoriaServlet extends BaseServlet<categoria> {

    private final categoriaDAO dao = new categoriaDAO();

    @Override
    protected Class<categoria> obtenerTipo() {
        return categoria.class;
    }

    @Override
    protected void asignarId(categoria objeto, int id) {
        objeto.setId_categoria(id);
    }

    @Override
    protected boolean insertar(categoria objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<categoria> listar() {
        return dao.listar();
    }

    @Override
    protected categoria buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(categoria objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}
