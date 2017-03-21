package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Created by hyc on 2017/3/21.
 */
public interface SqlWriteOperator {
    void execute() throws
    NoSuchFieldException,
    InvocationTargetException,
    IllegalAccessException,
    KeyNotFoundException,
    NoSuchMethodException,
    ClassNotFoundException,
    SQLException,
    InstantiationException;
}
