package com.manywho.services.dummy.repositories;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.services.dummy.dummy.Account;
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

    public List<Account> findAll(ListFilter filter) {
        String sql = "SELECT * FROM account";

        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .executeAndFetch(Account.class);
        }
    }

    public Account update(Account account) {
        String sql = "UPDATE account SET name = :name, company = :company WHERE id = :id";

        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", account.getId())
                    .addParameter("name", account.getName())
                    .addParameter("company", account.getCompany())
                    .executeUpdate();
        }

        return account;
    }

    public Account create(Account account) {
        String sql = "INSERT INTO account (id, name, company) VALUES (:id, :name, :company)";

        try (Connection connection = sql2o.open()) {
            int newAccountId = connection.createQuery(sql)
                    .addParameter("name", account.getName())
                    .addParameter("company", account.getCompany())
                    .executeUpdate()
                    .getResult();

            account.setId("" + newAccountId);
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
