<%-- 
     PROYECTO  : Urban Caps
     ARCHIVO   : formulario.jsp
     UBICACIÓN : Web Pages/admin/roles/formulario.jsp
     DESCRIPCIÓN: Formulario para crear o editar un rol.
     Requiere ROLES_CREAR (nuevo) o ROLES_EDITAR (editar).
      --%>

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

    List<String> privs =
        (List<String>) session.getAttribute("nombresPrivilegios");

    Rol rol       = (Rol)    request.getAttribute("rol");
    String accion = (String) request.getAttribute("accion");
    String titulo = (String) request.getAttribute("titulo");

    if (rol == null) rol = new Rol();
    if (accion == null) accion = "guardar";
    if (titulo == null) titulo = "Nuevo Rol";

    // ── Verificación de privilegio según la acción ─
    // Si es nuevo necesita ROLES_CREAR
    // Si es editar necesita ROLES_EDITAR
    boolean esNuevo = accion.equals("guardar");
    boolean tienePermiso = privs != null &&
        ((esNuevo  && privs.contains("ROLES_CREAR")) ||
         (!esNuevo && privs.contains("ROLES_EDITAR")));

    if (!tienePermiso) {
        response.sendRedirect(request.getContextPath() + "/roles?accion=listar");
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
                <a href="${pageContext.request.contextPath}/roles?accion=listar" class="nav-item activo">
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
                    Urban Caps &rsaquo; Roles &rsaquo; <%= titulo %>
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
                            <%= esNuevo ? "Registra un nuevo tipo de usuario" : "Modifica los datos del rol" %>
                        </div>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/roles" method="post">
                    <input type="hidden" name="accion" value="<%= accion %>">
                    <input type="hidden" name="idRol"  value="<%= rol.getIdRol() %>">

                    <div class="form-cuerpo">

                        <div class="form-grupo">
                            <label class="form-etiqueta" for="nombreRol">
                                Nombre del Rol <span style="color:var(--color-error);">*</span>
                            </label>
                            <input type="text"
                                   id="nombreRol"
                                   name="nombreRol"
                                   class="form-campo"
                                   placeholder="Ej: Administrador, Empleado..."
                                   value="<%= rol.getNombreRol() != null ? rol.getNombreRol() : "" %>"
                                   required maxlength="50">
                            <div class="form-ayuda">Máximo 50 caracteres.</div>
                        </div>

                        <div class="form-grupo">
                            <label class="form-etiqueta" for="descripcion">
                                Descripción
                            </label>
                            <textarea id="descripcion"
                                      name="descripcion"
                                      class="form-campo"
                                      placeholder="Describe qué puede hacer este rol..."
                                      maxlength="150"><%= rol.getDescripcion() != null ? rol.getDescripcion() : "" %></textarea>
                            <div class="form-ayuda">Opcional. Máximo 150 caracteres.</div>
                        </div>

                        <div class="form-grupo">
                            <label class="form-etiqueta" for="estado">
                                Estado <span style="color:var(--color-error);">*</span>
                            </label>
                            <select id="estado" name="estado" class="form-campo">
                                <option value="1" <%= rol.getEstado() == 1 ? "selected" : "" %>>Activo</option>
                                <option value="0" <%= rol.getEstado() == 0 ? "selected" : "" %>>Inactivo</option>
                            </select>
                        </div>

                    </div>

                    <div class="botones-form">
                        <button type="submit" class="btn-guardar">
                            <i class="bi bi-check-lg"></i>
                            <%= esNuevo ? "Crear Rol" : "Guardar Cambios" %>
                        </button>
                        <a href="${pageContext.request.contextPath}/roles?accion=listar"
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
