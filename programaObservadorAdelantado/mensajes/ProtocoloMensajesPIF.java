package mensajes;

import dominio.PIF;

public class ProtocoloMensajesPIF {

    public static String formatearPIF(PIF p) {
        return "PIF"
        		+ "|BLANCO=" + p.getBlanco().getNombre()
                + "|NAT=" + p.getBlanco().getNaturaleza()    
             
                + "|MISION=" + p.getModoMision()
                + "|REGSOBRE=" + p.getRegistroSobre()
                + "|BARRFRENTE=" + p.getBarreraFrente()
                + "|BARRINC=" + p.getBarreraInclinacion()
        
                + "|EFECTO=" + p.getEfectoDeseado()
                + "|MODO DE FUEGO=" + p.getModoFuego()
                + "|CERCANO=" + (p.isCercano() ? "SI" : "NO")
                + "|GRANANGULO=" + (p.isGranAngulo() ? "SI" : "NO")
                + "|GRANADA=" + p.getGranada()
                + "|ESPOLETA=" + p.getEspoleta()
                + "|VOLUMEN=" + p.getVolumen()
                + "|HAZ=" + p.getHaz()
                + "|PIEZAS=" + p.getPiezas()
                + "|SECCION=" + p.getSeccion()
                + "|FGOCONT=" + p.isFgoCont()
                + "|FGOCONT=" + p.isTes();
    }
}
