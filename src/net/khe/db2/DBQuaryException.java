package net.khe.db2;

/**
 * Created by hyc on 2017/3/26.
 */
public class DBQuaryException extends Exception{
    DBQuaryException(String what){
        super(what);
    }
    DBQuaryException(Exception e){
        super(e);
    }
}
