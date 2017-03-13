package net.khe.script;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hyc on 2017/3/10.
 */

/**
 * 该类代表公式脚本内的函数
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
     * @return 函数名称
     */
    public String getName(){return name;}

    /**
     * @return 函数参数类型
     */
    public Type[] getParamTypes(){return paramTypes;}

    /**
     * 调用该函数
     * @param params 函数实参列表
     * @return 函数返回值
     */
    public abstract Variable invoke(Variable[] params) throws Variable.TypeException;
}

/**
 * 求和函数
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
 * 求平均值函数
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
 * 开方函数
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
 * 求乘积函数
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
 * 求绝对值函数
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