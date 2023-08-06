package ru.otus.test;

import ru.otus.customannotations.After;
import ru.otus.customannotations.Before;
import ru.otus.customannotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class executeTest {
    public static Method[] getAnnotationMethods(Method[] methods, Class annotationClass) {
        List<Method> mt = new ArrayList<>();
        for(Method m : methods) {
            if (m.getAnnotation(annotationClass)!=null) mt.add(m);
        }
        return mt.toArray(new Method[0]);
    }

    private static void customInvokeMethod(Method m, Object obj) {
        boolean ac = m.canAccess(obj);
        try {
            if (m.trySetAccessible()) {
                m.invoke(obj);
            }
        } catch (InvalidCompareException e) {
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            System.out.println(e.getCause().getMessage());
        } finally {
            m.setAccessible(ac);
        }
    }

    public static void runTest(Object obj) {

        Method[] methods = obj.getClass().getDeclaredMethods();
        Method[] testMethods = getAnnotationMethods(methods, Test.class);
        Method[] beforeMethods = getAnnotationMethods(methods, Before.class);
        Method[] afterMethods = getAnnotationMethods(methods, After.class);

        for(Method m : testMethods) {
            for(Method b : beforeMethods) customInvokeMethod(b, obj);
            customInvokeMethod(m, obj);
            for(Method a : afterMethods) customInvokeMethod(a, obj);
        }

    }

    public static void main(String[] args)  {

        BoxTest boxtest = new BoxTest();
        runTest(boxtest);

    }

}
