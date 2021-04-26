package io.videos.application.pages.home

import io.videos.application.cqrs.Queries
import io.videos.application.domains.identity.RegisteredUser
import org.springframework.stereotype.Service

@Service
class HomeModelLoader(private val queries: Queries) {

    fun load(): HomeModel =
        HomeModelQuery.exec(queries)
}

class HomeModel(
    val totalViews: Int = 0,
    val videos: List<Video>,
    val users: List<RegisteredUser>
)

