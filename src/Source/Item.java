package Source;

import java.util.Objects;

public class Item {
    private String id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private int venta;
    private int compra;

    public Item(String nombre, String descripcion, int precioVenta, int precioCompra) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.venta = precioVenta;
        this.compra = precioCompra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVenta() {
        return venta;
    }

    public int getCompra() {
        return compra;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setprecioVenta(int precioVenta) {
        this.venta = precioVenta;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;
        if (this.venta != other.venta) {
            return false;
        }
        if (this.compra != other.compra) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        if (!Objects.equals(this.descripcion, other.descripcion)) {
            return false;
        }
        if (!Objects.equals(this.categoria, other.categoria)) {
            return false;
        }
        return true;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", categoria=" + categoria + ", precioVenta=" +venta + ", precioCompra=" + compra + '}';
    }

    public String getCategoria() {
        return categoria;
    }
}
