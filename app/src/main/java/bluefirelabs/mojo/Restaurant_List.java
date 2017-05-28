package bluefirelabs.mojo;

/**
 * Created by Reza Rajan on 2017-05-28.
 */

public class Restaurant_List {

    private String restaurant;
    private String description;
    private String icon;

    public Restaurant_List(){

    }

    public Restaurant_List(String restaurant, String description, String icon){
        this.restaurant = restaurant;
        this.description = description;
        this.icon = icon;
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

}
