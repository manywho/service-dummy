package com.manywho.services.dummy.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.dummy.ApplicationConfiguration;

public class DummyDatabase implements Database<ApplicationConfiguration, Dummy> {
    
    private static HashMap<String, Dummy> DUMMIES;
    
    static
    {
        DUMMIES = DummyDatabaseDataBuilder.BuildDummies();
    }

    @Override
    public Dummy find(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, String id) {   
        return DUMMIES.get(id);
    }

    @Override
    public List<Dummy> findAll(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, ListFilter filter, List<MObject> objects) {
        
        List<Dummy> pagedDummies = DUMMIES
            .values()
            .stream()
            .skip(filter.getOffset())
            .limit(filter.getLimit())
            .collect(Collectors.toList());
            
        return pagedDummies;
    }

    @Override
    public Dummy create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Dummy dummy) {
        dummy.setId(UUID.randomUUID().toString());
        DUMMIES.put(dummy.getId(), dummy);
        
        return dummy;
    }

    @Override
    public List<Dummy> create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<Dummy> list) {
        list.forEach(dummy -> {
            dummy.setId(UUID.randomUUID().toString());
            DUMMIES.put(dummy.getId(), dummy);});
        
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