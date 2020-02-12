package host;

import host.utils.Partition;

public class Producer extends Thread {

    private int id;
    private Buffer shared;
    private Schaduler boss;
    private Coder coder;

    public Producer(int id, Schaduler schaduler, Buffer buffer) {
        this.id = id;
        this.boss = schaduler;
        this.shared = buffer;

        //TODO: inital coder
    }

    @Override
    public void run() {
        while (true) {
            // shared.waitSemaphoreProduce();
            Partition partition = boss.getWork(id);
            if (partition == null) {return;}
            byte[] encrypted = code(partition.data);
            try {
                shared.putProduced(new Partition(partition.index, encrypted));
            } catch (Exception e) {
                // TODO: Report error
            }
        }
    }

    private byte[] code(byte[] data){
        return this.coder.code(data);
    }

}