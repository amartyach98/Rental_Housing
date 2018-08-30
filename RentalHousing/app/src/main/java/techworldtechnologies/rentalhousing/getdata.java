package techworldtechnologies.rentalhousing;

public class getdata {
    private String tname;
    private String thousenumber;
    getdata(String tname,String thousenumber){
        this.setTname(tname);
        this.setThousenumber(thousenumber);

    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getThousenumber() {
        return thousenumber;
    }

    public void setThousenumber(String thousenumber) {
        this.thousenumber = thousenumber;
    }
}
