package ru.domclicktest.dct.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.domclicktest.dct.models.Account
import ru.domclicktest.dct.repositories.AccountRepository
import java.util.*

@Service
class AccountOperationsService : OperationsService {
    @Autowired
    lateinit var accountRepository: AccountRepository


    @Transactional
    override fun check(accountId: Long, howMatch: Double): OperationsService.Operation {
        val account: Optional<Account> = accountRepository.findById(accountId)
        if (account.isPresent) {
            return if (account.get().balance - howMatch > 0) {
                OperationsService.Operation.Sufficient(account.get().balance - howMatch)
            } else {
                OperationsService.Operation.InsufficientFunds(account.get().id, account.get().balance)
            }
        }
        return OperationsService.Operation.NotFound(accountId)
    }

    @Transactional
    override fun withdraw(accountId: Long, howMatch: Double): OperationsService.Operation {
        var account: Optional<Account> = accountRepository.findById(accountId)
        if (account.isPresent) {
            return if (check(account.get().id, howMatch) is OperationsService.Operation.Sufficient) {
                account.get().balance -= howMatch
                OperationsService.Operation.Success(accountRepository.save(account.get()).balance)
            } else OperationsService.Operation.InsufficientFunds(accountId, account.get().balance)
        }
        return OperationsService.Operation.NotFound(accountId)

    }

    @Transactional
    override fun put(accountId: Long, howMatch: Double): OperationsService.Operation {
        var account: Optional<Account> = accountRepository.findById(accountId)
        if (account.isPresent) {
            account.get().balance += howMatch
            return OperationsService.Operation.Success(accountRepository.save(account.get()).balance)
        }
        return OperationsService.Operation.NotFound(accountId)
    }

    @Transactional(rollbackFor = [(Exception::class)])
    override fun transfer(fromAccount: Long, toAccount: Long, howMatch: Double): OperationsService.Operation {
        val withdraw = withdraw(fromAccount, howMatch)
        if (withdraw is OperationsService.Operation.Success) {
            val put = put(toAccount, howMatch)
            if(put is OperationsService.Operation.Success){
                return OperationsService.Operation.Success(put.balance)
            }else throw Exception("Account not exist")
        }
        return withdraw
    }

}