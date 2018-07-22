package ru.domclicktest.dct.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import ru.domclicktest.dct.DctApplication
import ru.domclicktest.dct.services.AccountOperationsService
import ru.domclicktest.dct.services.OperationsService

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes= [(DctApplication::class)])
@DirtiesContext
@WebAppConfiguration
class OperationsControllerTest {
    @Autowired
    lateinit var webApplicationContext: WebApplicationContext
    lateinit var mockMvc: MockMvc
    @Mock
    lateinit var accountOperationsService: AccountOperationsService
    @Autowired
    @InjectMocks
    lateinit var operationsController: OperationsController


    @Before
    fun init(){
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(accountOperationsService.put(rightAccountId,50.0)).thenReturn(OperationsService.Operation.Success(150.0))
        Mockito.`when`(accountOperationsService.put(badAccountId,50.0)).thenReturn(OperationsService.Operation.NotFound(badAccountId))
        Mockito.`when`(accountOperationsService.withdraw(rightAccountId,50.0)).thenReturn(OperationsService.Operation.Success(50.0))
        Mockito.`when`(accountOperationsService.withdraw(rightAccountId,5000.0)).thenReturn(OperationsService.Operation.InsufficientFunds(rightAccountId,100.0))
        Mockito.`when`(accountOperationsService.withdraw(badAccountId,100.0)).thenReturn(OperationsService.Operation.NotFound(badAccountId))
        Mockito.`when`(accountOperationsService.transfer(rightAccountId,rightAccountId2,50.0)).thenReturn(OperationsService.Operation.Success(150.0))
        Mockito.`when`(accountOperationsService.transfer(rightAccountId,rightAccountId2,5000.0)).thenReturn(OperationsService.Operation.InsufficientFunds(rightAccountId,100.0))
        Mockito.`when`(accountOperationsService.transfer(rightAccountId,badAccountId,100.0)).then { throw Exception() }
    }
    @Test
    fun testPutMoney(){
        mockMvc.perform(MockMvcRequestBuilders.post(putMoneyPath)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(jacksonObjectMapper()
                        .writeValueAsString(OperationsController.SingleOperationDTO(rightAccountId, 50.0))))
                .andExpect(MockMvcResultMatchers.content().json("""{"balance": 150.0}"""))

    }
    @Test
    fun testPutOnNotExistingAccount(){
        mockMvc.perform(MockMvcRequestBuilders
                .post(putMoneyPath)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper()
                        .writeValueAsString(OperationsController.SingleOperationDTO(badAccountId,50.0))))
                .andExpect(MockMvcResultMatchers.content().json("""{"notFoundAccountId":$badAccountId}"""))
    }
    @Test
    fun testWithdrawMoney(){
        mockMvc.perform(MockMvcRequestBuilders
                .post(withdrawMoneyPath)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper()
                        .writeValueAsString(OperationsController.SingleOperationDTO(rightAccountId,50.0))))
                .andExpect(MockMvcResultMatchers.content().json("""{"balance": 50.0}"""))
    }
    @Test
    fun testWithdrawMoneyIfInsufficientFunds(){
        mockMvc.perform(MockMvcRequestBuilders
                .post(withdrawMoneyPath)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper()
                        .writeValueAsString(OperationsController.SingleOperationDTO(rightAccountId,5000.0))))
                .andExpect(MockMvcResultMatchers.content().json("""{"insufficientFundsOnAccountId": $rightAccountId,"balance": 100.0}"""))
    }
    @Test
    fun testWithdrawMoneyIfAccountNotExist(){
        mockMvc.perform(MockMvcRequestBuilders
                .post(withdrawMoneyPath)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper()
                        .writeValueAsString(OperationsController.SingleOperationDTO(badAccountId,100.0))))
                .andExpect(MockMvcResultMatchers.content().json("""{"notFoundAccountId":$badAccountId}"""))
    }
    @Test
    fun testTransferMoney(){
        mockMvc.perform(MockMvcRequestBuilders
                .post(transferMoneyPath)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper()
                        .writeValueAsString(OperationsController.MultipleOperationDTO(rightAccountId,rightAccountId2,50.0))))
                .andExpect(MockMvcResultMatchers.content().json("""{"balance": 150.0}"""))
    }
    @Test
    fun testTransferMoneyIfInsufficientFunds(){
        mockMvc.perform(MockMvcRequestBuilders
                .post(transferMoneyPath)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper()
                        .writeValueAsString(OperationsController.MultipleOperationDTO(rightAccountId,rightAccountId2,5000.0))))
                .andExpect(MockMvcResultMatchers.content().json("""{"insufficientFundsOnAccountId": $rightAccountId,"balance": 100.0}"""))
    }
    @Test
    fun testTransferMoneyIfAccountNotExist(){
        mockMvc.perform(MockMvcRequestBuilders
                .post(transferMoneyPath)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper()
                        .writeValueAsString(OperationsController.MultipleOperationDTO(rightAccountId,badAccountId,100.0))))
                .andExpect(MockMvcResultMatchers.content().json("""{"notFoundAccountId":$badAccountId}"""))
    }
    @Test
    fun controllerMustReturn404IfBadKey() {
        mockMvc.perform(MockMvcRequestBuilders.get("/$badPath"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
    companion object {
        private const val putMoneyPath="/putMoney"
        private const val withdrawMoneyPath="/withdrawMoney"
        private const val transferMoneyPath="/transferMoney"
        private const val badPath = "/badPath"
        private const val rightAccountId:Long=100500
        private const val rightAccountId2:Long=100501
        private const val badAccountId:Long=111555
    }
}