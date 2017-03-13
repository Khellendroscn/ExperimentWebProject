package net.khe.script;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hyc on 2017/3/9.
 */

/**
 * Parserʵ����
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
        //��ʼ��ƥ��ģʽ
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
    *�����﷨
     */
    private void equals_()
            throws Parser.ParseException{//�Ƚ�/��ֵ���㣨���ȼ�: 1)
        if(nextToken.value.equals("submit")){//�ύ���
            advance();
            expr();
            judger.addProcess(judger::setSubmit);
            endFlag = true;
        }else{
            expr();
            if(accept("EQUALS")){
                if(accept("EQUALS")){//==�����
                    expr();
                    judger.addProcess(judger::equals_);
                }else{//=�����
                    expr();
                    judger.addProcess(judger::assign);
                }
            }
        }
    }
    private void expr() throws Parser.ParseException{//�Ӽ������㣨���ȼ���2��
        term();//���������ջ
        while (accept("PLUS")|accept("MINUS")){
            String op = token.value;//�鿴�����
            term();//�в�������ջ
            //������㲽��
            if(op.equals("+")){
                judger.addProcess(judger::plus);
            }else if(op.equals("-")){
                judger.addProcess(judger::minus);
            }
        }
    }
    private void term() throws Parser.ParseException{//�˳������㣨���ȼ���3��
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
    private void func()throws Parser.ParseException{//�������ã����ȼ���4��
        if(accept("NAME")){//������ֺ������������ú���
            String funcName = token.value;
            if(!judger.funcMap.containsKey(funcName))//��麯���Ƿ����
                throw new Parser.ParseException("Function "+funcName+" is inexistent",cursor);
            Function f = judger.funcMap.get(token.value);//�Ӻ����б���ȡ�ú���
            expect("LPARM");
            for(Type t:f.getParamTypes()){
                expr();
            }
            expect("RPARM");
            judger.addProcess(()->judger.function(f));
        }else{//�����½���factor����
            factor();
        }
    }
    private void factor() throws Parser.ParseException{//ȡֵ���㣨���ȼ���5��
        if(accept("NUM")){//��ֵ
            final Double n = new Double(token.value);
            judger.addProcess(
                    ()->{judger.pushNum(n);}
            );
        }else if(accept("VAR")){//����
            expect("NAME");
            final String var = token.value;
            parseVar(var);
        }else if(accept("LPARM")){
            expr();
            expect("RPARM");
        }else if(accept("MINUS")){//����
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
        if(!judger.vars.containsKey(var))//�������Ϊ�����������ñ���
            judger.vars.put(var,new NamedVar(new Null(),var));
        judger.addProcess(
                ()->{judger.varStack.push(judger.vars.get(var));}
        );
        parseIndex();
    }
    private void parseIndex() throws ParseException {//�±����
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
     * ���캯��
     * @param text ��Ҫ�������ı�
     * @param tableMeta ���ݱ�Ԫ����
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
