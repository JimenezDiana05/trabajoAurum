/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.productoDAO;
import modelo.producto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/api/productos"})
public class ProductoServlet extends HttpServlet {

    private final productoDAO dao = new productoDAO();
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

    // GET: consultar todos los productos o buscar uno por ID
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

            producto p = dao.buscarPorId(id);

            if (p != null) {
                mapper.writeValue(response.getWriter(), p);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Producto no encontrado."));
            }
        } else {
            List<producto> productos = dao.listar();
            mapper.writeValue(response.getWriter(), productos);
        }
    }

    // POST: registrar un producto
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        try {
            producto p = mapper.readValue(request.getInputStream(), producto.class);

            if (dao.insertar(p)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Producto registrado correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo registrar el producto."));
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
            producto existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Producto no encontrado."));
                return;
            }

            producto actualizado = mapper.readerForUpdating(existente)
                    .readValue(request.getInputStream());

            actualizado.setId_producto(id);

            if (dao.actualizar(actualizado)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Producto modificado parcialmente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo modificar el producto."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se pudieron procesar los datos enviados."));
        }
    }

    // PUT: reemplazar los datos del producto
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
            producto existente = dao.buscarPorId(id);

            if (existente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Producto no encontrado."));
                return;
            }

            producto p = mapper.readValue(request.getInputStream(), producto.class);
            p.setId_producto(id);

            if (dao.actualizar(p)) {
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Producto actualizado correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "No se pudo actualizar el producto."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Los datos enviados no tienen un formato válido."));
        }
    }

    // DELETE: eliminar un producto
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
                    Map.of("mensaje", "Producto eliminado correctamente."));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "No se encontró el producto o no pudo eliminarse."));
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
