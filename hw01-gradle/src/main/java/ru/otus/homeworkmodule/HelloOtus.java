package ru.otus.homeworkmodule;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import com.google.common.base.Joiner;

public class HelloOtus {
    public void runDemo() {
        List<String> buf = new ArrayList<>();
        StringBuffer sbResult = new StringBuffer();

        buf.add("что");
        buf.add("я");
        buf.add("знаю");
        buf.add("про");
        buf.add("Java");
        buf.add(null);
        buf.add("?");
        buf.add("Правильно!");
        buf.add("Ничего!");

        for (String str : buf) {
            if (str != null) {
                sbResult.append(str);
                sbResult.append(", ");
            }
        }
        sbResult.replace(sbResult.length()-2,sbResult.length(),"");

        System.out.println("Without Guava Variant: " + sbResult);
        System.out.println("With Guava Variant: " + Joiner.on(", ").skipNulls().join(buf));

    }
}


