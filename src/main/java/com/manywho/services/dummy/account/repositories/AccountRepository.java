package com.manywho.services.dummy.account.repositories;

import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.services.dummy.account.Account;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import java.util.List;

public class AccountRepository {
    private final Sql2o sql2o;

    @Inject
    public AccountRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public Account find(String id) {
        String sql = "SELECT * FROM account WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Account.class);
        }
    }

    public Account findAll(String filterBy, String value) {
        String sql = "SELECT * FROM account WHERE " + filterBy + "= :" + value;
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter(filterBy, filterBy)
                    .executeAndFetchFirst(Account.class);
        }
    }

    public List<Account> findAll(ListFilter filter) throws ServiceProblemException {

        String sql = "";
        if (filter != null && filter.getWhere() != null && filter.getWhere().size() != 0) {
            String columnname = filter.getWhere().get(0).getColumnName();
            String value = filter.getWhere().get(0).getContentValue();
            CriteriaType criteriaType = filter.getWhere().get(0).getCriteriaType();

            if (columnname.equals("id")) {

                if (criteriaType == CriteriaType.GreaterThan) {
                    sql = "SELECT * FROM account WHERE id > " + value;
                } else if (criteriaType == CriteriaType.GreaterThanOrEqual) {
                    sql = "SELECT * FROM account WHERE id >= " + value;
                } else if (criteriaType == CriteriaType.LessThan) {
                    sql = "SELECT * FROM account WHERE id < " + value;
                } else if (criteriaType == CriteriaType.LessThanOrEqual) {
                    sql = "SELECT * FROM account WHERE id <= " + value;
                } else if (criteriaType == CriteriaType.Equal) {
                    sql = "SELECT * FROM account WHERE id == " + value;
                } else if (criteriaType == CriteriaType.NotEqual) {
                    sql = "SELECT * FROM account WHERE id != " + value;
                } else if (criteriaType == CriteriaType.IsEmpty) {
                    sql = (Boolean.valueOf(filter.getWhere().get(0).getContentValue())) ?
                            "SELECT * FROM account WHERE id is null" :
                            "SELECT * FROM account WHERE id is not null";
                } else {
                    throw new ServiceProblemException(400, "Integer type \"id\" only supports EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL and IS_EMPTY operations");
                }
            } else if (columnname.equals("company") || columnname.equals("name") || columnname.equals("stateid")) {

                if (criteriaType == CriteriaType.StartsWith) {
                    sql = "SELECT * FROM account WHERE " + columnname + " LIKE \'" + value + "%\'";
                } else if (criteriaType == CriteriaType.EndsWith) {
                    sql = "SELECT * FROM account WHERE " + columnname + " LIKE \'%" + value + "\'";
                } else if (criteriaType == CriteriaType.IsEmpty) {
                    sql = (Boolean.valueOf(filter.getWhere().get(0).getContentValue())) ?
                            "SELECT * FROM account WHERE " + columnname + " is null" :
                            "SELECT * FROM account WHERE " + columnname + " is not null";
                } else if (criteriaType == CriteriaType.Contains) {
                    sql = "SELECT * FROM account WHERE " + columnname + " LIKE \'%" + value + "%\'";
                } else if (criteriaType == CriteriaType.Equal) {
                    sql = "SELECT * FROM account WHERE " + columnname + " == \'" + value + "\'";
                } else if (criteriaType == CriteriaType.NotEqual) {
                    sql = "SELECT * FROM account WHERE " + columnname + " != \'" + value + "\'";
                } else {
                    throw new ServiceProblemException(400, "String CriteriaType only supports EQUAL, NOT_EQUAL, STARTS_WITH, ENDS_WITH, CONTAINS and IS_EMPTY operations.");
                }
            }
        } else {
            sql = "SELECT * FROM account ";
        }

        try (
                Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .executeAndFetch(Account.class);
        }
    }

    public Account update(Account account) {
        String sql = "UPDATE account SET name = :name, company = :company, stateid = :stateid WHERE id = :id";

        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", account.getId())
                    .addParameter("name", account.getName())
                    .addParameter("company", account.getCompany())
                    .addParameter("stateid", account.getStateid())
                    .executeUpdate();
        }
        return account;
    }

    public Account create(Account account) {
        String sql = "INSERT INTO account (id, name, company, stateid) VALUES (:id, :name, :company, :stateid)";

        try (Connection connection = sql2o.open()) {
            int newAccountId = connection.createQuery(sql)
                    .addParameter("name", account.getName())
                    .addParameter("company", account.getCompany())
                    .addParameter("stateid", account.getStateid())
                    .executeUpdate()
                    .getResult();

            account.setId(String.valueOf(newAccountId));
        }

        return account;
    }

    public void delete(String id) {
        String sql = "DELETE FROM account WHERE id = :id";

        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }
}
