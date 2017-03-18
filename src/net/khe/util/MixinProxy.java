package net.khe.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hyc on 2016/10/11.
 * 混型代理
 */
public class MixinProxy implements InvocationHandler {
    //通过方法名映射对象
    Map<String,Object> delegatesByMothod = new HashMap<>();
    public MixinProxy(Pair<Object,Class<?>>...pairs){
        for(Pair<Object,Class<?>> pair:pairs){
            //初始化map
            for(Method method:pair.second.getMethods()){
                //将该对象的所有方法映射到该对象
                String methodName = method.getName();
                if(!delegatesByMothod.containsKey(methodName)){
                    delegatesByMothod.put(methodName,pair.first);
                }
            }
        }
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();//获取方法名
        Object delegate = delegatesByMothod.get(methodName);//获取对象
        return method.invoke(delegate,args);//调用代理方法
    }
    @SuppressWarnings("unchecked")
    public static Object newInstance(Pair...pairs){
        //获取混型实例
        Class[] interfaces = new Class[pairs.length];
        //获取接口数组
        for(int i = 0;i < pairs.length;++i){
            interfaces[i] = (Class)pairs[i].second;
        }
        //获取一个类加载器
        ClassLoader cl = pairs[0].first.getClass().getClassLoader();
        //返回代理后的对象
        return Proxy.newProxyInstance(cl,interfaces,new MixinProxy(pairs));
    }
}
