package ru.domclicktest.dct.services

interface OperationsService {
    interface Operation {
        data class Success(var balance: Double) : Operation
        data class NotFound(var notFoundAccountId: Long) : Operation
        data class InsufficientFunds(var insufficientFundsOnAccountId: Long, var balance: Double) : Operation
        data class Sufficient( var residue:Double) : Operation
    }

    fun withdraw(accountId: Long, howMatch: Double): Operation
    fun put(accountId: Long, howMatch: Double): Operation
    fun transfer(fromAccount: Long, toAccount: Long, howMatch: Double): Operation
    fun check(accountId: Long, howMatch: Double): Operation
}