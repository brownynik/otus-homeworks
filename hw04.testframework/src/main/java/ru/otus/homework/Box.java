package ru.otus.homework;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@Getter
@Setter
public class Box<E extends Fruit> implements Comparable<Box<? extends Fruit>>  {
    private ArrayList<E> fruits;
    public double weight() {
        double w = 0;
        // Считаю нужным через суммирование, так как:
        // Будет правильно работать для разных фруктов, если ящик станет гетерогенным с т.з. фруктового наполнения
        // Будет без лишних проверок правильно работать для граничных условий
        // Будет правильно работать, если не все фрукты будут одинаково весить
        for (E itm:fruits) {
            w+= ((Fruit)itm).getWeight();
        }
        return w;
    };

    public Box() {
        fruits = new ArrayList<>();
        System.out.println("Box Created");
    }
    public void appendFruit(E oneFruit) {
        fruits.add(oneFruit);
    }

    public E popFruit() throws Exception {
        if (fruits==null) throw new Exception("Box container is null");
        if (fruits.isEmpty()) throw new Exception("Box is empty");
        E fruit = fruits.get(fruits.size() - 1);
        fruits.remove(fruits.size() - 1);
        return fruit;
    }

    public void pourOver(Box<E> fruitBox) throws Exception {
        if (fruits==null) throw new Exception("Box container is null");
        if (fruits.isEmpty()) throw new Exception("Box is empty");

        while (!fruits.isEmpty()) {
            fruitBox.appendFruit(this.popFruit());
        }
    }

    @Override
    public int compareTo(Box<? extends Fruit> o) {
        if (o == null) {
            throw new IllegalArgumentException("Object is null");
        } else if (o.hashCode()!= this.hashCode()) {
            if ((o.weight() - this.weight()) > 0.001) {
                return 1;
            } else
            if ((this.weight() - o.weight()) > 0.001) {
                return -1;
            };
        };
        return 0;
    }

    public boolean compare(Box<? extends Fruit> o) {
        return compareTo(o)==0;
    }

    @Override
    public String toString() {

        StringBuilder msg = new StringBuilder("Box: {fruits = [");
        if (fruits!=null) {
            for (E itm : fruits) {
                msg.append(((Fruit) itm).getClass().getName() + ": weight = " + ((Fruit) itm).getWeight() + ";");
            };
        };
        msg.append("]}");
        return msg.toString();
    }

    // public boolean Compare()

}
