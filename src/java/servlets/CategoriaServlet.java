/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.categoriaDAO;
import modelo.categoria;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CategoriaServlet", urlPatterns = {"/api/categorias"})
public class CategoriaServlet extends HttpServlet {

    private final categoriaDAO dao = new categoriaDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    private void configurarRespuesta(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

    private int obtenerId(HttpServletRequest request) {
        String id = request.getParameter("id");

        if (id == null || id.isEmpty()) return -1;

        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // GET: consultar todas las categorías o buscar una por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);
        int id = obtenerId(request);

        if (request.getParameter("id") != null) {
            if (id <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "El ID proporcionado no es válido."));
                return;
            }

            categoria c = dao.buscarPorId(id);

            if (c != null) {
                mapper.writeValue(response.getWriter(), c);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Categoría no encontrada."));
            }
        } else {
            List<categoria> categorias = dao.listar();
            mapper.writeValue(response.getWriter(), categorias);
        }
    }

    // POST: registrar una categoría
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        try {
            categoria c = mapper.readValue(request.getInputStream(), categoria.class);

            if (dao.insertar(c)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Categoría registrada correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo registrar la categoría."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Los datos enviados no tienen un formato válido."));
        }
    }

    // PATCH: modificar solamente los datos enviados
    protected void doPatch(HttpServletRequest request, HttpServletResponse response)
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
            categoria existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Categoría no encontrada."));
                return;
            }

            categoria actualizado = mapper.readerForUpdating(existente)
                    .readValue(request.getInputStream());

            actualizado.setId_categoria(id);

            if (dao.actualizar(actualizado)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Categoría modificada parcialmente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo modificar la categoría."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se pudieron procesar los datos enviados."));
        }
    }

    // PUT: reemplazar los datos de la categoría
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
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
            categoria existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Categoría no encontrada."));
                return;
            }

            categoria c = mapper.readValue(request.getInputStream(), categoria.class);
            c.setId_categoria(id);

            if (dao.actualizar(c)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Categoría actualizada correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo actualizar la categoría."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Los datos enviados no tienen un formato válido."));
        }
    }

    // DELETE: eliminar una categoría
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
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
                    Map.of("mensaje", "Categoría eliminada correctamente."));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se encontró la categoría o no pudo eliminarse."));
        }
    }

    // Habilitar PATCH
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }
}