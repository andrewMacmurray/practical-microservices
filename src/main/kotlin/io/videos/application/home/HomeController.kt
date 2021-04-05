package io.videos.application.home

import io.videos.application.pipe
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import java.util.UUID

@Controller
class HomeController(private val home: HomeService) {

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("model", home.model())
        return "home"
    }

    @PostMapping("/upload-video")
    fun uploadVideo(upload: Upload): ModelAndView =
        home.uploadVideo(upload)
            .pipe { backHome }

    @PostMapping("/record-viewing/{videoId}")
    fun recordViewing(@PathVariable("videoId") id: UUID): ModelAndView =
        home.recordView(id)
            .pipe { backHome }

    private val backHome: ModelAndView =
        ModelAndView("redirect:/")
}

class Upload(
    val name: String
)

