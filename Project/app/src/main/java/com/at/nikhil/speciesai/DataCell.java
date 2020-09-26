package com.at.nikhil.speciesai;

public class DataCell {

    private String heading;
    private String description;

    public DataCell(String heading,String description){
        this.heading  = heading;
        this.description = description;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
