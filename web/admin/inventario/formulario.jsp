<%-- 
     PROYECTO  : Urban Caps
     ARCHIVO   : formulario.jsp
     UBICACIÓN : Web Pages/admin/inventario/formulario.jsp
     DESCRIPCIÓN: Formulario para registrar o editar un producto en el inventario. Requiere INVENTARIO_CREAR
     o INVENTARIO_EDITAR según la acción.

     VARIABLES que recibe del InventarioServlet:
      producto    : objeto Inventario
      proveedores : List<Proveedor> activos para el selector
      categorias  : List<Categoria> para el selector
      accion      : "guardar" o "actualizar"
      titulo      : "Registrar Producto" o "Editar Producto"
      --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="com.urbancaps.modelo.bean.Inventario" %>
<%@ page import="com.urbancaps.modelo.bean.Proveedor" %>
<%@ page import="com.urbancaps.modelo.bean.Categoria" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>

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

    Inventario producto      = (Inventario)  request.getAttribute("producto");
    List<Proveedor> proveedores = (List<Proveedor>) request.getAttribute("proveedores");
    List<Categoria> categorias  = (List<Categoria>) request.getAttribute("categorias");
    String accion            = (String)      request.getAttribute("accion");
    String titulo            = (String)      request.getAttribute("titulo");

    if (producto == null) producto = new Inventario();
    if (accion   == null) accion   = "guardar";
    if (titulo   == null) titulo   = "Registrar Producto en Inventario";

    boolean esNuevo = accion.equals("guardar");
    boolean tienePermiso = privs != null &&
        ((esNuevo  && privs.contains("INVENTARIO_CREAR")) ||
         (!esNuevo && privs.contains("INVENTARIO_EDITAR")));

    if (!tienePermiso) {
        response.sendRedirect(request.getContextPath() + "/inventario?accion=listar");
        return;
    }

    // Formateamos la fecha para el input type="date" (yyyy-MM-dd)
    String fechaFormateada = "";
    if (producto.getFechaIngreso() != null) {
        fechaFormateada = new SimpleDateFormat("yyyy-MM-dd")
                              .format(producto.getFechaIngreso());
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
                <a href="${pageContext.request.contextPath}/inventario?accion=listar" class="nav-item activo">
                    <i class="bi bi-box-seam"></i>Inventario
                </a>
            <% } %>
            <% if (privs.contains("CATALOGO_VER")) { %>
                <a href="${pageContext.request.contextPath}/catalogo?accion=listar" class="nav-item">
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
                    Urban Caps &rsaquo; Inventario &rsaquo; <%= titulo %>
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
                            <%= esNuevo ? "Registra un nuevo producto en el inventario" : "Modifica los datos del producto" %>
                        </div>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/inventario" method="post">
                    <input type="hidden" name="accion"       value="<%= accion %>">
                    <input type="hidden" name="idInventario" value="<%= producto.getIdInventario() %>">

                    <div class="form-cuerpo">

                        <%-- Nombre del producto --%>
                        <div class="form-grupo">
                            <label class="form-etiqueta" for="nombreProducto">
                                Nombre del Producto <span style="color:var(--color-error);">*</span>
                            </label>
                            <input type="text"
                                   id="nombreProducto" name="nombreProducto"
                                   class="form-campo"
                                   placeholder="Ej: Gorra Snapback New York Yankees"
                                   value="<%= producto.getNombreProducto() != null ? producto.getNombreProducto() : "" %>"
                                   required maxlength="150">
                        </div>

                        <%-- Fila: Proveedor y Categoría --%>
                        <div class="form-grid-2">
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="idProveedor">
                                    Proveedor <span style="color:var(--color-error);">*</span>
                                </label>
                                <select id="idProveedor" name="idProveedor"
                                        class="form-campo" required>
                                    <option value="">— Selecciona un proveedor —</option>
                                    <%
                                        if (proveedores != null) {
                                            for (Proveedor p : proveedores) {
                                                String sel = (p.getIdProveedor() == producto.getIdProveedor())
                                                             ? "selected" : "";
                                    %>
                                        <option value="<%= p.getIdProveedor() %>" <%= sel %>>
                                            <%= p.getNombre() %> <%= p.getApellidos() %>
                                        </option>
                                    <% }} %>
                                </select>
                            </div>
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="idCategoria">
                                    Categoría <span style="color:var(--color-error);">*</span>
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
                            </div>
                        </div>

                        <%-- Fila: Cantidad y Precio de Compra --%>
                        <div class="form-grid-2">
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="cantidad">
                                    Cantidad <span style="color:var(--color-error);">*</span>
                                </label>
                                <input type="number"
                                       id="cantidad" name="cantidad"
                                       class="form-campo"
                                       placeholder="Ej: 50"
                                       value="<%= producto.getCantidad() > 0 ? producto.getCantidad() : "" %>"
                                       required min="0">
                                <div class="form-ayuda">Unidades que ingresan al inventario.</div>
                            </div>
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="precioCompra">
                                    Precio de Compra <span style="color:var(--color-error);">*</span>
                                </label>
                                <input type="number"
                                       id="precioCompra" name="precioCompra"
                                       class="form-campo"
                                       placeholder="Ej: 35000"
                                       value="<%= producto.getPrecioCompra() > 0 ? producto.getPrecioCompra() : "" %>"
                                       required min="0" step="0.01">
                                <div class="form-ayuda">Precio pagado al proveedor.</div>
                            </div>
                        </div>

                        <%-- Fila: Fecha y Estado --%>
                        <div class="form-grid-2">
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="fechaIngreso">
                                    Fecha de Ingreso <span style="color:var(--color-error);">*</span>
                                </label>
                                <input type="date"
                                       id="fechaIngreso" name="fechaIngreso"
                                       class="form-campo"
                                       value="<%= fechaFormateada %>"
                                       required>
                            </div>
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="estado">
                                    Estado <span style="color:var(--color-error);">*</span>
                                </label>
                                <select id="estado" name="estado" class="form-campo">
                                    <option value="1" <%= producto.getEstado() == 1 ? "selected" : "" %>>Activo</option>
                                    <option value="0" <%= producto.getEstado() == 0 ? "selected" : "" %>>Inactivo</option>
                                </select>
                            </div>
                        </div>

                    </div>

                    <div class="botones-form">
                        <button type="submit" class="btn-guardar">
                            <i class="bi bi-check-lg"></i>
                            <%= esNuevo ? "Registrar en Inventario" : "Guardar Cambios" %>
                        </button>
                        <a href="${pageContext.request.contextPath}/inventario?accion=listar"
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
