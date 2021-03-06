package com.manywho.services.dummy.account;


import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Account", summary = "A description of an account")
public class Account implements Type {

    @Type.Identifier
    @Type.Property(name = "id", contentType = ContentType.String)
    private String id;

    @Type.Property(name = "name", contentType = ContentType.String)
    private String name;

    @Type.Property(name = "company", contentType = ContentType.String)
    private String company;

    @Type.Property(name = "stateid", contentType = ContentType.String)
    private String stateid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStateid() {
        return stateid;
    }

    public void setStateid(String stateid) {
        this.stateid = stateid;
    }
}