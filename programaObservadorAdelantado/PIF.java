import java.time.LocalDateTime;
import java.util.UUID;

public class PIF {

    private String id; 
    private LocalDateTime fechaHora; 
    private String solicitante;           
    private Blanco blanco; 
    private String naturaleza;      
    private String metodoAtaque; 
    private int piezas; private int rondas; 
    private String tipoMunicion; 
    private String espoleta; 
    private String carga;                                                   
    
    public PIF(String id,LocalDateTime fechaHora,String solicitante,Blanco blanco,String crs,String naturaleza,String metodoAtaque,int piezas,int rondas,
            String tipoMunicion,String espoleta,String carga) {
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
}
