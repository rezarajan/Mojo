package bluefirelabs.mojo.handlers.adapters;

/**
 * Created by Reza Rajan on 2017-05-28.
 */

public class Vendor_Order_List {

    private String restaurant;
    private String description;
    private String icon;
    private String orderid;
    private String name;
    private String vendoruid;
    private String result;
    private String type;

    public Vendor_Order_List() {

    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendoruid() {
        return vendoruid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setVendoruid(String vendoruid) {
        this.vendoruid = vendoruid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
