package xyz.hanks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BBKnife {
    public static void bind(Object view){
        try {
            String cla = view.getClass().getName()+BindViewProcessor.SUFFIX;
            Class clazz = Class.forName(cla);
            Object instance = clazz.newInstance();
            Method bind = clazz.getMethod("bind",Object.class);
            bind.invoke(instance,view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
