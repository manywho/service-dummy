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

    @Type.Property(name = "Bio", contentType = ContentType.Content)
    private String bio;

    @Type.Property(name = "Remote", contentType = ContentType.Boolean)
    private Boolean remote;

    public Dummy() {}

    public Dummy(String id, String name, int age, Date hired, String bio, boolean remote) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.hired = hired;
        this.bio = bio;
        this.remote = remote;
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

    public Date getHired() {
        return hired;
    }

    public String getBio() {
        return bio;
    }

    public boolean getRemote() {
        return remote;
    }
}
