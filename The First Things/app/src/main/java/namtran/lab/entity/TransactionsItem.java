package namtran.lab.entity;

/**
 * Created by Legendary on 27/04/2016.
 */
public class TransactionsItem {
    private String cost;
    private String type;
    private String note;
    private String date;
    private String id;

    public TransactionsItem() {
        // Cần constructor này để Firebase đồng bộ
    }

    public TransactionsItem(String cost, String type, String note, String date, String id) {
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

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return cost + "-" + date;
    }
}
