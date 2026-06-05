<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="com.urbancaps.modelo.bean.Catalogo" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");
    String nombreRol     = (String) session.getAttribute("nombreRol");
    List<String> privs   = (List<String>) session.getAttribute("nombresPrivilegios");
    if (privs == null || !privs.contains("CATALOGO_VER")) { response.sendRedirect(request.getContextPath() + "/dashboard"); return; }
    boolean puedeCrear    = privs.contains("CATALOGO_CREAR");
    boolean puedeEditar   = privs.contains("CATALOGO_EDITAR");
    boolean puedeEliminar = privs.contains("CATALOGO_ELIMINAR");
    boolean puedeExportar = privs.contains("CATALOGO_EXPORTAR");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Catálogo</title>
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
            <% if (privs.contains("INVENTARIO_VER")) { %><a href="${pageContext.request.contextPath}/inventario?accion=listar" class="nav-item"><i class="bi bi-box-seam"></i>Inventario</a><% } %>
            <% if (privs.contains("CATALOGO_VER")) { %><a href="${pageContext.request.contextPath}/catalogo?accion=listar" class="nav-item activo"><i class="bi bi-grid-3x3-gap"></i>Catálogo</a><% } %>
        </nav>
        <div class="sidebar-footer">
            <div class="usuario-info"><div class="usuario-avatar"><%= nombreUsuario != null ? String.valueOf(nombreUsuario.charAt(0)).toUpperCase() : "U" %></div><div><div class="usuario-nombre"><%= nombreUsuario %></div><div class="usuario-rol"><%= nombreRol %></div></div></div>
            <a href="${pageContext.request.contextPath}/usuarios?accion=logout" class="btn-logout"><i class="bi bi-box-arrow-left"></i>Cerrar sesión</a>
        </div>
    </aside>
    <main class="main-content">
        <header class="top-header">
            <div><div class="header-titulo">Catálogo</div><div class="header-ruta">Urban Caps &rsaquo; Gestión de Negocio &rsaquo; Catálogo</div></div>
            <a href="${pageContext.request.contextPath}/website" class="btn-visitar" target="_blank"><i class="bi bi-globe2"></i>Ver Website</a>
        </header>
        <div class="content-area">
            <% if (request.getAttribute("mensaje") != null) { %><div class="alerta alerta-exito"><i class="bi bi-check-circle-fill"></i><%= request.getAttribute("mensaje") %></div><% } %>
            <% if (request.getAttribute("error") != null) { %><div class="alerta alerta-error"><i class="bi bi-exclamation-circle-fill"></i><%= request.getAttribute("error") %></div><% } %>
            <div class="pagina-card">
                <div class="pagina-card-header">
                    <div><div class="pagina-card-titulo">Productos Publicados</div><div class="pagina-card-desc">Gorras visibles en el website público para los clientes</div></div>
                    <div style="display:flex; gap:8px;">
                        <% if (puedeCrear) { %>
                            <a href="${pageContext.request.contextPath}/catalogo?accion=nuevo" class="btn-nuevo"><i class="bi bi-plus-lg"></i>Publicar Producto</a>
                        <% } %>
                        <% if (puedeExportar) { %>
                            <a href="${pageContext.request.contextPath}/catalogo?accion=exportar" class="btn-cancelar"><i class="bi bi-file-earmark-excel"></i>Exportar Excel</a>
                        <% } %>
                    </div>
                </div>
                <div class="tabla-contenedor">
                    <table class="tabla-admin">
                        <thead><tr><th>#</th><th>Imagen</th><th>Título</th><th>Categoría</th><th>Precio Venta</th><th>Stock</th><th>Visibilidad</th><% if (puedeEditar || puedeEliminar) { %><th>Acciones</th><% } %></tr></thead>
                        <tbody>
                            <%
                                List<Catalogo> productos = (List<Catalogo>) request.getAttribute("productos");
                                if (productos != null && !productos.isEmpty()) {
                                    for (Catalogo cat : productos) {
                            %>
                            <tr>
                                <td style="color:#555;font-size:12px;">#<%= cat.getIdCatalogo() %></td>
                                <td>
                                    <% if (cat.getImagen() != null && !cat.getImagen().isEmpty()) { %>
                                        <img src="${pageContext.request.contextPath}/img/gorras/<%= cat.getImagen() %>" alt="<%= cat.getTitulo() %>" style="width:48px;height:48px;object-fit:cover;border-radius:6px;border:1px solid var(--gris-borde);">
                                    <% } else { %>
                                        <div style="width:48px;height:48px;background:var(--gris-input);border-radius:6px;display:flex;align-items:center;justify-content:center;font-size:20px;border:1px solid var(--gris-borde);">🧢</div>
                                    <% } %>
                                </td>
                                <td>
                                    <div style="font-weight:500;color:var(--blanco);margin-bottom:2px;"><%= cat.getTitulo() %></div>
                                    <div style="font-size:11px;color:#555;">Inventario: <%= cat.getNombreProducto() %></div>
                                </td>
                                <td><span style="background:var(--acento-suave);color:var(--acento);border:1px solid var(--acento-borde);padding:2px 8px;border-radius:20px;font-size:11px;font-weight:600;"><%= cat.getNombreCategoria() %></span></td>
                                <td style="font-weight:600;color:var(--acento);">$<%= String.format("%,.0f", cat.getPrecioVenta()) %></td>
                                <td><span style="font-weight:600;color:<%= cat.getStock() > 10 ? "var(--color-exito)" : cat.getStock() > 0 ? "var(--color-alerta)" : "var(--color-error)" %>;"><%= cat.getStock() %> uds</span></td>
                                <td>
                                    <% if (cat.getEstado() == 1) { %><span class="badge-estado badge-activo"><i class="bi bi-eye" style="font-size:9px;"></i>Visible</span>
                                    <% } else { %><span class="badge-estado badge-inactivo"><i class="bi bi-eye-slash" style="font-size:9px;"></i>Oculto</span><% } %>
                                </td>
                                <% if (puedeEditar || puedeEliminar) { %>
                                <td><div class="col-acciones">
                                    <% if (puedeEditar) { %><a href="${pageContext.request.contextPath}/catalogo?accion=editar&id=<%= cat.getIdCatalogo() %>" class="btn-editar"><i class="bi bi-pencil"></i>Editar</a><% } %>
                                    <% if (puedeEliminar) { %><a href="${pageContext.request.contextPath}/catalogo?accion=eliminar&id=<%= cat.getIdCatalogo() %>" class="btn-eliminar" onclick="return confirmarOcultar('<%= cat.getTitulo() %>')"><i class="bi bi-eye-slash"></i>Ocultar</a><% } %>
                                </div></td>
                                <% } %>
                            </tr>
                            <% } } else { %>
                            <tr><td colspan="8" class="tabla-vacia"><i class="bi bi-grid-3x3-gap" style="font-size:32px;display:block;margin-bottom:8px;opacity:.4;"></i>No hay productos publicados en el catálogo.<% if (puedeCrear) { %><br><a href="${pageContext.request.contextPath}/catalogo?accion=nuevo" style="color:var(--acento);font-size:13px;margin-top:8px;display:inline-block;">+ Publicar el primer producto</a><% } %></td></tr>
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
<script>function confirmarOcultar(titulo) { return confirm('¿Ocultar "' + titulo + '" del website?\n\nEl producto dejará de ser visible para los clientes.\nPuedes volver a activarlo editando el producto.'); }</script>
</body></html>
