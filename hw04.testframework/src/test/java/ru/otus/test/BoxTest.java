
package ru.otus.test;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import ru.otus.customannotations.After;
import ru.otus.customannotations.Before;
import ru.otus.customannotations.Test;
import ru.otus.homework.Apple;
import ru.otus.homework.Box;
import ru.otus.homework.Fruit;
import ru.otus.homework.Orange;

import java.util.Random;

@NoArgsConstructor
public class BoxTest {
    @Setter
    @Getter
    private long startTime;

    Box<Orange> orangeBox;
    Box<Apple> appleBox;
    Box<Fruit> fruitBox;
    Box<Orange> orangeBoxSecond;
    Box<Apple> appleBoxSecond;

    @Before
    void initAllBoxes() {
        orangeBox = new Box<>();
        appleBox = new Box<>();
        fruitBox = new Box<>();
        orangeBoxSecond = new Box<>();
        appleBoxSecond = new Box<>();

        appleBox.appendFruit(new Apple((new Random()).nextDouble() * 90 + 10));
        orangeBox.appendFruit(new Orange((new Random()).nextDouble() * 90 + 10));

        orangeBoxSecond.appendFruit(new Orange(10));
        orangeBoxSecond.appendFruit(new Orange(20));
        appleBoxSecond.appendFruit(new Apple(10));
        appleBoxSecond.appendFruit(new Apple(20.0001));

        startTime = System.currentTimeMillis();
    }

    @After
    void displayDurationInfo() {
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Duration = " + String.valueOf(duration));
    }

    @Test
    private void executeSimpleTest() {
        System.out.println("executeSimpleTest");

        if (appleBoxSecond.compareTo(orangeBoxSecond)!= 0)
            throw new InvalidCompareException("Некорректное вычисление в классе " + appleBoxSecond.getClass().getSimpleName());
    }


}

