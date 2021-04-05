package io.videos.application

import org.springframework.stereotype.Service

@Service
class HomeModelService(
    private val commands: Commands,
    private val queries: Queries
) {

    fun uploadVideo(cmd: UploadVideo) {
        commands.issue(cmd)
    }

    fun recordView(cmd: ViewVideo) {
        commands.issue(cmd)
    }

    fun model(): HomeModel =
        queries.get(HomeModelQuery, HomeModel::class)
}

class HomeModel(
    val videosWatched: Int = 0,
    val videos: List<Video>
)

object HomeModelQuery : Query
