package pl.marcin.malocha.minivideojournalapp.domain.usecase.file

import android.net.Uri
import pl.marcin.malocha.minivideojournalapp.domain.repository.FileRepository

class CreateVideoUriUseCase(
    private val fileRepository: FileRepository
) {
    operator fun invoke(): Uri {
        return fileRepository.createVideoUri()
    }
}