package ru.courier.office.core;

/**
 * Current user of the application
 */
public class User {

    public User()
    {

    }

    public int Id;
    public String Name;
    public String Phone;
    public String Email;
    public boolean IsValid;

    public User(int id, String name, String phone, String email, boolean isValid) {
        Id = id;
        Name = name;
        Phone = phone;
        Email = email;
        IsValid = isValid;
    }
}
