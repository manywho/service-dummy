package com.manywho.services.dummy.dummy;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                .filter(c ->
                {
                    if (filter == null || filter.getWhere() == null || filter.getWhere().size() == 0) {
                        return true;
                    }
                    String filterColumn = filter.getWhere().get(0).getColumnName();
                    return filterByColumnName(filter, c, filterColumn);
                })
                .sorted((a, b) -> {
                            if (filter == null || filter.getOrderByDirectionType() == null) {
                                return 1;
                            }
                            String sortColumn = filter.getOrderByPropertyDeveloperName();
                            return (filter.getOrderByDirectionType().toString() == "DESC") ?
                                    propertyComparation(sortColumn, a, b) :
                                    propertyComparation(sortColumn, b, a);
                        }
                )
                .skip(filter.getOffset())
                .limit(filter.getLimit())
                .collect(Collectors.toList());
    }

    private boolean filterByColumnName(ListFilter filter, Dummy c, String filterColumn) {
        if (filterColumn.equals("Id")) {
            return c.getId().equals(filter.getWhere().get(0).getContentValue());
        } else if (filterColumn.equals("Name")) {
            return c.getName().equals(filter.getWhere().get(0).getContentValue());
        } else if (filterColumn.equals("Age")) {
            return String.valueOf(c.getAge()).equals(filter.getWhere().get(0).getContentValue());
        } else if (filterColumn.equals("Bio")) {
            return c.getBio().equals(filter.getWhere().get(0).getContentValue());
        } else if (filterColumn.equals("Remote")) {
            return String.valueOf(c.getRemote()).equals(filter.getWhere().get(0).getContentValue());
        } else if (filterColumn.equals("Hired")) {
            return String.valueOf(c.getHired()).equals(filter.getWhere().get(0).getContentValue());
        } else {
            System.out.println("Invalid filter columnName " + filterColumn);
            return false;
        }
    }

    private int propertyComparation(String propertyName, Dummy a, Dummy b) {
        if (propertyName.equals("Id")) {
            return b.getId().compareTo(a.getId());
        } else if (propertyName.equals("Name")) {
            return b.getName().compareTo(a.getName());
        } else if (propertyName.equals("Age")) {
            return b.getAge() - a.getAge();
        } else if (propertyName.equals("Bio")) {
            return b.getBio().compareTo(a.getBio());
        } else if (propertyName.equals("Remote")) {
            return Boolean.compare(b.getRemote(), a.getRemote());
        } else if (propertyName.equals("Hired")) {
            return b.getHired().compareTo(a.getHired());
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