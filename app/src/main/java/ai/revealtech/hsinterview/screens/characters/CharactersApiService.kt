package ai.revealtech.hsinterview.screens.characters

import retrofit2.http.GET
import retrofit2.http.Query

interface CharactersApiService {

    @GET("character")
    suspend fun getCharacters(
            @Query("page") page: Int = 1,
            @Query("name") name: String? = null,
            @Query("status") status: String? = null,
            @Query("species") species: String? = null,
            @Query("gender") gender: String? = null
    ): CharactersResponse
}
