//  ARCHIVO   : UsuarioServlet.java
//  PAQUETE   : com.urbancaps.controlador
//  DESCRIPCIÓN: Servlet del módulo Usuario.


package com.urbancaps.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import com.urbancaps.modelo.bean.Usuario;
import com.urbancaps.modelo.bean.Rol;
import com.urbancaps.modelo.dao.UsuarioDAO;
import com.urbancaps.modelo.dao.RolDAO;
import com.urbancaps.util.ExcelUtil;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/usuarios"})
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        // Logout no requiere verificar sesión
        if (accion.equals("logout")) {
            logout(request, response);
            return;
        }

        if (!verificarSesion(request, response)) return;

        switch (accion) {
            case "listar":   listar(request, response);                break;
            case "nuevo":    mostrarFormularioNuevo(request, response); break;
            case "editar":   mostrarFormularioEditar(request, response); break;
            case "eliminar": eliminar(request, response);               break;
            case "exportar": exportar(request, response);               break;
            default:         listar(request, response);                 break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!verificarSesion(request, response)) return;
        request.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");
        if (accion == null) accion = "";

        switch (accion) {
            case "guardar":    guardar(request, response);    break;
            case "actualizar": actualizar(request, response); break;
            default:           listar(request, response);     break;
        }
    }

    // Métodos privados

    private void listar(HttpServletRequest request,
                        HttpServletResponse response)
            throws ServletException, IOException {
        List<Usuario> usuarios = new UsuarioDAO().listar();
        request.setAttribute("usuarios", usuarios);
        request.getRequestDispatcher("/admin/usuarios/lista.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request,
                                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("usuario", new Usuario());
        request.setAttribute("accion", "guardar");
        request.setAttribute("titulo", "Nuevo Usuario");
        request.setAttribute("roles", new RolDAO().listar());
        request.getRequestDispatcher("/admin/usuarios/formulario.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request,
                                          HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/usuarios?accion=listar");
            return;
        }
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.obtenerPorId(Integer.parseInt(idParam));
        if (usuario == null) {
            request.setAttribute("error", "El usuario no fue encontrado.");
            listar(request, response);
            return;
        }
        request.setAttribute("usuario", usuario);
        request.setAttribute("roles", new RolDAO().listar());
        request.setAttribute("accion", "actualizar");
        request.setAttribute("titulo", "Editar Usuario");
        request.getRequestDispatcher("/admin/usuarios/formulario.jsp")
               .forward(request, response);
    }

    private void guardar(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String nombre      = request.getParameter("nombre");
        String apellido    = request.getParameter("apellido");
        String email       = request.getParameter("email");
        String password    = request.getParameter("password");
        String idRolParam  = request.getParameter("idRol");
        String estadoParam = request.getParameter("estado");

        if (nombre == null || nombre.trim().isEmpty() ||
            apellido == null || apellido.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            idRolParam == null || idRolParam.trim().isEmpty()) {

            request.setAttribute("error", "Todos los campos son obligatorios.");
            request.setAttribute("usuario", new Usuario());
            request.setAttribute("accion", "guardar");
            request.setAttribute("titulo", "Nuevo Usuario");
            request.setAttribute("roles", new RolDAO().listar());
            request.getRequestDispatcher("/admin/usuarios/formulario.jsp")
                   .forward(request, response);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        if (dao.emailExiste(email.trim(), 0)) {
            request.setAttribute("error", "El correo ya está registrado.");
            request.setAttribute("usuario", new Usuario());
            request.setAttribute("accion", "guardar");
            request.setAttribute("titulo", "Nuevo Usuario");
            request.setAttribute("roles", new RolDAO().listar());
            request.getRequestDispatcher("/admin/usuarios/formulario.jsp")
                   .forward(request, response);
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setEmail(email.trim());
        usuario.setPassword(password);
        usuario.setIdRol(Integer.parseInt(idRolParam));
        usuario.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = dao.insertar(usuario);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Usuario creado correctamente."
                  : "Ocurrió un error al crear el usuario.");
        listar(request, response);
    }

    private void actualizar(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {
        String idParam     = request.getParameter("idUsuario");
        String nombre      = request.getParameter("nombre");
        String apellido    = request.getParameter("apellido");
        String email       = request.getParameter("email");
        String password    = request.getParameter("password");
        String idRolParam  = request.getParameter("idRol");
        String estadoParam = request.getParameter("estado");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/usuarios?accion=listar");
            return;
        }

        int idUsuario = Integer.parseInt(idParam);
        UsuarioDAO dao = new UsuarioDAO();

        if (dao.emailExiste(email != null ? email.trim() : "", idUsuario)) {
            request.setAttribute("error", "El correo ya está en uso por otro usuario.");
            mostrarFormularioEditar(request, response);
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(nombre != null ? nombre.trim() : "");
        usuario.setApellido(apellido != null ? apellido.trim() : "");
        usuario.setEmail(email != null ? email.trim() : "");
        usuario.setPassword(password != null ? password.trim() : "");
        usuario.setIdRol(idRolParam != null ? Integer.parseInt(idRolParam) : 0);
        usuario.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = dao.actualizar(usuario);

        if (exito) {
            // Actualizamos el nombre en sesión si editó su propio perfil
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario sesionUsuario = (Usuario) session.getAttribute("usuario");
                if (sesionUsuario != null && sesionUsuario.getIdUsuario() == idUsuario) {
                    session.setAttribute("nombreUsuario",
                        usuario.getNombre() + " " + usuario.getApellido());
                }
            }
        }

        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Usuario actualizado correctamente."
                  : "Ocurrió un error al actualizar el usuario.");
        listar(request, response);
    }

    private void eliminar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/usuarios?accion=listar");
            return;
        }
        int idUsuario = Integer.parseInt(idParam);

        // Evitar que el admin se desactive a sí mismo
        HttpSession session = request.getSession(false);
        if (session != null) {
            Usuario u = (Usuario) session.getAttribute("usuario");
            if (u != null && u.getIdUsuario() == idUsuario) {
                request.setAttribute("error", "No puedes desactivar tu propia cuenta.");
                listar(request, response);
                return;
            }
        }

        boolean exito = new UsuarioDAO().eliminar(idUsuario);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Usuario desactivado correctamente."
                  : "No se pudo desactivar el usuario.");
        listar(request, response);
    }

    private void logout(HttpServletRequest request,
                        HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        response.sendRedirect(request.getContextPath() + "/login");
    }

    // exportar()  reporte Excel de usuarios

    private void exportar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Usuario> usuarios = new UsuarioDAO().listar();
            ExcelUtil.exportarUsuarios(usuarios, response);
        } catch (Exception e) {
            System.out.println("✘ Error al exportar usuarios: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el reporte Excel.");
            listar(request, response);
        }
    }

    private boolean verificarSesion(HttpServletRequest request,
                                     HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}