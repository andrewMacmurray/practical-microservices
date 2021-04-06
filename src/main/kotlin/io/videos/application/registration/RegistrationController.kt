package io.videos.application.registration

import io.videos.application.map
import io.videos.application.onError
import io.videos.application.pipe
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class RegistrationController(private val users: UsersService) {

    @GetMapping("/register")
    fun register(): String {
        return "register"
    }

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @PostMapping("/register")
    fun register(registration: Registration): ModelAndView =
        users
            .register(registration)
            .pipe { ModelAndView("redirect:/") }

    @PostMapping("/login")
    fun login(login: Login): ModelAndView =
        users
            .login(login)
            .map { ModelAndView("redirect:/", mapOf("user" to it)) }
            .onError { ModelAndView("login", mapOf("error" to it)) }
}

class Registration(
    val email: String,
    val password: String
)

class Login(
    val email: String,
    val password: String
)