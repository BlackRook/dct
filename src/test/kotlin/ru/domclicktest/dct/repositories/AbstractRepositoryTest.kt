package ru.domclicktest.dct.repositories

import com.github.springtestdbunit.DbUnitTestExecutionListener
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests
import ru.domclicktest.dct.DctApplication

@TestPropertySource(locations = ["classpath:repositoriesTest.properties"])
@TestExecutionListeners(DbUnitTestExecutionListener::class)
@ContextConfiguration(classes= [DctApplication::class])
@DirtiesContext
abstract class AbstractRepositoryTest: AbstractTransactionalJUnit4SpringContextTests()
