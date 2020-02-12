package client.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleDataTree implements DataTree {

    @Override
    public void update(int dataTag, byte[] data) {
        Path p = Path.of("/home/farzin/Documents/SMServer/client/files/dummy"+dataTag);
        try {
            Files.write(p, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}