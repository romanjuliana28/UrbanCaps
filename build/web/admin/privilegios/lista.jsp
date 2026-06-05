<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="com.urbancaps.modelo.bean.Privilegio" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");
    String nombreRol     = (String) session.getAttribute("nombreRol");
    List<String> privs   = (List<String>) session.getAttribute("nombresPrivilegios");
    if (privs == null || !privs.contains("PRIVILEGIOS_VER")) { response.sendRedirect(request.getContextPath() + "/dashboard"); return; }
    boolean puedeCrear    = privs.contains("PRIVILEGIOS_CREAR");
    boolean puedeEditar   = privs.contains("PRIVILEGIOS_EDITAR");
    boolean puedeEliminar = privs.contains("PRIVILEGIOS_ELIMINAR");
    boolean puedeExportar = privs.contains("PRIVILEGIOS_EXPORTAR");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Privilegios</title>
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
            <% if (privs.contains("PRIVILEGIOS_VER")) { %><a href="${pageContext.request.contextPath}/privilegios?accion=listar" class="nav-item activo"><i class="bi bi-key"></i>Privilegios</a><% } %>
            <% if (privs.contains("USUARIOS_VER")) { %><a href="${pageContext.request.contextPath}/usuarios?accion=listar" class="nav-item"><i class="bi bi-people"></i>Usuarios</a><% } %>
            <% if (privs.contains("PROVEEDORES_VER") || privs.contains("INVENTARIO_VER") || privs.contains("CATALOGO_VER")) { %><div class="nav-grupo-titulo">Gestión de Negocio</div><% } %>
            <% if (privs.contains("PROVEEDORES_VER")) { %><a href="${pageContext.request.contextPath}/proveedores?accion=listar" class="nav-item"><i class="bi bi-truck"></i>Proveedores</a><% } %>
            <% if (privs.contains("INVENTARIO_VER")) { %><a href="${pageContext.request.contextPath}/inventario?accion=listar" class="nav-item"><i class="bi bi-box-seam"></i>Inventario</a><% } %>
            <% if (privs.contains("CATALOGO_VER")) { %><a href="${pageContext.request.contextPath}/catalogo?accion=listar" class="nav-item"><i class="bi bi-grid-3x3-gap"></i>Catálogo</a><% } %>
        </nav>
        <div class="sidebar-footer">
            <div class="usuario-info"><div class="usuario-avatar"><%= nombreUsuario != null ? String.valueOf(nombreUsuario.charAt(0)).toUpperCase() : "U" %></div><div><div class="usuario-nombre"><%= nombreUsuario %></div><div class="usuario-rol"><%= nombreRol %></div></div></div>
            <a href="${pageContext.request.contextPath}/usuarios?accion=logout" class="btn-logout"><i class="bi bi-box-arrow-left"></i>Cerrar sesión</a>
        </div>
    </aside>
    <main class="main-content">
        <header class="top-header">
            <div><div class="header-titulo">Privilegios</div><div class="header-ruta">Urban Caps &rsaquo; Gestión de Acceso &rsaquo; Privilegios</div></div>
        </header>
        <div class="content-area">
            <% if (request.getAttribute("mensaje") != null) { %><div class="alerta alerta-exito"><i class="bi bi-check-circle-fill"></i><%= request.getAttribute("mensaje") %></div><% } %>
            <% if (request.getAttribute("error") != null) { %><div class="alerta alerta-error"><i class="bi bi-exclamation-circle-fill"></i><%= request.getAttribute("error") %></div><% } %>
            <div class="pagina-card">
                <div class="pagina-card-header">
                    <div><div class="pagina-card-titulo">Lista de Privilegios</div><div class="pagina-card-desc">Acciones del sistema agrupadas por módulo</div></div>
                    <div style="display:flex; gap:8px;">
                        <% if (puedeCrear) { %>
                            <a href="${pageContext.request.contextPath}/privilegios?accion=nuevo" class="btn-nuevo"><i class="bi bi-plus-lg"></i>Nuevo Privilegio</a>
                        <% } %>
                        <% if (puedeExportar) { %>
                            <a href="${pageContext.request.contextPath}/privilegios?accion=exportar" class="btn-cancelar"><i class="bi bi-file-earmark-excel"></i>Exportar Excel</a>
                        <% } %>
                    </div>
                </div>
                <div class="tabla-contenedor">
                    <table class="tabla-admin">
                        <thead><tr><th>#</th><th>Nombre</th><th>Módulo</th><th>Descripción</th><th>Estado</th><% if (puedeEditar || puedeEliminar) { %><th>Acciones</th><% } %></tr></thead>
                        <tbody>
                            <%
                                List<Privilegio> privilegios = (List<Privilegio>) request.getAttribute("privilegios");
                                if (privilegios != null && !privilegios.isEmpty()) {
                                    for (Privilegio p : privilegios) {
                            %>
                            <tr>
                                <td style="color:#555;font-size:12px;">#<%= p.getIdPrivilegio() %></td>
                                <td><span style="font-family:monospace;font-size:11px;background:var(--gris-input);padding:3px 8px;border-radius:4px;color:var(--acento);"><%= p.getNombre() %></span></td>
                                <td><span style="background:var(--acento-suave);color:var(--acento);border:1px solid var(--acento-borde);padding:2px 10px;border-radius:20px;font-size:11px;font-weight:600;"><%= p.getModulo() %></span></td>
                                <td style="font-size:12px;"><%= p.getDescripcion() != null ? p.getDescripcion() : "—" %></td>
                                <td>
                                    <% if (p.getEstado() == 1) { %><span class="badge-estado badge-activo"><i class="bi bi-circle-fill" style="font-size:7px;"></i>Activo</span>
                                    <% } else { %><span class="badge-estado badge-inactivo"><i class="bi bi-circle-fill" style="font-size:7px;"></i>Inactivo</span><% } %>
                                </td>
                                <% if (puedeEditar || puedeEliminar) { %>
                                <td><div class="col-acciones">
                                    <% if (puedeEditar) { %><a href="${pageContext.request.contextPath}/privilegios?accion=editar&id=<%= p.getIdPrivilegio() %>" class="btn-editar"><i class="bi bi-pencil"></i>Editar</a><% } %>
                                    <% if (puedeEliminar) { %><a href="${pageContext.request.contextPath}/privilegios?accion=eliminar&id=<%= p.getIdPrivilegio() %>" class="btn-eliminar" onclick="return confirmarEliminar('<%= p.getNombre() %>')"><i class="bi bi-trash3"></i>Desactivar</a><% } %>
                                </div></td>
                                <% } %>
                            </tr>
                            <% } } else { %>
                            <tr><td colspan="6" class="tabla-vacia"><i class="bi bi-inbox" style="font-size:32px;display:block;margin-bottom:8px;opacity:.4;"></i>No hay privilegios registrados.</td></tr>
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
<script>function confirmarEliminar(nombre) { return confirm('¿Desactivar el privilegio "' + nombre + '"?'); }</script>
</body></html>
