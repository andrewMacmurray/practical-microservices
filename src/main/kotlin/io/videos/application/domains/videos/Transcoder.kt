package io.videos.application.domains.videos

import org.springframework.stereotype.Component
import java.util.UUID

interface Transcoder {
    fun transcode(
        video: Video,
        onSuccess: (Video.Transcoded) -> Unit,
        onFailure: (reason: String, video: Video) -> Unit
    )
}

@Component
class InMemoryTranscoder : Transcoder {
    override fun transcode(
        video: Video,
        onSuccess: (Video.Transcoded) -> Unit,
        onFailure: (reason: String, video: Video) -> Unit
    ) {
        onSuccess(
            Video.Transcoded(
                id = video.id,
                ownerId = video.ownerId,
                sourceUri = video.sourceUri,
                transcodedUri = "transcoded:${video.sourceUri}"
            )
        )
    }
}

class Video(
    val id: UUID,
    val ownerId: UUID,
    val sourceUri: String
) {
    class Transcoded(
        val id: UUID,
        val ownerId: UUID,
        val sourceUri: String,
        val transcodedUri: String
    )
}