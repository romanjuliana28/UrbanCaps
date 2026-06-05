<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="com.urbancaps.modelo.bean.Inventario" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");
    String nombreRol     = (String) session.getAttribute("nombreRol");
    List<String> privs   = (List<String>) session.getAttribute("nombresPrivilegios");
    if (privs == null || !privs.contains("INVENTARIO_VER")) { response.sendRedirect(request.getContextPath() + "/dashboard"); return; }
    boolean puedeCrear    = privs.contains("INVENTARIO_CREAR");
    boolean puedeEditar   = privs.contains("INVENTARIO_EDITAR");
    boolean puedeEliminar = privs.contains("INVENTARIO_ELIMINAR");
    boolean puedeExportar = privs.contains("INVENTARIO_EXPORTAR");
    String textoBusqueda  = (String) request.getAttribute("textoBusqueda");
    if (textoBusqueda == null) textoBusqueda = "";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Inventario</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<div class="admin-wrapper">
    <aside class="sidebar">
        <div class="sidebar-logo"><div class="sidebar-logo-icon">🧢</div><div><div class="sidebar-logo-texto">URBAN CAPS</div><div class="sidebar-logo-sub">Admin Panel</div></div></div>
        <nav class="sidebar-nav">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-item"><i class="bi bi-grid-1x2"></i>Dashboard</a>
            <% if (privs.contains("ROLES_VER") || privs.contains("PRIVILEGIOS_VER") || privs.contains("USUARIOS_VER")) { %><div class="nav-grupo-titulo">Gestión de Acceso</div><% } %>
            <% if (privs.contains("ROLES_VER")) { %><a href="${pageContext.request.contextPath}/roles?accion=listar" class="nav-item"><i class="bi bi-shield"></i>Roles</a><% } %>
            <% if (privs.contains("PRIVILEGIOS_VER")) { %><a href="${pageContext.request.contextPath}/privilegios?accion=listar" class="nav-item"><i class="bi bi-key"></i>Privilegios</a><% } %>
            <% if (privs.contains("USUARIOS_VER")) { %><a href="${pageContext.request.contextPath}/usuarios?accion=listar" class="nav-item"><i class="bi bi-people"></i>Usuarios</a><% } %>
            <% if (privs.contains("PROVEEDORES_VER") || privs.contains("INVENTARIO_VER") || privs.contains("CATALOGO_VER")) { %><div class="nav-grupo-titulo">Gestión de Negocio</div><% } %>
            <% if (privs.contains("PROVEEDORES_VER")) { %><a href="${pageContext.request.contextPath}/proveedores?accion=listar" class="nav-item"><i class="bi bi-truck"></i>Proveedores</a><% } %>
            <% if (privs.contains("INVENTARIO_VER")) { %><a href="${pageContext.request.contextPath}/inventario?accion=listar" class="nav-item activo"><i class="bi bi-box-seam"></i>Inventario</a><% } %>
            <% if (privs.contains("CATALOGO_VER")) { %><a href="${pageContext.request.contextPath}/catalogo?accion=listar" class="nav-item"><i class="bi bi-grid-3x3-gap"></i>Catálogo</a><% } %>
        </nav>
        <div class="sidebar-footer">
            <div class="usuario-info"><div class="usuario-avatar"><%= nombreUsuario != null ? String.valueOf(nombreUsuario.charAt(0)).toUpperCase() : "U" %></div><div><div class="usuario-nombre"><%= nombreUsuario %></div><div class="usuario-rol"><%= nombreRol %></div></div></div>
            <a href="${pageContext.request.contextPath}/usuarios?accion=logout" class="btn-logout"><i class="bi bi-box-arrow-left"></i>Cerrar sesión</a>
        </div>
    </aside>
    <main class="main-content">
        <header class="top-header">
            <div><div class="header-titulo">Inventario</div><div class="header-ruta">Urban Caps &rsaquo; Gestión de Negocio &rsaquo; Inventario</div></div>
        </header>
        <div class="content-area">
            <% if (request.getAttribute("mensaje") != null) { %><div class="alerta alerta-exito"><i class="bi bi-check-circle-fill"></i><%= request.getAttribute("mensaje") %></div><% } %>
            <% if (request.getAttribute("error") != null) { %><div class="alerta alerta-error"><i class="bi bi-exclamation-circle-fill"></i><%= request.getAttribute("error") %></div><% } %>
            <div class="pagina-card">
                <div class="pagina-card-header">
                    <div><div class="pagina-card-titulo">Lista de Inventario</div><div class="pagina-card-desc">Productos registrados con su stock disponible</div></div>
                    <div style="display:flex; gap:8px;">
                        <% if (puedeCrear) { %>
                            <a href="${pageContext.request.contextPath}/inventario?accion=nuevo" class="btn-nuevo"><i class="bi bi-plus-lg"></i>Nuevo Producto</a>
                        <% } %>
                        <% if (puedeExportar) { %>
                            <a href="${pageContext.request.contextPath}/inventario?accion=exportar" class="btn-cancelar"><i class="bi bi-file-earmark-excel"></i>Exportar Excel</a>
                        <% } %>
                    </div>
                </div>
                <form action="${pageContext.request.contextPath}/inventario" method="get" class="lista-buscador">
                    <input type="hidden" name="accion" value="buscar">
                    <input type="text" name="texto" class="buscador-input" placeholder="Buscar producto por nombre..." value="<%= textoBusqueda %>">
                    <button type="submit" class="btn-buscar"><i class="bi bi-search"></i>Buscar</button>
                    <% if (!textoBusqueda.isEmpty()) { %><a href="${pageContext.request.contextPath}/inventario?accion=listar" class="btn-cancelar" style="padding:8px 14px;"><i class="bi bi-x-lg"></i>Limpiar</a><% } %>
                </form>
                <div class="tabla-contenedor">
                    <table class="tabla-admin">
                        <thead><tr><th>#</th><th>Producto</th><th>Categoría</th><th>Proveedor</th><th>Cantidad</th><th>Precio Compra</th><th>Fecha Ingreso</th><th>Estado</th><% if (puedeEditar || puedeEliminar) { %><th>Acciones</th><% } %></tr></thead>
                        <tbody>
                            <%
                                List<Inventario> productos = (List<Inventario>) request.getAttribute("productos");
                                if (productos != null && !productos.isEmpty()) {
                                    for (Inventario inv : productos) {
                            %>
                            <tr>
                                <td style="color:#555;font-size:12px;">#<%= inv.getIdInventario() %></td>
                                <td style="font-weight:500;color:var(--blanco);"><%= inv.getNombreProducto() %></td>
                                <td><span style="background:var(--acento-suave);color:var(--acento);border:1px solid var(--acento-borde);padding:2px 8px;border-radius:20px;font-size:11px;font-weight:600;"><%= inv.getNombreCategoria() %></span></td>
                                <td style="font-size:12px;"><%= inv.getNombreProveedor() %></td>
                                <td><span style="font-weight:600;color:<%= inv.getCantidad() > 10 ? "var(--color-exito)" : inv.getCantidad() > 0 ? "var(--color-alerta)" : "var(--color-error)" %>;"><%= inv.getCantidad() %> uds</span></td>
                                <td style="font-size:12px;">$<%= String.format("%,.0f", inv.getPrecioCompra()) %></td>
                                <td style="font-size:12px;color:#666;"><%= inv.getFechaIngreso() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(inv.getFechaIngreso()) : "—" %></td>
                                <td>
                                    <% if (inv.getEstado() == 1) { %><span class="badge-estado badge-activo"><i class="bi bi-circle-fill" style="font-size:7px;"></i>Activo</span>
                                    <% } else { %><span class="badge-estado badge-inactivo"><i class="bi bi-circle-fill" style="font-size:7px;"></i>Inactivo</span><% } %>
                                </td>
                                <% if (puedeEditar || puedeEliminar) { %>
                                <td><div class="col-acciones">
                                    <% if (puedeEditar) { %><a href="${pageContext.request.contextPath}/inventario?accion=editar&id=<%= inv.getIdInventario() %>" class="btn-editar"><i class="bi bi-pencil"></i>Editar</a><% } %>
                                    <% if (puedeEliminar) { %><a href="${pageContext.request.contextPath}/inventario?accion=eliminar&id=<%= inv.getIdInventario() %>" class="btn-eliminar" onclick="return confirmarEliminar('<%= inv.getNombreProducto() %>')"><i class="bi bi-trash3"></i>Desactivar</a><% } %>
                                </div></td>
                                <% } %>
                            </tr>
                            <% } } else { %>
                            <tr><td colspan="9" class="tabla-vacia"><i class="bi bi-box-seam" style="font-size:32px;display:block;margin-bottom:8px;opacity:.4;"></i><%= !textoBusqueda.isEmpty() ? "No se encontraron productos con \"" + textoBusqueda + "\"" : "No hay productos en el inventario." %></td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>
<script>function confirmarEliminar(nombre) { return confirm('¿Desactivar el producto "' + nombre + '"?\n\nEl producto no estará disponible para publicar en el catálogo.'); }</script>
</body></html>
