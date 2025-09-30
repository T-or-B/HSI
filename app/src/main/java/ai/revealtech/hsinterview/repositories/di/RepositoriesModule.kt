package ai.revealtech.hsinterview.repositories.di

import ai.revealtech.hsinterview.data.networking.CharactersApiService
import ai.revealtech.hsinterview.domain.repositories.RickAndMortyRepository
import ai.revealtech.hsinterview.repositories.RickAndMortyRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    @Singleton
    fun provideRickAndMortyRepository(
            apiService: CharactersApiService
    ): RickAndMortyRepository = RickAndMortyRepositoryImpl(apiService)
}
