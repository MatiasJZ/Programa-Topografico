public abstract class tipoDeBlanco {

    private boolean aliado; // true = aliado, false = enemigo

    public tipoDeBlanco(boolean aliado) {
        this.aliado = aliado;
    }

    public boolean isAliado() {
        return aliado;
    }

    public void setAliado(boolean aliado) {
        this.aliado = aliado;
    }

    // Método que devuelve 1 si aliado, 0 si enemigo
    public int getEstadoNumerico() {
        return aliado ? 1 : 0;
    }
}
