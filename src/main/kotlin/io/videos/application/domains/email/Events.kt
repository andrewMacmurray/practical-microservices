package io.videos.application.domains.email

import io.videos.application.cqrs.Event
import io.videos.application.cqrs.EventName
import java.util.UUID

@EventName("EmailSent")
class EmailSent(
    val emailId: UUID,
    val to: String,
    val subject: String,
    val text: String,
    val html: String
) : Event

@EventName("EmailSendRetried")
class EmailSendRetried(
    val emailId: UUID,
    val tries: Int,
    val to: String,
    val subject: String,
    val text: String,
    val html: String
) : Event

@EventName("EmailSendFailed")
class EmailSendFailed(
    val emailId: UUID,
    val reason: String,
    val to: String,
    val subject: String,
    val text: String,
    val html: String
) : Event