<%-- 
     PROYECTO  : Urban Caps
     ARCHIVO   : index.jsp
     UBICACIÓN : Web Pages/cliente/index.jsp
     DESCRIPCIÓN: Website público del catálogo de gorras Urban Caps.
     No requiere sesión — cualquier persona puede verlo.
     Muestra las gorras disponibles con botón de WhatsApp.

     VARIABLES que recibe del CatalogoServlet (/website):
      productos       : List<Catalogo> productos visibles
      categorias      : List<Categoria> para el header
      whatsapp        : número de WhatsApp del negocio
      textoBusqueda   : texto buscado (si viene de búsqueda)
      categoriaActiva : ID de la categoría filtrada
      tituloPagina    : título de la sección actual
      sinResultados   : mensaje si no hay resultados
      --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.urbancaps.modelo.bean.Catalogo" %>
<%@ page import="com.urbancaps.modelo.bean.Categoria" %>
<%@ page import="java.util.List" %>

<%
    // Leemos los datos enviados por el CatalogoServlet
    List<Catalogo>  productos       = (List<Catalogo>)  request.getAttribute("productos");
    List<Categoria> categorias      = (List<Categoria>) request.getAttribute("categorias");
    String          whatsapp        = (String) request.getAttribute("whatsapp");
    String          textoBusqueda   = (String) request.getAttribute("textoBusqueda");
    Integer         categoriaActiva = (Integer) request.getAttribute("categoriaActiva");
    String          tituloPagina    = (String) request.getAttribute("tituloPagina");
    String          sinResultados   = (String) request.getAttribute("sinResultados");

    // Valores por defecto si no vienen del Servlet
    if (textoBusqueda   == null) textoBusqueda   = "";
    if (tituloPagina    == null) tituloPagina    = "Todas las Gorras";
    if (whatsapp        == null) whatsapp        = "573152741191";
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Catálogo de Gorras</title>

    <%-- SEO básico --%>
    <meta name="description"
          content="Urban Caps — Catálogo de gorras premium. Snapback, Planas, Trucker y más.">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<%-- web-body aplica el fondo oscuro del website público --%>
<body class="web-body">

    <%-- 
         HEADER DEL WEBSITE
         Contiene: logo, buscador y categorías
         --%>
    <header class="web-header">

        <%-- Barra superior: logo + buscador --%>
        <div class="web-header-top">

            <%-- Logo de la marca --%>
            <a href="${pageContext.request.contextPath}/website"
               class="web-logo">
                <span class="web-logo-icon">🧢</span>
                URBAN CAPS
            </a>

            <%--
                BUSCADOR INTELIGENTE
                Envía al CatalogoServlet con accion=buscar.
                El Servlet llama a CatalogoDAO.buscar() que usa
                LIKE en título Y descripción del producto.
            --%>
            <form class="web-buscador"
                  action="${pageContext.request.contextPath}/website"
                  method="get">
                <input type="hidden" name="accion" value="buscar">
                <input type="text"
                       name="texto"
                       class="web-buscador-input"
                       placeholder="Buscar gorras..."
                       value="<%= textoBusqueda %>"
                       autocomplete="off">
                <button type="submit" class="web-buscador-btn"
                        title="Buscar">
                    <i class="bi bi-search"></i>
                </button>
            </form>

        </div>

        <%-- Barra de categorías --%>
        <div class="web-categorias">
            <div class="web-categorias-lista">

                <%-- Botón "Todas" --%>
                <a href="${pageContext.request.contextPath}/website"
                   class="web-categoria-btn <%= categoriaActiva == null && textoBusqueda.isEmpty() ? "activa" : "" %>">
                    <i class="bi bi-grid-2x2"></i>
                    Todas
                </a>

                <%--
                    Categorías dinámicas desde la BD.
                    Cada categoría activa aparece como botón en el header.
                    Al hacer clic filtra los productos de esa categoría.
                --%>
                <%
                    if (categorias != null) {
                        for (Categoria cat : categorias) {
                            boolean esActiva = categoriaActiva != null &&
                                               categoriaActiva == cat.getIdCategoria();
                %>
                    <a href="${pageContext.request.contextPath}/website?accion=categoria&id=<%= cat.getIdCategoria() %>"
                       class="web-categoria-btn <%= esActiva ? "activa" : "" %>">
                        🧢 <%= cat.getNombreCategoria() %>
                    </a>
                <%
                        }
                    }
                %>

            </div>
        </div>

    </header>


    <%-- 
         CONTENIDO PRINCIPAL DEL WEBSITE
         --%>
    <div class="web-contenido">

        <%-- Título de la sección y subtítulo --%>
        <div class="web-seccion-titulo"><%= tituloPagina %></div>
        <div class="web-seccion-sub">
            <% if (!textoBusqueda.isEmpty()) { %>
                Resultados para: <strong style="color:var(--acento);">"<%= textoBusqueda %>"</strong>
                — <a href="${pageContext.request.contextPath}/website"
                     style="color:#666;font-size:12px;">
                    <i class="bi bi-x-circle"></i> Limpiar búsqueda
                </a>
            <% } else if (categoriaActiva != null) { %>
                Mostrando gorras de esta categoría
            <% } else { %>
                Descubre nuestra colección completa de gorras
            <% } %>
        </div>

        <%-- 
             MENSAJE SIN RESULTADOS
             Se muestra cuando la búsqueda no encontró productos.
              --%>
        <% if (sinResultados != null) { %>
            <div class="web-sin-resultados">
                <i class="bi bi-search"></i>
                <p><%= sinResultados %></p>
                <span>Intenta con otro término o explora todas las categorías.</span>
                <br>
                <a href="${pageContext.request.contextPath}/website"
                   style="display:inline-block;margin-top:16px;
                          background:var(--acento);color:var(--negro);
                          padding:8px 20px;border-radius:var(--radio-sm);
                          font-weight:600;font-size:13px;">
                    Ver todos los productos
                </a>
            </div>
        <% } %>

        <%-- 
             GRID DE PRODUCTOS
             Muestra cada gorra como una tarjeta con imagen, título, descripción, precio y botón de WhatsApp.
             --%>
        <% if (productos != null && !productos.isEmpty()) { %>

            <div class="productos-grid">

                <%
                    for (Catalogo prod : productos) {
                        // Construimos el mensaje de WhatsApp para este producto.
                        // encodeURIComponent no existe en Java, usamos URLEncoder.
                        String mensajeWA = "¡Hola! Me interesa la gorra: " +
                                           prod.getTitulo() +
                                           " — Precio: $" +
                                           String.format("%,.0f", prod.getPrecioVenta()) +
                                           ". ¿Tienen disponibilidad?";
                        try {
                            mensajeWA = java.net.URLEncoder.encode(mensajeWA, "UTF-8");
                        } catch (Exception e) {
                            mensajeWA = "";
                        }
                        // URL completa del botón de WhatsApp
                        String urlWhatsApp = "https://wa.me/" + whatsapp +
                                             "?text=" + mensajeWA;
                %>

                <%-- TARJETA DE PRODUCTO --%>
                <div class="producto-card">

                    <%-- Contenedor de imagen --%>
                    <div class="producto-imagen-contenedor">

                        <%-- Badge de categoría sobre la imagen --%>
                        <span class="producto-categoria-badge">
                            <%= prod.getNombreCategoria() %>
                        </span>

                        <%--
                            Imagen del producto.
                            Si no tiene imagen muestra el ícono de gorra.
                            La imagen debe estar en img/gorras/
                        --%>
                        <% if (prod.getImagen() != null && !prod.getImagen().isEmpty()) { %>
                            <img src="${pageContext.request.contextPath}/img/gorras/<%= prod.getImagen() %>"
                                 alt="<%= prod.getTitulo() %>"
                                 class="producto-imagen"
                                 onerror="this.style.display='none';
                                          this.nextElementSibling.style.display='flex';">
                            <div class="producto-sin-imagen" style="display:none;">🧢</div>
                        <% } else { %>
                            <div class="producto-sin-imagen">🧢</div>
                        <% } %>

                    </div>

                    <%-- Información del producto --%>
                    <div class="producto-info">

                        <%-- Título --%>
                        <div class="producto-titulo">
                            <%= prod.getTitulo() %>
                        </div>

                        <%-- Descripción (máx 2 líneas por CSS) --%>
                        <div class="producto-descripcion">
                            <%= prod.getDescripcion() %>
                        </div>

                        <%-- Disponibilidad --%>
                        <% if (prod.getStock() > 0) { %>
                            <div style="font-size:11px;color:var(--color-exito);">
                                <i class="bi bi-check-circle-fill"></i>
                                Disponible (<%= prod.getStock() %> unidades)
                            </div>
                        <% } else { %>
                            <div style="font-size:11px;color:var(--color-error);">
                                <i class="bi bi-x-circle-fill"></i>
                                Agotado temporalmente
                            </div>
                        <% } %>

                    </div>

                    <%-- Footer: precio + botón WhatsApp --%>
                    <div class="producto-footer">

                        <%-- Precio de venta formateado con separador de miles --%>
                        <div class="producto-precio">
                            $<%= String.format("%,.0f", prod.getPrecioVenta()) %>
                        </div>

                        <%--
                            BOTÓN DE WHATSAPP
                            Abre WhatsApp con un mensaje predeterminado que incluye el nombre y precio del producto.
                            target="_blank" lo abre en una pestaña nueva.
                        --%>
                        <a href="<%= urlWhatsApp %>"
                           class="btn-whatsapp"
                           target="_blank"
                           rel="noopener noreferrer"
                           title="Comprar por WhatsApp">
                            <i class="bi bi-whatsapp"></i>
                            Comprar
                        </a>

                    </div>

                </div><%-- fin producto-card --%>

            <%  } %>

            </div><%-- fin productos-grid --%>

        <% } else if (sinResultados == null) { %>
            <%-- No hay productos en el catálogo aún --%>
            <div class="web-sin-resultados">
                <i class="bi bi-inbox"></i>
                <p>Próximamente nuevos productos</p>
                <span>Estamos preparando nuestra colección. ¡Vuelve pronto!</span>
            </div>
        <% } %>

    </div><%-- fin web-contenido --%>


    <%-- 
         FOOTER DEL WEBSITE
          --%>
    <footer class="web-footer">
        <div style="font-family:'Bebas Neue',sans-serif;font-size:20px;
                    letter-spacing:2px;color:var(--acento);margin-bottom:8px;">
            🧢 URBAN CAPS
        </div>
        <div>Tu tienda de gorras premium</div>
        <div style="margin-top:12px;">
            <a href="https://wa.me/<%= whatsapp %>"
               target="_blank"
               style="color:var(--acento);text-decoration:none;font-size:13px;">
                <i class="bi bi-whatsapp"></i>
                Contáctanos por WhatsApp
            </a>
        </div>
        <div style="margin-top:16px;font-size:11px;color:#333;">
            &copy; 2025 Urban Caps — Todos los derechos reservados — Juliana Restrepo Roman.
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>

    <script>

        // Oculta el header al hacer scroll hacia abajo y lo muestra al hacer scroll hacia arriba.
        // Mejora la experiencia en móvil.

        let ultimoScroll = 0;
        const header = document.querySelector('.web-header');

        window.addEventListener('scroll', () => {
            const scrollActual = window.scrollY;

            if (scrollActual > ultimoScroll && scrollActual > 80) {
                // Scrolleando hacia abajo — oculta header
                header.style.transform = 'translateY(-100%)';
                header.style.transition = 'transform 0.3s ease';
            } else {
                // Scrolleando hacia arriba — muestra header
                header.style.transform = 'translateY(0)';
            }

            ultimoScroll = scrollActual;
        });
    </script>

</body>
</html>
