//  PROYECTO  : Urban Caps
//  ARCHIVO   : ProveedorServlet.java
//  DESCRIPCIÓN: Servlet del módulo Proveedor.

package com.urbancaps.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import com.urbancaps.modelo.bean.Proveedor;
import com.urbancaps.modelo.dao.ProveedorDAO;
import com.urbancaps.util.ExcelUtil;

@WebServlet(name = "ProveedorServlet", urlPatterns = {"/proveedores"})
public class ProveedorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!verificarSesion(request, response)) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

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
        List<Proveedor> proveedores = new ProveedorDAO().listar();
        request.setAttribute("proveedores", proveedores);
        request.getRequestDispatcher("/admin/proveedores/lista.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request,
                                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("proveedor", new Proveedor());
        request.setAttribute("accion", "guardar");
        request.setAttribute("titulo", "Nuevo Proveedor");
        request.getRequestDispatcher("/admin/proveedores/formulario.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request,
                                          HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/proveedores?accion=listar");
            return;
        }
        Proveedor proveedor = new ProveedorDAO()
                                  .obtenerPorId(Integer.parseInt(idParam));
        if (proveedor == null) {
            request.setAttribute("error", "El proveedor no fue encontrado.");
            listar(request, response);
            return;
        }
        request.setAttribute("proveedor", proveedor);
        request.setAttribute("accion", "actualizar");
        request.setAttribute("titulo", "Editar Proveedor");
        request.getRequestDispatcher("/admin/proveedores/formulario.jsp")
               .forward(request, response);
    }

    private void guardar(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String nombre      = request.getParameter("nombre");
        String apellidos   = request.getParameter("apellidos");
        String contacto    = request.getParameter("contacto");
        String direccion   = request.getParameter("direccion");
        String correo      = request.getParameter("correo");
        String estadoParam = request.getParameter("estado");

        if (nombre == null    || nombre.trim().isEmpty()    ||
            apellidos == null || apellidos.trim().isEmpty() ||
            contacto == null  || contacto.trim().isEmpty()  ||
            direccion == null || direccion.trim().isEmpty()) {

            request.setAttribute("error",
                "Nombre, apellidos, contacto y dirección son obligatorios.");
            request.setAttribute("proveedor", new Proveedor());
            request.setAttribute("accion", "guardar");
            request.setAttribute("titulo", "Nuevo Proveedor");
            request.getRequestDispatcher("/admin/proveedores/formulario.jsp")
                   .forward(request, response);
            return;
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(nombre.trim());
        proveedor.setApellidos(apellidos.trim());
        proveedor.setContacto(contacto.trim());
        proveedor.setDireccion(direccion.trim());
        proveedor.setCorreo(
            (correo != null && !correo.trim().isEmpty()) ? correo.trim() : null
        );
        proveedor.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new ProveedorDAO().insertar(proveedor);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Proveedor registrado correctamente."
                  : "Ocurrió un error al registrar el proveedor.");
        listar(request, response);
    }

    private void actualizar(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {
        String idParam     = request.getParameter("idProveedor");
        String nombre      = request.getParameter("nombre");
        String apellidos   = request.getParameter("apellidos");
        String contacto    = request.getParameter("contacto");
        String direccion   = request.getParameter("direccion");
        String correo      = request.getParameter("correo");
        String estadoParam = request.getParameter("estado");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/proveedores?accion=listar");
            return;
        }

        if (nombre == null    || nombre.trim().isEmpty()    ||
            apellidos == null || apellidos.trim().isEmpty() ||
            contacto == null  || contacto.trim().isEmpty()  ||
            direccion == null || direccion.trim().isEmpty()) {

            request.setAttribute("error",
                "Nombre, apellidos, contacto y dirección son obligatorios.");
            mostrarFormularioEditar(request, response);
            return;
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(Integer.parseInt(idParam));
        proveedor.setNombre(nombre.trim());
        proveedor.setApellidos(apellidos.trim());
        proveedor.setContacto(contacto.trim());
        proveedor.setDireccion(direccion.trim());
        proveedor.setCorreo(
            (correo != null && !correo.trim().isEmpty()) ? correo.trim() : null
        );
        proveedor.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new ProveedorDAO().actualizar(proveedor);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Proveedor actualizado correctamente."
                  : "Ocurrió un error al actualizar el proveedor.");
        listar(request, response);
    }

    private void eliminar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/proveedores?accion=listar");
            return;
        }
        boolean exito = new ProveedorDAO().eliminar(Integer.parseInt(idParam));
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Proveedor desactivado correctamente."
                  : "No se pudo desactivar el proveedor.");
        listar(request, response);
    }

    // exportar()  reporte Excel de proveedores
    
    private void exportar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Proveedor> proveedores = new ProveedorDAO().listar();
            ExcelUtil.exportarProveedores(proveedores, response);
        } catch (Exception e) {
            System.out.println("✘ Error al exportar proveedores: " + e.getMessage());
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