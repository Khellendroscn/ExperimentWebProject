package net.khe.script;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hyc on 2017/3/9.
 */

/**
 * Parser实现类
 */
public class ScriptParser implements Parser {
    private transient Iterable<String> texts;
    private transient int cursor = 0;
    private transient TokenGen tokenGen;
    private transient TokenPair token;
    private transient TokenPair nextToken;
    private TableJudger judger = new TableJudger();
    private boolean endFlag = false;
    private final static Map<String,String> patternMap = new HashMap<>();
    static {
        //初始化匹配模式
        patternMap.put("NUM","(?<NUM>\\d+(\\.\\d+)?)");
        patternMap.put("PLUS","(?<PLUS>\\+)");
        patternMap.put("MINUS","(?<MINUS>-)");
        patternMap.put("TIMES","(?<TIMES>\\*)");
        patternMap.put("DIVIDE","(?<DIVIDE>/)");
        patternMap.put("VAR","(?<VAR>\\$)");
        patternMap.put("NAME","(?<NAME>)\\w+");
        patternMap.put("WS","(?<WS>\\s+)");
        patternMap.put("LPARM","(?<LPARM>\\()");
        patternMap.put("RPARM","(?<RPARM>\\))");
        patternMap.put("LBRACKET","(?<LBRACKET>\\[)");
        patternMap.put("RBRACKET","(?<RBRACKET>\\])");
        patternMap.put("EQUALS","(?<EQUALS>=)");
        patternMap.put("COMMA","(?<COMMA>,)");
        patternMap.put("LBRACE","(?<LBRACE>\\{)");
        patternMap.put("RBRACE","(?<RBRACE>\\})");
    }
    private final static Pattern pattern = getPattern();
    private static Pattern getPattern(){
        StringBuilder sb = new StringBuilder();
        for(String k:patternMap.keySet()){
            sb.append(patternMap.get(k));
            sb.append('|');
        }
        sb.deleteCharAt(sb.length()-1);
        return Pattern.compile(sb.toString());
    }
    private static class TokenPair{
        public final String type;
        public final String value;
        public TokenPair(String type,String value){
            this.type = type;
            this.value = value;
        }
        @Override
        public String toString(){
            return "TokenPair("+type+", "+value+")";
        }
    }
    private static class TokenGen{
        private Matcher matcher;
        public TokenGen(String text){
            matcher = pattern.matcher(text);
        }
        TokenPair next(){
            if(matcher.find()){
                if(matcher.group("WS")!=null||
                        matcher.group("COMMA")!=null)
                    return next();
                for(String groupName:patternMap.keySet()){
                    if(matcher.group(groupName)!=null){
                        return new TokenPair(groupName,matcher.group());
                    }
                }
                return null;
            }
            else return null;
        }
    }
    private void advance(){
        token = nextToken;
        nextToken = tokenGen.next();
    }
    private boolean accept(String acType){
        if (nextToken!=null&&nextToken.type.equals(acType)){
            advance();
            return true;
        }else return false;
    }
    private void expect(String acType) throws Parser.ParseException {
        if(!accept(acType)) throw new Parser.ParseException("Expected "+acType,cursor);
    }

    /*
    *解析语法
     */
    private void equals_()
            throws Parser.ParseException{//比较/赋值运算（优先级: 1)
        if(nextToken.value.equals("submit")){//提交语句
            advance();
            expr();
            judger.addProcess(judger::setSubmit);
            endFlag = true;
        }else{
            expr();
            if(accept("EQUALS")){
                if(accept("EQUALS")){//==的情况
                    expr();
                    judger.addProcess(judger::equals_);
                }else{//=的情况
                    expr();
                    judger.addProcess(judger::assign);
                }
            }
        }
    }
    private void expr() throws Parser.ParseException{//加减法运算（优先级：2）
        term();//左操作数入栈
        while (accept("PLUS")|accept("MINUS")){
            String op = token.value;//查看运算符
            term();//有操作数入栈
            //添加运算步骤
            if(op.equals("+")){
                judger.addProcess(judger::plus);
            }else if(op.equals("-")){
                judger.addProcess(judger::minus);
            }
        }
    }
    private void term() throws Parser.ParseException{//乘除法运算（优先级：3）
        func();
        while (accept("TIMES")|accept("DIVIDE")){
            String op = token.value;
            func();
            if(op.equals("*")){
                judger.addProcess(judger::times);
            }else if(op.equals("/")){
                judger.addProcess(judger::divide);
            }
        }
    }
    private void func()throws Parser.ParseException{//函数调用（优先级：4）
        if(accept("NAME")){//如果发现函数名，解析该函数
            String funcName = token.value;
            if(!judger.funcMap.containsKey(funcName))//检查函数是否存在
                throw new Parser.ParseException("Function "+funcName+" is inexistent",cursor);
            Function f = judger.funcMap.get(token.value);//从函数列表中取得函数
            expect("LPARM");
            for(Type t:f.getParamTypes()){
                expr();
            }
            expect("RPARM");
            judger.addProcess(()->judger.function(f));
        }else{//否则下降至factor运算
            factor();
        }
    }
    private void factor() throws Parser.ParseException{//取值运算（优先级：5）
        if(accept("NUM")){//数值
            final Double n = new Double(token.value);
            judger.addProcess(
                    ()->{judger.pushNum(n);}
            );
        }else if(accept("VAR")){//变量
            expect("NAME");
            final String var = token.value;
            parseVar(var);
        }else if(accept("LPARM")){
            expr();
            expect("RPARM");
        }else if(accept("MINUS")){//负数
            func();
            judger.processes.add(
                    ()->{
                        Double d = judger.popNum();
                        judger.pushNum(-d);
                    }
            );
        }else if(accept("LBRACE")){
            int count = 0;
            while (!accept("RBRACE")){
                expr();
                ++count;
            }
            final int len = count;
            judger.addProcess(()->judger.makeArray(len));
        } else
            throw new Parser.ParseException("Expected number,variable or lparm",cursor);
    }
    private void parseVar(String var) throws ParseException {
        if(!judger.vars.containsKey(var))//如果变量为声明，声明该变量
            judger.vars.put(var,new NamedVar(new Null(),var));
        judger.addProcess(
                ()->{judger.varStack.push(judger.vars.get(var));}
        );
        parseIndex();
    }
    private void parseIndex() throws ParseException {//下标操作
        while (accept("LBRACKET")){
            if(nextToken.value.equals("]"))
                throw new ParseException("Excepted index expr in the bracket",cursor);
            expr();
            expect("RBRACKET");
            judger.addProcess(judger::index);
        }
    }


    private void parseLine(String expr)
            throws Parser.ParseException{
        tokenGen = new TokenGen(expr);
        advance();
        equals_();
    }
    @Override
    public void parse() throws Parser.ParseException{
        for(String expr:texts){
            if(endFlag) return;
            ++cursor;
            parseLine(expr);
        }
    }
    @Override
    public Judger getJudger() {
        return judger;
    }

    /**
     * 构造函数
     * @param text 需要解析的文本
     * @param tableMeta 数据表元数据
     * @see TableMeta
     */
    public ScriptParser(String text,TableMeta tableMeta) throws ParseException {
        if(!text.contains("submit"))
            throw new ParseException("Expected submit statement",0);
        this.texts = splitText(text);
        for(Field f:tableMeta.getFields()){
            judger.vars.put(f.name,null);
        }
    }
    private static Iterable<String> splitText(String text){
        String spliter = ";";
        return Arrays.asList(text.split(spliter));
    }
}
