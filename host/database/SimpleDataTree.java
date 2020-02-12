package host.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleDataTree implements DataTree {

    public static int DATATREE_SIZE = 20;

    private Path[] paths;

    public SimpleDataTree() {
        this.paths = new Path[DATATREE_SIZE];

        //TODO: delete this
        addDummy();
    }

    @Override
    public Path getFilePath(int id) {
        return paths[id];
    }

    /**
     * Add Dummy path to database for test phase
     */
    public void addDummy() {
        paths[0] = Path.of("/home/farzin/Documents/SMServer/host/files/dummy");
    }

    public static void main(String[] args) {
        Path p0 = Path.of("/home/farzin/Documents/SMServer/host/files/dummy");
        //File folder = new File("/home/farzin/Documents/SMServer/host/files/dummy");
        try {
            byte[] data = Files.readAllBytes(p0);
            System.out.println(data[5]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}