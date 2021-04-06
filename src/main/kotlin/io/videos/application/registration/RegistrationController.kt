package io.videos.application.registration

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

    @PostMapping("/register")
    fun register(registration: Registration): ModelAndView =
        users
            .register(registration)
            .pipe { ModelAndView("redirect:/") }
}

class Registration(
    val email: String,
    val password: String
)