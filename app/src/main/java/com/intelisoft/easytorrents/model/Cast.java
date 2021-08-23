package com.intelisoft.easytorrents.model;

import java.io.Serializable;

/**
 * Created by Technophile on 11/27/16.
 */

public class Cast implements Serializable {
    String name, characterName, urlSmallImage, imdbCode;


    public Cast() {

    }

    public Cast(String name, String characterName, String urlSmallImage, String imdbCode) {
        this.name = name;
        this.characterName = characterName;
        this.urlSmallImage = urlSmallImage;
        this.imdbCode = imdbCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getUrlSmallImage() {
        return urlSmallImage;
    }

    public void setUrlSmallImage(String urlSmallImage) {
        this.urlSmallImage = urlSmallImage;
    }

    public String getImdbCode() {
        return imdbCode;
    }

    public void setImdbCode(String imdbCode) {
        this.imdbCode = imdbCode;
    }
}
