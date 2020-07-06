package com.manywho.services.dummy.dummy;

import java.util.Date;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Dummy")
public class Dummy implements Type {
    @Type.Identifier
    private String id;

    @Type.Property(name = "Name", contentType = ContentType.String)
    private String name;

    @Type.Property(name = "Age", contentType = ContentType.Number)
    private int age;

    @Type.Property(name = "Hired", contentType = ContentType.DateTime)
    private Date hired;

    public Dummy() {}

    public Dummy(String id, String name, int age, Date birthDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.hired = birthDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Date hired() {
        return hired;
    }
}
