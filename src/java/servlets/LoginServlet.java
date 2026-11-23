/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.usuarioDAO;
import modelo.usuario;
import Componentes.Password;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = {"/api/login"})
public class LoginServlet extends HttpServlet {

    private final usuarioDAO dao = new usuarioDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            JsonNode datos = mapper.readTree(request.getInputStream());

            String correo = datos.hasNonNull("correo")
                    ? datos.get("correo").asText().trim() : "";

            String contrasena = datos.hasNonNull("contrasena")
                    ? datos.get("contrasena").asText() : "";

            if (correo.isBlank() || contrasena.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Debes ingresar correo y contraseña."));
                return;
            }

            // Buscar usuario por correo
            usuario u = dao.buscarPorCorreo(correo);

            // Verificar que exista y que la contraseña sea correcta
            if (u == null || !Password.verificar(
                    contrasena, u.getContrasena())) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Correo o contraseña incorrectos."));
                return;
            }

            // Verificar que la cuenta esté activa
            if (u.getEstado() == null
                    || !u.getEstado().trim().equalsIgnoreCase("Activo")) {

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                mapper.writeValue(response.getWriter(),
                        Map.of("mensaje", "Tu cuenta está inactiva."));
                return;
            }

            // Crear sesión
            HttpSession sesion = request.getSession(true);

            sesion.setAttribute("id_usuario", u.getId_usuario());
            sesion.setAttribute("id_cliente", u.getId_cliente());
            sesion.setAttribute("id_rol", u.getId_rol());

            // Respuesta sin exponer la contraseña
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("mensaje", "Inicio de sesión exitoso.");
            resultado.put("id_usuario", u.getId_usuario());
            resultado.put("id_cliente", u.getId_cliente());
            resultado.put("id_rol", u.getId_rol());

            response.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(response.getWriter(), resultado);

        } catch (Exception e) {
            System.out.println("Error en LoginServlet: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(response.getWriter(),
                    Map.of("mensaje", "Ocurrió un error al iniciar sesión."));
        }
    }
}

