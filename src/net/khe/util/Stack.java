package net.khe.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by hyc on 2016/11/4.
 */
public class Stack<T> {
    private LinkedList<T> list;
    public Stack(){list = new LinkedList<T>();}
    public Stack(Collection<T> collection){list = new LinkedList<T>(collection);}
    public void push(T item){list.addFirst(item);}
    public T pop(){return list.pop();}
    public T top(){return list.getFirst();}
    public boolean isEmpty(){return list.isEmpty();}
    public int size(){return list.size();}
    @Override
    public String toString(){
        return list.toString();
    }
    public LinkedList<T> toLinkedList(){
        return list;
    }
    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>(Arrays.asList(1,2,3,4,5));
        Print.println("pop: "+stack.pop());
        Print.println("top: "+stack.top());
        stack.push(999);
        Print.println("top: "+stack.top());
    }
}
