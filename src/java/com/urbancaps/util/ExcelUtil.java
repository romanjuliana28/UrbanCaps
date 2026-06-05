package com.urbancaps.util;

import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import org.apache.poi.ss.util.CellRangeAddress;

import jakarta.servlet.http.HttpServletResponse;

import com.urbancaps.modelo.bean.Rol;
import com.urbancaps.modelo.bean.Privilegio;
import com.urbancaps.modelo.bean.Usuario;
import com.urbancaps.modelo.bean.Proveedor;
import com.urbancaps.modelo.bean.Inventario;
import com.urbancaps.modelo.bean.Catalogo;

import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.io.IOException;

public class ExcelUtil {

    private static final SimpleDateFormat SDF =
        new SimpleDateFormat("dd/MM/yyyy");

    private static final NumberFormat MONEDA =
        NumberFormat.getNumberInstance(new Locale("es", "CO"));

    private static CellStyle crearEstiloEncabezado(Workbook wb) {
        CellStyle estilo = wb.createCellStyle();

        Font fuente = wb.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        fuente.setFontHeightInPoints((short) 11);
        estilo.setFont(fuente);

        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        estilo.setAlignment(HorizontalAlignment.CENTER);

        return estilo;
    }

    private static CellStyle crearEstiloTitulo(Workbook wb) {
        CellStyle estilo = wb.createCellStyle();

        Font fuente = wb.createFont();
        fuente.setBold(true);
        fuente.setFontHeightInPoints((short) 14);
        fuente.setColor(IndexedColors.BLACK.getIndex());
        estilo.setFont(fuente);

        estilo.setFillForegroundColor(IndexedColors.GOLD.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        estilo.setAlignment(HorizontalAlignment.CENTER);

        return estilo;
    }

    private static CellStyle crearEstiloFila(Workbook wb) {
        CellStyle estilo = wb.createCellStyle();
        Font fuente = wb.createFont();
        fuente.setFontHeightInPoints((short) 10);
        estilo.setFont(fuente);
        return estilo;
    }

    private static void configurarDescarga(
            HttpServletResponse response, String nombreArchivo) {

        response.setContentType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"" + nombreArchivo + "\""
        );

        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
    }

    private static void agregarTitulo(
            Sheet hoja, Workbook wb,
            String titulo, int totalColumnas) {

        Row filaTitulo = hoja.createRow(0);
        filaTitulo.setHeightInPoints(28);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("🧢 URBAN CAPS — " + titulo);
        celdaTitulo.setCellStyle(crearEstiloTitulo(wb));

        hoja.addMergedRegion(
            new CellRangeAddress(0, 0, 0, totalColumnas - 1)
        );

        Row filaFecha = hoja.createRow(1);
        Cell celdaFecha = filaFecha.createCell(0);
        celdaFecha.setCellValue(
            "Generado el: " + new SimpleDateFormat("dd/MM/yyyy HH:mm")
                                  .format(new java.util.Date())
        );
        hoja.addMergedRegion(
            new CellRangeAddress(1, 1, 0, totalColumnas - 1)
        );

        hoja.createRow(2);
    }

    public static void exportarRoles(
            List<Rol> roles, HttpServletResponse response)
            throws IOException {

        Workbook wb = new XSSFWorkbook();

        Sheet hoja = wb.createSheet("Roles");

        CellStyle estiloEnc  = crearEstiloEncabezado(wb);
        CellStyle estiloFila = crearEstiloFila(wb);

        agregarTitulo(hoja, wb, "Reporte de Roles", 4);

        String[] encabezados = {"#", "Nombre del Rol", "Descripción", "Estado"};
        Row filaEnc = hoja.createRow(3);
        filaEnc.setHeightInPoints(18);

        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEnc.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEnc);
        }

        int numFila = 4;
        if (roles != null) {
            for (Rol rol : roles) {
                Row fila = hoja.createRow(numFila++);

                fila.createCell(0).setCellValue(rol.getIdRol());
                fila.createCell(1).setCellValue(rol.getNombreRol());
                fila.createCell(2).setCellValue(
                    rol.getDescripcion() != null ? rol.getDescripcion() : "—"
                );
                fila.createCell(3).setCellValue(
                    rol.getEstado() == 1 ? "Activo" : "Inactivo"
                );

                for (int i = 0; i < 4; i++) {
                    fila.getCell(i).setCellStyle(estiloFila);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            hoja.autoSizeColumn(i);
        }

        configurarDescarga(response, "Urban_Caps_Roles.xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    public static void exportarPrivilegios(
            List<Privilegio> privilegios, HttpServletResponse response)
            throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet hoja  = wb.createSheet("Privilegios");

        CellStyle estiloEnc  = crearEstiloEncabezado(wb);
        CellStyle estiloFila = crearEstiloFila(wb);

        agregarTitulo(hoja, wb, "Reporte de Privilegios", 5);

        String[] encabezados = {"#", "Nombre", "Módulo", "Descripción", "Estado"};
        Row filaEnc = hoja.createRow(3);
        filaEnc.setHeightInPoints(18);

        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEnc.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEnc);
        }

        int numFila = 4;
        if (privilegios != null) {
            for (Privilegio p : privilegios) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(p.getIdPrivilegio());
                fila.createCell(1).setCellValue(p.getNombre());
                fila.createCell(2).setCellValue(p.getModulo());
                fila.createCell(3).setCellValue(
                    p.getDescripcion() != null ? p.getDescripcion() : "—"
                );
                fila.createCell(4).setCellValue(
                    p.getEstado() == 1 ? "Activo" : "Inactivo"
                );
                for (int i = 0; i < 5; i++) {
                    fila.getCell(i).setCellStyle(estiloFila);
                }
            }
        }

        for (int i = 0; i < 5; i++) hoja.autoSizeColumn(i);

        configurarDescarga(response, "Urban_Caps_Privilegios.xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    public static void exportarUsuarios(
            List<Usuario> usuarios, HttpServletResponse response)
            throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet hoja  = wb.createSheet("Usuarios");

        CellStyle estiloEnc  = crearEstiloEncabezado(wb);
        CellStyle estiloFila = crearEstiloFila(wb);

        agregarTitulo(hoja, wb, "Reporte de Usuarios", 6);

        String[] encabezados = {
            "#", "Nombre", "Apellido", "Email", "Rol", "Estado"
        };
        Row filaEnc = hoja.createRow(3);
        filaEnc.setHeightInPoints(18);

        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEnc.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEnc);
        }

        int numFila = 4;
        if (usuarios != null) {
            for (Usuario u : usuarios) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(u.getIdUsuario());
                fila.createCell(1).setCellValue(u.getNombre());
                fila.createCell(2).setCellValue(u.getApellido());
                fila.createCell(3).setCellValue(u.getEmail());
                fila.createCell(4).setCellValue(u.getNombreRol());
                fila.createCell(5).setCellValue(
                    u.getEstado() == 1 ? "Activo" : "Inactivo"
                );
                for (int i = 0; i < 6; i++) {
                    fila.getCell(i).setCellStyle(estiloFila);
                }
            }
        }

        for (int i = 0; i < 6; i++) hoja.autoSizeColumn(i);

        configurarDescarga(response, "Urban_Caps_Usuarios.xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    public static void exportarProveedores(
            List<Proveedor> proveedores, HttpServletResponse response)
            throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet hoja  = wb.createSheet("Proveedores");

        CellStyle estiloEnc  = crearEstiloEncabezado(wb);
        CellStyle estiloFila = crearEstiloFila(wb);

        agregarTitulo(hoja, wb, "Reporte de Proveedores", 7);

        String[] encabezados = {
            "#", "Nombre", "Apellidos", "Contacto",
            "Correo", "Dirección", "Estado"
        };
        Row filaEnc = hoja.createRow(3);
        filaEnc.setHeightInPoints(18);

        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEnc.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEnc);
        }

        int numFila = 4;
        if (proveedores != null) {
            for (Proveedor p : proveedores) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(p.getIdProveedor());
                fila.createCell(1).setCellValue(p.getNombre());
                fila.createCell(2).setCellValue(p.getApellidos());
                fila.createCell(3).setCellValue(p.getContacto());
                fila.createCell(4).setCellValue(
                    p.getCorreo() != null ? p.getCorreo() : "—"
                );
                fila.createCell(5).setCellValue(p.getDireccion());
                fila.createCell(6).setCellValue(
                    p.getEstado() == 1 ? "Activo" : "Inactivo"
                );
                for (int i = 0; i < 7; i++) {
                    fila.getCell(i).setCellStyle(estiloFila);
                }
            }
        }

        for (int i = 0; i < 7; i++) hoja.autoSizeColumn(i);

        configurarDescarga(response, "Urban_Caps_Proveedores.xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    public static void exportarInventario(
            List<Inventario> inventario, HttpServletResponse response)
            throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet hoja  = wb.createSheet("Inventario");

        CellStyle estiloEnc  = crearEstiloEncabezado(wb);
        CellStyle estiloFila = crearEstiloFila(wb);

        agregarTitulo(hoja, wb, "Reporte de Inventario", 8);

        String[] encabezados = {
            "#", "Producto", "Categoría", "Proveedor",
            "Cantidad", "Precio Compra", "Fecha Ingreso", "Estado"
        };
        Row filaEnc = hoja.createRow(3);
        filaEnc.setHeightInPoints(18);

        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEnc.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEnc);
        }

        int numFila = 4;
        if (inventario != null) {
            for (Inventario inv : inventario) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(inv.getIdInventario());
                fila.createCell(1).setCellValue(inv.getNombreProducto());
                fila.createCell(2).setCellValue(inv.getNombreCategoria());
                fila.createCell(3).setCellValue(inv.getNombreProveedor());
                fila.createCell(4).setCellValue(inv.getCantidad());
                // Precio formateado con separador de miles
                fila.createCell(5).setCellValue(
                    "$" + MONEDA.format(inv.getPrecioCompra())
                );
                fila.createCell(6).setCellValue(
                    inv.getFechaIngreso() != null
                    ? SDF.format(inv.getFechaIngreso()) : "—"
                );
                fila.createCell(7).setCellValue(
                    inv.getEstado() == 1 ? "Activo" : "Inactivo"
                );
                for (int i = 0; i < 8; i++) {
                    fila.getCell(i).setCellStyle(estiloFila);
                }
            }
        }

        for (int i = 0; i < 8; i++) hoja.autoSizeColumn(i);

        configurarDescarga(response, "Urban_Caps_Inventario.xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    public static void exportarCatalogo(
            List<Catalogo> catalogo, HttpServletResponse response)
            throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet hoja  = wb.createSheet("Catálogo");

        CellStyle estiloEnc  = crearEstiloEncabezado(wb);
        CellStyle estiloFila = crearEstiloFila(wb);

        agregarTitulo(hoja, wb, "Reporte de Catálogo", 7);

        String[] encabezados = {
            "#", "Título", "Categoría", "Producto Inventario",
            "Precio Venta", "Stock", "Visibilidad"
        };
        Row filaEnc = hoja.createRow(3);
        filaEnc.setHeightInPoints(18);

        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEnc.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEnc);
        }

        int numFila = 4;
        if (catalogo != null) {
            for (Catalogo cat : catalogo) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(cat.getIdCatalogo());
                fila.createCell(1).setCellValue(cat.getTitulo());
                fila.createCell(2).setCellValue(cat.getNombreCategoria());
                fila.createCell(3).setCellValue(cat.getNombreProducto());
                fila.createCell(4).setCellValue(
                    "$" + MONEDA.format(cat.getPrecioVenta())
                );
                fila.createCell(5).setCellValue(cat.getStock());
                fila.createCell(6).setCellValue(
                    cat.getEstado() == 1 ? "Visible" : "Oculto"
                );
                for (int i = 0; i < 7; i++) {
                    fila.getCell(i).setCellStyle(estiloFila);
                }
            }
        }

        for (int i = 0; i < 7; i++) hoja.autoSizeColumn(i);

        configurarDescarga(response, "Urban_Caps_Catalogo.xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }
}