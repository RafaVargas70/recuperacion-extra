package modelo

data class LIBRERIA(
    var uuid: String,
    var nombre:String,
    var autor:String,
    var publicacion:String,
    var estado: String,
    var ISBM:Int,
    var paginas:Int,
    var genero:String,
    var editorial:String
)