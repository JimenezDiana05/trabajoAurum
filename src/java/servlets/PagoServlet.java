/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.pagoDAO;
import modelo.pago;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PagoServlet", urlPatterns = {"/api/pagos"})
public class PagoServlet extends HttpServlet {

    private final pagoDAO dao = new pagoDAO();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

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

    // GET: consultar todos los pagos o buscar uno por ID
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

            pago p = dao.buscarPorId(id);

            if (p != null) {
                mapper.writeValue(response.getWriter(), p);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Pago no encontrado."));
            }
        } else {
            List<pago> pagos = dao.listar();
            mapper.writeValue(response.getWriter(), pagos);
        }
    }

    // POST: registrar un pago
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        try {
            pago p = mapper.readValue(request.getInputStream(), pago.class);

            if (p.getFecha_pago() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "La fecha de pago es obligatoria."));
                return;
            }

            if (dao.insertar(p)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Pago registrado correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo registrar el pago."));
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
            pago existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Pago no encontrado."));
                return;
            }

            pago actualizado = mapper.readerForUpdating(existente)
                    .readValue(request.getInputStream());

            actualizado.setId_pago(id);

            if (actualizado.getFecha_pago() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "La fecha de pago es obligatoria."));
                return;
            }

            if (dao.actualizar(actualizado)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Pago modificado parcialmente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo modificar el pago."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se pudieron procesar los datos enviados."));
        }
    }

    // PUT: reemplazar los datos del pago
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
            pago existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Pago no encontrado."));
                return;
            }

            pago p = mapper.readValue(request.getInputStream(), pago.class);
            p.setId_pago(id);

            if (p.getFecha_pago() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "La fecha de pago es obligatoria."));
                return;
            }

            if (dao.actualizar(p)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Pago actualizado correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo actualizar el pago."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Los datos enviados no tienen un formato válido."));
        }
    }

    // DELETE: eliminar un pago
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
                    Map.of("mensaje", "Pago eliminado correctamente."));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se encontró el pago o no pudo eliminarse."));
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
