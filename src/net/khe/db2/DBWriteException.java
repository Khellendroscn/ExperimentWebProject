package net.khe.db2;

/**
 * Created by hyc on 2017/3/26.
 */
public class DBWriteException extends Exception {
    DBWriteException(String what){
        super(what);
    }
    DBWriteException(Exception e){
        super(e);
    }
}
