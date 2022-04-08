import java.sql.Connection
import java.sql.SQLException

class TiendaDAO(
    nombre_tabla: String, nombre_seq: String,
    nombre_trigger: String, c: Connection
) : DAO<Tienda>(nombre_tabla, nombre_seq, nombre_trigger, c) {
//Queries


    override val TABLE = "$nombre_tabla"
    override val DROP_TABLE = "drop table $nombre_tabla cascade constraints"
    override val DROP_SEQUENCE = "drop sequence $nombre_seq"
    override val CREATE_TABLE =
        "CREATE TABLE $nombre_tabla (ID_TIENDA NUMBER(10,0) CONSTRAINT PK_ID_TIENDA PRIMARY KEY AUTO_INCREMENT, NOMBRE_TIENDA VARCHAR2(40), DIRECCION_TIENDA VARCHAR2(200) );"
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
        var tienda = Tienda(nombre = "", direccion = "")
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
                    tienda = Tienda(nombre = nombre, direccion = direccion)
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
                    tienda = Tienda(id, nombre = nombre, direccion = direccion)
                    tiendas.add(tienda)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tiendas
    }

    //Función que actualiza un libro localizándolo por su id
    override fun update(tienda: Tienda): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE).use { st ->
                st.setString(1, tienda.nombre)
                st.setString(2, tienda.direccion)
                println("Dentro: " + tienda.direccion)
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