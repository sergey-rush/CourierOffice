package ru.courier.office.core;

import java.util.Date;

/**
 * Created by rash on 22.08.2017.
 */

public class Status {
    public int Id;
    public String ApplicationId;
    public String Code;
    public String Category;
    public String Info;
    public Date Created;

    public Status() {

    }

    public Status(int id, Date created) {
        Id = id;
        Created = created;
    }
}
