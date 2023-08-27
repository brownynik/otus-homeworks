package ru.otus.test;

import ru.otus.customannotations.After;
import ru.otus.customannotations.Before;
import ru.otus.customannotations.Test;
import ru.otus.test.storedStat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class executeTest {
    public static Method[] getAnnotationMethods(Method[] methods, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        List<Method> mt = new ArrayList<>();
        for(Method m : methods) {
            if (m.getAnnotation(annotationClass)!=null) mt.add(m);
        }
        return mt.toArray(new Method[0]);
    }

    private static void customInvokeMethod(Method m, Object obj, storedStat st) {
        boolean ac = m.canAccess(obj);
        try {
            if (m.trySetAccessible()) {
                m.invoke(obj);
                if (st!=null) st.incSuccess();
            }
        } catch (InvalidCompareException e) {
            System.out.println(e.getMessage());
            if (st!=null) st.incFail();
        } catch (IllegalAccessException e) {
            if (st!=null) st.incFail();
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            if (st!=null) st.incFail();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (st!=null) st.incFail();
            System.out.println(e.getCause().getMessage());
        } finally {
            m.setAccessible(ac);
        }
    }

    public static void runTest(Object obj, storedStat st) {

        Method[] methods = obj.getClass().getDeclaredMethods();
        Method[] testMethods = getAnnotationMethods(methods, Test.class);
        Method[] beforeMethods = getAnnotationMethods(methods, Before.class);
        Method[] afterMethods = getAnnotationMethods(methods, After.class);

        for(Method m : testMethods) {
            for(Method b : beforeMethods) customInvokeMethod(b, obj, null);
            customInvokeMethod(m, obj, st);
            for(Method a : afterMethods) customInvokeMethod(a, obj, null);
        }

    }

    public static void main(String[] args)  {
        storedStat st = new storedStat();
        BoxTest boxtest = new BoxTest();
        AppleTest appletest = new AppleTest();
        OrangeTest orangetest = new OrangeTest();
        runTest(boxtest, st);
        runTest(appletest, st);
        runTest(orangetest, st);
        st.publicate();
    }

}
