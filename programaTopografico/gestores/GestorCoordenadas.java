package gestores;

import java.util.Locale;

public class GestorCoordenadas {

    private static final double OFFSET_X_PRINCIPAL = 500000.0;
    private static final double OFFSET_X_SECUNDARIO = 600000.0;
    private static final double OFFSET_Y = 5600000.0;

    public static double aInternaX(String visual) throws NumberFormatException {
        if (visual == null || visual.trim().isEmpty()) return 0.0;
        double val = Double.parseDouble(visual.trim().replace(",", "."));
        if (val == 0.0) return 0.0; 
        
        if (val >= OFFSET_X_PRINCIPAL) return val; 

        if (val < 10000.0) {
            return val + OFFSET_X_SECUNDARIO;
        } 
        else {
            return val + OFFSET_X_PRINCIPAL;
        }
    }

    public static double aInternaY(String visual) throws NumberFormatException {
        if (visual == null || visual.trim().isEmpty()) return 0.0;
        double val = Double.parseDouble(visual.trim().replace(",", "."));
        if (val == 0.0) return 0.0;
        if (val >= OFFSET_Y) return val; 
        return val + OFFSET_Y;
    }
    
    public static double aInternaCota(String visual) throws NumberFormatException {
        if (visual == null || visual.trim().isEmpty()) return 0.0;
        return Double.parseDouble(visual.trim().replace(",", "."));
    }

    public static String aVisualX(double interna, int decimales) {
        if (interna == 0) return ""; 
        double visual = extraerSesgoX(interna);
        return formatearXConCeros(visual, decimales);
    }

    public static String aVisualY(double interna, int decimales) {
        if (interna == 0) return "";
        double visual = interna >= OFFSET_Y ? interna - OFFSET_Y : interna;
        return formatearNormal(visual, decimales);
    }

    public static String aVisualX(double interna) {
        if (interna == 0) return "0";
        double visual = extraerSesgoX(interna);
        return formatearXDinamicoConCeros(visual);
    }

    public static String aVisualY(double interna) {
        if (interna == 0) return "0";
        double visual = interna >= OFFSET_Y ? interna - OFFSET_Y : interna;
        return formatearDinamicoNormal(visual);
    }
    
    private static double extraerSesgoX(double interna) {
        if (interna >= OFFSET_X_SECUNDARIO && interna < 700000.0) {
            return interna - OFFSET_X_SECUNDARIO;
        } else if (interna >= OFFSET_X_PRINCIPAL && interna < OFFSET_X_SECUNDARIO) {
            return interna - OFFSET_X_PRINCIPAL;
        }
        return interna; 
    }

    private static String formatearNormal(double valor, int decimales) {
        if (decimales == 0) return String.format(Locale.US, "%.0f", valor);
        return String.format(Locale.US, "%." + decimales + "f", valor);
    }

    private static String formatearDinamicoNormal(double valor) {
        if (valor == Math.floor(valor)) return String.format(Locale.US, "%.0f", valor);
        return String.format(Locale.US, "%.2f", valor);
    }

    private static String formatearXConCeros(double valor, int decimales) {
        if (decimales == 0) {
            return String.format(Locale.US, "%05.0f", valor);
        } else {
            int totalChars = 5 + 1 + decimales;
            return String.format(Locale.US, "%0" + totalChars + "." + decimales + "f", valor);
        }
    }

    private static String formatearXDinamicoConCeros(double valor) {
        if (valor == Math.floor(valor)) {
            return String.format(Locale.US, "%05.0f", valor);
        } else {
            return String.format(Locale.US, "%08.2f", valor);
        }
    }
}