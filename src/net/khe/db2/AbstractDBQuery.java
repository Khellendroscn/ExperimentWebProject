package net.khe.db2;

import java.sql.ResultSet;
import java.util.stream.Stream;

/**
 * Created by hyc on 2017/3/26.
 */
public abstract class AbstractDBQuery<T> implements DBQuery<T> {
    protected ResultSet rs;
    AbstractDBQuery(ResultSet rs){
        this.rs = rs;
    }
    @Override
    public Stream<T> stream() {
        try{
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            return Stream.generate(()->next()).limit(size);
        }catch (Exception e){
            throw new RuntimeException(new DBQuaryException(e));
        }
    }
}
