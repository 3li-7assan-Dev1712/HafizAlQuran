package app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses

data class Ayah(
    val audio: String,
    val audioSecondary: List<String>,
    val hizbQuarter: Int,
    val juz: Int,
    val manzil: Int,
    val number: Int,
    val numberInSurah: Int,
    val page: Int,
    val ruku: Int,
    val sajda: Boolean,
    val surah: Surah,
    val text: String
)