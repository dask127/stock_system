package Source;

import java.util.ArrayList;

public class LogItem {
    private String action;
    private String id;
    private ArrayList<Item> items;

    public LogItem() {
        items = new ArrayList<Item>();
    }

    public LogItem(String action, String id) {
        items = new ArrayList<Item>();
        this.action = action;
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }

    public String getId() {
        return id;
    }

    public void addItem(Item ff) {
        items.add(ff);
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "LogItem{" + ", action=" + action + ", items=" + items + '}';
    }

    @Override
    public boolean equals(Object obj) {
        try {
            LogItem ff = (LogItem) obj;
            if ((ff.getAction().equals(this.action)) && (ff.getId().equals(this.id)))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

}
