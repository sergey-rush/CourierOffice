package ru.courier.office.core;

import java.util.List;

/**
 * Created by rash on 29.08.2017.
 */

public class Document {
    public int Id;
    public String DocumentId;
    public String ApplicationId;
    public String Title;
    public List<Scan> ScanList;
    public int Count = 0;
}
