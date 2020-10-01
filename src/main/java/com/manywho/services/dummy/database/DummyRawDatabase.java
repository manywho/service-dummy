package com.manywho.services.dummy.database;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.services.dummy.ApplicationConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DummyRawDatabase implements RawDatabase<ApplicationConfiguration> {
    @Override
    public MObject find(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, Command command, String id) {
        return null;
    }

    @Override
    public List<MObject> findAll(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType,
                                 Command command, ListFilter listFilter, List<MObject> objects) {

        List<MObject> mObjects = new ArrayList<>();
        boolean filterByObjects = filteringByObjects(objects, listFilter);

        if (objectDataType.getDeveloperName().equals("hn-binding")) {
            if (filterByObjects == false) {
                // it returns all the objects
                mObjects.add(createHackerNewsHeadlines());
            } else if (containsOneOfOurCurrentHeadlines(objects, "How (not) to sign a JSON object")) {
                // it returns only the objects with above headline
                mObjects.add(createHackerNewsHeadlines());
            }

            return mObjects;
        } else if (objectDataType.getDeveloperName().equals("bbc-binding")) {
            if (filterByObjects == false) {
                // it returns all the objects
                mObjects.add(createBBCHeadlines());
                mObjects.add(createBBCHeadlines2());

                return mObjects;
            } else {
                // it returns only the objects with above headline
                if (containsOneOfOurCurrentHeadlines(objects, "UK heatwave set to break records")) {
                    mObjects.add(createBBCHeadlines());
                }

                if (containsOneOfOurCurrentHeadlines(objects, "Andy Murray will not play at Flushing Meadows")) {
                    mObjects.add(createBBCHeadlines2());
                }

                return mObjects;
            }
        }

        throw new RuntimeException(String.format("The type %s is not supported", objectDataType.getDeveloperName()));
    }

    private boolean filteringByObjects(List<MObject> objects, ListFilter filter) {
        return objects != null && objects.size() > 0 && filter != null && filter.isFilterByProvidedObjects();
    }

    private boolean containsOneOfOurCurrentHeadlines(List<MObject> objects, String headline) {
        return objects
                .stream()
                .anyMatch(obj -> obj.getProperties().stream().anyMatch(p -> headline.equals(p.getContentValue())));
    }

    @Override
    public MObject create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, MObject mObject) {
        return null;
    }

    @Override
    public List<MObject> create(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<MObject> list) {
        return null;
    }

    @Override
    public void delete(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, MObject mObject) {

    }

    @Override
    public void delete(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<MObject> list) {

    }

    @Override
    public MObject update(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, MObject mObject) {
        return null;
    }

    @Override
    public List<MObject> update(ApplicationConfiguration applicationConfiguration, ObjectDataType objectDataType, List<MObject> list) {
        return null;
    }

    private MObject createHackerNewsHeadlines() {
        Property propertyTitleHackerNews = new Property("headlines", "How (not) to sign a JSON object");
        Property propertyPointsHackerNews = new Property("points", "3");
        Property propertyAuthorHackerNews = new Property("author", createAuthorWithDefaultBinding("Jose"));

        // linked article
        Property titleLinkedArticle = new Property("headlines", "Catj: A new way to display JSON files");
        Property pointLinkedArticle = new Property("points", "388");
        Property authorLinedArticle = new Property("author", createAuthorWithDefaultBinding("Juan"));

        MObject linkedArticle = new MObject("linked-articles", "hn-1-1", Arrays.asList(titleLinkedArticle, pointLinkedArticle, authorLinedArticle));
        linkedArticle.setTypeElementBindingDeveloperName("hn-binding");

        // add linked article as property
        Property propertyLinkedArticlesHackerNews = new Property("linked-articles", Arrays.asList(linkedArticle));

        return new MObject("hn-binding", "hn-1",
                Arrays.asList(propertyTitleHackerNews, propertyPointsHackerNews, propertyLinkedArticlesHackerNews, propertyAuthorHackerNews));
    }

    private MObject createBBCHeadlines() {
        Property propertyTitleBBC = new Property("headlines", "UK heatwave set to break records");
        Property author = new Property("author", createAuthorWithDefaultBinding("Dante"));
        MObject linkedArticle = new MObject("linked-articles", "bbc-1-1", Arrays.asList(
                new Property("headlines", "Hot weather: How to sleep in a heatwave"),
                new Property("author", createAuthorWithDefaultBinding("Rosario")))
        );

        linkedArticle.setTypeElementBindingDeveloperName("bbc-binding");
        Property propertyLinkedArticlesBBC = new Property("linked-articles", Arrays.asList(linkedArticle));


        return new MObject("bbc-binding", "bbc-1", Arrays.asList(propertyTitleBBC, author, propertyLinkedArticlesBBC));
    }

    private MObject createBBCHeadlines2() {
        Property propertyTitle = new Property("headlines", "Andy Murray will not play at Flushing Meadows");
        Property propertkyLinkedArticles = new Property("linked-articles", new ArrayList());
        Property propertyAuthorArticle = new Property("author", createAuthorFromSpreadsheet("John", 1));

        return new MObject("bbc-binding", "bbc-2", Arrays.asList(propertyTitle, propertkyLinkedArticles, propertyAuthorArticle));
    }

    private MObject createAuthorWithDefaultBinding(String name) {
        MObject author = new MObject("User", "id-" + name.toLowerCase(),
                Arrays.asList(new Property("name", name, ContentType.Object)));

        author.setTypeElementBindingDeveloperName("user-binding");

        return author;
    }

    private MObject createAuthorFromSpreadsheet(String name, Integer rowNumber) {
        MObject author = new MObject("User", "id-" + name.toLowerCase(),
                Arrays.asList(
                        new Property("name", name, ContentType.String),
                        new Property("row", rowNumber.toString())
                )
        );

        author.setTypeElementBindingDeveloperName("user-spreadsheet-binding");

        return author;
    }
}