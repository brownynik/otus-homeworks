package ru.otus.test;

import lombok.Getter;
import lombok.Setter;
import ru.otus.customannotations.After;
import ru.otus.customannotations.Before;
import ru.otus.customannotations.Test;
import ru.otus.homework.Orange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OrangeTest {
    @Setter
    @Getter
    private long startTime;
    private long totalCount = 0;

    List<Orange> lstOrange;

    public OrangeTest() {
        lstOrange = new ArrayList<>();
    }
    @Before
    private void initRandomOrange() {
        double localWeight;
        lstOrange.clear();
        int cn = (int) (Math.random() * 99);
        cn++;
        for(var i=0;i<cn;i++) {
            localWeight = Math.random() * 199 + 1;
            totalCount++;
            lstOrange.add(new Orange(localWeight));
        }
        startTime = System.currentTimeMillis();
    }
    @After
    private void emptyList() {

        Iterator<Orange> orange = lstOrange.iterator();
        while (orange.hasNext()) {
            orange.next();
            orange.remove();
        };
        long duration = (System.currentTimeMillis() - startTime);
        System.out.println("(orangeTest) Duration = " + String.valueOf(duration));
    }
    @Test
    private void executeSimpleTest() {
        System.out.println("executeSimpleTest (orangeTest)");
        long checkedCount = lstOrange.size();
        if (checkedCount!= totalCount)
            throw new InvalidCompareException("Некорректное хранение элементов класса " + Orange.class.getSimpleName());
    }
}
