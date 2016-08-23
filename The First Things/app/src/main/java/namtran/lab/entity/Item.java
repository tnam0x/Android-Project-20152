package namtran.lab.entity;

/**
 * Created by Legendary on 27/04/2016.
 */
public class Item {
    private String cost;
    private String type;
    private String note;
    private String date;
    private int id;

    public Item() {
        // Cần constructor này để Firebase đồng bộ
    }

    public Item(String cost, String type, String note, String date, int id) {
        this.cost = cost;
        this.type = type;
        this.note = note;
        this.date = date;
        this.id = id;
    }

    public String getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return cost + "-" + date;
    }
}
