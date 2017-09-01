package ru.courier.office.core;


import java.util.Date;
import java.util.List;

public class Application {
    public int Id;
    public String ApplicationId;
    public String MerchantId;
    public String MerchantName;
    public String Inn;
    public String Email;
    public String Site;
    public String ManagerName;
    public String ManagerPhone;
    public String PersonId;
    public String PersonName;
    public Date BirthDate;
    public int Gender;
    public String Amount;
    public String DeliveryAddress;
    public List<Document> DocumentList;
    public List<Status> StatusList;
    public Date Created;
}
