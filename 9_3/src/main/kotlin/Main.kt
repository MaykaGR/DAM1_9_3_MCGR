import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

fun main() {
    val c = ConnectionBuilder()
    println("conectando.....")

    if (c.connection.isValid(10)) {
        println("Conexión válida")

        c.connection.use {
            val h2DAO = TiendaDAO("tiendas", "tiendas_seq", "tiendas_trigger", c.connection)
            val lista = listOf<Tienda>(Tienda(nombre = "La Nena", direccion = "Callejon de la Nena #123, Colonia Dulce Amor"),
                Tienda(nombre= "La Virgen", direccion = "Calle Rosa de Guadalupe #2, Colonia Bajo del Cerro"),
                Tienda(nombre= "La Piscina",direccion = "Avenida de los Charcos #78, Colonia El Mojado"),
                Tienda(nombre="El Churro",direccion = "Calle el Pason #666, Colonia El Viaje"),
                Tienda(nombre= "Don Pancho",direccion = "Avenida del Reboso #1521, Colonia El Burro"),
                Tienda(nombre= "Nombre", direccion= "Direccion")
            )
            //Comprueba si existe la tabla, si ya existe la borra para volver a crearla y sino, la crea

            h2DAO.prepareTable()
            lista.forEach{it-> h2DAO.insert(it)}

            // Busca una tienda por su id
            var u = h2DAO.selectById(6)
            u.id = 6
            println(u)
            //Modifica una tienda por su id
            if (u != null) {
                u.direccion = "Calle de la O"
                println("Fuera:" + u.direccion)
                h2DAO.update(u)
            }
            println(u)
            println(h2DAO.selectAll())
            //Borra una tienda por su id
            h2DAO.deleteById(6)
            println(h2DAO.selectAll())
            val h2DAO_2 = InventarioDAO("inventarios", "inventario_seq", "inventario_trigger", c.connection)
            val listaProductos = listOf<Producto>(Producto(nombre = "CD-DVD",comentario = "900 MB DE ESPACIO", precio = 35.50F, id_tienda =5),
                Producto(nombre = "USB-HP", comentario = "32GB, USB 3.0",precio =155.90F,id_tienda =4),
                Producto(nombre = "Laptop SONY", comentario = "4GB RAM, 300 HDD, i5 2.6 GHz.", precio = 13410.07F, id_tienda = 3),
                Producto(nombre = "Mouse Optico", comentario = "700 DPI", precio = 104.40F, id_tienda = 2),
                Producto(nombre = "Disco Duro", comentario = "200 TB, HDD, USB 3.0", precio = 2300.00F, id_tienda = 1),
                Producto(nombre = "Proyector TSHB", comentario = "TOSHIBA G155", precio = 5500.00F, id_tienda = 5))
            h2DAO_2.prepareTable()
            listaProductos.forEach{it-> h2DAO_2.insert(it)}
            // Busca una tienda por su id
             var producto = h2DAO_2.selectById(6)
            producto.id = 6
            println(producto)
            //Modifica una tienda por su id
            if (producto != null) {
                producto.comentario = "Comentario"
                h2DAO_2.update(producto)
            }
            println(producto)
            println(h2DAO_2.selectAll())
            //Borra una tienda por su id
            h2DAO_2.deleteById(1)
            println(h2DAO_2.selectAll())

        }

    } else
        println("Conexión ERROR")

}

/**
 * AbstractDAO.java This DAO class provides CRUD database operations for the
 * table users in the database.
 *
 * @author edu
 */
//La clase que crea la conexión, no le he quitado lo de arriba porque no he modificado nada
// así que sigue siendo la tuya
class ConnectionBuilder {
    // TODO Auto-generated catch block
    lateinit var connection: Connection
    private val jdbcURL = "jdbc:h2:mem:default"
    private val jdbcUsername = "PROG"
    private val jdbcPassword = "prog"

    init {
        try {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)
            connection.setAutoCommit(false)
        } catch (e: SQLException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

}




