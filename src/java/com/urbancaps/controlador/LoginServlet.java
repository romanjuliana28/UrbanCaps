//  ARCHIVO   : LoginServlet.java
//  PAQUETE   : com.urbancaps.controlador
//  DESCRIPCIÓN: Servlet que maneja el inicio de sesión.

package com.urbancaps.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;

import com.urbancaps.modelo.bean.Usuario;
import com.urbancaps.modelo.bean.Privilegio;
import com.urbancaps.modelo.dao.UsuarioDAO;
import com.urbancaps.modelo.dao.PrivilegioDAO;
import com.urbancaps.util.Encriptador;

import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        // Validación de campos vacíos
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error",
                "Por favor ingrese su email y contraseña.");
            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);
            return;
        }

        // Buscar el usuario por email en la BD
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.obtenerPorEmail(email.trim());

        // Verificar si el usuario existe
        if (usuario == null) {
            request.setAttribute("error",
                "El correo electrónico no está registrado.");
            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);
            return;
        }

        // Verificar que el usuario esté activo
        if (usuario.getEstado() == 0) {
            request.setAttribute("error",
                "Su cuenta está inactiva. Contacte al administrador.");
            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);
            return;
        }

        // Verificar la contraseña con BCrypt
        boolean passwordCorrecta = Encriptador.verificar(
            password, usuario.getPassword()
        );

        if (!passwordCorrecta) {
            request.setAttribute("error",
                "La contraseña es incorrecta.");
            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);
            return;
        }

        // LOGIN EXITOSO — Crear la sesión con todos los datos

        HttpSession session = request.getSession(true);

        // Guardamos el objeto Usuario completo
        session.setAttribute("usuario", usuario);
        session.setAttribute("nombreUsuario",
            usuario.getNombre() + " " + usuario.getApellido());
        session.setAttribute("idRol", usuario.getIdRol());
        session.setAttribute("nombreRol", usuario.getNombreRol());

        // Cargamos los privilegios del rol del usuario

        PrivilegioDAO privilegioDAO = new PrivilegioDAO();

        // IDs de privilegios asignados al rol (List<Integer>)
        List<Integer> idsPrivilegios =
            privilegioDAO.listarPorRol(usuario.getIdRol());
        session.setAttribute("idsPrivilegios", idsPrivilegios);

        // Lista completa de privilegios del sistema
        List<Privilegio> todosPrivilegios = privilegioDAO.listar();
        session.setAttribute("privilegios", todosPrivilegios);

        // Guardamos los NOMBRES de los privilegios asignados al rol como List<String>.
        // Esto permite que los JSP verifiquen permisos así:
        // privs.contains("USUARIOS_VER") en lugar de: ids.contains(11)
        // Mucho más legible y no depende de IDs de la BD.

        List<String> nombresPrivilegios = new ArrayList<>();
        for (Privilegio p : todosPrivilegios) {
            // Solo agregamos el nombre si el ID está en la lista de privilegios asignados al rol del usuario
            if (idsPrivilegios.contains(p.getIdPrivilegio())) {
                nombresPrivilegios.add(p.getNombre());
            }
        }
        // Guardamos los nombres en sesión
        // Uso en JSP: session.getAttribute("nombresPrivilegios")
        session.setAttribute("nombresPrivilegios", nombresPrivilegios);

        // Sesión válida por 30 minutos de inactividad
        session.setMaxInactiveInterval(30 * 60);

        // Redirigimos al dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}