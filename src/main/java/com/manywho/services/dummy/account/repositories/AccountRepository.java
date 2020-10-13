package com.manywho.services.dummy.account.repositories;

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

    public Account find(String id ) {
        String sql = "SELECT * FROM account WHERE id = :id";

        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Account.class);
        }
    }

    public List<Account> findAll(ListFilter filter) {
        String sql = "SELECT * FROM account";

        try (Connection connection = sql2o.open()) {
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

    public void delete(String id ) {
        String sql = "DELETE FROM account WHERE id = :id";

        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }
}
