import armyc2.c5isr.renderer.utilities.MSLookup;
import armyc2.c5isr.renderer.utilities.MSInfo;

public class ExploradorSIDC_Modificadores {

    // tablas:
    private static final String[] HQ_CODES = {"00", "01", "02", "03"}; 
    // 00 = None, 01 = HQ, 02 = TF, 03 = TF HQ

    private static final String[][] ECHELONS = {
        {"00", "None"},
        {"11", "Team/Crew"},
        {"12", "Squad"},
        {"13", "Section"},
        {"14", "Platoon/Detachment"},
        {"15", "Company/Battery/Troop"},
        {"16", "Battalion/Squadron"},
        {"17", "Regiment/Group"},
        {"18", "Brigade"},
        {"21", "Division"},
        {"22", "Corps/Military District"},
        {"23", "Army"},
        {"24", "Army Group/Front"},
        {"25", "Region/Theater"},
        {"26", "Command"},
    };

    private static final String[][] MOBILITY = {
        {"00", "None"},
        {"61", "Wheeled (Limited Cross Country)"},
        {"62", "Wheeled (Cross Country)"},
        {"63", "Tracked"},
        {"64", "Wheeled and Tracked"},
        {"65", "Towed"},
        {"66", "Rail"},
        {"67", "Pack Animal"},
        {"68", "Over Snow (Ski)"},
        {"69", "Sled"},
        {"70", "Barge"},
        {"71", "Amphibious"},
        {"72", "Airborne"},
        {"73", "Air Assault"},
        {"74", "Attack"},
        {"75", "Utility"},
    };

    public static void main(String[] args) {
        MSLookup lookup = MSLookup.getInstance();
        String symbolSet = "10";
        String entity = "121100";

        System.out.println("Explorador de SIDC – HQ / Task Force / Echelon / Mobility \n");

        for (String hq : HQ_CODES) {
            for (String[] echelon : ECHELONS) {
                for (String[] mob : MOBILITY) {
                    String sidc = "10" + "03" + symbolSet + "00" + hq + entity + echelon[0] + mob[0];

                    MSInfo info = null;
                    try {
                        info = MSLookup.getInstance().getMSLInfo(sidc);
                    } catch (Exception ignored) {}

                    String name = (info != null && info.getName() != null) ? info.getName() : "Unknown";
                    String geom = (info != null && info.getGeometry() != null) ? info.getGeometry() : "-";

                    if (!"Unknown".equals(name)) {
                        System.out.printf(
                            "%s → %-35s (%s) | HQ:%s | Echelon:%s | Mobility:%s%n",
                            sidc,
                            name,
                            geom,
                            hq,
                            echelon[1],
                            mob[1]
                        );
                    }
                }
            }
        }

        System.out.println("/n Exploracion completa. Combinaciones listadas arriba.");
    }
}
