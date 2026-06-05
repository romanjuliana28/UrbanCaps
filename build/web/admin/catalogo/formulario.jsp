<%-- 
     PROYECTO  : Urban Caps
     ARCHIVO   : formulario.jsp
     UBICACIÓN : Web Pages/admin/catalogo/formulario.jsp
     DESCRIPCIÓN: Formulario para publicar o editar un producto
                  en el catálogo del website.
                  Requiere CATALOGO_CREAR o CATALOGO_EDITAR.

     VARIABLES que recibe del CatalogoServlet:
      producto           : objeto Catalogo
      productosInventario: List<Inventario> activos para el selector
      categorias         : List<Categoria> para el selector
      accion             : "guardar" o "actualizar"
      titulo             : título de la página
      --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="com.urbancaps.modelo.bean.Catalogo" %>
<%@ page import="com.urbancaps.modelo.bean.Inventario" %>
<%@ page import="com.urbancaps.modelo.bean.Categoria" %>
<%@ page import="java.util.List" %>

<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");
    String nombreRol     = (String) session.getAttribute("nombreRol");

    List<String> privs =
        (List<String>) session.getAttribute("nombresPrivilegios");

    Catalogo producto              = (Catalogo)    request.getAttribute("producto");
    List<Inventario> productosInv  = (List<Inventario>) request.getAttribute("productosInventario");
    List<Categoria>  categorias    = (List<Categoria>)  request.getAttribute("categorias");
    String accion                  = (String)      request.getAttribute("accion");
    String titulo                  = (String)      request.getAttribute("titulo");

    if (producto == null) producto = new Catalogo();
    if (accion   == null) accion   = "guardar";
    if (titulo   == null) titulo   = "Publicar Producto en Catálogo";

    boolean esNuevo = accion.equals("guardar");
    boolean tienePermiso = privs != null &&
        ((esNuevo  && privs.contains("CATALOGO_CREAR")) ||
         (!esNuevo && privs.contains("CATALOGO_EDITAR")));

    if (!tienePermiso) {
        response.sendRedirect(request.getContextPath() + "/catalogo?accion=listar");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — <%= titulo %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<div class="admin-wrapper">

    <aside class="sidebar">
        <div class="sidebar-logo">
            <div class="sidebar-logo-icon">🧢</div>
            <div>
                <div class="sidebar-logo-texto">URBAN CAPS</div>
                <div class="sidebar-logo-sub">Admin Panel</div>
            </div>
        </div>
        <nav class="sidebar-nav">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">
                <i class="bi bi-grid-1x2"></i>Dashboard
            </a>
            <% if (privs.contains("ROLES_VER") || privs.contains("PRIVILEGIOS_VER") || privs.contains("USUARIOS_VER")) { %>
                <div class="nav-grupo-titulo">Gestión de Acceso</div>
            <% } %>
            <% if (privs.contains("ROLES_VER")) { %>
                <a href="${pageContext.request.contextPath}/roles?accion=listar" class="nav-item">
                    <i class="bi bi-shield"></i>Roles
                </a>
            <% } %>
            <% if (privs.contains("PRIVILEGIOS_VER")) { %>
                <a href="${pageContext.request.contextPath}/privilegios?accion=listar" class="nav-item">
                    <i class="bi bi-key"></i>Privilegios
                </a>
            <% } %>
            <% if (privs.contains("USUARIOS_VER")) { %>
                <a href="${pageContext.request.contextPath}/usuarios?accion=listar" class="nav-item">
                    <i class="bi bi-people"></i>Usuarios
                </a>
            <% } %>
            <% if (privs.contains("PROVEEDORES_VER") || privs.contains("INVENTARIO_VER") || privs.contains("CATALOGO_VER")) { %>
                <div class="nav-grupo-titulo">Gestión de Negocio</div>
            <% } %>
            <% if (privs.contains("PROVEEDORES_VER")) { %>
                <a href="${pageContext.request.contextPath}/proveedores?accion=listar" class="nav-item">
                    <i class="bi bi-truck"></i>Proveedores
                </a>
            <% } %>
            <% if (privs.contains("INVENTARIO_VER")) { %>
                <a href="${pageContext.request.contextPath}/inventario?accion=listar" class="nav-item">
                    <i class="bi bi-box-seam"></i>Inventario
                </a>
            <% } %>
            <% if (privs.contains("CATALOGO_VER")) { %>
                <a href="${pageContext.request.contextPath}/catalogo?accion=listar" class="nav-item activo">
                    <i class="bi bi-grid-3x3-gap"></i>Catálogo
                </a>
            <% } %>
        </nav>
        <div class="sidebar-footer">
            <div class="usuario-info">
                <div class="usuario-avatar">
                    <%= nombreUsuario != null ? String.valueOf(nombreUsuario.charAt(0)).toUpperCase() : "U" %>
                </div>
                <div>
                    <div class="usuario-nombre"><%= nombreUsuario %></div>
                    <div class="usuario-rol"><%= nombreRol %></div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/usuarios?accion=logout" class="btn-logout">
                <i class="bi bi-box-arrow-left"></i>Cerrar sesión
            </a>
        </div>
    </aside>

    <main class="main-content">
        <header class="top-header">
            <div>
                <div class="header-titulo"><%= titulo %></div>
                <div class="header-ruta">
                    Urban Caps &rsaquo; Catálogo &rsaquo; <%= titulo %>
                </div>
            </div>
        </header>

        <div class="content-area">

            <% if (request.getAttribute("error") != null) { %>
                <div class="alerta alerta-error">
                    <i class="bi bi-exclamation-circle-fill"></i>
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <div class="pagina-card">
                <div class="pagina-card-header">
                    <div>
                        <div class="pagina-card-titulo"><%= titulo %></div>
                        <div class="pagina-card-desc">
                            <%= esNuevo
                                ? "Selecciona un producto del inventario y configura cómo se verá en el website"
                                : "Modifica los datos del producto publicado" %>
                        </div>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/catalogo" method="post">
                    <input type="hidden" name="accion"      value="<%= accion %>">
                    <input type="hidden" name="idCatalogo"  value="<%= producto.getIdCatalogo() %>">

                    <div class="form-cuerpo">

                        <%-- Fila: Producto del inventario y Categoría --%>
                        <div class="form-grid-2">
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="idInventario">
                                    Producto del Inventario <span style="color:var(--color-error);">*</span>
                                </label>
                                <select id="idInventario" name="idInventario"
                                        class="form-campo" required>
                                    <option value="">— Selecciona un producto —</option>
                                    <%
                                        if (productosInv != null) {
                                            for (Inventario inv : productosInv) {
                                                String sel = (inv.getIdInventario() == producto.getIdInventario())
                                                             ? "selected" : "";
                                    %>
                                        <option value="<%= inv.getIdInventario() %>" <%= sel %>>
                                            <%= inv.getNombreProducto() %>
                                            (Stock: <%= inv.getCantidad() %>)
                                        </option>
                                    <% }} %>
                                </select>
                                <div class="form-ayuda">
                                    Solo aparecen productos activos del inventario.
                                </div>
                            </div>
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="idCategoria">
                                    Categoría en el sitio WEB <span style="color:var(--color-error);">*</span>
                                </label>
                                <select id="idCategoria" name="idCategoria"
                                        class="form-campo" required>
                                    <option value="">— Selecciona una categoría —</option>
                                    <%
                                        if (categorias != null) {
                                            for (Categoria c : categorias) {
                                                String sel = (c.getIdCategoria() == producto.getIdCategoria())
                                                             ? "selected" : "";
                                    %>
                                        <option value="<%= c.getIdCategoria() %>" <%= sel %>>
                                            <%= c.getNombreCategoria() %>
                                        </option>
                                    <% }} %>
                                </select>
                                <div class="form-ayuda">
                                    Define en qué sección del header aparece.
                                </div>
                            </div>
                        </div>

                        <%-- Título comercial --%>
                        <div class="form-grupo">
                            <label class="form-etiqueta" for="titulo_prod">
                                Título del Producto <span style="color:var(--color-error);">*</span>
                            </label>
                            <input type="text"
                                   id="titulo_prod" name="titulo"
                                   class="form-campo"
                                   placeholder="Ej: Gorra Snapback Premium New York"
                                   value="<%= producto.getTitulo() != null ? producto.getTitulo() : "" %>"
                                   required maxlength="150">
                            <div class="form-ayuda">
                                Nombre visible para el cliente en el website.
                            </div>
                        </div>

                        <%-- Descripción --%>
                        <div class="form-grupo">
                            <label class="form-etiqueta" for="descripcion">
                                Descripción <span style="color:var(--color-error);">*</span>
                            </label>
                            <textarea id="descripcion" name="descripcion"
                                      class="form-campo"
                                      placeholder="Describe la gorra: material, talla, colores disponibles, características especiales..."
                                      required style="min-height:110px;"><%= producto.getDescripcion() != null ? producto.getDescripcion() : "" %></textarea>
                            <div class="form-ayuda">
                                Esta descripción aparece en la tarjeta del producto en el website.
                            </div>
                        </div>

                        <%-- Fila: Precio de venta y Stock --%>
                        <div class="form-grid-2">
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="precioVenta">
                                    Precio de Venta <span style="color:var(--color-error);">*</span>
                                </label>
                                <input type="number"
                                       id="precioVenta" name="precioVenta"
                                       class="form-campo"
                                       placeholder="Ej: 65000"
                                       value="<%= producto.getPrecioVenta() > 0 ? producto.getPrecioVenta() : "" %>"
                                       required min="0" step="0.01">
                                <div class="form-ayuda">Precio visible al cliente en el sitio WEB.</div>
                            </div>
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="stock">
                                    Stock para la Venta <span style="color:var(--color-error);">*</span>
                                </label>
                                <input type="number"
                                       id="stock" name="stock"
                                       class="form-campo"
                                       placeholder="Ej: 20"
                                       value="<%= producto.getStock() >= 0 ? producto.getStock() : "" %>"
                                       required min="0">
                                <div class="form-ayuda">Unidades disponibles para vender.</div>
                            </div>
                        </div>

                        <%-- Imagen --%>
                        <div class="form-grupo">
                            <label class="form-etiqueta" for="imagen">
                                Nombre del archivo de imagen
                            </label>
                            <input type="text"
                                   id="imagen" name="imagen"
                                   class="form-campo"
                                   placeholder="Ej: snapback-ny-001.jpg"
                                   value="<%= producto.getImagen() != null ? producto.getImagen() : "" %>"
                                   maxlength="255">
                            <div class="form-ayuda">
                                Opcional. Coloca la imagen en la carpeta
                                <strong>Web Pages/img/gorras/</strong> y escribe aquí solo el nombre del archivo.
                            </div>
                        </div>

                        <%-- Fila: Visibilidad --%>
                        <div class="form-grupo">
                            <label class="form-etiqueta" for="estado">
                                Visibilidad en el sitio WEB <span style="color:var(--color-error);">*</span>
                            </label>
                            <select id="estado" name="estado" class="form-campo">
                                <option value="1" <%= producto.getEstado() == 1 || esNuevo ? "selected" : "" %>>
                                    Visible — el cliente puede verlo
                                </option>
                                <option value="0" <%= producto.getEstado() == 0 && !esNuevo ? "selected" : "" %>>
                                    Oculto — no aparece en el website
                                </option>
                            </select>
                        </div>

                    </div>

                    <div class="botones-form">
                        <button type="submit" class="btn-guardar">
                            <i class="bi bi-check-lg"></i>
                            <%= esNuevo ? "Publicar en Catálogo" : "Guardar Cambios" %>
                        </button>
                        <a href="${pageContext.request.contextPath}/catalogo?accion=listar"
                           class="btn-cancelar">
                            <i class="bi bi-x-lg"></i>Cancelar
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>
</body>
</html>
