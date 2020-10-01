package com.manywho.services.dummy.providers;

import com.google.inject.Provider;
import org.sql2o.Sql2o;

public class Sql2oProvider implements Provider<Sql2o> {
    @Override
    public Sql2o get() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new Sql2o("jdbc:sqlite::resource:database.sqlite3", null, null);
    }
}
