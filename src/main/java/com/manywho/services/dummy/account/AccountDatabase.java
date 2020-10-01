package com.manywho.services.dummy.account;

import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.dummy.ApplicationConfiguration;
import com.manywho.services.dummy.account.repositories.AccountRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class AccountDatabase implements Database<ApplicationConfiguration, Account> {
    private final AccountRepository repositoryDatabase;

    @Inject
    public AccountDatabase(AccountRepository repositoryDatabase) {
        this.repositoryDatabase = repositoryDatabase;
    }


    @Override
    public Account find(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Command command, String id) {
        return repositoryDatabase.find(id);
    }

    @Override
    public List<Account> findAll(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Command command, ListFilter listFilter, List<MObject> list) {
        return repositoryDatabase.findAll(listFilter);
    }

    @Override
    public Account create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Account account) {
        return repositoryDatabase.create(account);
    }

    @Override
    public List<Account> create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<Account> list) {
        return list.stream()
                .map(repositoryDatabase::create)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Account account) {
        repositoryDatabase.delete(account.getId());

    }

    @Override
    public void delete(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<Account> list) {
        list.forEach(account -> repositoryDatabase.delete(account.getId()));
    }

    @Override
    public Account update(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Account account) {
        return repositoryDatabase.update(account);
    }

    @Override
    public List<Account> update(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<Account> list) {
        return list.stream()
                .map(repositoryDatabase::update)
                .collect(Collectors.toList());
    }
}
