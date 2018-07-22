package ru.domclicktest.dct.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.domclicktest.dct.models.Account

@Repository
interface AccountRepository: CrudRepository<Account, Long>