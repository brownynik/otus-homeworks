package ru.otus.test;

import lombok.Getter;
import lombok.Setter;
import ru.otus.customannotations.After;
import ru.otus.customannotations.Before;
import ru.otus.customannotations.Test;
import ru.otus.homework.Apple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppleTest {
    @Setter
    @Getter
    private long startTime;
    private double totalWeight = 0;

    List<Apple> lstApple;

    public AppleTest() {
        lstApple = new ArrayList<>();
    }
    @Before
    private void initRandomApple() {
        double localWeight;
        lstApple.clear();
        int cn = (int) (Math.random() * 99);
        cn++;
        for(var i=0;i<cn;i++) {
            localWeight = Math.random() * 199 + 1;
            totalWeight+= localWeight;
            lstApple.add(new Apple(localWeight));
        }
        startTime = System.currentTimeMillis();
    }
    @After
    private void emptyList() {

        Iterator<Apple> apple = lstApple.iterator();
        while (apple.hasNext()) {
            apple.next();
            apple.remove();
        };
        long duration = (System.currentTimeMillis() - startTime);
        System.out.println("(AppleTest) Duration = " + String.valueOf(duration));
    }
    @Test
    private void executeSimpleTest() {
        System.out.println("executeSimpleTest (appleTest)");
        double checkedWeight = 0;
        for(Apple apple:lstApple) {
            checkedWeight+= (apple!= null)? apple.getWeight() : 0;
        }

        if (Math.abs (checkedWeight - totalWeight) > 0.000000001)
            throw new InvalidCompareException("Некорректное хранение веса в классе " + Apple.class.getSimpleName());
    }
}
