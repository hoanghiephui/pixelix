package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow

class GetIsFocusModeEnabledUseCase(
    private val repository: StorageRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.getIsFocusModeEnabled()
    }
}