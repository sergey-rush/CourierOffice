package ru.courier.office.core;


import java.util.Date;
import java.util.List;

public class Application {
    public String Id;
    public String MerchantId;
    public String PersonId;
    public String Amount;
    public String DeliveryAddress;
    public List<Status> StatusList;
    public Merchant Merchant;
    public Person Person;
    public Date Created;
}
