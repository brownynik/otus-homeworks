package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Spy
    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardsDao cardsDao;

    @Mock
    private AccountService accountService;

    @Mock
    private MoneyBoxService moneyBoxService;

    private CashMachineServiceImpl cashMachineService;

    private CashMachine cashMachine = new CashMachine(new MoneyBox());

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
        //cardService.createCard("5410501",1L,"123");
        //System.out.println(cardService);

    }


    @Test
    void getMoney() {
// @TODO create get money test using spy as mock

        cashMachine.getMoneyBox().setNote5000(3);
        cashMachine.getMoneyBox().setNote1000(0);
        cashMachine.getMoneyBox().setNote500(0);
        cashMachine.getMoneyBox().setNote100(0);
        MoneyBox mBox = cashMachine.getMoneyBox();

        BigDecimal checkedSumm =    BigDecimal.valueOf(
                                        mBox.getNote100() * 100
                                        + mBox.getNote500()*500
                                        + mBox.getNote1000() * 1000
                                        + mBox.getNote5000() * 5000
                                    );

        int intNote100 = mBox.getNote100();
        int intNote500 = mBox.getNote500();
        int intNote1000 = mBox.getNote1000();
        int intNote5000 = mBox.getNote5000();

        // Account acc = new Account(1L, BigDecimal.valueOf(10000));
        Card card = new Card(1000L, "5410501", 1L, TestUtil.getHash("1234"));
        when(cardsDao.getCardByNumber("5410501")).thenReturn(card);
        when(cardService.getMoney("5410501","1234", checkedSumm)).thenReturn(checkedSumm);
        when(moneyBoxService.getMoney(mBox, checkedSumm.intValue())).thenReturn(Arrays.asList(intNote5000,intNote1000,intNote500,intNote100));
        //when(accountService.getMoney(1L,BigDecimal.valueOf(15000))).thenReturn(BigDecimal.valueOf(15000));


        List <Integer> amount =  cashMachineService.getMoney(cashMachine,"5410501","1234",checkedSumm);

        Integer noteCount = 0;
        BigDecimal resultAmount = BigDecimal.ZERO;
        Map <Integer,BigDecimal> m = new HashMap<>();
        int i = 0;
        m.put(0, BigDecimal.valueOf(5000));
        m.put(1, BigDecimal.valueOf(1000));
        m.put(2, BigDecimal.valueOf(500));
        m.put(3, BigDecimal.valueOf(100));

        for(Integer e:amount) {
            noteCount+= e.intValue();
            resultAmount = resultAmount.add(m.get(i).multiply(BigDecimal.valueOf(e)));
            i++;
        }


        assertEquals(noteCount, mBox.getNote100() + mBox.getNote500() + mBox.getNote1000() + mBox.getNote5000(),
                "cashMachineService.getMoney вернул некорректное число купюр");




        /*
        System.out.println("Amount.Size = " + amount.size());
        System.out.println("noteCount = " + noteCount);
        System.out.println("resultAmount = " + resultAmount);
        */

    }

    @Test
    void putMoney() {

        Card card = new Card(1000L, "5410501", 1L, TestUtil.getHash("1234"));
        when(cardsDao.getCardByNumber("5410501")).thenReturn(card);
        when(cardService.getBalance("5410501","1234")).thenReturn(BigDecimal.valueOf(16600));
        when(accountService.putMoney(1L, BigDecimal.valueOf(16600))).thenReturn(BigDecimal.valueOf(16600));

        List <Integer> depositNotes = new ArrayList<>();
        depositNotes.add(3);
        depositNotes.add(1);
        depositNotes.add(1);
        depositNotes.add(1);
        BigDecimal depositSumm = cashMachineService.putMoney(cashMachine,"5410501","1234", depositNotes);

        BigDecimal controlSumm = BigDecimal.valueOf(depositNotes.get(0) * 5000
                + depositNotes.get(1)*1000
                + depositNotes.get(2)*500
                + depositNotes.get(3)*100);

        assertEquals(controlSumm, depositSumm,
                "cashMachineService.putMoney некорректно распределил купюры на баланс счёта");

    }

    @Test
    void checkBalance() {
        cashMachine.getMoneyBox().setNote5000(3);
        cashMachine.getMoneyBox().setNote1000(1);
        cashMachine.getMoneyBox().setNote500(1);
        cashMachine.getMoneyBox().setNote100(1);
        MoneyBox mBox = cashMachine.getMoneyBox();

        BigDecimal checkedSumm =    BigDecimal.valueOf(
                mBox.getNote100() * 100
                        + mBox.getNote500()*500
                        + mBox.getNote1000() * 1000
                        + mBox.getNote5000() * 5000
        );

        int intNote100 = mBox.getNote100();
        int intNote500 = mBox.getNote500();
        int intNote1000 = mBox.getNote1000();
        int intNote5000 = mBox.getNote5000();

        Card card = new Card(1000L, "5410501", 1L, TestUtil.getHash("1234"));
        when(cardsDao.getCardByNumber("5410501")).thenReturn(card);
        when(cardService.getBalance("5410501","1234")).thenReturn(checkedSumm);
        //when(accountService.putMoney(1L, BigDecimal.valueOf(16600))).thenReturn(checkedSumm);

        BigDecimal balance = cashMachineService.checkBalance(cashMachine,"5410501","1234");

        assertEquals(balance, checkedSumm, "Некорректно определён баланс счёта");

        // System.out.println("Balance = " + balance);
    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn

        Card card = new Card(1000L, "5410501", 1L, TestUtil.getHash("1234"));
        Card spyCard = Mockito.spy(card);

        ArgumentCaptor<Card> CardCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(eq("5410501"))).thenReturn(spyCard);
        // when(cardsDao.saveCard(CardCaptor.capture())).then(returnsFirstArg());
        when(cardsDao.saveCard(CardCaptor.capture())).thenReturn(spyCard);


        boolean isChangedPin = cashMachineService.changePin("5410501","1234","5678");



        assertTrue(CardCaptor.getValue().getPinCode().equals(TestUtil.getHash("5678")),"Ошибка изменения пин кода");


        // Проверяем, что если не указать карту, будет поднято определённое исключение
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            cardService.cnangePin(null, "1234", "5678");
        });
        assertEquals(thrown.getMessage(), "No card found");



    }

    @Test
    void changePinWithAnswer() {// @TODO create change pin test using spy as implementation and mock an thenAnswer

        Card card = new Card(1000L, "5410501", 1L, TestUtil.getHash("1234"));
        Card spyCard = Mockito.spy(card);

        when(cardsDao.getCardByNumber(eq("5410501"))).thenReturn(spyCard);
        when(cardsDao.saveCard(any())).thenAnswer(accountDao -> accountDao.getArguments()[0]);
        when(cardService.cnangePin("5410501", "1234", "5678")).thenAnswer(cardService->spyCard.getPinCode().equals(TestUtil.getHash("5678")));

        boolean isChangedPin = cashMachineService.changePin("5410501","1234","5678");

        assertEquals(spyCard.getPinCode(), TestUtil.getHash("5678"), "Ошибка изменения пин кода");
        assertTrue(isChangedPin, "Провалена проверка результата выполнения cardService.cnangePin");

    }
}