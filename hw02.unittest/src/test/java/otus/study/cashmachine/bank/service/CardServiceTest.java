package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

public class CardServiceTest {
    AccountService accountService;

    CardsDao cardsDao;

    CardService cardService;

    @BeforeEach
    void init() {
        cardsDao = mock(CardsDao.class);
        accountService = mock(AccountService.class);
        cardService = new CardServiceImpl(accountService, cardsDao);
    }

    @Test
    void testCreateCard() {
        when(cardsDao.createCard("5555", 1L, "0123")).thenReturn(
                new Card(1L, "5555", 1L, "0123"));

        Card newCard = cardService.createCard("5555", 1L, "0123");
        assertNotEquals(0, newCard.getId());
        assertEquals("5555", newCard.getNumber());
        assertEquals(1L, newCard.getAccountId());
        assertEquals("0123", newCard.getPinCode());
    }

    @Test
    void checkBalance() {
        Card card = new Card(1L, "1234", 1L, TestUtil.getHash("0000"));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(accountService.checkBalance(1L)).thenReturn(new BigDecimal(1000));

        BigDecimal sum = cardService.getBalance("1234", "0000");
        assertEquals(0, sum.compareTo(new BigDecimal(1000)));
    }

    @Test
    void getMoney() {
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 100L, TestUtil.getHash("0000")));

        when(accountService.getMoney(idCaptor.capture(), amountCaptor.capture()))
                .thenReturn(BigDecimal.TEN);

        cardService.getMoney("1111", "0000", BigDecimal.ONE);

        verify(accountService, only()).getMoney(anyLong(), any());
        assertEquals(BigDecimal.ONE, amountCaptor.getValue());
        assertEquals(100L, idCaptor.getValue().longValue());

        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
                    cardService.getMoney("1111", "2222", BigDecimal.ONE);
        });

    }

    @Test
    void putMoney() {
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<Long> idCardCaptor = ArgumentCaptor.forClass(Long.class);

        when(cardsDao.getCardByNumber("5050")).thenReturn(new Card(1L, "5050", 10L, TestUtil.getHash("1234")));
        when(accountService.putMoney(idCardCaptor.capture(), amountCaptor.capture())).thenReturn(BigDecimal.valueOf(2000));

        BigDecimal balance = cardService.putMoney("5050","1234",BigDecimal.valueOf(2000));

        verify(accountService, only()).putMoney(anyLong(), any());
        assertEquals(BigDecimal.valueOf(2000), amountCaptor.getValue());
        assertEquals(10L, idCardCaptor.getValue().longValue());

        // System.out.println("idCardCaptor = " + idCardCaptor.getValue());
        // System.out.println("amountCaptor = " + amountCaptor.getValue());
    }

    @Test
    void checkIncorrectPin() {
        Card card = new Card(1L, "1234", 1L, "0000");
        when(cardsDao.getCardByNumber(eq("1234"))).thenReturn(card);

        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            cardService.getBalance("1234", "0012");
        });
        assertEquals(thrown.getMessage(), "Pincode is incorrect");
    }

    @Test
    void checkChangePin() {
        Card card = new Card(600L, "6060", 10L, TestUtil.getHash("1234"));
        ArgumentCaptor<Card> CardCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(eq("6060"))).thenReturn(card);
        when(cardsDao.saveCard(CardCaptor.capture())).then(returnsFirstArg());

        boolean isChangedPin = cardService.cnangePin("6060", "1234", "5678");

        assertTrue(CardCaptor.getValue().getPinCode().equals(TestUtil.getHash("5678")),"Ошибка изменения пин кода");

        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            cardService.cnangePin(null, "1234", "5678");
        });
        assertEquals(thrown.getMessage(), "No card found");

        // System.out.println("isChangedPin = " + isChangedPin);
        // System.out.println("CardCaptor.getValue().getPinCode() = " + CardCaptor.getValue().getPinCode().equals(TestUtil.getHash("5678")));
    }
}