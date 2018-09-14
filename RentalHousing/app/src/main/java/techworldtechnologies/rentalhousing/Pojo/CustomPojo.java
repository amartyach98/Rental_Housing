package techworldtechnologies.rentalhousing.Pojo;

import java.util.ArrayList;

public class CustomPojo {

    //POJO class consists of get method and set method
    private String name;
    private String time, content;
    private ArrayList<CustomPojo> customPojo = new ArrayList<>();


    public CustomPojo() {

    }

    //getting content value
    public String getContent() {
        return content;
    }

    //setting content value
    public void setContent(String content) {
        this.content = content;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
