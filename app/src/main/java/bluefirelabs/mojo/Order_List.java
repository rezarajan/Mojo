package bluefirelabs.mojo;

/**
 * Created by Reza Rajan on 2017-05-28.
 */

public class Order_List {

    private String orderid;
    private String items;

    public Order_List(){

    }

    public Order_List(String orderid, String items) {
        this.orderid = orderid;
        this.items = items;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

}
