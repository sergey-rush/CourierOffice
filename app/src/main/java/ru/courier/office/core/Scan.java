package ru.courier.office.core;

/**
 * Created by rash on 30.08.2017.
 */

public class Scan {
    public int Id;
    public String PhotoGuid;
    public String StreamGuid;
    public String ApplicationGuid;
    public String DocumentGuid;
    public int DocumentId;
    public int PageNum;
    public int ImageLength;
    public ScanStatus ScanStatus;
    public byte[] SmallPhoto;
    public byte[] LargePhoto;
    public int ByteNum;
}
