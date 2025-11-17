package dominio;
import java.time.LocalDateTime;
import java.util.UUID;

public class PIF {

    private String id; 
    private LocalDateTime fechaHora; 
    private String solicitante;           
    private Blanco blanco; 
    private String naturaleza;      
    private String metodoAtaque; 
    private int piezas; 
    private int rondas; 
    private String tipoMunicion; 
    private String espoleta; 
    private String carga;      
    private String modoFuego;         
    private boolean fuegoContinuo;     
    private boolean tes;            
    private String totSegundos;      
    private String seccion;          

    public PIF(String id, LocalDateTime fechaHora, String solicitante, Blanco blanco, String crs,
               String naturaleza, String metodoAtaque, int piezas, int rondas,
               String tipoMunicion, String espoleta, String carga) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
        this.solicitante = solicitante;
        this.blanco = blanco;
        this.naturaleza = naturaleza;
        this.metodoAtaque = metodoAtaque;
        this.piezas = piezas;
        this.rondas = rondas;
        this.tipoMunicion = tipoMunicion;
        this.espoleta = espoleta;
        this.carga = carga;
    }
    
    public String getId() { return id; }
    
    public LocalDateTime getFechaHora() { return fechaHora; }
    
    public String getSolicitante() { return solicitante; }
    
    public Blanco getBlanco() { return blanco; }
    
    public String getNaturaleza() { return naturaleza; }
    
    public String getMetodoAtaque() { return metodoAtaque; }
    
    public int getPiezas() { return piezas; }
    
    public int getRondas() { return rondas; }
    
    public String getTipoMunicion() { return tipoMunicion; }
    
    public String getEspoleta() { return espoleta; }
    
    public String getCarga() { return carga; }

    public String getModoFuego() { return modoFuego; }
    
    public void setModoFuego(String modoFuego) { this.modoFuego = modoFuego; }

    public boolean isFuegoContinuo() { return fuegoContinuo; }
    
    public void setFuegoContinuo(boolean fuegoContinuo) { this.fuegoContinuo = fuegoContinuo; }

    public boolean isTes() { return tes; }
    
    public void setTes(boolean tes) { this.tes = tes; }

    public String getTotSegundos() { return totSegundos; }
    
    public void setTotSegundos(String totSegundos) { this.totSegundos = totSegundos; }

    public String getSeccion() { return seccion; }
    
    public void setSeccion(String seccion) { this.seccion = seccion; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s | %s (%s)",
                fechaHora.toLocalTime().withNano(0),
                blanco != null ? blanco.getNombre() : "Sin blanco",
                metodoAtaque,
                naturaleza,
                solicitante);
    }

	public Object getHaz() {
		return 1;
	}
}
