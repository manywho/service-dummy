package com.manywho.services.dummy.dummy;

import java.util.HashMap;

public class DummyDatabaseDataBuilder {
    
    public static HashMap<String, Dummy> BuildDummies(){
        
        HashMap<String, Dummy> dummies = new HashMap<String, Dummy>();
        
        String firstNames[] = new String[] { 
            "Iain", "Xiao", "Jose", "Dale", "James", "Josh", "Mark", "Dominic", "Ben", "Thomas", "Dalibor", "Ian", "Richard", "Calvin", "Jonjo", "Philippa" };

        String surnames[] = new String[] { 
            "Earl", "Shi", "Collazzi", "Shoulders", "Bratt", "Catling", "Sellings", "Holmes", "Fullalove", "Allthalove", "Nenadic", "Haycox", "Timmis", "Robbins", "McKay", "Carnelley" };

        for (int offset = 0; offset < surnames.length; offset++){

            for (String firstName : firstNames){
                
                String id = Integer.toString(dummies.size()+1);
                int surnameIndex = offset < surnames.length ? offset : (offset - surnames.length);
                String surname = surnames[surnameIndex];
                int age = firstName.length() + surname.length() * 3;
                
                dummies.put(id, new Dummy(id, firstName + " " + surname, age));
            }
        }

        return dummies;
    }
}