/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import dao.usuarioDAO;
import modelo.usuario;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/api/usuarios"})
public class UsuarioServlet extends BaseServlet<usuario> {

    private final usuarioDAO dao = new usuarioDAO();

    @Override
    protected Class<usuario> obtenerTipo() {
        return usuario.class;
    }

    @Override
    protected void asignarId(usuario objeto, int id) {
        objeto.setId_usuario(id);
    }

    @Override
    protected boolean insertar(usuario objeto) {
        return dao.insertar(objeto);
    }

    @Override
    protected List<usuario> listar() {
        return dao.listar();
    }

    @Override
    protected usuario buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    @Override
    protected boolean actualizar(usuario objeto) {
        return dao.actualizar(objeto);
    }

    @Override
    protected boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}

