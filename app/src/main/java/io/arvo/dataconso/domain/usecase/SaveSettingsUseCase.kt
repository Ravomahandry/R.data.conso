package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.AppSettings
import io.arvo.dataconso.DataRepository
import javax.inject.Inject

/**
 * UseCase dédié à la persistance des paramètres de l'application.
 * Permet d'alléger le MainViewModel en encapsulant l'écriture en base via le DataRepository.
 */
class SaveSettingsUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(settings: AppSettings) {
        repository.saveSettings(settings)
    }
}
