package ru.domclicktest.dct.services

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import ru.domclicktest.dct.models.Account
import ru.domclicktest.dct.repositories.AccountRepository
import java.util.*

class AccountOperationsServiceTest {
    @InjectMocks
    private val service: OperationsService = AccountOperationsService()
    @Mock
    lateinit var accountRepository: AccountRepository

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(accountRepository.findById(rightAccountId)).thenReturn(Optional.of(Account("Jon", 100.0, rightAccountId)))
        Mockito.`when`(accountRepository.findById(rightAccountId2)).thenReturn(Optional.of(Account("Snow", 100.0, rightAccountId2)))
        Mockito.`when`(accountRepository.findById(badAccountId)).thenReturn(Optional.empty())
        Mockito.`when`(accountRepository.save(Account("Jon", 50.0, rightAccountId))).thenReturn(Account("Jon", 50.0, rightAccountId))
        Mockito.`when`(accountRepository.save(Account("Jon", 150.0, rightAccountId))).thenReturn(Account("Jon", 150.0, rightAccountId))
        Mockito.`when`(accountRepository.save(Account("Snow", 150.0, rightAccountId2))).thenReturn(Account("Jon", 150.0, rightAccountId))

    }

    @Test
    fun clientCanPutMoneyOnAccount() {
        Assert.assertEquals(OperationsService.Operation.Success(150.0), service.put(rightAccountId, 50.0))

    }

    @Test
    fun clientCanWithdrawMoneyFromAccount() {
        Assert.assertEquals(OperationsService.Operation.Success(50.0), service.withdraw(rightAccountId, 50.0))

    }

    @Test
    fun clientCanNotWithdrawMoneyFromNonexistentAccount() {
        Assert.assertEquals(OperationsService.Operation.NotFound(badAccountId), service.withdraw(badAccountId, 50.0))

    }

    @Test
    fun clientCanNotPutMoneyToNonexistentAccount() {
        Assert.assertEquals(OperationsService.Operation.NotFound(badAccountId), service.put(badAccountId, 50.0))

    }

    @Test
    fun clientCanNotWithdrawMoneyFromAccountIfThereNotEnough() {
        Assert.assertEquals(OperationsService.Operation.InsufficientFunds(rightAccountId, 100.0), service.withdraw(rightAccountId, 50000.0))

    }

    @Test
    fun clientCanTransferMoneyFromAccount() {
        Assert.assertEquals(OperationsService.Operation.Success(150.0), service.transfer(rightAccountId, rightAccountId2, 50.0))
    }

    @Test
    fun clientCanNotTransferMoneyFromAccountIfThereNotEnough() {
        Assert.assertEquals(OperationsService.Operation.InsufficientFunds(rightAccountId, 100.0), service.transfer(rightAccountId, rightAccountId2, 150.0))
    }

    @Test
    fun clientCanNotTransferMoneyFromAccountIfSecondAccountNotExist() {
        try {
            service.transfer(rightAccountId, badAccountId, 50.0)
            Assert.fail()
        } catch (e: Exception) {
            Assert.assertEquals(exceptionMessage, e.message)
        }

    }

    companion object {
        private const val rightAccountId: Long = 100500
        private const val rightAccountId2: Long = 100501
        private const val badAccountId: Long = 111555
        private const val exceptionMessage: String = "Account not exist"

    }
}