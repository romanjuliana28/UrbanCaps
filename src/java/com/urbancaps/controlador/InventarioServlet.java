//  PROYECTO  : Urban Caps
//  ARCHIVO   : InventarioServlet.java
//  DESCRIPCIÓN: Servlet del módulo Inventario.

package com.urbancaps.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.urbancaps.modelo.bean.Inventario;
import com.urbancaps.modelo.bean.Proveedor;
import com.urbancaps.modelo.bean.Categoria;
import com.urbancaps.modelo.dao.InventarioDAO;
import com.urbancaps.modelo.dao.ProveedorDAO;
import com.urbancaps.modelo.dao.CategoriaDAO;
import com.urbancaps.util.ExcelUtil;

@WebServlet(name = "InventarioServlet", urlPatterns = {"/inventario"})
public class InventarioServlet extends HttpServlet {

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
            case "buscar":   buscar(request, response);                 break;
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
        List<Inventario> productos = new InventarioDAO().listar();
        request.setAttribute("productos", productos);
        request.getRequestDispatcher("/admin/inventario/lista.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request,
                                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("producto", new Inventario());
        request.setAttribute("accion", "guardar");
        request.setAttribute("titulo", "Registrar Producto en Inventario");
        cargarSelectores(request);
        request.getRequestDispatcher("/admin/inventario/formulario.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request,
                                          HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/inventario?accion=listar");
            return;
        }
        Inventario producto = new InventarioDAO()
                                  .obtenerPorId(Integer.parseInt(idParam));
        if (producto == null) {
            request.setAttribute("error", "El producto no fue encontrado.");
            listar(request, response);
            return;
        }
        cargarSelectores(request);
        request.setAttribute("producto", producto);
        request.setAttribute("accion", "actualizar");
        request.setAttribute("titulo", "Editar Producto de Inventario");
        request.getRequestDispatcher("/admin/inventario/formulario.jsp")
               .forward(request, response);
    }

    private void guardar(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String idProveedorParam  = request.getParameter("idProveedor");
        String idCategoriaParam  = request.getParameter("idCategoria");
        String nombreProducto    = request.getParameter("nombreProducto");
        String cantidadParam     = request.getParameter("cantidad");
        String precioCompraParam = request.getParameter("precioCompra");
        String fechaIngresoParam = request.getParameter("fechaIngreso");
        String estadoParam       = request.getParameter("estado");

        if (idProveedorParam == null || idProveedorParam.trim().isEmpty() ||
            idCategoriaParam == null || idCategoriaParam.trim().isEmpty() ||
            nombreProducto   == null || nombreProducto.trim().isEmpty()   ||
            cantidadParam    == null || cantidadParam.trim().isEmpty()     ||
            precioCompraParam == null || precioCompraParam.trim().isEmpty() ||
            fechaIngresoParam == null || fechaIngresoParam.trim().isEmpty()) {

            request.setAttribute("error", "Todos los campos son obligatorios.");
            request.setAttribute("producto", new Inventario());
            request.setAttribute("accion", "guardar");
            request.setAttribute("titulo", "Registrar Producto en Inventario");
            cargarSelectores(request);
            request.getRequestDispatcher("/admin/inventario/formulario.jsp")
                   .forward(request, response);
            return;
        }

        Date fechaIngreso = parsearFecha(fechaIngresoParam);
        if (fechaIngreso == null) {
            request.setAttribute("error", "El formato de la fecha no es válido.");
            request.setAttribute("producto", new Inventario());
            request.setAttribute("accion", "guardar");
            request.setAttribute("titulo", "Registrar Producto en Inventario");
            cargarSelectores(request);
            request.getRequestDispatcher("/admin/inventario/formulario.jsp")
                   .forward(request, response);
            return;
        }

        Inventario producto = new Inventario();
        producto.setIdProveedor(Integer.parseInt(idProveedorParam));
        producto.setIdCategoria(Integer.parseInt(idCategoriaParam));
        producto.setNombreProducto(nombreProducto.trim());
        producto.setCantidad(Integer.parseInt(cantidadParam));
        producto.setPrecioCompra(
            Double.parseDouble(precioCompraParam.replace(",", "."))
        );
        producto.setFechaIngreso(fechaIngreso);
        producto.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new InventarioDAO().insertar(producto);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Producto registrado en inventario correctamente."
                  : "Ocurrió un error al registrar el producto.");
        listar(request, response);
    }

    private void actualizar(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {
        String idParam           = request.getParameter("idInventario");
        String idProveedorParam  = request.getParameter("idProveedor");
        String idCategoriaParam  = request.getParameter("idCategoria");
        String nombreProducto    = request.getParameter("nombreProducto");
        String cantidadParam     = request.getParameter("cantidad");
        String precioCompraParam = request.getParameter("precioCompra");
        String fechaIngresoParam = request.getParameter("fechaIngreso");
        String estadoParam       = request.getParameter("estado");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/inventario?accion=listar");
            return;
        }

        Date fechaIngreso = parsearFecha(fechaIngresoParam);
        if (fechaIngreso == null) {
            request.setAttribute("error", "El formato de la fecha no es válido.");
            mostrarFormularioEditar(request, response);
            return;
        }

        Inventario producto = new Inventario();
        producto.setIdInventario(Integer.parseInt(idParam));
        producto.setIdProveedor(Integer.parseInt(idProveedorParam));
        producto.setIdCategoria(Integer.parseInt(idCategoriaParam));
        producto.setNombreProducto(nombreProducto != null ? nombreProducto.trim() : "");
        producto.setCantidad(Integer.parseInt(cantidadParam));
        producto.setPrecioCompra(
            Double.parseDouble(precioCompraParam.replace(",", "."))
        );
        producto.setFechaIngreso(fechaIngreso);
        producto.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new InventarioDAO().actualizar(producto);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Producto actualizado correctamente."
                  : "Ocurrió un error al actualizar el producto.");
        listar(request, response);
    }

    private void eliminar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/inventario?accion=listar");
            return;
        }
        boolean exito = new InventarioDAO().eliminar(Integer.parseInt(idParam));
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Producto desactivado del inventario correctamente."
                  : "No se pudo desactivar el producto.");
        listar(request, response);
    }

    private void buscar(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String texto = request.getParameter("texto");
        if (texto == null || texto.trim().isEmpty()) {
            listar(request, response);
            return;
        }
        List<Inventario> productos = new InventarioDAO().buscar(texto.trim());
        request.setAttribute("productos", productos);
        request.setAttribute("textoBusqueda", texto.trim());
        request.getRequestDispatcher("/admin/inventario/lista.jsp")
               .forward(request, response);
    }

    // exportar() reporte Excel de inventario

    private void exportar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Inventario> inventario = new InventarioDAO().listar();
            ExcelUtil.exportarInventario(inventario, response);
        } catch (Exception e) {
            System.out.println("✘ Error al exportar inventario: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el reporte Excel.");
            listar(request, response);
        }
    }

    private void cargarSelectores(HttpServletRequest request) {
        List<Proveedor> proveedores = new ProveedorDAO().listarActivos();
        List<Categoria> categorias  = new CategoriaDAO().listar();
        request.setAttribute("proveedores", proveedores);
        request.setAttribute("categorias",  categorias);
    }

    private Date parsearFecha(String fechaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            return sdf.parse(fechaStr);
        } catch (ParseException e) {
            return null;
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