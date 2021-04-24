package io.videos.application.pages

import io.videos.application.domains.identity.RegisteredUser
import io.videos.application.domains.identity.InMemoryUsersRepository
import io.videos.application.pipe
import org.springframework.stereotype.Component
import java.util.UUID
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AccountFilter(private val users: InMemoryUsersRepository) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        request as HttpServletRequest
        response as HttpServletResponse
        val account = findAccount(request)
        request.setAttribute("account", account)
        chain.doFilter(request, response)
    }

    private fun findAccount(request: HttpServletRequest): Account =
        Account(findRegisteredUser(request))

    private fun findRegisteredUser(request: HttpServletRequest): RegisteredUser? =
        request.cookies
            .find { it.name == "userId" }
            ?.pipe { UUID.fromString(it.value) }
            ?.pipe { users.find(it) }
}

data class Account(val user: RegisteredUser?) {
    val email: String? = user?.email
}
