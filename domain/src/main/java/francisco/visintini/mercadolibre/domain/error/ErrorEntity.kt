package francisco.visintini.mercadolibre.domain.error

sealed class ErrorEntity {
    object NetworkError : ErrorEntity()
    object ParsingError : ErrorEntity()
    object NotFound : ErrorEntity()
    object AccessDenied : ErrorEntity()
    object ServiceUnavailable : ErrorEntity()
    object UnknownError : ErrorEntity()
}
