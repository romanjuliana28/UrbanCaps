<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="com.urbancaps.modelo.bean.Rol" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");
    String nombreRol     = (String) session.getAttribute("nombreRol");
    List<String> privs   = (List<String>) session.getAttribute("nombresPrivilegios");
    if (privs == null || !privs.contains("ROLES_VER")) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
    boolean puedeCrear    = privs.contains("ROLES_CREAR");
    boolean puedeEditar   = privs.contains("ROLES_EDITAR");
    boolean puedeEliminar = privs.contains("ROLES_ELIMINAR");
    boolean puedeExportar = privs.contains("ROLES_EXPORTAR");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Roles</title>
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
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-item"><i class="bi bi-grid-1x2"></i>Dashboard</a>
            <% if (privs.contains("ROLES_VER") || privs.contains("PRIVILEGIOS_VER") || privs.contains("USUARIOS_VER")) { %><div class="nav-grupo-titulo">Gestión de Acceso</div><% } %>
            <% if (privs.contains("ROLES_VER")) { %><a href="${pageContext.request.contextPath}/roles?accion=listar" class="nav-item activo"><i class="bi bi-shield"></i>Roles</a><% } %>
            <% if (privs.contains("PRIVILEGIOS_VER")) { %><a href="${pageContext.request.contextPath}/privilegios?accion=listar" class="nav-item"><i class="bi bi-key"></i>Privilegios</a><% } %>
            <% if (privs.contains("USUARIOS_VER")) { %><a href="${pageContext.request.contextPath}/usuarios?accion=listar" class="nav-item"><i class="bi bi-people"></i>Usuarios</a><% } %>
            <% if (privs.contains("PROVEEDORES_VER") || privs.contains("INVENTARIO_VER") || privs.contains("CATALOGO_VER")) { %><div class="nav-grupo-titulo">Gestión de Negocio</div><% } %>
            <% if (privs.contains("PROVEEDORES_VER")) { %><a href="${pageContext.request.contextPath}/proveedores?accion=listar" class="nav-item"><i class="bi bi-truck"></i>Proveedores</a><% } %>
            <% if (privs.contains("INVENTARIO_VER")) { %><a href="${pageContext.request.contextPath}/inventario?accion=listar" class="nav-item"><i class="bi bi-box-seam"></i>Inventario</a><% } %>
            <% if (privs.contains("CATALOGO_VER")) { %><a href="${pageContext.request.contextPath}/catalogo?accion=listar" class="nav-item"><i class="bi bi-grid-3x3-gap"></i>Catálogo</a><% } %>
        </nav>
        <div class="sidebar-footer">
            <div class="usuario-info">
                <div class="usuario-avatar"><%= nombreUsuario != null ? String.valueOf(nombreUsuario.charAt(0)).toUpperCase() : "U" %></div>
                <div><div class="usuario-nombre"><%= nombreUsuario %></div><div class="usuario-rol"><%= nombreRol %></div></div>
            </div>
            <a href="${pageContext.request.contextPath}/usuarios?accion=logout" class="btn-logout"><i class="bi bi-box-arrow-left"></i>Cerrar sesión</a>
        </div>
    </aside>
    <main class="main-content">
        <header class="top-header">
            <div>
                <div class="header-titulo">Roles</div>
                <div class="header-ruta">Urban Caps &rsaquo; Gestión de Acceso &rsaquo; Roles</div>
            </div>
        </header>
        <div class="content-area">
            <% if (request.getAttribute("mensaje") != null) { %><div class="alerta alerta-exito"><i class="bi bi-check-circle-fill"></i><%= request.getAttribute("mensaje") %></div><% } %>
            <% if (request.getAttribute("error") != null) { %><div class="alerta alerta-error"><i class="bi bi-exclamation-circle-fill"></i><%= request.getAttribute("error") %></div><% } %>
            <div class="pagina-card">
                <div class="pagina-card-header">
                    <div>
                        <div class="pagina-card-titulo">Lista de Roles</div>
                        <div class="pagina-card-desc">Gestiona los tipos de usuario del sistema</div>
                    </div>
                    <%-- Botones del header: Nuevo + Exportar Excel --%>
                    <div style="display:flex; gap:8px;">
                        <% if (puedeCrear) { %>
                            <a href="${pageContext.request.contextPath}/roles?accion=nuevo" class="btn-nuevo">
                                <i class="bi bi-plus-lg"></i>Nuevo Rol
                            </a>
                        <% } %>
                        <% if (puedeExportar) { %>
                            <a href="${pageContext.request.contextPath}/roles?accion=exportar" class="btn-cancelar">
                                <i class="bi bi-file-earmark-excel"></i>Exportar Excel
                            </a>
                        <% } %>
                    </div>
                </div>
                <div class="tabla-contenedor">
                    <table class="tabla-admin">
                        <thead>
                            <tr>
                                <th>#</th><th>Nombre del Rol</th><th>Descripción</th>
                                <th>Estado</th><th>Privilegios</th>
                                <% if (puedeEditar || puedeEliminar) { %><th>Acciones</th><% } %>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<Rol> roles = (List<Rol>) request.getAttribute("roles");
                                if (roles != null && !roles.isEmpty()) {
                                    for (Rol rol : roles) {
                            %>
                            <tr>
                                <td style="color:#555;font-size:12px;">#<%= rol.getIdRol() %></td>
                                <td style="font-weight:500;color:var(--blanco);"><%= rol.getNombreRol() %></td>
                                <td><%= rol.getDescripcion() != null ? rol.getDescripcion() : "<span style='color:#444'>—</span>" %></td>
                                <td>
                                    <% if (rol.getEstado() == 1) { %>
                                        <span class="badge-estado badge-activo"><i class="bi bi-circle-fill" style="font-size:7px;"></i>Activo</span>
                                    <% } else { %>
                                        <span class="badge-estado badge-inactivo"><i class="bi bi-circle-fill" style="font-size:7px;"></i>Inactivo</span>
                                    <% } %>
                                </td>
                                <td>
                                    <% if (privs.contains("PRIVILEGIOS_VER")) { %>
                                        <a href="${pageContext.request.contextPath}/privilegios?accion=asignar&idRol=<%= rol.getIdRol() %>" class="btn-editar"><i class="bi bi-key"></i>Asignar</a>
                                    <% } else { %><span style="color:#444;font-size:12px;">—</span><% } %>
                                </td>
                                <% if (puedeEditar || puedeEliminar) { %>
                                <td>
                                    <div class="col-acciones">
                                        <% if (puedeEditar) { %><a href="${pageContext.request.contextPath}/roles?accion=editar&id=<%= rol.getIdRol() %>" class="btn-editar"><i class="bi bi-pencil"></i>Editar</a><% } %>
                                        <% if (puedeEliminar) { %><a href="${pageContext.request.contextPath}/roles?accion=eliminar&id=<%= rol.getIdRol() %>" class="btn-eliminar" onclick="return confirmarEliminar('<%= rol.getNombreRol() %>')"><i class="bi bi-trash3"></i>Desactivar</a><% } %>
                                    </div>
                                </td>
                                <% } %>
                            </tr>
                            <% } } else { %>
                            <tr><td colspan="6" class="tabla-vacia"><i class="bi bi-inbox" style="font-size:32px;display:block;margin-bottom:8px;opacity:.4;"></i>No hay roles registrados.</td></tr>
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
<script>
    function confirmarEliminar(nombre) {
        return confirm('¿Desactivar el rol "' + nombre + '"?\n\nEsta acción cambiará su estado a Inactivo.');
    }
</script>
</body>
</html>
