package hcvs.cs.myapplication

data class musicResponse(
    val resultList: List<MusicItem>
)

data class MusicItem(
    val SongURL: String? = null,
    val imageURL: String? = null,
    val SongName: String? = null
)