package ru.otus.test;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@NoArgsConstructor
@Getter
@Setter
public class storedStat {
    private int testCount = 0;
    private int testSuccess = 0;
    private int testFail = 0;

    void publicate() {
        System.out.println(String.format("Всего тестов: %d, Успешно: %d, Провалено: %d", testCount, testSuccess, testFail));
    }

    void incSuccess() {
        testCount++;
        testSuccess++;
    }

    void incFail() {
        testCount++;
        testFail++;
    }
}
