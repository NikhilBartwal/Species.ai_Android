package com.at.nikhil.speciesai.DataContainer;

public class BirdData {

    private String name;
    private String tag;
    private String description;
    private String url;
    private String taxon;
    private String genus;
    private String species;
    private String scientific_name;

    public BirdData(String name,String tag,String description,String url,String taxon,String genus,String species,String scientific_name){
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.url = url;
        this.taxon = taxon;
        this.genus = genus;
        this.species = species;
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

    public String getGenus() {
        return genus;
    }

    public String getSpecies() {
        return species;
    }

    public String getScientific_name() {
        return scientific_name;
    }
}
