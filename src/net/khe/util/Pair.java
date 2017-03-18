package net.khe.util;

/**
 * Created by hyc on 2016/10/11.
 * 二元元组
 */
public class Pair<Ta,Tb> {
    public final Ta first;
    public final Tb second;
    public Pair(Ta first,Tb second){
        this.first = first;
        this.second = second;
    }
    @Override
    public boolean equals(Object obj){
        Pair<Ta,Tb> pair = (Pair<Ta,Tb>) obj;
        return first.equals(pair.first)&&second.equals(pair.second);
    }
    public static void main(String[] args){
        Pair<Integer,String> pair = new Pair<>(0,"Hello World!");
        Print.println(pair.first+pair.second);
    }
}
