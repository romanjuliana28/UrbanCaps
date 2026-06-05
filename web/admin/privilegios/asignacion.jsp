<%-- 
     PROYECTO  : Urban Caps
     ARCHIVO   : asignacion.jsp
     UBICACIÓN : Web Pages/admin/privilegios/asignacion.jsp
     DESCRIPCIÓN: Pantalla de checkboxes para asignar privilegios a un rol. Requiere PRIVILEGIOS_EDITAR.
     --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="com.urbancaps.modelo.bean.Rol" %>
<%@ page import="com.urbancaps.modelo.bean.Privilegio" %>
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

    // Requiere PRIVILEGIOS_EDITAR para asignar
    if (privs == null || !privs.contains("PRIVILEGIOS_EDITAR")) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }

    Rol rol = (Rol) request.getAttribute("rol");
    List<Privilegio> todosLosPrivilegios =
        (List<Privilegio>) request.getAttribute("todosLosPrivilegios");
    List<Integer> idsAsignados =
        (List<Integer>) request.getAttribute("idsAsignados");

    if (rol == null) {
        response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
        return;
    }

    String[] modulos = {"ROLES","PRIVILEGIOS","USUARIOS",
                        "PROVEEDORES","INVENTARIO","CATALOGO"};
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Asignar Privilegios</title>
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
                <a href="${pageContext.request.contextPath}/privilegios?accion=listar" class="nav-item activo">
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
                <div class="header-titulo">Asignar Privilegios</div>
                <div class="header-ruta">
                    Urban Caps &rsaquo; Roles &rsaquo; Asignar Privilegios
                </div>
            </div>
        </header>

        <div class="content-area">

            <div class="dashboard-banner" style="margin-bottom:20px;">
                <div>
                    <div class="dashboard-banner-titulo">
                        <i class="bi bi-shield"></i> Rol: <%= rol.getNombreRol() %>
                    </div>
                    <div class="dashboard-banner-desc">
                        Marca los privilegios que tendrá este rol.
                        Los cambios aplican a todos los usuarios con este rol.
                    </div>
                </div>
                <div class="badge-sesion">
                    <i class="bi bi-people"></i><%= rol.getNombreRol() %>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/privilegios" method="post">
                <input type="hidden" name="accion" value="guardarAsignacion">
                <input type="hidden" name="idRol"  value="<%= rol.getIdRol() %>">

                <% for (String modulo : modulos) { %>
                    <div class="modulo-grupo">
                        <div class="modulo-grupo-header">
                            <i class="bi bi-collection" style="color:var(--acento);"></i>
                            <div class="modulo-grupo-titulo"><%= modulo %></div>
                            <button type="button" class="btn-editar"
                                    style="margin-left:auto;"
                                    onclick="toggleModulo('<%= modulo %>')">
                                <i class="bi bi-check2-all"></i>Seleccionar todos
                            </button>
                        </div>
                        <div class="modulo-grupo-items">
                            <%
                                if (todosLosPrivilegios != null) {
                                    for (Privilegio p : todosLosPrivilegios) {
                                        if (p.getModulo().equals(modulo) && p.getEstado() == 1) {
                                            boolean asignado = idsAsignados != null &&
                                                               idsAsignados.contains(p.getIdPrivilegio());
                            %>
                                <label class="privilegio-item">
                                    <input type="checkbox"
                                           name="privilegios"
                                           value="<%= p.getIdPrivilegio() %>"
                                           data-modulo="<%= modulo %>"
                                           <%= asignado ? "checked" : "" %>>
                                    <div>
                                        <div class="privilegio-nombre"><%= p.getNombre() %></div>
                                        <div class="privilegio-desc">
                                            <%= p.getDescripcion() != null ? p.getDescripcion() : "" %>
                                        </div>
                                    </div>
                                </label>
                            <%  }}} %>
                        </div>
                    </div>
                <% } %>

                <div class="botones-form"
                     style="background:var(--gris-card);border:1px solid var(--gris-borde);
                            border-radius:var(--radio-lg);margin-top:4px;">
                    <button type="submit" class="btn-guardar">
                        <i class="bi bi-check-lg"></i>Guardar Privilegios
                    </button>
                    <a href="${pageContext.request.contextPath}/roles?accion=listar"
                       class="btn-cancelar">
                        <i class="bi bi-x-lg"></i>Cancelar
                    </a>
                </div>
            </form>
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>
<script>
    function toggleModulo(modulo) {
        const checkboxes = document.querySelectorAll(
            'input[type="checkbox"][data-modulo="' + modulo + '"]'
        );
        const todosActivos = Array.from(checkboxes).every(cb => cb.checked);
        checkboxes.forEach(cb => cb.checked = !todosActivos);
    }
</script>
</body>
</html>
