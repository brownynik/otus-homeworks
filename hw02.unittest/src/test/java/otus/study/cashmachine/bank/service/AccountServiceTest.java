package otus.study.cashmachine.bank.service;


import net.bytebuddy.build.ToStringPlugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import org.mockito.ArgumentCaptor;
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
        if (info.getDisplayName().equals("getAccount()")) return;
        accountDao = Mockito.mock(AccountDao.class);
        //accountDao = new AccountDao();
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher

        // проверка ради проверки. Просто что сервис создан.
        Assertions.assertNotNull(accountServiceImpl);

        // а царь-то ненастоящий! (с) Делаем возврат значения из имитатора
        when(accountDao.saveAccount(any(Account.class))).thenReturn(new Account(1,BigDecimal.valueOf(1000.00)));

        Account testAcc = accountServiceImpl.createAccount(BigDecimal.valueOf(2000));
        Assertions.assertNotNull(testAcc);
        Assertions.assertEquals(testAcc.getAmount(), BigDecimal.valueOf(1000.00));

    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        // а теперь каптор. Я сказал, каптор! (с) братья Вайнеры
        ArgumentCaptor<Account> accountObject = ArgumentCaptor.forClass(Account.class);
        //when(accountDao.saveAccount(accountObject.capture())).thenAnswer(accountDao -> accountDao.getArguments()[0]);
        when(accountDao.saveAccount(accountObject.capture())).then(returnsFirstArg());

        //Mockito.verify(accountDao, any()).saveAccount(accountObject.capture());
        Account testAccSecond = accountServiceImpl.createAccount(BigDecimal.valueOf(3000));
        // System.out.println("testAccSecond.getAmount() = " + accountObject.getValue().getAmount());
        // System.out.println("testAccSecond.getAmount() = " + testAccSecond.getAmount());

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
        //ArgumentCaptor<Account> accountObject = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(/*accountObject.capture()*/any(Account.class))).then(returnsFirstArg());
        Account testAccount = accountServiceImpl.createAccount(BigDecimal.valueOf(3000));
        when(accountDao.getAccount(testAccount.getId())).thenReturn(testAccount);

        Assertions.assertNotNull(testAccount,"Тестовый счёт не был создан");
        accountServiceImpl.getMoney(testAccount.getId(), BigDecimal.valueOf(2000));
        Assertions.assertEquals(testAccount.getAmount(), BigDecimal.valueOf(1000), "Сумма не была снята со счёта");
    }

    @Test

    void getAccount() {
        //Account dummyAccount;
        //Account checkAccount = new Account(0,BigDecimal.valueOf(0));
        // тут попробуем работать с реальными объектами
        accountDao = new AccountDao();
        accountServiceImpl = new AccountServiceImpl(accountDao);
        AccountServiceImpl accountServiceSpy = spy(accountServiceImpl);

        Account testAccount = accountServiceSpy.createAccount(BigDecimal.valueOf(3000));
        //verify(accountServiceSpy).getAccount(testAccount.getId());
        // Mockito.when(accountServiceSpy.getAccount(testAccount.getId())).thenReturn(checkAccount);

        Assertions.assertEquals(testAccount.hashCode(), accountServiceSpy.getAccount(testAccount.getId()).hashCode(), "Тестовый объект не равен исходному - транспорт нарушен");

        //dummyAccount = accountServiceSpy.getAccount(testAccount.getId());
        //Assertions.assertNotNull(checkAccount,"Тестовый счёт не вернулся");
        //System.out.println("testAccount = " + testAccount.getId());
        //System.out.println("checkAccount = " + checkAccount.getId());
    }

    @Test
    void checkBalance() {

        // ArgumentCaptor<Account> accountObject = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(any(Account.class))).then(returnsFirstArg());
        Account testAccount = accountServiceImpl.createAccount(BigDecimal.valueOf(3000));
        when(accountDao.getAccount(testAccount.getId())).thenReturn(testAccount);

        testAccount = accountServiceImpl.getAccount(testAccount.getId());
        //System.out.println("Account balance = " + testAccount.getAmount());

        //  проверяем, что счёт не уйдёт в минус. это допустимо?
        accountServiceImpl.putMoney(testAccount.getId(), testAccount.getAmount().negate().subtract(BigDecimal.valueOf(1000)));

        // значение поменялось
        //testAccount = accountServiceImpl.getAccount(testAccount.getId());
        // Assertions.assertTrue(accountServiceImpl.checkBalance(testAccount.getId()).compareTo(BigDecimal.ZERO) >= 0, "Счёт ушёл в минус");

        // возвращаем баланс в плюс
        accountServiceImpl.putMoney(testAccount.getId(), testAccount.getAmount().abs().add(BigDecimal.valueOf(1000)));
        Assertions.assertEquals(testAccount.getAmount(), accountServiceImpl.checkBalance(testAccount.getId()), "Сверка баланса и прямое обращение к счёту: сумма не сошлась.");

    }
}
