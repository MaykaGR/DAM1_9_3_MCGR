import java.sql.Connection
import java.sql.Driver
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
            println(u)
            //Modifica una tienda por su id
            if (u != null) {
                u.direccion = "Calle de la O"
                h2DAO.update(6)
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
            println(producto)
            //Modifica una tienda por su id
            if (producto != null) {
                producto.comentario = "Comentario"
                h2DAO_2.update(6)
            }
            println(producto)
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


class TiendaDAO(
    override val nombre_tabla: String, override val nombre_seq: String,
    override val nombre_trigger: String, override val c: Connection
) : DAO(nombre_tabla, nombre_seq, nombre_trigger, c) {
//Queries


    override val TABLE = "$nombre_tabla"
    override val DROP_TABLE = "drop table $nombre_tabla cascade constraints"
    override val DROP_SEQUENCE = "drop sequence $nombre_seq"
    override val CREATE_TABLE =
        "CREATE TABLE TIENDAS (ID_TIENDA NUMBER(10,0) CONSTRAINT PK_ID_TIENDA PRIMARY KEY AUTO_INCREMENT, NOMBRE_TIENDA VARCHAR2(40), DIRECCION_TIENDA VARCHAR2(200) );"
    override val INSERT = "INSERT INTO $nombre_tabla (nombre_tienda, direccion_tienda) VALUES (?, ?)"
    //override val CREATE_SEQUENCE = "CREATE SEQUENCE $nombre_seq START WITH 1"
    //override val CREATE_TRIGGER =
        //"CREATE OR REPLACE TRIGGER $nombre_trigger BEFORE INSERT ON $nombre_tabla FOR EACH ROW BEGIN SELECT $nombre_seq.NEXTVAL INTO :new.ID FROM dual; END;"
    override val SELECT_BYID = "select * from $nombre_tabla where id_tienda =?"
    override val SELECT_ALL = "select * from $nombre_tabla"
    override val DELETE = "delete from $nombre_tabla where id_tienda = ?"
    override val UPDATE = "update $nombre_tabla set nombre_tienda = ?, direccion_tienda = ? where id_tienda = ?"

    //Función que inserta libros en la tabla
    fun insert(tienda: Tienda) {
        //println(INSERT)
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT).use { st ->
                st.setString(1, tienda.nombre)
                st.setString(2, tienda.direccion)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    //Función que encuentra un libro por su id
    override fun selectById(id: Int): Tienda {
        var tienda = Tienda(nombre = "", direccion =  "")
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_BYID).use { st ->
                st.setInt(1, id)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val nombre = rs.getString("NOMBRE_TIENDA")
                    val direccion = rs.getString("DIRECCION_TIENDA")
                    tienda = Tienda(nombre = nombre, direccion =  direccion)

                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tienda
    }

    //Función que muestra lo que haya en la tabla
    override fun selectAll(): List<Tienda> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val tiendas: MutableList<Tienda> = ArrayList()
        // Step 1: Establishing a Connection
        lateinit var tienda: Tienda
        try {
            c.prepareStatement(SELECT_ALL).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getInt("ID_TIENDA")
                    val nombre = rs.getString("NOMBRE_TIENDA")
                    val direccion = rs.getString("DIRECCION_TIENDA")
                    tienda = Tienda(id,nombre = nombre, direccion =  direccion)
                    tiendas.add(tienda)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tiendas
    }

    //Función que actualiza un libro localizándolo por su id
    override fun update(id: Int): Boolean {
        var rowUpdated = false
        var tienda = selectById(id)
        try {
            c.prepareStatement(UPDATE).use { st ->
                st.setString(1, tienda.nombre)
                st.setString(2, tienda.direccion)
                st.setInt(3, tienda.id)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }
}


class InventarioDAO(
    override val nombre_tabla: String, override val nombre_seq: String,
    override val nombre_trigger: String, override val c: Connection
) : DAO(nombre_tabla, nombre_seq, nombre_trigger, c) {
//Queries

    override val TABLE = "$nombre_tabla"
    override val DROP_TABLE = "drop table $nombre_tabla cascade constraints"
    override val DROP_SEQUENCE = "drop sequence $nombre_seq"
    override val CREATE_TABLE =
        "CREATE TABLE INVENTARIOS (\n" +
                "ID_ARTICULO NUMBER(10,0) CONSTRAINT PK_ID_ARTICULO PRIMARY KEY AUTO_INCREMENT, \n" +
                "NOMBRE VARCHAR2(50) UNIQUE, COMENTARIO VARCHAR2(200) NOT\n" +
                "NULL, PRECIO NUMBER(10,2) CHECK(PRECIO>0), \n" +
                "ID_TIENDA NUMBER(10,0) CONSTRAINT FK_ID_TIENDA REFERENCES TIENDAS(ID_TIENDA));"
    override val INSERT = "INSERT INTO $nombre_tabla (nombre, comentario, precio, id_tienda) VALUES (?, ?, ?, ?)"
    //override val CREATE_SEQUENCE = "CREATE SEQUENCE $nombre_seq START WITH 1"
    //override val CREATE_TRIGGER =
        //"CREATE OR REPLACE TRIGGER $nombre_trigger BEFORE INSERT ON $nombre_tabla FOR EACH ROW BEGIN SELECT $nombre_seq.NEXTVAL INTO :new.ID FROM dual; END;"
    override val SELECT_BYID = "select * from $nombre_tabla where id_articulo =?"
    override val SELECT_ALL = "select * from $nombre_tabla"
    override val DELETE = "delete from $nombre_tabla where id_articulo = ?"
    override val UPDATE = "update $nombre_tabla set nombre = ?, comentario = ?, precio = ? where id_articulo = ?"

    //Función que inserta libros en la tabla
    fun insert(producto: Producto) {
        //println(INSERT)
        try {
            c.prepareStatement(INSERT).use { st ->
                st.setString(1, producto.nombre)
                st.setString(2, producto.comentario)
                st.setFloat(3, producto.precio)
                st.setInt(4,producto.id_tienda)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }}


    //Función que encuentra un libro por su id
    override fun selectById(id: Int): Producto {
        var producto = Producto(nombre = "", comentario = "", precio = 0F, id_tienda = 0)
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_BYID).use { st ->
                st.setInt(1, id)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val nombre = rs.getString("NOMBRE")
                    val comentario = rs.getString("COMENTARIO")
                    val precio = rs.getFloat("PRECIO")
                    val idTienda = rs.getInt("ID_TIENDA")
                    producto = Producto(nombre = nombre, comentario =  comentario, precio =  precio, id_tienda = idTienda)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return producto}


    //Función que muestra lo que haya en la tabla
    override fun selectAll(): List<Producto> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val productos: MutableList<Producto> = ArrayList()
        lateinit var producto: Producto
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_ALL).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getInt("ID_ARTICULO")
                    val nombre = rs.getString("NOMBRE")
                    val comentario = rs.getString("COMENTARIO")
                    val precio = rs.getFloat("PRECIO")
                    val idTienda = rs.getInt("ID_TIENDA")
                    producto = Producto(id,nombre = nombre, comentario =  comentario, precio =  precio, id_tienda = idTienda)
                    productos.add(producto)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return productos}


    //Función que actualiza un libro localizándolo por su id
    override fun update(id: Int): Boolean {
        var rowUpdated = false
        var producto = selectById(id)
        try {
            c.prepareStatement(UPDATE).use { st ->
                st.setString(1, producto.nombre)
                st.setString(2, producto.comentario)
                st.setFloat(3, producto.precio)
                st.setInt(4, producto.id)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated}
}




