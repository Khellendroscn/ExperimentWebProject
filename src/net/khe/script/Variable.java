package net.khe.script;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hyc on 2017/3/10.
 */

/**
 * 表示脚本中的变量
 */
public abstract class Variable implements Serializable{
    private Type type;
    public Variable(Type type){
        this.type = type;
    }
    public Type getType(){return type;}
    public abstract Object getValue();
    public abstract void setValue(Object value);
    public void assertType(Type t) throws TypeException{
        if(!type.equals(t)) throw new TypeException("Expected "+t.toString());
    }

    public static class TypeException extends Exception{
        public TypeException(String what){super(what);}
    }
    public interface Named{//实现该接口表示可变变量
        String getName();
    }
}
class Num extends Variable{
    private Double value;
    public Num(Double value) {
        super(Type.NUM);
        setValue(value);
    }
    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (Double)value;
    }

    @Override
    public String toString(){
        return "Num("+value.toString()+")";
    }
}
class Array extends Variable{
    private List<Variable> value;
    public Array(List<Variable> value) {
        super(Type.ARRAY);
        setValue(value);
    }
    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (List<Variable>)value;
    }
    @Override
    public String toString(){
        return "Array("+value.toString()+")";
    }
}
class Null extends Variable{
    public Null(){
        super(Type.NULL);
    }
    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {}
}
class NamedVar extends Variable implements Variable.Named{
    private Variable v;
    private String name;
    public NamedVar(Variable v,String name) {
        super(v.getType());
        this.v = v;
        this.name = name;
    }

    @Override
    public Object getValue() {
        return v.getValue();
    }

    @Override
    public void setValue(Object value) {
        v.setValue(value);
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public String toString(){
        return "Named_"+v.toString();
    }
}