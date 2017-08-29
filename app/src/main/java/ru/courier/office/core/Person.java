package ru.courier.office.core;

import java.util.Date;

/**
 * An applicant
 */
public class Person {

    public int Id;
    public String PersonId;
    public String ApplicationId;
    public String FirstName;
    public String MiddleName;
    public String LastName;
    public Date BirthDate;
    public String Gender;

    public String getName()
    {
        String fullName = String.format("%s %s", FirstName, LastName);
        return fullName;
    }
}
