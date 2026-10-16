package br.edu.pucgoias.pocketarcade;

public class Item {

    private final String palavra;
    private final String categoria;

    public Item(String palavra, String categoria) {
        this.palavra = palavra;
        this.categoria = categoria;
    }

    public String getPalavra() {
        return palavra;
    }

    public String getCategoria() {
        return categoria;
    }
}
