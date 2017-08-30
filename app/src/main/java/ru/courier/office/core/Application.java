package ru.courier.office.core;


import java.util.Date;
import java.util.List;

public class Application {

    public int Id;
    public String ApplicationId;
    public int MerchantId;
    public int PersonId;
    public String PersonName;
    public String MerchantName;
    public String Amount;
    public String DeliveryAddress;
    public List<Status> StatusList;
    public Merchant Merchant;
    public Person Person;
    public Date Created;
}
