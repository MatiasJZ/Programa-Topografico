package interfaces;

/**
 * Interface for providing designation information, including a prefix and a counter.
 * Implementations of this interface are responsible for managing a string prefix and an integer counter,
 * which can be used for generating unique designations or identifiers.
 */
public interface DesignacionProvider {
    String getPrefijo();
    int getContador();
    void setPrefijo(String s);
    void setContador(int i);
}
