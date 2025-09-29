package ai.revealtech.hsinterview.domain.models

data class Character(
        val id: Int,
        val name: String,
        val status: String,
        val species: String,
        val type: String,
        val gender: String,
        val origin: CharacterLocation,
        val location: CharacterLocation,
        val image: String,
        val episode: List<String>,
        val url: String,
        val created: String
)

data class CharacterLocation(
        val name: String,
        val url: String
)

data class CharactersCollection(
        val collectionInfo: CollectionInfo,
        val characterList: List<Character>
)

data class CollectionInfo(
        val count: Int,
        val pages: Int,
        val next: String?,
        val prev: String?
)
