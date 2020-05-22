package com.manywho.services.dummy.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.dummy.ApplicationConfiguration;

public class DummyDatabase implements Database<ApplicationConfiguration, Dummy> {
    
    private static final HashMap<String, Dummy> DUMMIES = new HashMap<String, Dummy>();
    
    static
    {
        DUMMIES.put("123", new Dummy("123", "Jonjo", 23));
        DUMMIES.put("345", new Dummy("345", "Dom", 39));
    }

    @Override
    public Dummy find(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, String id) {
        
        return DUMMIES.get(id);
    }

    @Override
    public List<Dummy> findAll(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, ListFilter filter, List<MObject> objects) {
        return new ArrayList<Dummy>(DUMMIES.values());
    }

    @Override
    public Dummy create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Dummy dummy) {
        DUMMIES.put(dummy.getId(), dummy);
        return dummy;
    }

    @Override
    public List<Dummy> create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<Dummy> list) {
        
        list.forEach(d -> DUMMIES.put(d.getId(), d));
        return new ArrayList<Dummy>(DUMMIES.values());
    }

    @Override
    public void delete(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Dummy dummy) {

    }

    @Override
    public void delete(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<Dummy> list) {

    }

    @Override
    public Dummy update(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Dummy dummy) {
        DUMMIES.put(dummy.getId(), dummy);
        return dummy;
    }

    @Override
    public List<Dummy> update(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<Dummy> list) {
        return null;
    }
}
