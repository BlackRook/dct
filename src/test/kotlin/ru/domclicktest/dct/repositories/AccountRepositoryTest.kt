package ru.domclicktest.dct.repositories

import com.github.springtestdbunit.annotation.DatabaseOperation
import com.github.springtestdbunit.annotation.DatabaseTearDown
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import ru.domclicktest.dct.models.Account
import java.util.*

@TestPropertySource(locations = ["classpath:repositoriesTest.properties"])
@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL)
class AccountRepositoryTest : AbstractRepositoryTest() {
    @Autowired
    lateinit var repository: AccountRepository

    @Test
    fun findOneExisting() {
        val got: Optional<Account> = repository.findById(rightAccountId)
        MatcherAssert.assertThat(got.isPresent, Matchers.equalTo(true))
        val link = got.get()
        MatcherAssert.assertThat(link, Matchers.equalTo(Account("Jon",100.0 ,rightAccountId)))
    }
    @Test
    fun findOneNotExisting() {
        val got: Optional<Account> = repository.findById(badAccountId)
        MatcherAssert.assertThat(got.isPresent, Matchers.equalTo(false))

    }
    @Test
    fun saveNew() {
        val toBeSaved: Account = Account("Snow",1000000.0)
        val got: Account = repository.save(toBeSaved)
        val list: List<Account> = repository.findAll() as List<Account>
        MatcherAssert.assertThat(list, Matchers.hasSize(4))
        MatcherAssert.assertThat(got.name, Matchers.equalTo(name))
    }
    companion object {
        private const val rightAccountId: Long = 100500
        private const val badAccountId: Long= 111555
        private const val name: String = "Snow"

    }
}