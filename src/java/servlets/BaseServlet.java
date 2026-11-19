/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlets;

/**
 *
 * @author jimen
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class BaseServlet<T> extends HttpServlet {

    protected final ObjectMapper mapper = new ObjectMapper();

    protected abstract Class<T> obtenerTipo();
    protected abstract void asignarId(T objeto, int id);

    protected abstract boolean insertar(T objeto);
    protected abstract List<T> listar();
    protected abstract T buscarPorId(int id);
    protected abstract boolean actualizar(T objeto);
    protected abstract boolean eliminar(int id);

    private void configurarRespuesta(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

    private int obtenerId(HttpServletRequest request) {
        try {
            return Integer.parseInt(request.getParameter("id"));
        } catch (Exception e) {
            return -1;
        }
    }

    private void mensaje(HttpServletResponse response, int estado, String texto)
            throws IOException {
        response.setStatus(estado);
        mapper.writeValue(response.getWriter(), Map.of("mensaje", texto));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        if (request.getParameter("id") != null) {
            int id = obtenerId(request);

            if (id <= 0) {
                mensaje(response, 400, "El ID proporcionado no es válido.");
                return;
            }

            T objeto = buscarPorId(id);

            if (objeto != null) {
                mapper.writeValue(response.getWriter(), objeto);
            } else {
                mensaje(response, 404, "Registro no encontrado.");
            }
        } else {
            mapper.writeValue(response.getWriter(), listar());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);

        try {
            T objeto = mapper.readValue(request.getInputStream(), obtenerTipo());

            if (insertar(objeto)) {
                mensaje(response, 201, "Registro creado correctamente.");
            } else {
                mensaje(response, 400, "No se pudo crear el registro. Revisa los datos y las relaciones.");
            }
        } catch (Exception e) {
            mensaje(response, 400, "Los datos enviados no tienen un formato válido.");
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);
        int id = obtenerId(request);

        if (id <= 0) {
            mensaje(response, 400, "Debes proporcionar un ID válido.");
            return;
        }

        try {
            T existente = buscarPorId(id);

            if (existente == null) {
                mensaje(response, 404, "Registro no encontrado.");
                return;
            }

            T actualizado = mapper.readerForUpdating(existente)
                    .readValue(request.getInputStream());

            asignarId(actualizado, id);

            if (actualizar(actualizado)) {
                mensaje(response, 200, "Registro modificado parcialmente.");
            } else {
                mensaje(response, 400, "No se pudo modificar el registro.");
            }
        } catch (Exception e) {
            mensaje(response, 400, "No se pudieron procesar los datos enviados.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);
        int id = obtenerId(request);

        if (id <= 0) {
            mensaje(response, 400, "Debes proporcionar un ID válido.");
            return;
        }

        try {
            if (buscarPorId(id) == null) {
                mensaje(response, 404, "Registro no encontrado.");
                return;
            }

            T objeto = mapper.readValue(request.getInputStream(), obtenerTipo());
            asignarId(objeto, id);

            if (actualizar(objeto)) {
                mensaje(response, 200, "Registro actualizado correctamente.");
            } else {
                mensaje(response, 400, "No se pudo actualizar el registro.");
            }
        } catch (Exception e) {
            mensaje(response, 400, "Los datos enviados no tienen un formato válido.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarRespuesta(response);
        int id = obtenerId(request);

        if (id <= 0) {
            mensaje(response, 400, "Debes proporcionar un ID válido.");
            return;
        }

        if (eliminar(id)) {
            mensaje(response, 200, "Registro eliminado correctamente.");
        } else {
            mensaje(response, 404, "No se encontró el registro o no pudo eliminarse.");
        }
    }

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

