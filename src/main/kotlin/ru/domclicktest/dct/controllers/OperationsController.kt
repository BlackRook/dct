package ru.domclicktest.dct.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import ru.domclicktest.dct.services.AccountOperationsService
import ru.domclicktest.dct.services.OperationsService

@Controller
@RequestMapping("/")
class OperationsController {
    @Autowired
    lateinit var accountOperationsService: AccountOperationsService

    @RequestMapping("withdrawMoney")
    @ResponseBody
    fun withdrawMoney(@RequestBody SingleOperationDTO: SingleOperationDTO): OperationsService.Operation {
        return accountOperationsService.withdraw(SingleOperationDTO.accountId, SingleOperationDTO.howMatch)
    }

    @RequestMapping("putMoney")
    @ResponseBody
    fun putMoney(@RequestBody SingleOperationDTO: SingleOperationDTO): OperationsService.Operation {
        return accountOperationsService.put(SingleOperationDTO.accountId, SingleOperationDTO.howMatch)
    }

    @RequestMapping("transferMoney")
    @ResponseBody
    fun transferMoney(@RequestBody MultipleOperationDTO: MultipleOperationDTO): OperationsService.Operation {
        return try {
            accountOperationsService.transfer(MultipleOperationDTO.fromAccount,
                    MultipleOperationDTO.toAccount, MultipleOperationDTO.howMatch)

        } catch (e: Exception) {
            OperationsService.Operation.NotFound(MultipleOperationDTO.toAccount)
        }


    }

    data class SingleOperationDTO(var accountId: Long = 0, var howMatch: Double = 0.0)
    data class MultipleOperationDTO(var fromAccount: Long, var toAccount: Long, var howMatch: Double)
}