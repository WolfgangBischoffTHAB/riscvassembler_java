package com.mycompany.filehandling;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class FileHandling {

    public static final int STDIN = 0;

    public static final int STDOUT = 1;

    public static final int STDERR = 2;

    public Map<Integer, FileDescriptor> fileMap = new HashMap<>();

    public Map<String, FileDescriptor> filenameMap = new HashMap<>();

    /**
    * #define stdin  (__acrt_iob_func(0))
    * #define stdout (__acrt_iob_func(1))
    * #define stderr (__acrt_iob_func(2))
    */
    public int nextId = 3; // three because 0, 1 and 2 are stdin, stdout, stderr

    public static final String PWD = "C:/Users/lapto/dev/c/zork/";

    private static final int EOF = -1;

    public int fopen(String filepath, String filemode) {
        if (filenameMap.containsKey(filepath)) {
            return 0;
        }
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.filepath = filepath;
        fileDescriptor.handle = nextId;
        fileDescriptor.filemode = filemode;
        fileMap.put(nextId, fileDescriptor);
        filenameMap.put(filepath, fileDescriptor);
        nextId++;

        return fileDescriptor.handle;
    }

    public int getc(int fileHandle) throws IOException {
        if (!fileMap.containsKey(fileHandle)) {
            throw new RuntimeException("no handle!");
        }
        FileDescriptor fileDescriptor = fileMap.get(fileHandle);
        if (fileDescriptor.fileInputStream == null) {

            String fileMode = fileDescriptor.filemode;
            if ("rb".equalsIgnoreCase(fileMode)) {
                fileMode = "r";
            }
            fileDescriptor.fileInputStream = new RandomAccessFile(PWD + fileDescriptor.filepath, fileMode);
        }
        fileDescriptor.seekPosition++;

        return fileDescriptor.fileInputStream.read();
    }

    public int ftell(int fileHandle) throws IOException {
        if (!fileMap.containsKey(fileHandle)) {
            throw new RuntimeException("no handle!");
        }
        FileDescriptor fileDescriptor = fileMap.get(fileHandle);

        return fileDescriptor.seekPosition;
    }

    /**
     * origin	Beschreibung
     * SEEK_SET	Der Start des Streams markiert den Ursprung.
     * SEEK_CUR	Aktuelle Position im Stream setzt den Ursprung.
     * SEEK_END	Das Ende des Streams wird als Ursprung gewählt.
     * @throws IOException 
     */
    public int fseek(int fileHandle, int newSeekPosition, int seekMode) {
        if (!fileMap.containsKey(fileHandle)) {
            throw new RuntimeException("no handle!");
        }
        FileDescriptor fileDescriptor = fileMap.get(fileHandle);

        // int offset = newSeekPosition - fileDescriptor.seekPosition;

        try {
            fileDescriptor.fileInputStream.seek(newSeekPosition);
        } catch (Exception e) {
            e.printStackTrace();
            return EOF;
        }
        return 0;
    }

    public void fclose(int fileHandle) throws IOException {
        if (!fileMap.containsKey(fileHandle)) {
            throw new RuntimeException("no handle!");
        }
        FileDescriptor fileDescriptor = fileMap.get(fileHandle);

        fileMap.remove(fileHandle);
        filenameMap.remove(fileDescriptor.filepath);
        
        fileDescriptor.fileInputStream.close();
    }
    
}
