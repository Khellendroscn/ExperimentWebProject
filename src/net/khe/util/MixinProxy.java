package net.khe.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hyc on 2016/10/11.
 * ���ʹ���
 */
public class MixinProxy implements InvocationHandler {
    //ͨ��������ӳ�����
    Map<String,Object> delegatesByMothod = new HashMap<>();
    public MixinProxy(Pair<Object,Class<?>>...pairs){
        for(Pair<Object,Class<?>> pair:pairs){
            //��ʼ��map
            for(Method method:pair.second.getMethods()){
                //���ö�������з���ӳ�䵽�ö���
                String methodName = method.getName();
                if(!delegatesByMothod.containsKey(methodName)){
                    delegatesByMothod.put(methodName,pair.first);
                }
            }
        }
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();//��ȡ������
        Object delegate = delegatesByMothod.get(methodName);//��ȡ����
        return method.invoke(delegate,args);//���ô�����
    }
    @SuppressWarnings("unchecked")
    public static Object newInstance(Pair...pairs){
        //��ȡ����ʵ��
        Class[] interfaces = new Class[pairs.length];
        //��ȡ�ӿ�����
        for(int i = 0;i < pairs.length;++i){
            interfaces[i] = (Class)pairs[i].second;
        }
        //��ȡһ���������
        ClassLoader cl = pairs[0].first.getClass().getClassLoader();
        //���ش����Ķ���
        return Proxy.newProxyInstance(cl,interfaces,new MixinProxy(pairs));
    }
}
