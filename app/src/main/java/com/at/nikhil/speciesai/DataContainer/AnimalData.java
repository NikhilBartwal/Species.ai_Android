package com.at.nikhil.speciesai.DataContainer;

public class AnimalData {
    private String name;
    private String tag;
    private String description;
    private String url;
    private String taxon;
    private String nickname;
    private String lifespan;
    private String scientific_name;
    private String country;

    public AnimalData(String name,String tag,String description,String url,String taxon,String nickname, String scientific_name,String lifespan,String country){
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.url = url;
        this.taxon = taxon;
        this.nickname = nickname;
        this.lifespan = lifespan;
        this.country = country;
        this.scientific_name = scientific_name;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getTaxon() {
        return taxon;
    }

    public String getNickname() {
        return nickname;
    }

    public String getLifespan() {
        return lifespan;
    }

    public String getScientific_name() {
        return scientific_name;
    }

    public String getCountry() {
        return country;
    }
}
