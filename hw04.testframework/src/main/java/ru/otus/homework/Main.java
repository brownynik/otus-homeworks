package ru.otus.homework;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;


public class Main {
    public static void main(String[] args) throws Exception {

        Box<Orange> orangeBox = new Box<>();
        Box<Apple> appleBox = new Box<>();
        Box<Fruit> fruitBox = new Box<>();
        Box<Orange> orangeBoxSecond = new Box<>();
        Box<Apple> appleBoxSecond = new Box<>();

        appleBox.appendFruit(new Apple((new Random()).nextDouble() * 90 + 10));
        orangeBox.appendFruit(new Orange((new Random()).nextDouble() * 90 + 10));

        orangeBoxSecond.appendFruit(new Orange(10));
        orangeBoxSecond.appendFruit(new Orange(20));
        appleBoxSecond.appendFruit(new Apple(10));
        appleBoxSecond.appendFruit(new Apple(20.0001));

        int iscompare = appleBoxSecond.compareTo(orangeBoxSecond);
        String msg = iscompare==0?"Равны":iscompare>0?"Меньше":"Больше";
        System.out.println(msg);

        msg = appleBoxSecond.compare(orangeBoxSecond)?"Равны (boolean)":"Не равны (boolean)";
        System.out.println(msg);

        orangeBoxSecond.pourOver(orangeBox);
        System.out.println("Пересыпаем из orangeBoxSecond в orangeBox");
        System.out.println("orangeBoxSecond: " + orangeBoxSecond.toString());
        System.out.println("orangeBox: " + orangeBox.toString());

    }
}
