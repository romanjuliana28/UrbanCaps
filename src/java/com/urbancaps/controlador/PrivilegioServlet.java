//  PROYECTO  : Urban Caps
//  ARCHIVO   : PrivilegioServlet.java
//  DESCRIPCIÓN: Servlet del módulo Privilegio.

package com.urbancaps.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.urbancaps.modelo.bean.Privilegio;
import com.urbancaps.modelo.dao.PrivilegioDAO;
import com.urbancaps.modelo.bean.Rol;
import com.urbancaps.modelo.dao.RolDAO;
import com.urbancaps.util.ExcelUtil;

@WebServlet(name = "PrivilegioServlet", urlPatterns = {"/privilegios"})
public class PrivilegioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!verificarSesion(request, response)) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar":    listar(request, response);               break;
            case "nuevo":     mostrarFormularioNuevo(request, response); break;
            case "editar":    mostrarFormularioEditar(request, response); break;
            case "eliminar":  eliminar(request, response);             break;
            case "asignar":   mostrarAsignacion(request, response);    break;
            case "exportar":  exportar(request, response);             break;
            default:          listar(request, response);               break;
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
            case "guardar":           guardar(request, response);           break;
            case "actualizar":        actualizar(request, response);        break;
            case "guardarAsignacion": guardarAsignacion(request, response); break;
            default:                  listar(request, response);            break;
        }
    }

    private void listar(HttpServletRequest request,
                        HttpServletResponse response)
            throws ServletException, IOException {
        List<Privilegio> privilegios = new PrivilegioDAO().listar();
        request.setAttribute("privilegios", privilegios);
        request.getRequestDispatcher("/admin/privilegios/lista.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request,
                                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("privilegio", new Privilegio());
        request.setAttribute("accion", "guardar");
        request.setAttribute("titulo", "Nuevo Privilegio");
        request.setAttribute("modulos", obtenerModulos());
        request.getRequestDispatcher("/admin/privilegios/formulario.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request,
                                          HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/privilegios?accion=listar");
            return;
        }
        Privilegio p = new PrivilegioDAO().obtenerPorId(Integer.parseInt(idParam));
        if (p == null) {
            request.setAttribute("error", "El privilegio no fue encontrado.");
            listar(request, response);
            return;
        }
        request.setAttribute("privilegio", p);
        request.setAttribute("accion", "actualizar");
        request.setAttribute("titulo", "Editar Privilegio");
        request.setAttribute("modulos", obtenerModulos());
        request.getRequestDispatcher("/admin/privilegios/formulario.jsp")
               .forward(request, response);
    }

    private void guardar(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String nombre      = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String modulo      = request.getParameter("modulo");
        String estadoParam = request.getParameter("estado");

        if (nombre == null || nombre.trim().isEmpty() ||
            modulo == null || modulo.trim().isEmpty()) {
            request.setAttribute("error", "Nombre y módulo son obligatorios.");
            request.setAttribute("privilegio", new Privilegio());
            request.setAttribute("accion", "guardar");
            request.setAttribute("titulo", "Nuevo Privilegio");
            request.setAttribute("modulos", obtenerModulos());
            request.getRequestDispatcher("/admin/privilegios/formulario.jsp")
                   .forward(request, response);
            return;
        }

        Privilegio p = new Privilegio();
        p.setNombre(nombre.trim().toUpperCase().replace(" ", "_"));
        p.setDescripcion(descripcion != null ? descripcion.trim() : "");
        p.setModulo(modulo.trim().toUpperCase());
        p.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new PrivilegioDAO().insertar(p);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Privilegio creado correctamente."
                  : "Ocurrió un error al crear el privilegio.");
        listar(request, response);
    }

    private void actualizar(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {
        String idParam     = request.getParameter("idPrivilegio");
        String nombre      = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String modulo      = request.getParameter("modulo");
        String estadoParam = request.getParameter("estado");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/privilegios?accion=listar");
            return;
        }

        Privilegio p = new Privilegio();
        p.setIdPrivilegio(Integer.parseInt(idParam));
        p.setNombre(nombre != null ? nombre.trim().toUpperCase().replace(" ", "_") : "");
        p.setDescripcion(descripcion != null ? descripcion.trim() : "");
        p.setModulo(modulo != null ? modulo.trim().toUpperCase() : "");
        p.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new PrivilegioDAO().actualizar(p);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Privilegio actualizado correctamente."
                  : "Ocurrió un error al actualizar el privilegio.");
        listar(request, response);
    }

    private void eliminar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/privilegios?accion=listar");
            return;
        }
        boolean exito = new PrivilegioDAO().eliminar(Integer.parseInt(idParam));
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Privilegio desactivado correctamente."
                  : "No se pudo desactivar el privilegio.");
        listar(request, response);
    }

    private void mostrarAsignacion(HttpServletRequest request,
                                    HttpServletResponse response)
            throws ServletException, IOException {
        String idRolParam = request.getParameter("idRol");
        if (idRolParam == null || idRolParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
            return;
        }
        int idRol = Integer.parseInt(idRolParam);
        Rol rol = new RolDAO().obtenerPorId(idRol);
        if (rol == null) {
            response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
            return;
        }
        PrivilegioDAO privDAO = new PrivilegioDAO();
        request.setAttribute("rol", rol);
        request.setAttribute("todosLosPrivilegios", privDAO.listar());
        request.setAttribute("idsAsignados", privDAO.listarPorRol(idRol));
        request.getRequestDispatcher("/admin/privilegios/asignacion.jsp")
               .forward(request, response);
    }

    private void guardarAsignacion(HttpServletRequest request,
                                    HttpServletResponse response)
            throws ServletException, IOException {
        String idRolParam = request.getParameter("idRol");
        if (idRolParam == null || idRolParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
            return;
        }
        int idRol = Integer.parseInt(idRolParam);
        String[] seleccionados = request.getParameterValues("privilegios");
        List<Integer> ids = new ArrayList<>();
        if (seleccionados != null) {
            for (String id : seleccionados) ids.add(Integer.parseInt(id));
        }
        boolean exito = new PrivilegioDAO().actualizarPrivilegiosRol(idRol, ids);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Privilegios actualizados correctamente."
                  : "Error al actualizar los privilegios.");
        response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
    }

    // exportar() reporte Excel de privilegios

    private void exportar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Privilegio> privilegios = new PrivilegioDAO().listar();
            ExcelUtil.exportarPrivilegios(privilegios, response);
        } catch (Exception e) {
            System.out.println("✘ Error al exportar privilegios: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el reporte Excel.");
            listar(request, response);
        }
    }

    private List<String> obtenerModulos() {
        List<String> modulos = new ArrayList<>();
        modulos.add("ROLES");
        modulos.add("PRIVILEGIOS");
        modulos.add("USUARIOS");
        modulos.add("PROVEEDORES");
        modulos.add("INVENTARIO");
        modulos.add("CATALOGO");
        return modulos;
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