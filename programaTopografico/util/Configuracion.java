package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuracion {

    private static Properties propiedades;
    private static final String ARCHIVO_CONFIG = "config.properties";

    static {
        propiedades = new Properties();
        cargarConfiguracion();
    }

    private static void cargarConfiguracion() {
        File archivo = new File(ARCHIVO_CONFIG);

        if (!archivo.exists()) {
            crearConfiguracionPorDefecto(archivo);
        }

        try (FileInputStream fis = new FileInputStream(archivo)) {
            propiedades.load(fis);
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo leer config.properties: " + e.getMessage());
        }
    }

    private static void crearConfiguracionPorDefecto(File archivo) {
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            propiedades.setProperty("operadores_autorizados", "admin,juarez");
            propiedades.setProperty("puerto_enlace", "10011");
            propiedades.setProperty("ips_destinos", "192.168.1.2,192.168.2.2");
            propiedades.setProperty("ip_sugerida_UI", "192.168.0.");
            propiedades.setProperty("ruta_mapa_defecto", "C:/Users/54293/Desktop/Archivos SARGO/mapaV1.TIF");
            propiedades.setProperty("prefijo_blancos", "AF");
            propiedades.setProperty("contador_blancos_inicio", "6400");
            
            propiedades.store(fos, "--- CONFIGURACIÓN DEL SISTEMA TÁCTICO SARGO ---");
            System.out.println("[INFO] Archivo config.properties creado por defecto.");
        } catch (IOException e) {
            System.err.println("[ERROR] Al crear config.properties: " + e.getMessage());
        }
    }

    public static String get(String clave) {
        return propiedades.getProperty(clave, "");
    }

    public static String get(String clave, String valorPorDefecto) {
        return propiedades.getProperty(clave, valorPorDefecto);
    }

    public static int getInt(String clave, int valorPorDefecto) {
        try {
            return Integer.parseInt(propiedades.getProperty(clave, String.valueOf(valorPorDefecto)));
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }
}