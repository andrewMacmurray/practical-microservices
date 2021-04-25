package io.videos.application.domains.email

import io.videos.application.Entity
import org.springframework.stereotype.Component
import java.io.File
import java.util.UUID

interface Mailer {
    fun send(
        email: Email,
        onSuccess: (Email) -> Unit,
        onError: (Error) -> Unit
    )

    fun pause()

    class Error(
        val reason: String,
        val email: Email
    )
}

@Component
class FileSystemMailer : Mailer {

    override fun send(
        email: Email,
        onSuccess: (Email) -> Unit,
        onError: (Mailer.Error) -> Unit
    ) {
        try {
            File("emails.txt").appendText(email.toContents())
            onSuccess(email)
        } catch (e: Exception) {
            onError(
                Mailer.Error(
                    reason = e.localizedMessage,
                    email = email.incrementRetries()
                )
            )
        }
    }

    override fun pause() {
        Thread.sleep(5000)
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
