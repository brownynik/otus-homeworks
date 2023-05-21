package ru.otus.homework;
import ru.otus.homeworkmodule.HelloOtus;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Main Class");

        // runtime error: error: package ru.otus.homeworkmodule does not exist
        // why?
        // may be, determine the dependence through GRADLE.build?
        HelloOtus ho = new HelloOtus();
        ho.runDemo();

    }
}
