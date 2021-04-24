package io.videos.application.pages.registration

import io.videos.application.map
import io.videos.application.onError
import io.videos.application.pages.Account
import io.videos.application.pipe
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import java.util.UUID
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Controller
class RegistrationController(private val users: UsersService) {

    @GetMapping("/register")
    fun register(): String {
        return "register"
    }

    @GetMapping("/login")
    fun login(account: Account): String {
        println(account)
        return "login"
    }

    @PostMapping("/register")
    fun register(registration: Registration, response: HttpServletResponse): ModelAndView =
        users
            .register(registration)
            .pipe { response.addUserId(it) }
            .pipe { ModelAndView("redirect:/") }

    @PostMapping("/login")
    fun login(login: Login, response: HttpServletResponse): ModelAndView =
        users
            .login(login)
            .map { response.addUserId(it.id) }
            .map { ModelAndView("redirect:/", mapOf("user" to it)) }
            .onError { ModelAndView("login", mapOf("error" to it)) }

    private fun HttpServletResponse.addUserId(id: UUID) {
        this.addCookie(Cookie("userId", id.toString()))
    }
}

class Registration(
    val email: String,
    val password: String
)

class Login(
    val email: String,
    val password: String
)