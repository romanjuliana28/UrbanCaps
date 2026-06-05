<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");
    String nombreRol     = (String) session.getAttribute("nombreRol");
    List<String> privs   = (List<String>) session.getAttribute("nombresPrivilegios");
    if (privs == null || !privs.contains("USUARIOS_VER")) { response.sendRedirect(request.getContextPath() + "/dashboard"); return; }
    boolean puedeCrear    = privs.contains("USUARIOS_CREAR");
    boolean puedeEditar   = privs.contains("USUARIOS_EDITAR");
    boolean puedeEliminar = privs.contains("USUARIOS_ELIMINAR");
    boolean puedeExportar = privs.contains("USUARIOS_EXPORTAR");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Usuarios</title>
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
            <% if (privs.contains("USUARIOS_VER")) { %><a href="${pageContext.request.contextPath}/usuarios?accion=listar" class="nav-item activo"><i class="bi bi-people"></i>Usuarios</a><% } %>
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
            <div><div class="header-titulo">Usuarios</div><div class="header-ruta">Urban Caps &rsaquo; Gestión de Acceso &rsaquo; Usuarios</div></div>
        </header>
        <div class="content-area">
            <% if (request.getAttribute("mensaje") != null) { %><div class="alerta alerta-exito"><i class="bi bi-check-circle-fill"></i><%= request.getAttribute("mensaje") %></div><% } %>
            <% if (request.getAttribute("error") != null) { %><div class="alerta alerta-error"><i class="bi bi-exclamation-circle-fill"></i><%= request.getAttribute("error") %></div><% } %>
            <div class="pagina-card">
                <div class="pagina-card-header">
                    <div><div class="pagina-card-titulo">Lista de Usuarios</div><div class="pagina-card-desc">Personal con acceso al panel de administración</div></div>
                    <div style="display:flex; gap:8px;">
                        <% if (puedeCrear) { %>
                            <a href="${pageContext.request.contextPath}/usuarios?accion=nuevo" class="btn-nuevo"><i class="bi bi-plus-lg"></i>Nuevo Usuario</a>
                        <% } %>
                        <% if (puedeExportar) { %>
                            <a href="${pageContext.request.contextPath}/usuarios?accion=exportar" class="btn-cancelar"><i class="bi bi-file-earmark-excel"></i>Exportar Excel</a>
                        <% } %>
                    </div>
                </div>
                <div class="tabla-contenedor">
                    <table class="tabla-admin">
                        <thead><tr><th>#</th><th>Usuario</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Fecha Registro</th><% if (puedeEditar || puedeEliminar) { %><th>Acciones</th><% } %></tr></thead>
                        <tbody>
                            <%
                                List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
                                if (usuarios != null && !usuarios.isEmpty()) {
                                    for (Usuario u : usuarios) {
                            %>
                            <tr>
                                <td style="color:#555;font-size:12px;">#<%= u.getIdUsuario() %></td>
                                <td>
                                    <div style="display:flex;align-items:center;gap:10px;">
                                        <div class="usuario-avatar" style="width:30px;height:30px;font-size:12px;flex-shrink:0;"><%= String.valueOf(u.getNombre().charAt(0)).toUpperCase() %></div>
                                        <div style="font-weight:500;color:var(--blanco);font-size:13px;"><%= u.getNombre() %> <%= u.getApellido() %></div>
                                    </div>
                                </td>
                                <td style="font-size:12px;"><%= u.getEmail() %></td>
                                <td><span style="background:var(--acento-suave);color:var(--acento);border:1px solid var(--acento-borde);padding:2px 10px;border-radius:20px;font-size:11px;font-weight:600;"><%= u.getNombreRol() %></span></td>
                                <td>
                                    <% if (u.getEstado() == 1) { %><span class="badge-estado badge-activo"><i class="bi bi-circle-fill" style="font-size:7px;"></i>Activo</span>
                                    <% } else { %><span class="badge-estado badge-inactivo"><i class="bi bi-circle-fill" style="font-size:7px;"></i>Inactivo</span><% } %>
                                </td>
                                <td style="font-size:12px;color:#666;"><%= u.getFechaRegistro() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(u.getFechaRegistro()) : "—" %></td>
                                <% if (puedeEditar || puedeEliminar) { %>
                                <td><div class="col-acciones">
                                    <% if (puedeEditar) { %><a href="${pageContext.request.contextPath}/usuarios?accion=editar&id=<%= u.getIdUsuario() %>" class="btn-editar"><i class="bi bi-pencil"></i>Editar</a><% } %>
                                    <% if (puedeEliminar && usuarioSesion.getIdUsuario() != u.getIdUsuario()) { %><a href="${pageContext.request.contextPath}/usuarios?accion=eliminar&id=<%= u.getIdUsuario() %>" class="btn-eliminar" onclick="return confirmarEliminar('<%= u.getNombre() %> <%= u.getApellido() %>')"><i class="bi bi-trash3"></i>Desactivar</a><% } %>
                                </div></td>
                                <% } %>
                            </tr>
                            <% } } else { %>
                            <tr><td colspan="7" class="tabla-vacia"><i class="bi bi-people" style="font-size:32px;display:block;margin-bottom:8px;opacity:.4;"></i>No hay usuarios registrados.</td></tr>
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
<script>function confirmarEliminar(nombre) { return confirm('¿Desactivar al usuario "' + nombre + '"?\n\nEl usuario no podrá iniciar sesión mientras esté inactivo.'); }</script>
</body></html>
