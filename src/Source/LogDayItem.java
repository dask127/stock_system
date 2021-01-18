package Source;

import Source.LogItem;
import java.util.ArrayList;

public class LogDayItem {
    private String fecha;
    private ArrayList<LogItem> acciones_del_dia;

    public LogDayItem() {
        acciones_del_dia = new ArrayList<LogItem>();
    }

    public LogDayItem(String fecha) {
        acciones_del_dia = new ArrayList<LogItem>();
        this.fecha = fecha;
    }

    public void setAcciones_del_dia(ArrayList<LogItem> acciones_del_dia) {
        this.acciones_del_dia = acciones_del_dia;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public ArrayList<LogItem> getAcciones_del_dia() {
        return acciones_del_dia;
    }

    public void addAccion(LogItem item) {
        acciones_del_dia.add(item);
    }

    public String getFecha() {
        return fecha;
    }

    public boolean isEmpty(){
        return acciones_del_dia.isEmpty();
    }

    @Override
    public String toString() {
        return "LogDayItem{" + "fecha=" + fecha + ", acciones_del_dia=" + acciones_del_dia + '}';
    }

    @Override
    public boolean equals(Object obj) {
        try {
            LogDayItem ff = (LogDayItem) obj;
            if (ff.getFecha().equals(this.fecha))
                return true;
            else
                return false;

        } catch (Exception e) {
            return false;
        }
    }

}
