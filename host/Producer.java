package host;

public class Producer extends Thread{
    
    private int id;
    private Buffer shared;
    private Schaduler boss;

    public Producer(int id, Schaduler schaduler, Buffer buffer){
        this.id = id;
        this.boss = schaduler;
        this.shared = buffer;
    }

    @Override
    public void run(){
        while(true){
            shared.waitSemaphoreProduce();
            String data = boss.getWork(id);
            if(data == null){return;}
            String encrypted = code(data);
            shared.putProduced(encrypted);
        }
    }

    private String code(String data){
        //TODO: encription java
        return data;
    }

}