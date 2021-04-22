package io.videos.application.domains.email

import io.videos.application.Entity
import org.springframework.stereotype.Component
import java.io.File
import java.util.UUID

interface Mailer {
    fun send(email: Email, onSuccess: () -> Unit, onFailure: (String) -> Unit)
}

@Component
class FileSystemMailer : Mailer {
    override fun send(
        email: Email,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            File("emails.txt").appendText(email.toContents())
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.localizedMessage)
        }
    }

    private fun Email.toContents(): String = """
          From: Videos
          To: ${this.to}
          Subject: ${this.subject}
          Body: ${this.text}
          
        """.trimIndent()
}

data class Email(
    override val id: UUID,
    val to: String,
    val subject: String,
    val text: String,
    val html: String,
    val retries: Int = 0
) : Entity {
    fun retried(): Email =
        this.copy(retries = retries + 1)
}
