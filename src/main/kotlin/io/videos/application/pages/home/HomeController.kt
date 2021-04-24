package io.videos.application.pages.home

import io.videos.application.pages.Account
import io.videos.application.pipe
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.servlet.ModelAndView
import java.util.UUID

@Controller
class HomeController(private val home: HomeService) {

    @GetMapping("/")
    fun home(
        model: Model,
        @RequestAttribute account: Account
    ): String {
        model.addAttribute("model", home.model())
        model.addAttribute("account", account)
        return "home"
    }

    @PostMapping("/upload-video")
    fun uploadVideo(
        upload: Upload,
        model: Model,
        @RequestAttribute account: Account
    ): ModelAndView =
        home.uploadVideo(upload, account.user!!)
            .pipe { backHome }

    @PostMapping("/record-viewing/{videoId}")
    fun recordViewing(@PathVariable("videoId") id: UUID): ModelAndView =
        home.recordView(id)
            .pipe { backHome }

    private val backHome: ModelAndView =
        ModelAndView("redirect:/")
}

class Upload(
    val uri: String
)

