//  ARCHIVO   : CatalogoServlet.java
//  VERSIÓN   : Actualizado con acción exportar Excel
//  DESCRIPCIÓN: Servlet del módulo Catalogo.

package com.urbancaps.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import com.urbancaps.modelo.bean.Catalogo;
import com.urbancaps.modelo.bean.Inventario;
import com.urbancaps.modelo.bean.Categoria;
import com.urbancaps.modelo.dao.CatalogoDAO;
import com.urbancaps.modelo.dao.InventarioDAO;
import com.urbancaps.modelo.dao.CategoriaDAO;
import com.urbancaps.util.ExcelUtil;

@WebServlet(name = "CatalogoServlet", urlPatterns = {"/catalogo", "/website"})
public class CatalogoServlet extends HttpServlet {

    // Número de WhatsApp del negocio
    private static final String WHATSAPP_NUMERO = "573152741191";

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Website públic,  no requiere sesión
        if ("/website".equals(path)) {
            manejarWebsite(request, response);
            return;
        }

        // Panel admin,  requiere sesión
        if (!verificarSesion(request, response)) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar":   listarAdmin(request, response);           break;
            case "nuevo":    mostrarFormularioNuevo(request, response); break;
            case "editar":   mostrarFormularioEditar(request, response); break;
            case "eliminar": eliminar(request, response);               break;
            case "exportar": exportar(request, response);               break;
            default:         listarAdmin(request, response);            break;
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
            default:           listarAdmin(request, response); break;
        }
    }

    // Website público

    private void manejarWebsite(HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "inicio";

        switch (accion) {
            case "categoria": filtrarPorCategoria(request, response); break;
            case "buscar":    buscarWebsite(request, response);       break;
            default:          mostrarWebsite(request, response);      break;
        }
    }

    private void mostrarWebsite(HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {
        CatalogoDAO dao = new CatalogoDAO();
        CategoriaDAO catDAO = new CategoriaDAO();
        request.setAttribute("productos",   dao.listarActivos());
        request.setAttribute("categorias",  catDAO.listarActivas());
        request.setAttribute("whatsapp",    WHATSAPP_NUMERO);
        request.getRequestDispatcher("/cliente/index.jsp")
               .forward(request, response);
    }

    private void filtrarPorCategoria(HttpServletRequest request,
                                      HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            mostrarWebsite(request, response);
            return;
        }
        int idCategoria = Integer.parseInt(idParam);
        CategoriaDAO catDAO = new CategoriaDAO();
        List<Categoria> categorias = catDAO.listarActivas();
        Categoria cat = catDAO.obtenerPorId(idCategoria);
        String nombreCat = cat != null ? cat.getNombreCategoria() : "Categoría";

        request.setAttribute("productos",        new CatalogoDAO().listarPorCategoria(idCategoria));
        request.setAttribute("categorias",       categorias);
        request.setAttribute("categoriaActiva",  idCategoria);
        request.setAttribute("tituloPagina",     "Gorras " + nombreCat);
        request.setAttribute("whatsapp",         WHATSAPP_NUMERO);
        request.getRequestDispatcher("/cliente/index.jsp")
               .forward(request, response);
    }

    private void buscarWebsite(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {
        String texto = request.getParameter("texto");
        if (texto == null || texto.trim().isEmpty()) {
            mostrarWebsite(request, response);
            return;
        }
        List<Catalogo> productos = new CatalogoDAO().buscar(texto.trim());
        request.setAttribute("productos",      productos);
        request.setAttribute("categorias",     new CategoriaDAO().listarActivas());
        request.setAttribute("textoBusqueda",  texto.trim());
        request.setAttribute("whatsapp",       WHATSAPP_NUMERO);
        if (productos.isEmpty()) {
            request.setAttribute("sinResultados",
                "No encontramos gorras que coincidan con \"" + texto.trim() + "\".");
        }
        request.getRequestDispatcher("/cliente/index.jsp")
               .forward(request, response);
    }

    // Panel admin

    private void listarAdmin(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {
        List<Catalogo> productos = new CatalogoDAO().listar();
        request.setAttribute("productos", productos);
        request.getRequestDispatcher("/admin/catalogo/lista.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request,
                                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("producto", new Catalogo());
        request.setAttribute("accion", "guardar");
        request.setAttribute("titulo", "Publicar Producto en Catálogo");
        cargarSelectores(request);
        request.getRequestDispatcher("/admin/catalogo/formulario.jsp")
               .forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request,
                                          HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/catalogo?accion=listar");
            return;
        }
        Catalogo producto = new CatalogoDAO().obtenerPorId(Integer.parseInt(idParam));
        if (producto == null) {
            request.setAttribute("error", "El producto no fue encontrado.");
            listarAdmin(request, response);
            return;
        }
        cargarSelectores(request);
        request.setAttribute("producto", producto);
        request.setAttribute("accion", "actualizar");
        request.setAttribute("titulo", "Editar Producto del Catálogo");
        request.getRequestDispatcher("/admin/catalogo/formulario.jsp")
               .forward(request, response);
    }

    private void guardar(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String idInventarioParam = request.getParameter("idInventario");
        String idCategoriaParam  = request.getParameter("idCategoria");
        String titulo            = request.getParameter("titulo");
        String descripcion       = request.getParameter("descripcion");
        String precioVentaParam  = request.getParameter("precioVenta");
        String imagen            = request.getParameter("imagen");
        String stockParam        = request.getParameter("stock");
        String estadoParam       = request.getParameter("estado");

        if (idInventarioParam == null || idInventarioParam.trim().isEmpty() ||
            idCategoriaParam  == null || idCategoriaParam.trim().isEmpty()  ||
            titulo == null            || titulo.trim().isEmpty()             ||
            descripcion == null       || descripcion.trim().isEmpty()        ||
            precioVentaParam == null  || precioVentaParam.trim().isEmpty()   ||
            stockParam == null        || stockParam.trim().isEmpty()) {

            request.setAttribute("error", "Todos los campos obligatorios deben completarse.");
            request.setAttribute("producto", new Catalogo());
            request.setAttribute("accion", "guardar");
            request.setAttribute("titulo", "Publicar Producto en Catálogo");
            cargarSelectores(request);
            request.getRequestDispatcher("/admin/catalogo/formulario.jsp")
                   .forward(request, response);
            return;
        }

        Catalogo producto = new Catalogo();
        producto.setIdInventario(Integer.parseInt(idInventarioParam));
        producto.setIdCategoria(Integer.parseInt(idCategoriaParam));
        producto.setTitulo(titulo.trim());
        producto.setDescripcion(descripcion.trim());
        producto.setPrecioVenta(
            Double.parseDouble(precioVentaParam.replace(",", "."))
        );
        producto.setImagen(
            (imagen != null && !imagen.trim().isEmpty()) ? imagen.trim() : null
        );
        producto.setStock(Integer.parseInt(stockParam));
        producto.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new CatalogoDAO().insertar(producto);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Producto publicado en el catálogo correctamente."
                  : "Ocurrió un error al publicar el producto.");
        listarAdmin(request, response);
    }

    private void actualizar(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {
        String idParam           = request.getParameter("idCatalogo");
        String idInventarioParam = request.getParameter("idInventario");
        String idCategoriaParam  = request.getParameter("idCategoria");
        String titulo            = request.getParameter("titulo");
        String descripcion       = request.getParameter("descripcion");
        String precioVentaParam  = request.getParameter("precioVenta");
        String imagen            = request.getParameter("imagen");
        String stockParam        = request.getParameter("stock");
        String estadoParam       = request.getParameter("estado");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/catalogo?accion=listar");
            return;
        }

        Catalogo producto = new Catalogo();
        producto.setIdCatalogo(Integer.parseInt(idParam));
        producto.setIdInventario(Integer.parseInt(idInventarioParam));
        producto.setIdCategoria(Integer.parseInt(idCategoriaParam));
        producto.setTitulo(titulo != null ? titulo.trim() : "");
        producto.setDescripcion(descripcion != null ? descripcion.trim() : "");
        producto.setPrecioVenta(
            Double.parseDouble(precioVentaParam.replace(",", "."))
        );
        producto.setImagen(
            (imagen != null && !imagen.trim().isEmpty()) ? imagen.trim() : null
        );
        producto.setStock(stockParam != null ? Integer.parseInt(stockParam) : 0);
        producto.setEstado(estadoParam != null ? Integer.parseInt(estadoParam) : 1);

        boolean exito = new CatalogoDAO().actualizar(producto);
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Producto actualizado correctamente."
                  : "Ocurrió un error al actualizar el producto.");
        listarAdmin(request, response);
    }

    private void eliminar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/catalogo?accion=listar");
            return;
        }
        boolean exito = new CatalogoDAO().eliminar(Integer.parseInt(idParam));
        request.setAttribute(exito ? "mensaje" : "error",
            exito ? "Producto ocultado del website correctamente."
                  : "No se pudo ocultar el producto.");
        listarAdmin(request, response);
    }

    // exportar() reporte Excel del catálogo

    private void exportar(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Catalogo> catalogo = new CatalogoDAO().listar();
            ExcelUtil.exportarCatalogo(catalogo, response);
        } catch (Exception e) {
            System.out.println("✘ Error al exportar catálogo: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el reporte Excel.");
            listarAdmin(request, response);
        }
    }

    private void cargarSelectores(HttpServletRequest request) {
        request.setAttribute("productosInventario",
            new InventarioDAO().listarActivos());
        request.setAttribute("categorias",
            new CategoriaDAO().listar());
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