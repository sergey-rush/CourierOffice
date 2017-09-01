package ru.courier.office.core;

/**
 * Created by rash on 30.08.2017.
 */

public class Scan {
    public int Id;
    public String ApplicationId;
    public int DocumentId;
    public int Page;
    public ScanStatus ScanStatus;
    public byte[] SmallPhoto;
    public byte[] LargePhoto;
}
