//  ARCHIVO   : RolServlet.java
//  PAQUETE   : com.urbancaps.controlador
//  DESCRIPCIÓN: Servlet del módulo Roles.


package com.urbancaps.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import com.urbancaps.modelo.bean.Rol;
import com.urbancaps.modelo.dao.RolDAO;
import com.urbancaps.util.ExcelUtil;

@WebServlet(name = "RolServlet", urlPatterns = {"/roles"})
public class RolServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!verificarSesion(request, response)) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar":
                listar(request, response);
                break;
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "editar":
                mostrarFormularioEditar(request, response);
                break;
            case "eliminar":
                eliminar(request, response);
                break;
            case "exportar":
                // Genera y descarga el reporte Excel de roles
                exportar(request, response);
                break;
            default:
                listar(request, response);
                break;
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
            case "guardar":
                guardar(request, response);
                break;
            case "actualizar":
                actualizar(request, response);
                break;
            default:
                listar(request, response);
                break;
        }
    }

    // Métodos privados

    private void listar(HttpServletRequest request,
                        HttpServletResponse response)
            throws ServletException, IOException {
        RolDAO dao = new RolDAO();
        List<Rol> roles = dao.listar();
        request.setAttribute("roles", roles);
        request.getRequestDispatcher("/admin/roles/lista.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request,
                                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("rol", new Rol());
        request.setAttribute("accion", "guardar");
        request.setAttribute("titulo", "Nuevo Rol");
        request.getRequestDispatcher("/admin/roles/formulario.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request,
                                          HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
            return;
        }
        RolDAO dao = new RolDAO();
        Rol rol = dao.obtenerPorId(Integer.parseInt(idParam));
        if (rol == null) {
            request.setAttribute("error", "El rol no fue encontrado.");
            listar(request, response);
            return;
        }
        request.setAttribute("rol", rol);
        request.setAttribute("accion", "actualizar");
        request.setAttribute("titulo", "Editar Rol");
        request.getRequestDispatcher("/admin/roles/formulario.jsp")
               .forward(request, response);
    }

    private void guardar(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String nombreRol   = request.getParameter("nombreRol");
        String descripcion = request.getParameter("descripcion");
        String estadoParam = request.getParameter("estado");

        if (nombreRol == null || nombreRol.trim().isEmpty()) {
            request.setAttribute("error", "El nombre del rol es obligatorio.");
            request.setAttribute("rol", new Rol());
            request.setAttribute("accion", "guardar");
            request.setAttribute("titulo", "Nuevo Rol");
            request.getRequestDispatcher("/admin/roles/formulario.jsp")
                   .forward(request, response);
            return;
        }

        Rol rol = new Rol();
        rol.setNombreRol(nombreRol.trim());
        rol.setDescripcion(descripcion != null ? descripcion.trim() : "");
        rol.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new RolDAO().insertar(rol);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Rol creado correctamente."
                  : "Ocurrió un error al crear el rol.");
        listar(request, response);
    }

    private void actualizar(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {
        String idParam     = request.getParameter("idRol");
        String nombreRol   = request.getParameter("nombreRol");
        String descripcion = request.getParameter("descripcion");
        String estadoParam = request.getParameter("estado");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
            return;
        }

        Rol rol = new Rol();
        rol.setIdRol(Integer.parseInt(idParam));
        rol.setNombreRol(nombreRol != null ? nombreRol.trim() : "");
        rol.setDescripcion(descripcion != null ? descripcion.trim() : "");
        rol.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new RolDAO().actualizar(rol);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Rol actualizado correctamente."
                  : "Ocurrió un error al actualizar el rol.");
        listar(request, response);
    }

    private void eliminar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
            return;
        }
        boolean exito = new RolDAO().eliminar(Integer.parseInt(idParam));
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Rol desactivado correctamente."
                  : "No se pudo desactivar el rol.");
        listar(request, response);
    }

    // exportar() genera y descarga el reporte Excel de roles

    private void exportar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Rol> roles = new RolDAO().listar();
            // ExcelUtil genera el archivo y lo envía al navegador
            ExcelUtil.exportarRoles(roles, response);
        } catch (Exception e) {
            System.out.println("✘ Error al exportar roles: " + e.getMessage());
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