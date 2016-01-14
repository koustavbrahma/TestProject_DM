package koustav.duelmasters.main.androidgamesframework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * Created by:koustav on 2/7/2015.
 * Abstract: Basic Interface for file I/O.
 */
public interface FileIO {
    public InputStream readAsset(String fileName) throws IOException;

    public InputStream readFile(String fileName) throws IOException;

    public OutputStream writeFile(String fileName) throws IOException;

    public String readTextFileFromResource(int resourceId);
}