package otus.study.cashmachine.bank.service;


import net.bytebuddy.build.ToStringPlugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import org.mockito.Mockito;

import java.math.BigDecimal;
import otus.study.cashmachine.bank.data.Account;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    AccountDao accountDao;

    AccountServiceImpl accountServiceImpl;


    @BeforeEach
    void initDao(TestInfo info) {
        accountDao = Mockito.mock(AccountDao.class);
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher

        ArgumentMatcher<Account> accountMatcher = new ArgumentMatcher<Account>() {
            @Override
            public boolean matches(Account argument) {
                if (argument == null) return false;
                return argument.getAmount().equals(BigDecimal.valueOf(3000.00));
            }
        };

        BigDecimal amount = BigDecimal.valueOf(3000.00);
        Account testAccPrime = accountServiceImpl.createAccount(amount);
        // use of customer ArgumentMatcher
        verify(accountDao,times(1)).saveAccount(argThat(accountMatcher));


        // а царь-то ненастоящий! (с) Делаем возврат значения из имитатора
        when(accountDao.saveAccount(any(Account.class))).thenReturn(new Account(1,BigDecimal.valueOf(1000.00)));
        Account testAcc = accountServiceImpl.createAccount(BigDecimal.valueOf(2000));
        Assertions.assertEquals(testAcc.getAmount(), BigDecimal.valueOf(1000.00));

        // это же тоже "тест ради теста?" Убираю.
        // Assertions.assertNotNull(testAcc);
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        // а теперь каптор. Я сказал, каптор! (с) братья Вайнеры
        ArgumentCaptor<Account> accountObject = ArgumentCaptor.forClass(Account.class);
        // Это не Dead-code. Поскольку работа ученическая, я показываю два варианта решения:
        // Для лямбда-выражения и через метод Mockito
        when(accountDao.saveAccount(accountObject.capture())).thenAnswer(accountDao -> accountDao.getArguments()[0]);
        // when(accountDao.saveAccount(accountObject.capture())).then(returnsFirstArg());

        Account testAccSecond = accountServiceImpl.createAccount(BigDecimal.valueOf(3000));

        Assertions.assertNotNull(testAccSecond);
        Assertions.assertEquals(accountObject.getValue().getAmount(), testAccSecond.getAmount());
    }

    @Test
    void addSum() {
        ArgumentCaptor<Account> accountObject = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(accountObject.capture())).then(returnsFirstArg());
        Account testAccount = accountServiceImpl.createAccount(BigDecimal.valueOf(3000));
        when(accountDao.getAccount(testAccount.getId())).thenReturn(testAccount);

        Assertions.assertNotNull(testAccount,"Тестовый счёт не был создан");
        accountServiceImpl.putMoney(testAccount.getId(), BigDecimal.valueOf(2000));
        Assertions.assertEquals(testAccount.getAmount(), BigDecimal.valueOf(5000), "Сумма не была добавлена на счёт");
    }

    @Test
    void getSum() {
        when(accountDao.saveAccount(any(Account.class))).then(returnsFirstArg());
        Account testAccount = accountServiceImpl.createAccount(BigDecimal.valueOf(3000));
        when(accountDao.getAccount(testAccount.getId())).thenReturn(testAccount);

        Assertions.assertNotNull(testAccount,"Тестовый счёт не был создан");
        accountServiceImpl.getMoney(testAccount.getId(), BigDecimal.valueOf(2000));
        Assertions.assertEquals(testAccount.getAmount(), BigDecimal.valueOf(1000), "Сумма не была снята со счёта");
    }

    @Test
    void getAccount() {
        accountDao = new AccountDao();
        accountServiceImpl = new AccountServiceImpl(accountDao);
        AccountServiceImpl accountServiceSpy = spy(accountServiceImpl);
        Account testAccount = accountServiceSpy.createAccount(BigDecimal.valueOf(3000));
        Assertions.assertEquals(testAccount.hashCode(), accountServiceSpy.getAccount(testAccount.getId()).hashCode(), "Тестовый объект не равен исходному - транспорт нарушен");
    }

    @Test
    void checkBalance() {
        when(accountDao.saveAccount(any(Account.class))).then(returnsFirstArg());
        Account testAccount = accountServiceImpl.createAccount(BigDecimal.valueOf(3000));
        when(accountDao.getAccount(testAccount.getId())).thenReturn(testAccount);

        testAccount = accountServiceImpl.getAccount(testAccount.getId());

        //  проверяем, что счёт не уйдёт в минус. это допустимо?
        accountServiceImpl.putMoney(testAccount.getId(), testAccount.getAmount().negate().subtract(BigDecimal.valueOf(1000)));

        // возвращаем баланс в плюс
        accountServiceImpl.putMoney(testAccount.getId(), testAccount.getAmount().abs().add(BigDecimal.valueOf(1000)));
        Assertions.assertEquals(testAccount.getAmount(), accountServiceImpl.checkBalance(testAccount.getId()), "Сверка баланса и прямое обращение к счёту: сумма не сошлась.");

    }
}
