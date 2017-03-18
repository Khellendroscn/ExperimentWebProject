package net.khe.db;

import java.util.Map;
import java.util.Set;

/**
 * Created by hyc on 2017/3/16.
 */
public interface DataFactory<T extends Data> {
    T create(Map<String,Object> props);
    Map<String,String> getFields();
}
