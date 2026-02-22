package milsymb;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;

import armyc2.c5isr.renderer.utilities.MSLookup;
import armyc2.c5isr.renderer.utilities.MSInfo;
import armyc2.c5isr.renderer.utilities.SymbolID;

/**
 * La clase {@code ExploradorDeSIDC} explora y reconstruye la lista completa de identificadores de símbolos (SIDC)
 * de la versión MIL-STD-2525D utilizando la biblioteca milsymb. Extrae los conjuntos de símbolos y códigos de entidad,
 * consulta sus nombres y geometrías asociadas, y genera un archivo de texto en el escritorio del usuario con la información
 * recopilada.
 *
 * <p>
 * El archivo de salida contiene una lista de todos los símbolos encontrados, mostrando el conjunto de símbolos, el código
 * de entidad, el nombre y la geometría de cada símbolo. El proceso muestra el progreso en consola cada 500 símbolos procesados.
 * </p>
 *
 * <p>
 * Uso principal: Ejecución directa desde la línea de comandos o desde un entorno de desarrollo para generar un inventario
 * de símbolos militares según la especificación 2525D.
 * </p>
 *
 * <p>
 * Dependencias:
 * <ul>
 *   <li>{@code SymbolID} para la versión de la norma.</li>
 *   <li>{@code MSLookup} para la obtención de la lista de IDs y la información de los símbolos.</li>
 *   <li>{@code MSInfo} para acceder a los detalles de cada símbolo.</li>
 * </ul>
 * </p>
 *
 * <p>
 * El archivo generado se guarda como {@code SIDC_2525D_Reconstruido.txt} en el escritorio del usuario.
 * </p>
 *
 * @author [Matias Leonel Juarez]
 */
public class ExploradorDeSIDC {

    public static void main(String[] args) {
    	
        try {
            int version = SymbolID.Version_2525D;
            MSLookup lookup = MSLookup.getInstance();

            List<String> idList = lookup.getIDList(version);
            System.out.println("📦 Total IDs encontrados: " + idList.size());

            String desktop = Paths.get(System.getProperty("user.home"), "Desktop").toString();
            String outFile = desktop + "/SIDC_2525D_Reconstruido.txt";

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
                bw.write("=== EXPLORACIÓN COMPLETA DE SÍMBOLOS (Reconstruyendo SIDC completo) ===\n");
                bw.write("Campos: [SymbolSet + EntityCode] → [Nombre] (Geometry)\n\n");

                int count = 0;
                for (String id : idList) {
                    if (id == null || id.length() < 8)
                        continue;

                    String symbolSet = id.substring(0, 2);   
                    String entityCode = id.substring(2, 8); 
                    
                    // formato: 10 03 [SymbolSet] 0000 [EntityCode] 00000000
                    String sidc = "10" + "03" + symbolSet + "0000" + entityCode + "00000000";

                    String name = "Unknown";
                    String geom = "Unknown";
                    try {
                        MSInfo info = MSLookup.getInstance().getMSLInfo(sidc);
                        if (info != null) {
                            if (info.getName() != null && !info.getName().isBlank())
                                name = info.getName().trim();
                            if (info.getGeometry() != null && !info.getGeometry().isBlank())
                                geom = info.getGeometry().trim();
                        }
                    } catch (Exception ignored) {}

                    bw.write(symbolSet + " + " + entityCode + " → " + name + "  (" + geom + ")\n");
                    count++;

                    if (count % 500 == 0)
                        System.out.println("Progreso: " + count + " / " + idList.size());
                }
                bw.write("\nTotal símbolos listados: " + count + "\n");
                System.out.println("\n✅ Archivo generado correctamente en: " + outFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
