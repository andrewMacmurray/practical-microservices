package io.videos.application.home

import io.videos.application.cqrs.Commands
import io.videos.application.cqrs.Queries
import io.videos.application.videos.UploadVideo
import io.videos.application.videos.VideoId
import io.videos.application.videos.RecordView
import org.springframework.stereotype.Service

@Service
class HomeService(
    private val commands: Commands,
    private val queries: Queries
) {

    fun uploadVideo(upload: Upload) {
        commands.issue(UploadVideo(upload.name))
    }

    fun recordView(id: VideoId) {
        commands.issue(RecordView(id))
    }

    fun model(): HomeModel =
        queries.get(HomeModelQuery, HomeModel::class)
}

class HomeModel(
    val videosWatched: Int = 0,
    val videos: List<Video>
)

