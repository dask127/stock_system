/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

/**
 *
 * @author octav
 */
public class CategoryStock {
    private int stock;
        private String categoria;

    public CategoryStock(String categoria, int stock) {
        this.categoria = categoria;
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
}
