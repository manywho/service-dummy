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

    public List<Account> findAll(ListFilter filter) throws ServiceProblemException {

        String sql = createSQL(filter);

        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql).executeAndFetch(Account.class);
        }
    }

    private String createSQL(ListFilter filter) {
        StringBuilder stringBuilder = new StringBuilder("SELECT * FROM account ");

        if (filter != null && filter.getWhere() != null && filter.getWhere().size() != 0) {

            if (filter.getWhere().size() > 1) {
                throw new ServiceProblemException(400, "Currently we only support filtering on one value, multiple values have been are provided.");
            }

            String value = filter.getWhere().get(0).getContentValue();
            String columnName = filter.getWhere().get(0).getColumnName();
            CriteriaType criteriaType = filter.getWhere().get(0).getCriteriaType();

            stringBuilder.append(" WHERE ");
            stringBuilder.append(columnName);

            if (columnName.equals("id")) {
                switch (criteriaType) {
                    case GreaterThan:
                        stringBuilder.append(" > ");
                        stringBuilder.append(value);
                        break;
                    case GreaterThanOrEqual:
                        stringBuilder.append(" >= ");
                        stringBuilder.append(value);
                        break;
                    case LessThan:
                        stringBuilder.append(" < ");
                        stringBuilder.append(value);
                        break;
                    case LessThanOrEqual:
                        stringBuilder.append(" <= ");
                        stringBuilder.append(value);
                        break;
                    case Equal:
                        stringBuilder.append(" = ");
                        stringBuilder.append(value);
                        break;
                    case NotEqual:
                        stringBuilder.append(" != ");
                        stringBuilder.append(value);
                        break;
                    case IsEmpty:
                        stringBuilder.append(" > ");
                        stringBuilder.append(value);
                        stringBuilder = (Boolean.valueOf(filter.getWhere().get(0).getContentValue())) ?
                                stringBuilder.append(" is null ") :
                                stringBuilder.append(" is not null");
                        break;
                    default:
                        throw new ServiceProblemException(400, "Integer CriteriaType only supports EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL and IS_EMPTY operations");
                }
            } else if (columnName.equals("company") || columnName.equals("name") || columnName.equals("stateid")) {
                switch (criteriaType) {
                    case StartsWith:
                        stringBuilder.append(" LIKE \'");
                        stringBuilder.append(value);
                        stringBuilder.append("%\'");
                        break;

                    case EndsWith:
                        stringBuilder.append(" LIKE \'%");
                        stringBuilder.append(value);
                        stringBuilder.append("\'");
                        break;

                    case IsEmpty:
                        stringBuilder = (Boolean.valueOf(filter.getWhere().get(0).getContentValue())) ?
                                stringBuilder.append(" is null ") :
                                stringBuilder.append(" is not null");
                        break;

                    case Contains:
                        stringBuilder.append(" LIKE \'%");
                        stringBuilder.append(value);
                        stringBuilder.append("%\'");
                        break;

                    case Equal:
                        stringBuilder.append(" = \'");
                        stringBuilder.append(value);
                        stringBuilder.append("\' ");

                        break;

                    case NotEqual:
                        stringBuilder.append(" != \'");
                        stringBuilder.append(value);
                        stringBuilder.append("\'");

                        break;

                    default:
                        throw new ServiceProblemException(400, "String CriteriaType only supports EQUAL, NOT_EQUAL, STARTS_WITH, ENDS_WITH, CONTAINS and IS_EMPTY operations.");
                }
            }
        }
        return stringBuilder.toString();
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
