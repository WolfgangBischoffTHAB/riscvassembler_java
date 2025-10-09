package com.mycompany.filehandling;

import java.io.FileInputStream;
import java.io.RandomAccessFile;

public class FileDescriptor {

    public String filepath;

    public int handle;

    public int seekPosition;

    public String filemode;

    //public FileInputStream fileInputStream;
    public RandomAccessFile fileInputStream;
    
}
