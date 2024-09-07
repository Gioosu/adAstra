package it.unimib.adastra.model.wiki;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WikiObj {

    @PrimaryKey
    int id;

    private String name;
    private String description;
    private String url;
    private String language;

    public WikiObj(int id, String name, String description, String url, String language) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Planet{" +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                ", url = '" + url + '\'' +
                ", language = '" + language + '\'' +
                '}';
    }
}
