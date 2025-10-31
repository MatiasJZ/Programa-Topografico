import java.util.HashMap;
import java.util.List;
import java.util.Map;

import armyc2.c5isr.renderer.utilities.MSInfo;
import armyc2.c5isr.renderer.utilities.MSLookup;
import armyc2.c5isr.renderer.utilities.SymbolID;

public class CodigosMilitares {

    private static final Map<String, String> mapa = new HashMap<>();
    
    private static String withHQorTF(String base, char pos17) {
        StringBuilder sb = new StringBuilder(base);
        sb.setCharAt(16, pos17);
        return sb.toString();
    }

    private static String withEchelon(String base, String echelon2d) {
        StringBuilder sb = new StringBuilder(base);
        sb.setCharAt(17, echelon2d.charAt(0));
        sb.setCharAt(18, echelon2d.charAt(1));
        return sb.toString();
    }

    private static void addHQTF(String keyBase, String sidcBase) {
        mapa.put(keyBase + "_HQ", withHQorTF(sidcBase, '2'));
        mapa.put(keyBase + "_TF", withHQorTF(sidcBase, '4'));
    }

    private static void addEchelon(String keyBase, String sidcBase, String echelonCode, String suffix) {
        mapa.put(keyBase + "_" + suffix, withEchelon(sidcBase, echelonCode));
    }

    static {
    	
    	//DRONES TERRESTRES
    	mapa.put("DRON_F", "10031000001219000000");
        mapa.put("DRON_H", "10061000001219000000");
        mapa.put("DRON_N", "10041000001219000000"); 
        mapa.put("DRON_U", "10011000001219000000");
        mapa.put("DRON_S", "10051000001219000000");
        mapa.put("DRON_P", "10001000001219000000");
        mapa.put("DRON_AA","10021000001219000000");
    	
        // INFANTERÍA
        mapa.put("INF_F", "10031000001211000000");
        mapa.put("INF_H", "10061000001211000000");
        mapa.put("INF_N", "10041000001211000000");
        mapa.put("INF_U", "10011000001211000000");
        mapa.put("INF_S", "10051000001211000000");
        mapa.put("INF_P", "10001000001211000000");
        mapa.put("INF_AA","10021000001211000000");
        
        //INFANTERIA MOTORIZADA
        mapa.put("INFMOT_F", "10031000001211040000");
        mapa.put("INFMOT_H", "10061000001211040000");
        mapa.put("INFMOT_N", "10041000001211040000"); 
        mapa.put("INFMOT_U", "10011000001211040000");
        mapa.put("INFMOT_S", "10051000001211040000");
        mapa.put("INFMOT_P", "10001000001211040000");
        mapa.put("INFMOT_AA","10021000001211040000");
        
        //INFANTERIA ANFIBIA
        mapa.put("INFANF_F", "10031000001211010000");
        mapa.put("INFANF_H", "10061000001211010000");
        mapa.put("INFANF_N", "10041000001211010000"); 
        mapa.put("INFANF_U", "10011000001211010000");
        mapa.put("INFANF_S", "10051000001211010000");
        mapa.put("INFANF_P", "10001000001211010000"); 
        mapa.put("INFANF_AA","10021000001211010000");
        
        //INFANTERIA MECANIZADA (ARMORED)
        mapa.put("INFP_F", "10031000001211020000");
        mapa.put("INFP_H", "10061000001211020000");
        mapa.put("INFP_N", "10041000001211020000");
        mapa.put("INFP_U", "10011000001211020000");
        mapa.put("INFP_S", "10051000001211020000");
        mapa.put("INFP_P", "10001000001211020000"); 
        mapa.put("INFP_AA","10021000001211020000");

        // TANQUES
        mapa.put("TNK_F", "10031000001201000000");
        mapa.put("TNK_H", "10061000001201000000");
        mapa.put("TNK_N", "10041000001201000000");
        mapa.put("TNK_U", "10011000001201000000");
        mapa.put("TNK_S", "10051000001201000000");
        mapa.put("TNK_P", "10001000001201000000");
        mapa.put("TNK_AA","10021000001201000000");

        // ARTILLERÍA
        mapa.put("ART_F", "10031000001303000000");
        mapa.put("ART_H", "10061000001303000000"); 
        mapa.put("ART_N", "10041000001303000000");
        mapa.put("ART_U", "10011000001303000000");
        mapa.put("ART_S", "10051000001303000000");
        mapa.put("ART_P", "10001000001303000000");
        mapa.put("ART_AA","10021000001303000000"); 
        
        //ARTILLERIA AUTOPROPULSADA
        mapa.put("ARTPRO_F", "10031000001303010000");
        mapa.put("ARTPRO_H", "10061000001303010000"); 
        mapa.put("ARTPRO_N", "10041000001303010000");
        mapa.put("ARTPRO_U", "10011000001303010000");
        mapa.put("ARTPRO_S", "10051000001303010000");
        mapa.put("ARTPRO_P", "10001000001303010000");
        mapa.put("ARTPRO_AA","10021000001303010000"); 
        
        //ARTILLERIA DE ADQUISICION DE BLANCOS
        mapa.put("ARTAD_F", "10031000001303020000");
        mapa.put("ARTAD_H", "10061000001303020000"); 
        mapa.put("ARTAD_N", "10041000001303020000"); 
        mapa.put("ARTAD_U", "10011000001303020000");
        mapa.put("ARTAD_S", "10051000001303020000");
        mapa.put("ARTAD_P", "10001000001303020000");
        mapa.put("ARTAD_AA","10021000001303020000");  

        // OBSERVADORES
        mapa.put("OBS_F", "10031000001212000000"); 
        mapa.put("OBS_H", "10061000001212000000");
        mapa.put("OBS_N", "10041000001212000000");
        mapa.put("OBS_U", "10011000001212000000");
        mapa.put("OBS_S", "10051000001212000000");
        mapa.put("OBS_P", "10001000001212000000");
        mapa.put("OBS_AA","10021000001212000000");
        
        // OBSERVADORES DE ARTILLERIA
        mapa.put("OBSAR_F", "10031000001304000000"); 
        mapa.put("OBSAR_H", "10061000001304000000");
        mapa.put("OBSAR_N", "10041000001304000000");
        mapa.put("OBSAR_U", "10011000001304000000");
        mapa.put("OBSAR_S", "10051000001304000000");
        mapa.put("OBSAR_P", "10001000001304000000");
        mapa.put("OBSAR_AA","10021000001304000000");
        
        // DEFENSA AEREA
        mapa.put("DEFA_F", "10031000001301000000"); 
        mapa.put("DEFA_H", "10061000001301000000");
        mapa.put("DEFA_N", "10041000001301000000");
        mapa.put("DEFA_U", "10011000001301000000");
        mapa.put("DEFA_S", "10051000001301000000");
        mapa.put("DEFA_P", "10001000001301000000");
        mapa.put("DEFA_AA","10021000001301000000");
        
        // GUERRA ELECTRONICA
        mapa.put("GE_F", "10031000001505000000"); 
        mapa.put("GE_H", "10061000001505000000");
        mapa.put("GE_N", "10041000001505000000");
        mapa.put("GE_U", "10011000001505000000");
        mapa.put("GE_S", "10051000001505000000");
        mapa.put("GE_P", "10001000001505000000");
        mapa.put("GE_AA","10021000001505000000");
        
        // COMANDO Y CONTROL
        mapa.put("CYC_F", "10031000001100000000"); 
        mapa.put("CYC_H", "10061000001100000000");
        mapa.put("CYC_N", "10041000001100000000");
        mapa.put("CYC_U", "10011000001100000000");
        mapa.put("CYC_S", "10051000001100000000");
        mapa.put("CYC_P", "10001000001100000000");
        mapa.put("CYC_AA","10021000001100000000");
        
        // MORTEROS 
        mapa.put("MOR_F", "10031000001308000000"); 
        mapa.put("MOR_H", "10061000001308000000");
        mapa.put("MOR_N", "10041000001308000000");
        mapa.put("MOR_U", "10011000001308000000");
        mapa.put("MOR_S", "10051000001308000000");
        mapa.put("MOR_P", "10001000001308000000");
        mapa.put("MOR_AA","10021000001308000000");

        // INGENIEROS
        mapa.put("ENG_F", "10031000001407000000");
        mapa.put("ENG_H", "10061000001407000000");
        mapa.put("ENG_N", "10041000001407000000"); 
        mapa.put("ENG_U", "10011000001407000000");
        mapa.put("ENG_S", "10051000001407000000");
        mapa.put("ENG_P", "10001000001407000000");
        mapa.put("ENG_AA","10021000001407000000");

        // SEÑALES
        mapa.put("SIG_F", "10031000001110000000");
        mapa.put("SIG_H", "10061000001110000000");
        mapa.put("SIG_N", "10041000001110000000");
        mapa.put("SIG_U", "10011000001110000000");
        mapa.put("SIG_S", "10051000001110000000");
        mapa.put("SIG_P", "10001000001110000000");
        mapa.put("SIG_AA","10021000001110000000");

        // MÉDICA
        mapa.put("MED_F", "10031000001613000000"); 
        mapa.put("MED_H", "10061000001613000000");

        // LOGÍSTICA
        mapa.put("LOG_F", "10031000001653000000");
        mapa.put("LOG_H", "10061000001653000000");
        mapa.put("LOG_N", "10041000001653000000");
        mapa.put("LOG_U", "10011000001653000000"); // Army Field Support
        mapa.put("LOG_S", "10051000001653000000");
        mapa.put("LOG_P", "10001000001653000000");
        mapa.put("LOG_AA","10021000001653000000");

        // HQ / TF (solo tácticos)
        String[] hqtfSet = {"DRON","INF","INFMOT","INFANF","INFP","TNK","ART","ARTPRO","ARTAD","OBS","OBSAR","DEFA","GE","CYC","MOR","ENG","SIG","LOG"};
        String[] affix = {"_F","_H","_N","_U","_S","_P","_AA"};
        for (String ent : hqtfSet) {
            for (String af : affix) {
                String key = ent + af;
                String sidc = mapa.get(key);
                if (sidc != null) {
                    addHQTF(key, sidc);
                }
            }
        }
        // ECHELON  
        for (String ent : new String[]{"INF","INFMOT","INFANF","INFP","TNK","ART","ARTPRO","ARTAD","DEFA","GE","ENG","SIG","LOG","CYC","MOR"}){
        	    for (String af : affix) {
        	        String key = ent + af;
        	        String sidc = mapa.get(key);
        	        if (sidc == null) continue;

        	        addEchelon(key, sidc, "14", "PLT");
        	        addEchelon(key, sidc, "15", "CO");
        	        addEchelon(key, sidc, "18", "BG");
        	        addEchelon(key, sidc, "21", "DIV");
        	    }
        }
    }
    
    public static String obtenerSIDC(String codigoAlfa) {
        if (codigoAlfa == null) return null;
        return mapa.get(codigoAlfa.toUpperCase());
    }
}
