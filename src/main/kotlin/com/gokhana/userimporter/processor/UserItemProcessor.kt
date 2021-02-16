package com.gokhana.userimporter.processor

import com.gokhana.userimporter.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.batch.item.ItemProcessor

class UserItemProcessor : ItemProcessor<User, User> {

    private val log: Logger = getLogger(UserItemProcessor::class.java)

    @Throws(Exception::class)
    override fun process(User: User): User {
        log.info("Converting ($User)")
        val firstName: String = User.name.toString()
        val lastName: String = User.surname.toString()
        val transformedUser = User(firstName, lastName)
        log.info("Converting ($User) into ($transformedUser)")
        return transformedUser
    }
}