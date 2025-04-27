package pl.marcin.malocha.minivideojournalapp.domain.usecase.videos

data class VideosUseCases(
    val getVideos: GetVideosUseCase,
    val insertVideo: InsertVideoUseCase
)