package ru.otus.test;

public class InvalidCompareException extends RuntimeException   {

    public InvalidCompareException(String msg){
        super(msg);
    }

    public InvalidCompareException(String msg, Exception cause){
        super(msg, cause);
    }


}
