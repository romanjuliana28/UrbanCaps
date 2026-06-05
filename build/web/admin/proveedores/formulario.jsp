<%-- 
     PROYECTO  : Urban Caps
     ARCHIVO   : formulario.jsp
     UBICACIÓN : Web Pages/admin/proveedores/formulario.jsp
     DESCRIPCIÓN: Formulario para crear o editar un proveedor.
     Requiere PROVEEDORES_CREAR o PROVEEDORES_EDITAR.

     VARIABLES que recibe del ProveedorServlet:
     proveedor : objeto Proveedor
     accion    : "guardar" o "actualizar"
     itulo    : "Nuevo Proveedor" o "Editar Proveedor"
     --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="com.urbancaps.modelo.bean.Proveedor" %>
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

    Proveedor proveedor = (Proveedor) request.getAttribute("proveedor");
    String accion       = (String)    request.getAttribute("accion");
    String titulo       = (String)    request.getAttribute("titulo");

    if (proveedor == null) proveedor = new Proveedor();
    if (accion    == null) accion    = "guardar";
    if (titulo    == null) titulo    = "Nuevo Proveedor";

    boolean esNuevo = accion.equals("guardar");
    boolean tienePermiso = privs != null &&
        ((esNuevo  && privs.contains("PROVEEDORES_CREAR")) ||
         (!esNuevo && privs.contains("PROVEEDORES_EDITAR")));

    if (!tienePermiso) {
        response.sendRedirect(request.getContextPath() + "/proveedores?accion=listar");
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
                <a href="${pageContext.request.contextPath}/proveedores?accion=listar" class="nav-item activo">
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
                    Urban Caps &rsaquo; Proveedores &rsaquo; <%= titulo %>
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
                            <%= esNuevo ? "Registra un nuevo proveedor de gorras" : "Modifica los datos del proveedor" %>
                        </div>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/proveedores" method="post">
                    <input type="hidden" name="accion"       value="<%= accion %>">
                    <input type="hidden" name="idProveedor"  value="<%= proveedor.getIdProveedor() %>">

                    <div class="form-cuerpo">

                        <%-- Fila: Nombre y Apellidos --%>
                        <div class="form-grid-2">
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="nombre">
                                    Nombre <span style="color:var(--color-error);">*</span>
                                </label>
                                <input type="text"
                                       id="nombre" name="nombre"
                                       class="form-campo"
                                       placeholder="Ej: Carlos"
                                       value="<%= proveedor.getNombre() != null ? proveedor.getNombre() : "" %>"
                                       required maxlength="100">
                            </div>
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="apellidos">
                                    Apellidos <span style="color:var(--color-error);">*</span>
                                </label>
                                <input type="text"
                                       id="apellidos" name="apellidos"
                                       class="form-campo"
                                       placeholder="Ej: García López"
                                       value="<%= proveedor.getApellidos() != null ? proveedor.getApellidos() : "" %>"
                                       required maxlength="100">
                            </div>
                        </div>

                        <%-- Fila: Contacto y Correo --%>
                        <div class="form-grid-2">
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="contacto">
                                    Teléfono / Celular <span style="color:var(--color-error);">*</span>
                                </label>
                                <input type="text"
                                       id="contacto" name="contacto"
                                       class="form-campo"
                                       placeholder="Ej: 3152741191"
                                       value="<%= proveedor.getContacto() != null ? proveedor.getContacto() : "" %>"
                                       required maxlength="20">
                            </div>
                            <div class="form-grupo">
                                <label class="form-etiqueta" for="correo">
                                    Correo electrónico
                                </label>
                                <input type="email"
                                       id="correo" name="correo"
                                       class="form-campo"
                                       placeholder="Ej: carlos@proveedor.com"
                                       value="<%= proveedor.getCorreo() != null ? proveedor.getCorreo() : "" %>"
                                       maxlength="150">
                                <div class="form-ayuda">Opcional.</div>
                            </div>
                        </div>

                        <%-- Dirección --%>
                        <div class="form-grupo">
                            <label class="form-etiqueta" for="direccion">
                                Dirección <span style="color:var(--color-error);">*</span>
                            </label>
                            <input type="text"
                                   id="direccion" name="direccion"
                                   class="form-campo"
                                   placeholder="Ej: Calle 10 #5-20, Medellín"
                                   value="<%= proveedor.getDireccion() != null ? proveedor.getDireccion() : "" %>"
                                   required maxlength="200">
                        </div>

                        <%-- Estado --%>
                        <div class="form-grupo">
                            <label class="form-etiqueta" for="estado">
                                Estado <span style="color:var(--color-error);">*</span>
                            </label>
                            <select id="estado" name="estado" class="form-campo">
                                <option value="1" <%= proveedor.getEstado() == 1 ? "selected" : "" %>>Activo</option>
                                <option value="0" <%= proveedor.getEstado() == 0 ? "selected" : "" %>>Inactivo</option>
                            </select>
                        </div>

                    </div>

                    <div class="botones-form">
                        <button type="submit" class="btn-guardar">
                            <i class="bi bi-check-lg"></i>
                            <%= esNuevo ? "Registrar Proveedor" : "Guardar Cambios" %>
                        </button>
                        <a href="${pageContext.request.contextPath}/proveedores?accion=listar"
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
