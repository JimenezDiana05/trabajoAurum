/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */


package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.usuarioDAO;
import modelo.usuario;
import Componentes.Password;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/api/usuarios"})
public class UsuarioServlet extends HttpServlet {

    private final usuarioDAO dao = new usuarioDAO();

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private void configurarRespuesta(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

    private int obtenerId(HttpServletRequest request) {
        String id = request.getParameter("id");

        if (id == null || id.isEmpty()) {
            return -1;
        }

        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Evitar enviar contraseñas y hashes en las respuestas.
    private void ocultarContrasena(usuario u) {
        if (u != null) {
            u.setContrasena(null);
        }
    }

    // GET: consultar usuarios o buscar por ID.
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        int id = obtenerId(request);

        if (request.getParameter("id") != null) {

            if (id <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "El ID no es válido."));
                return;
            }

            usuario u = dao.buscarPorId(id);

            if (u != null) {
                ocultarContrasena(u);
                mapper.writeValue(response.getWriter(), u);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Usuario no encontrado."));
            }

        } else {

            List<usuario> usuarios = dao.listar();

            usuarios.forEach(this::ocultarContrasena);

            mapper.writeValue(response.getWriter(), usuarios);
        }
    }

    // POST: registrar usuario con contraseña protegida.
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        try {
            usuario u = mapper.readValue(
                    request.getInputStream(), usuario.class);

            if (u.getFecha_creacion() == null
                    || u.getContrasena() == null
                    || u.getContrasena().isBlank()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "La fecha y la contraseña son obligatorias."));
                return;
            }

            u.setContrasena(Password.encriptar(u.getContrasena()));

            if (dao.insertar(u)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "Usuario registrado correctamente."));
            } else {
                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "No se pudo registrar el usuario."));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje",
                            "No se pudieron procesar los datos enviados."));
        }
    }

    // PATCH: modificar parcialmente un usuario.
    protected void doPatch(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        int id = obtenerId(request);

        if (id <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Debes proporcionar un ID válido."));
            return;
        }

        try {
            usuario existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Usuario no encontrado."));
                return;
            }

            usuario actualizado = mapper.readerForUpdating(existente)
                    .readValue(request.getInputStream());

            actualizado.setId_usuario(id);

            if (actualizado.getFecha_creacion() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "La fecha de creación es obligatoria."));
                return;
            }

            if (actualizado.getContrasena() != null) {
                if (actualizado.getContrasena().isBlank()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    mapper.writeValue(response.getWriter(),
                            Map.of("mensaje",
                                    "La contraseña no puede estar vacía."));
                    return;
                }

                actualizado.setContrasena(
                        Password.encriptar(actualizado.getContrasena()));
            }

            if (dao.actualizar(actualizado)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "Usuario modificado parcialmente."));
            } else {
                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "No se pudo modificar el usuario."));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje",
                            "No se pudieron procesar los datos enviados."));
        }
    }

    // PUT: reemplazar los datos del usuario.
    @Override
    protected void doPut(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        int id = obtenerId(request);

        if (id <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Debes proporcionar un ID válido."));
            return;
        }

        try {
            usuario existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Usuario no encontrado."));
                return;
            }

            usuario u = mapper.readValue(
                    request.getInputStream(), usuario.class);

            u.setId_usuario(id);

            if (u.getFecha_creacion() == null
                    || u.getContrasena() == null
                    || u.getContrasena().isBlank()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "La fecha y la contraseña son obligatorias."));
                return;
            }

            u.setContrasena(Password.encriptar(u.getContrasena()));

            if (dao.actualizar(u)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "Usuario actualizado correctamente."));
            } else {
                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje",
                                "No se pudo actualizar el usuario."));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje",
                            "Los datos enviados no tienen un formato válido."));
        }
    }

    // DELETE: eliminar usuario.
    @Override
    protected void doDelete(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        int id = obtenerId(request);

        if (id <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Debes proporcionar un ID válido."));
            return;
        }

        if (dao.eliminar(id)) {
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Usuario eliminado correctamente."));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje",
                            "No se encontró el usuario o no pudo eliminarse."));
        }
    }

    // Habilitar PATCH.
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }
}


