/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.direccionDAO;
import modelo.direccion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DireccionServlet", urlPatterns = {"/api/direcciones"})
public class DireccionServlet extends HttpServlet {

    private final direccionDAO dao = new direccionDAO();
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

    // GET: consultar todas las direcciones o buscar una por ID
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

            direccion d = dao.buscarPorId(id);

            if (d != null) {
                mapper.writeValue(response.getWriter(), d);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Dirección no encontrada."));
            }
        } else {
            List<direccion> direcciones = dao.listar();
            mapper.writeValue(response.getWriter(), direcciones);
        }
    }

    // POST: registrar una dirección
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        try {
            direccion d = mapper.readValue(request.getInputStream(), direccion.class);

            if (dao.insertar(d)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Dirección registrada correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo registrar la dirección."));
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
            direccion existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Dirección no encontrada."));
                return;
            }

            direccion actualizado = mapper.readerForUpdating(existente)
                    .readValue(request.getInputStream());

            actualizado.setId_direccion(id);

            if (dao.actualizar(actualizado)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Dirección modificada parcialmente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo modificar la dirección."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se pudieron procesar los datos enviados."));
        }
    }

    // PUT: reemplazar los datos de la dirección
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
            direccion existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Dirección no encontrada."));
                return;
            }

            direccion d = mapper.readValue(request.getInputStream(), direccion.class);
            d.setId_direccion(id);

            if (dao.actualizar(d)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Dirección actualizada correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo actualizar la dirección."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Los datos enviados no tienen un formato válido."));
        }
    }

    // DELETE: eliminar una dirección
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
                    Map.of("mensaje", "Dirección eliminada correctamente."));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se encontró la dirección o no pudo eliminarse."));
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

