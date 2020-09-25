package com.manywho.services.dummy.dummy;

import java.util.*;
import java.util.stream.Collectors;

import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.dummy.ApplicationConfiguration;

public class DummyDatabase implements Database<ApplicationConfiguration, Dummy> {

    private static HashMap<String, Dummy> DUMMIES;

    static {
        DUMMIES = DummyDatabaseDataBuilder.BuildDummies();
    }

    @Override
    public Dummy find(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, String id) {
        return DUMMIES.get(id);
    }

    @Override
    public List<Dummy> findAll(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, ListFilter filter, List<MObject> objects) {
        return DUMMIES
                .values()
                .stream()
                .filter(dummyInput ->
                {
                    if (filter == null || filter.getWhere() == null || filter.getWhere().size() == 0) {
                        return true;
                    } else if (filter.getWhere().size() > 1) {
                        System.out.println("Only one filter element is supported. " + filter.getWhere().size() + " has been provided.");
                    }
                    return filterByColumnName(filter, dummyInput);
                })
                .sorted((dummy1, dummy2) -> {
                            if (filter == null || filter.getOrderByDirectionType() == null) {
                                return 1;
                            }
                            String sortColumn = filter.getOrderByPropertyDeveloperName();
                            return (filter.getOrderByDirectionType().toString() == "ASC") ?
                                    propertyComparation(sortColumn, dummy1, dummy2) :
                                    propertyComparation(sortColumn, dummy2, dummy1);
                        }
                )
                .skip(filter.getOffset())
                .limit(filter.getLimit())
                .collect(Collectors.toList());
    }

    private boolean filterByColumnName(ListFilter filter, Dummy c) {
        if (filter != null && filter.getWhere() != null && filter.getWhere().size() != 0) {
            if (filter.getWhere().get(0).getColumnName().equals("Id")) {
                return c.getId().equals(filter.getWhere().get(0).getContentValue());
            } else if (filter.getWhere().get(0).getColumnName().equals("Name")) {
                return c.getName().equals(filter.getWhere().get(0).getContentValue());
            } else if (filter.getWhere().get(0).getColumnName().equals("Age")) {
                return String.valueOf(c.getAge()).equals(filter.getWhere().get(0).getContentValue());
            } else if (filter.getWhere().get(0).getColumnName().equals("Bio")) {
                return c.getBio().equals(filter.getWhere().get(0).getContentValue());
            } else if (filter.getWhere().get(0).getColumnName().equals("Remote")) {
                return String.valueOf(c.getRemote()).equals(filter.getWhere().get(0).getContentValue());
            } else if (filter.getWhere().get(0).getColumnName().equals("Hired")) {
                return String.valueOf(c.getHired()).equals(filter.getWhere().get(0).getContentValue());
            } else {
                System.out.println("Invalid filter columnName " + filter.getWhere().get(0).getColumnName());
                return false;
            }
        } else {
            return true;
        }
    }

    private int propertyComparation(String propertyName, Dummy a, Dummy b) {
        if (propertyName.equals("Id")) {
            return a.getId().compareTo(b.getId());
        } else if (propertyName.equals("Name")) {
            return a.getName().compareTo(b.getName());
        } else if (propertyName.equals("Age")) {
            return a.getAge() - b.getAge();
        } else if (propertyName.equals("Bio")) {
            return a.getBio().compareTo(b.getBio());
        } else if (propertyName.equals("Remote")) {
            return Boolean.compare(a.getRemote(), b.getRemote());
        } else if (propertyName.equals("Hired")) {
            return a.getHired().compareTo(b.getHired());
        } else {
            System.out.println("Invalid orderType name " + propertyName);
            return 0;
        }
    }

    @Override
    public Dummy create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Dummy dummy) {
        dummy.setId(UUID.randomUUID().toString());
        DUMMIES.put(dummy.getId(), dummy);

        return dummy;
    }

    @Override
    public List<Dummy> create(ApplicationConfiguration applicationConfiguration, ObjectDataType
            objectDataType, List<Dummy> list) {
        list.forEach(dummy -> {
            dummy.setId(UUID.randomUUID().toString());
            DUMMIES.put(dummy.getId(), dummy);
        });

        return new ArrayList<Dummy>(DUMMIES.values());
    }

    @Override
    public void delete(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Dummy
            dummy) {

    }

    @Override
    public void delete(ApplicationConfiguration applicationConfiguration, ObjectDataType
            objectDataType, List<Dummy> list) {

    }

    @Override
    public Dummy update(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Dummy
            dummy) {
        DUMMIES.put(dummy.getId(), dummy);

        return dummy;
    }

    @Override
    public List<Dummy> update(ApplicationConfiguration applicationConfiguration, ObjectDataType
            objectDataType, List<Dummy> list) {
        return null;
    }
}