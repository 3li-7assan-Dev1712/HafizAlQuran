package app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses

data class Data(
    val ayahs: List<Ayah>,
    val edition: Edition,
    val number: Int,
    val surahs: Surahs
)