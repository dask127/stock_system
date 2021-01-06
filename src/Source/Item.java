package Source;

import java.util.Objects;

public class Item {
    private String id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private int venta;
    private int compra;
    private int cantidad;
    private String fecha_compra;
    private String fecha_venta;

    public Item(String nombre, String descripcion, int precioVenta, int precioCompra, int cantidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.venta = precioVenta;
        this.compra = precioCompra;
        this.cantidad = cantidad;
        fecha_venta = null;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getId() {
        return id;
    }

    public void setCompra(int compra) {
        this.compra = compra;
    }

    public String getFechaCompra() {
        return fecha_compra;
    }

    public String getFechaVenta() {
        return fecha_venta;
    }

    public void setVenta(int venta) {
        this.venta = venta;
    }

    public void setFechaCompra(String fecha_compra) {
        this.fecha_compra = fecha_compra;
    }

    public void setFechaVenta(String fecha_venta) {
        this.fecha_venta = fecha_venta;
    }

    public int getCantidad() {
        return cantidad;
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

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
        public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", categoria=" + categoria + ", venta=" + venta + ", compra=" + compra + ", cantidad=" + cantidad + ", fecha_compra=" + fecha_compra + '}';
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
        if (!Objects.equals(this.categoria, other.categoria)) {
            return false;
        }
        return true;
    }

    public String getCategoria() {
        return categoria;
    }
}
