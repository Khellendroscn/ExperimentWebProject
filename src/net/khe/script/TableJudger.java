package net.khe.script;

import java.util.*;

/**
 * Created by hyc on 2017/3/10.
 */

/**
 * Judger实现类
 */
public class TableJudger implements Judger {
    List<Process> processes = new LinkedList<>();
    Stack<Variable> varStack = new Stack<>();
    Map<String,Variable> vars = new HashMap<>();
    List<Variable> resultSet = new ArrayList<>();
    Double submit = null;

    /**
     * 构造函数为包访问权限
     */
    TableJudger(){}
    Map<String,Function> funcMap = new HashMap<>();
    {
        //初始化函数列表
        funcMap.put("sum",new Sum());
        funcMap.put("avg",new Avg());
        funcMap.put("sqrt",new Sqrt());
        funcMap.put("pow",new Pow());
        funcMap.put("abs",new Abs());
        funcMap.put("results",new Results());
    }
    @Override
    public void judge(Map<String,Variable> row) throws Variable.TypeException {
        for(String k:row.keySet()){
            vars.put(k,row.get(k));
        }
        for(Process p:processes){
            p.go();
        }
    }

    @Override
    public Map<String, Variable> getVars() {
        return vars;
    }

    @Override
    public Double getSubmit() {
        return submit;
    }

    @Override
    public void clearResultSet() {
        resultSet.clear();
    }

    Double popNum()throws Variable.TypeException{
        Variable v = varStack.pop();
        v.assertType(Type.NUM);
        Double d = (Double)v.getValue();
        return d;
    }
    Double[] popNumPair()throws Variable.TypeException{
        Variable rvar = varStack.pop();
        Variable lvar = varStack.pop();
        rvar.assertType(Type.NUM);
        lvar.assertType(Type.NUM);
        Double[] ret = new Double[2];
        ret[0] = (Double) lvar.getValue();
        ret[1] = (Double) rvar.getValue();
        return ret;
    }
    List<Variable> popArray()throws Variable.TypeException{
        Variable v = varStack.pop();
        v.assertType(Type.ARRAY);
        List<Variable> array = (List<Variable>) v.getValue();
        return array;
    }
    void pushNum(Double num){
        varStack.push(new Num(num));
    }
    Variable[] popPair(){
        Variable[] ret = new Variable[2];
        ret[1] = varStack.pop();
        ret[0] = varStack.pop();
        return ret;
    }
    void addProcess(Process process){
        processes.add(process);
    }
    /*
    *解析运算符
    */
    //"+"
    void plus() throws Variable.TypeException {
        Variable[] vPair = popPair();
        if(vPair[0].getType().equals(Type.NUM)){
            //数值相加
            Double l = (Double)vPair[0].getValue();
            vPair[1].assertType(Type.NUM);
            Double r = (Double)vPair[1].getValue();
            pushNum(l+r);
        }else if(vPair[0].getType().equals(Type.ARRAY)){
            //数组相加
            List<Variable> list =
                    new ArrayList<>((List<Variable>)vPair[0].getValue());
            if(vPair[1].getType().equals(Type.ARRAY)){
                list.addAll((List<Variable>)vPair[1].getValue());
            }else{
                list.add(vPair[1]);
            }
            varStack.push(new Array(list));
        }else throw new Variable.TypeException("Expected NUM or ARRAY");
    }
    //"-"
    void minus()throws Variable.TypeException{
        Double[] dPair = popNumPair();
        pushNum(dPair[0]-dPair[1]);
    }
    //"*"
    void times()throws Variable.TypeException{
        Double[] dPair = popNumPair();
        pushNum(dPair[0]*dPair[1]);
    }
    //"/"
    void divide()throws Variable.TypeException{
        Double[] dPair = popNumPair();
        pushNum(dPair[0]/dPair[1]);
    }
    //函数调用
    void function(Function f) throws Variable.TypeException {
        int len = f.getParamTypes().length;
        Variable[] params = new Variable[len];
        for(int i = len-1;i>=0;--i){
            params[i] = varStack.pop();
        }
        Variable ret = f.invoke(params);
        varStack.push(ret);
    }
    //下标操作
    void index() throws Variable.TypeException {
        int i = popNum().intValue();
        List<Variable> arr = popArray();
        varStack.push(arr.get(i));
    }
    //创建数组
    void makeArray(int len)throws Variable.TypeException{
        Variable[] array = new Variable[len];
        for(int i=len-1;i>=0;--i){
            array[i] = varStack.pop();
        }
        varStack.push(new Array(Arrays.asList(array)));
    }
    //赋值
    void assign()throws Variable.TypeException{
        Variable val = varStack.pop();
        Variable var = varStack.pop();
        Variable v =
                val instanceof Variable.Named?val:new NamedVar(val,null);
        if(var instanceof Variable.Named && var.getType().equals(Type.NULL)){
            //变量初始化
            Variable.Named temp = (Variable.Named)var;
            vars.put(temp.getName(),v);
        }else  if(var.getType().equals(val.getType())){
            var.setValue(val.getValue());
        } else
            throw new Variable.TypeException(var.getType()+" variable can't be assigned with "+val.getType());
    }
    //求误差
    void equals_() throws Variable.TypeException {
        Double[] numPair = popNumPair();
        Double temp = Math.abs(numPair[0]-numPair[1])/numPair[1];
        resultSet.add(new Num(Math.abs(temp)));
    }
    //提交
    void setSubmit() throws Variable.TypeException {
        submit = popNum();
    }
    
    //获取结果集的脚本函数
    class Results extends Function{
        public Results(){
            super("results",new Type[]{});
        }
        @Override
        public Variable invoke(Variable[] params) throws Variable.TypeException {
            return new Array(resultSet);
        }
    }
}
