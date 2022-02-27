package app.netlify.dev_ali_hassan.hafizalquran.util

sealed class Resource<T>(
    data: T? = null,
    message: String? = null
) {

    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(data: T? = null, message: String) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()

}