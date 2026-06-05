<%-- 
     PROYECTO  : Urban Caps
     ARCHIVO   : login.jsp
     UBICACIÓN : Web Pages/login.jsp
     DESCRIPCIÓN: Página de inicio de sesión del panel de administración. Recibe email y contraseña, los envía al LoginServlet mediante POST.

     VARIABLES que recibe del LoginServlet:
     request.getAttribute("error") : mensaje si las credenciales son incorrectas
      --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urban Caps — Iniciar Sesión</title>

    <%-- Bootstrap CSS descargado localmente en css/ --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">

    <%-- Bootstrap Icons descargado localmente en css/ --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">

    <%-- Hoja de estilos principal del proyecto --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<%--
    La clase login-body aplica el fondo oscuro y el centrado vertical.
    Está definida en estilos.css — Sección 5 LOGIN.
--%>
<body class="login-body">

    <%-- 
         CONTENEDOR PRINCIPAL DEL LOGIN
         login-wrapper: limita el ancho máximo y centra el contenido
         --%>
    <div class="login-wrapper">

        <%-- 
             SECCIÓN DE MARCA
             Muestra el ícono, nombre y subtítulo de Urban Caps
             --%>
        <div class="login-brand">
            <%-- Cuadro dorado con el ícono de la gorra --%>
            <div class="login-brand-icon">🧢</div>

            <%-- Nombre de la marca con fuente Bebas Neue --%>
            <div class="login-brand-nombre">URBAN CAPS</div>

            <%-- Subtítulo debajo del nombre --%>
            <div class="login-brand-sub">Panel de Administración</div>
        </div>

        <%-- 
             TARJETA DEL FORMULARIO
           --%>
        <div class="login-card">

            <div class="login-titulo">Bienvenido de vuelta</div>
            <div class="login-desc">Ingresa tus credenciales para continuar</div>

            <%-- 
                 MENSAJE DE ERROR
                 Se muestra cuando LoginServlet detecta credencialesincorrectas o campos vacíos.

                 Scriplet <% if() %>: ejecuta Java dentro del JSP.
                 request.getAttribute("error"): lee el atributo que el Servlet guardó con request.setAttribute("error","...").
             --%>
            <% if (request.getAttribute("error") != null) { %>
                <div class="alerta alerta-error">
                    <i class="bi bi-exclamation-circle-fill"></i>
                    <%-- <%= %> imprime el valor del atributo en el HTML --%>
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <%-- 
                 FORMULARIO DE LOGIN
                 action → URL del LoginServlet (/login)
                 method="post" - envía los datos en el cuerpo HTTP
                 autocomplete="off" - desactiva el autocompletado
                  --%>
            <form action="${pageContext.request.contextPath}/login"
                  method="post"
                  autocomplete="off">

                <%-- CAMPO EMAIL --%>
                <div class="form-grupo">
                    <label class="login-label" for="email">
                        Correo electrónico
                    </label>
                    <div class="login-campo-grupo">
                        <%-- Ícono del sobre dentro del campo --%>
                        <i class="bi bi-envelope login-campo-icono"></i>
                        <%--
                            name="email" - coincide con request.getParameter("email") en LoginServlet.
                            required - validación del navegador.
                            autofocus - el cursor llega aquí al cargar.
                        --%>
                        <input type="email"
                               id="email"
                               name="email"
                               class="login-input"
                               placeholder="admin@urbancaps.com"
                               required
                               autofocus>
                    </div>
                </div>

                <%-- CAMPO CONTRASEÑA --%>
                <div class="form-grupo">
                    <label class="login-label" for="passwordField">
                        Contraseña
                    </label>
                    <div class="login-campo-grupo">
                        <%-- Ícono del candado dentro del campo --%>
                        <i class="bi bi-lock login-campo-icono"></i>
                        <%--
                            name="password" - coincide con request.getParameter("password") en LoginServlet.
                            type="password" - oculta el texto con puntos.
                        --%>
                        <input type="password"
                               id="passwordField"
                               name="password"
                               class="login-input"
                               placeholder="••••••••"
                               required>
                        <%-- Botón ojo para mostrar/ocultar contraseña --%>
                        <button type="button"
                                class="login-toggle-pwd"
                                onclick="togglePassword()"
                                title="Mostrar u ocultar contraseña">
                            <i class="bi bi-eye" id="iconoOjo"></i>
                        </button>
                    </div>
                </div>

                <%-- BOTÓN DE INGRESO --%>
                <button type="submit" class="login-btn">
                    <i class="bi bi-box-arrow-in-right"></i>
                    Ingresar al sistema
                </button>

            </form>

            <%-- PIE DE LA TARJETA --%>
            <div class="login-footer">
                <i class="bi bi-shield-lock"></i>
                Acceso restringido — solo personal autorizado
            </div>

        </div><%-- fin login-card --%>

    </div><%-- fin login-wrapper --%>


    <%-- Bootstrap JS descargado localmente en js/ --%>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>

    <%-- Scripts generales del proyecto --%>
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>

    <script>

        // togglePassword()
        // Muestra u oculta la contraseña al hacer clic en el ícono ojo.
        // Alterna el type del input entre "password" y "text".

        function togglePassword() {
            const campo = document.getElementById('passwordField');
            const icono = document.getElementById('iconoOjo');

            if (campo.type === 'password') {
                // Mostrar contraseña en texto
                campo.type = 'text';
                icono.className = 'bi bi-eye-slash';
            } else {
                // Ocultar contraseña con puntos
                campo.type = 'password';
                icono.className = 'bi bi-eye';
            }
        }
    </script>

</body>
</html>
