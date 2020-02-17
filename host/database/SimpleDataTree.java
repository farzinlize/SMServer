package host.database;

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
        paths[0] = Path.of(System.getProperty("user.dir") + "/host/files/dummy");
    }

    public static void main(String[] args) {
        System.out.println("Working Directory = " +
              System.getProperty("user.dir"));

    }
}