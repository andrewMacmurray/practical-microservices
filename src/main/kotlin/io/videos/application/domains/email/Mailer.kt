package io.videos.application.domains.email

import io.videos.application.Entity
import org.springframework.stereotype.Component
import java.io.File
import java.util.UUID

interface Mailer {
    fun send(
        email: Email,
        onSuccess: (Email) -> Unit,
        onFailure: (reason: String, email: Email) -> Unit
    )
}

@Component
class FileSystemMailer : Mailer {

    override fun send(
        email: Email,
        onSuccess: (Email) -> Unit,
        onFailure: (reason: String, email: Email) -> Unit
    ) {
        try {
            File("emails.txt").appendText(email.toContents())
            onSuccess(email)
        } catch (e: Exception) {
            onFailure(e.localizedMessage, email.incrementRetries())
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
    fun incrementRetries(): Email =
        this.copy(retries = retries + 1)

    fun retryLimitReached(): Boolean =
        retries > 5
}
