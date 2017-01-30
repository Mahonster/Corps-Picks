package com.mahoneysoftware.corpspicks.Objects;

/**
 * An object for reference to each corps.
 * Created by Dylan on 12/23/2016.
 */

public class Corps {
    private String id = "";
    private String name = "";
    private String logoImageUrl = "";
    private String logoThumbUrl = "";

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogoImageUrl(String logoImageUrl) {
        this.logoImageUrl = logoImageUrl;
    }

    public void setLogoThumbUrl(String logoThumbUrl) {
        this.logoThumbUrl = logoThumbUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    public String getLogoThumbUrl() {
        return logoThumbUrl;
    }
}
