package mensajes;

import dominio.PIF;

public class ProtocoloMensajesPIF {

    public static String formatearPIF(PIF p) {
        return "PIF"
                + "|NOMBRE=" + p.getBlanco().getNombre()
                + "|NAT=" + p.getNaturaleza()
                + "|PIEZAS=" + p.getPiezas()
                + "|RONDAS=" + p.getRondas()
                + "|MUNICION=" + p.getTipoMunicion()
                + "|ESPOLETA=" + p.getEspoleta()
                + "|CARGA=" + p.getCarga()
                + "|MODO=" + p.getModoFuego()
                + "|TOT=" + p.getTotSegundos()
                + "|SECCION=" + p.getSeccion();
    }
}
