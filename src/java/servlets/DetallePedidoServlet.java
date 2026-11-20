/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.detalle_pedidoDAO;
import modelo.detalle_pedido;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DetallePedidoServlet", urlPatterns = {"/api/detalles-pedido"})
public class DetallePedidoServlet extends HttpServlet {

    private final detalle_pedidoDAO dao = new detalle_pedidoDAO();
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

    // GET: consultar todos los detalles o buscar uno por ID
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

            detalle_pedido d = dao.buscarPorId(id);

            if (d != null) {
                mapper.writeValue(response.getWriter(), d);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Detalle de pedido no encontrado."));
            }
        } else {
            List<detalle_pedido> detalles = dao.listar();
            mapper.writeValue(response.getWriter(), detalles);
        }
    }

    // POST: registrar un detalle de pedido
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        try {
            detalle_pedido d = mapper.readValue(
                    request.getInputStream(), detalle_pedido.class);

            if (dao.insertar(d)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Detalle de pedido registrado correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo registrar el detalle de pedido."));
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
            detalle_pedido existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Detalle de pedido no encontrado."));
                return;
            }

            detalle_pedido actualizado = mapper.readerForUpdating(existente)
                    .readValue(request.getInputStream());

            actualizado.setId_detalle(id);

            if (dao.actualizar(actualizado)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Detalle de pedido modificado parcialmente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo modificar el detalle de pedido."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se pudieron procesar los datos enviados."));
        }
    }

    // PUT: reemplazar los datos del detalle
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
            detalle_pedido existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Detalle de pedido no encontrado."));
                return;
            }

            detalle_pedido d = mapper.readValue(
                    request.getInputStream(), detalle_pedido.class);
            d.setId_detalle(id);

            if (dao.actualizar(d)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Detalle de pedido actualizado correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo actualizar el detalle de pedido."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Los datos enviados no tienen un formato válido."));
        }
    }

    // DELETE: eliminar un detalle de pedido
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
                    Map.of("mensaje", "Detalle de pedido eliminado correctamente."));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se encontró el detalle o no pudo eliminarse."));
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
