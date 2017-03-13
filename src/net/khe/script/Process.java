package net.khe.script;

import java.io.Serializable;

/**
 * Created by hyc on 2017/3/9.
 */
@FunctionalInterface
public interface Process extends Serializable{
    void go() throws Variable.TypeException;
}
