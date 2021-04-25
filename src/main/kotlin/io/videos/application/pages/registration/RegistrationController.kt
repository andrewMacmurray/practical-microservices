package io.videos.application.pages.registration

import io.videos.application.map
import io.videos.application.onError
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
    fun register(): String = "register"

    @GetMapping("/login")
    fun login(): String = "login"

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse): ModelAndView {
        clearUserCookie(response)
        return backHome
    }

    @PostMapping("/register")
    fun register(registration: Registration, response: HttpServletResponse): ModelAndView =
        users
            .register(registration)
            .pipe { response.addUserId(it) }
            .pipe { backHome }

    @PostMapping("/login")
    fun login(login: Login, response: HttpServletResponse): ModelAndView =
        users
            .login(login)
            .map { response.addUserId(it.id) }
            .map { backHome }
            .onError { ModelAndView("login") }

    private val backHome: ModelAndView =
        ModelAndView("redirect:/")

    private fun HttpServletResponse.addUserId(id: UUID) {
        this.addCookie(Cookie("userId", "$id"))
    }

    private fun clearUserCookie(response: HttpServletResponse) {
        val cookie = Cookie("userId", null)
        cookie.maxAge = 0
        response.addCookie(cookie)
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