package fuzzy;

public class FuzzyException extends Exception{

    public int usefulInt;

    /**
     *  auto generated serialVersionUID
     */
    private static final long serialVersionUID = 1682743171589115688L;

    public FuzzyException(String msg){
        super(msg);
    }

    public FuzzyException(String msg, int info){
        super(msg);
        this.usefulInt = info;
    }

}