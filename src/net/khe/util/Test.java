package net.khe.util;

/**
 * Created by hyc on 2016/10/20.
 */
public abstract class Test<C> {
    String name;
    public Test(String name){
        this.name = name;
    }
    public abstract int test(C container,TestParam tp);
}
