<%-- 
     PROYECTO  : Urban Caps
     ARCHIVO   : dashboard.jsp
     UBICACIÓN : Web Pages/admin/dashboard.jsp
     DESCRIPCIÓN: Página principal del panel de administración.
     Muestra solo las tarjetas de módulos a los que el usuario tiene acceso según sus privilegios.
      --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Usuario" %>
<%@ page import="java.util.List" %>

<%
    // ── Verificación de sesión ──
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");
    String nombreRol     = (String) session.getAttribute("nombreRol");

    // ── Cargamos nombres de privilegios del usuario ──
    // Estos fueron guardados en sesión por el LoginServlet al momento de iniciar sesión.
    List<String> privs =
        (List<String>) session.getAttribute("nombresPrivilegios");

    // ── Definimos qué módulos puede ver este usuario ──
    // Cada variable booleana controla si aparece la tarjeta del módulo correspondiente en el dashboard.
    boolean verRoles       = privs != null && privs.contains("ROLES_VER");
    boolean verPrivilegios = privs != null && privs.contains("PRIVILEGIOS_VER");
    boolean verUsuarios    = privs != null && privs.contains("USUARIOS_VER");
    boolean verProveedores = privs != null && privs.contains("PROVEEDORES_VER");
    boolean verInventario  = privs != null && privs.contains("INVENTARIO_VER");
    boolean verCatalogo    = privs != null && privs.contains("CATALOGO_VER");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>
<div class="admin-wrapper">

    <%-- SIDEBAR --%>
    <aside class="sidebar">
        <div class="sidebar-logo">
            <div class="sidebar-logo-icon">🧢</div>
            <div>
                <div class="sidebar-logo-texto">URBAN CAPS</div>
                <div class="sidebar-logo-sub">Admin Panel</div>
            </div>
        </div>
        <nav class="sidebar-nav">
            <a href="${pageContext.request.contextPath}/dashboard"
               class="nav-item activo">
                <i class="bi bi-grid-1x2"></i>Dashboard
            </a>

            <%-- Solo muestra la sección si tiene al menos un privilegio --%>
            <% if (verRoles || verPrivilegios || verUsuarios) { %>
                <div class="nav-grupo-titulo">Gestión de Acceso</div>
            <% } %>

            <% if (verRoles) { %>
                <a href="${pageContext.request.contextPath}/roles?accion=listar"
                   class="nav-item">
                    <i class="bi bi-shield"></i>Roles
                </a>
            <% } %>

            <% if (verPrivilegios) { %>
                <a href="${pageContext.request.contextPath}/privilegios?accion=listar"
                   class="nav-item">
                    <i class="bi bi-key"></i>Privilegios
                </a>
            <% } %>

            <% if (verUsuarios) { %>
                <a href="${pageContext.request.contextPath}/usuarios?accion=listar"
                   class="nav-item">
                    <i class="bi bi-people"></i>Usuarios
                </a>
            <% } %>

            <% if (verProveedores || verInventario || verCatalogo) { %>
                <div class="nav-grupo-titulo">Gestión de Negocio</div>
            <% } %>

            <% if (verProveedores) { %>
                <a href="${pageContext.request.contextPath}/proveedores?accion=listar"
                   class="nav-item">
                    <i class="bi bi-truck"></i>Proveedores
                </a>
            <% } %>

            <% if (verInventario) { %>
                <a href="${pageContext.request.contextPath}/inventario?accion=listar"
                   class="nav-item">
                    <i class="bi bi-box-seam"></i>Inventario
                </a>
            <% } %>

            <% if (verCatalogo) { %>
                <a href="${pageContext.request.contextPath}/catalogo?accion=listar"
                   class="nav-item">
                    <i class="bi bi-grid-3x3-gap"></i>Catálogo
                </a>
            <% } %>

        </nav>
        <div class="sidebar-footer">
            <div class="usuario-info">
                <div class="usuario-avatar">
                    <%= nombreUsuario != null
                        ? String.valueOf(nombreUsuario.charAt(0)).toUpperCase()
                        : "U" %>
                </div>
                <div>
                    <div class="usuario-nombre"><%= nombreUsuario %></div>
                    <div class="usuario-rol"><%= nombreRol %></div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/usuarios?accion=logout"
               class="btn-logout">
                <i class="bi bi-box-arrow-left"></i>Cerrar sesión
            </a>
        </div>
    </aside>

    <%-- CONTENIDO PRINCIPAL --%>
    <main class="main-content">

        <header class="top-header">
            <div>
                <div class="header-titulo">Dashboard</div>
                <div class="header-ruta">
                    Urban Caps &rsaquo; Panel de administración
                </div>
            </div>
            <div class="header-reloj" id="reloj"></div>
        </header>

        <div class="content-area">

            <%-- Banner de bienvenida --%>
            <div class="dashboard-banner">
                <div>
                    <div class="dashboard-banner-titulo">
                        ¡Bienvenido, <%= nombreUsuario %>! 👋
                    </div>
                    <div class="dashboard-banner-desc">
                        Acceso como
                        <strong style="color:var(--acento);">
                            <%= nombreRol %>
                        </strong>.
                        Tienes acceso a
                        <strong style="color:var(--acento);">
                            <%=
                                (privs != null ? privs.size() : 0)
                            %>
                        </strong>
                        privilegios en el sistema.
                    </div>
                </div>
                <div class="badge-sesion">
                    <i class="bi bi-shield-check"></i>
                    Sesión activa
                </div>
            </div>

            <%-- 
                 TARJETAS DE MÓDULOS
                 Solo se muestran las que el usuario puede ver.
                  --%>
            <div class="seccion-label">Módulos disponibles</div>

            <div class="modulos-grid">

                <%-- Tarjeta Roles — solo si tiene ROLES_VER --%>
                <% if (verRoles) { %>
                <a href="${pageContext.request.contextPath}/roles?accion=listar"
                   class="modulo-card">
                    <div class="modulo-icono icono-morado">
                        <i class="bi bi-shield"></i>
                    </div>
                    <div>
                        <div class="modulo-nombre">Roles</div>
                        <div class="modulo-desc">
                            Gestiona los tipos de usuario
                        </div>
                    </div>
                    <div class="modulo-flecha">
                        <i class="bi bi-arrow-right"></i>
                    </div>
                </a>
                <% } %>

                <%-- Tarjeta Privilegios — solo si tiene PRIVILEGIOS_VER --%>
                <% if (verPrivilegios) { %>
                <a href="${pageContext.request.contextPath}/privilegios?accion=listar"
                   class="modulo-card">
                    <div class="modulo-icono icono-azul">
                        <i class="bi bi-key"></i>
                    </div>
                    <div>
                        <div class="modulo-nombre">Privilegios</div>
                        <div class="modulo-desc">
                            Controla los permisos del sistema
                        </div>
                    </div>
                    <div class="modulo-flecha">
                        <i class="bi bi-arrow-right"></i>
                    </div>
                </a>
                <% } %>

                <%-- Tarjeta Usuarios — solo si tiene USUARIOS_VER --%>
                <% if (verUsuarios) { %>
                <a href="${pageContext.request.contextPath}/usuarios?accion=listar"
                   class="modulo-card">
                    <div class="modulo-icono icono-verde">
                        <i class="bi bi-people"></i>
                    </div>
                    <div>
                        <div class="modulo-nombre">Usuarios</div>
                        <div class="modulo-desc">
                            Administra el personal del panel
                        </div>
                    </div>
                    <div class="modulo-flecha">
                        <i class="bi bi-arrow-right"></i>
                    </div>
                </a>
                <% } %>

                <%-- Tarjeta Proveedores — solo si tiene PROVEEDORES_VER --%>
                <% if (verProveedores) { %>
                <a href="${pageContext.request.contextPath}/proveedores?accion=listar"
                   class="modulo-card">
                    <div class="modulo-icono icono-dorado">
                        <i class="bi bi-truck"></i>
                    </div>
                    <div>
                        <div class="modulo-nombre">Proveedores</div>
                        <div class="modulo-desc">
                            Registra quienes suministran gorras
                        </div>
                    </div>
                    <div class="modulo-flecha">
                        <i class="bi bi-arrow-right"></i>
                    </div>
                </a>
                <% } %>

                <%-- Tarjeta Inventario — solo si tiene INVENTARIO_VER --%>
                <% if (verInventario) { %>
                <a href="${pageContext.request.contextPath}/inventario?accion=listar"
                   class="modulo-card">
                    <div class="modulo-icono icono-rojo">
                        <i class="bi bi-box-seam"></i>
                    </div>
                    <div>
                        <div class="modulo-nombre">Inventario</div>
                        <div class="modulo-desc">
                            Controla el stock de gorras
                        </div>
                    </div>
                    <div class="modulo-flecha">
                        <i class="bi bi-arrow-right"></i>
                    </div>
                </a>
                <% } %>

                <%-- Tarjeta Catálogo — solo si tiene CATALOGO_VER --%>
                <% if (verCatalogo) { %>
                <a href="${pageContext.request.contextPath}/catalogo?accion=listar"
                   class="modulo-card">
                    <div class="modulo-icono icono-teal">
                        <i class="bi bi-grid-3x3-gap"></i>
                    </div>
                    <div>
                        <div class="modulo-nombre">Catálogo</div>
                        <div class="modulo-desc">
                            Publica productos en el website
                        </div>
                    </div>
                    <div class="modulo-flecha">
                        <i class="bi bi-arrow-right"></i>
                    </div>
                </a>
                <% } %>

            </div><%-- fin modulos-grid --%>

            <%-- Acceso al website público --%>
            <div class="seccion-label">Website público</div>
            <a href="${pageContext.request.contextPath}/website"
               class="website-card" target="_blank">
                <div style="display:flex;align-items:center;gap:16px;">
                    <div class="website-card-icono">
                        <i class="bi bi-globe2"></i>
                    </div>
                    <div>
                        <div class="website-card-titulo">
                            Ver Website de Urban Caps
                        </div>
                        <div class="website-card-desc">
                            Así ve el catálogo un cliente desde el navegador
                        </div>
                    </div>
                </div>
                <div class="btn-visitar">
                    <i class="bi bi-box-arrow-up-right"></i>
                    Visitar
                </div>
            </a>

        </div><%-- fin content-area --%>
    </main>
</div><%-- fin admin-wrapper --%>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>
<script>
    function actualizarReloj() {
        const ahora = new Date();
        const fecha = ahora.toLocaleDateString('es-CO', {
            weekday:'long', year:'numeric',
            month:'long', day:'numeric'
        });
        const hora = ahora.toLocaleTimeString('es-CO', {
            hour:'2-digit', minute:'2-digit'
        });
        const reloj = document.getElementById('reloj');
        if (reloj) reloj.textContent = fecha + ' — ' + hora;
    }
    actualizarReloj();
    setInterval(actualizarReloj, 60000);
</script>
</body>
</html>
