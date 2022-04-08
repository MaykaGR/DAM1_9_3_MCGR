class Animal

abstract class Padre<T>()
{
    abstract fun setHijo(t:T)
    abstract fun imprimeNombre(t:T)
    abstract fun dameHija():T

}

class Persona()

class Alemanes(nombre:String):Padre<Persona>(){
    override fun imprimeNombre(t: Persona) {
        TODO("Not yet implemented")
    }

    override fun setHijo(t: Persona) {
        TODO("Not yet implemented")
    }

    override fun dameHija(): Persona {
        TODO("Not yet implemented")
    }
}

fun main()
{
}
