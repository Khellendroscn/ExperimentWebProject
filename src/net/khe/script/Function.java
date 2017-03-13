package net.khe.script;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hyc on 2017/3/10.
 */

/**
 * �������ʽ�ű��ڵĺ���
 */
public abstract class Function implements Serializable{
    private String name;
    private Type[] paramTypes;
    private Variable[] params;
    public Function(String name,Type[] paramTypes){
        this.name = name;
        this.paramTypes = paramTypes;
    }

    /**
     * @return ��������
     */
    public String getName(){return name;}

    /**
     * @return ������������
     */
    public Type[] getParamTypes(){return paramTypes;}

    /**
     * ���øú���
     * @param params ����ʵ���б�
     * @return ��������ֵ
     */
    public abstract Variable invoke(Variable[] params) throws Variable.TypeException;
}

/**
 * ��ͺ���
 */
class Sum extends Function{
    public Sum(){
        super("sum",new Type[]{Type.ARRAY});
    }
    @Override
    public Variable invoke(Variable[] params) throws Variable.TypeException {
        List<Variable> list = (List<Variable>) params[0].getValue();
        double result = 0;
        for(Variable v:list){
            v.assertType(Type.NUM);
            Double d = (Double)v.getValue();
            result+=d;
        }
        return new Num(result);
    }
}

/**
 * ��ƽ��ֵ����
 */
class Avg extends Function{
    public Avg(){
        super("sum",new Type[]{Type.ARRAY});
    }
    @Override
    public Variable invoke(Variable[] params) throws Variable.TypeException {
        Sum s = new Sum();
        Variable v = s.invoke(params);
        Double d = (Double)v.getValue();
        List<Variable> list = (List<Variable>)params[0].getValue();
        int len = list.size();
        return new Num(d/len);
    }
}

/**
 * ��������
 */
class Sqrt extends Function{
    public Sqrt(){
        super("sqrt",new Type[]{Type.NUM});
    }

    @Override
    public Variable invoke(Variable[] params) {
        Double d = (Double)params[0].getValue();
        return new Num(Math.sqrt(d));
    }
}

/**
 * ��˻�����
 */
class Pow extends Function{
    public Pow(){
        super("pow",new Type[]{Type.NUM, Type.NUM});
    }

    @Override
    public Variable invoke(Variable[] params) {
        Double d = (Double)params[0].getValue();
        int n = ((Double)params[1].getValue()).intValue();
        return new Num(Math.pow(d,n));
    }
}

/**
 * �����ֵ����
 */
class Abs extends Function{
    public Abs(){
        super("abs",new Type[]{Type.NUM});
    }

    @Override
    public Variable invoke(Variable[] params) {
        Double d = (Double)params[0].getValue();
        return new Num(Math.abs(d));
    }
}