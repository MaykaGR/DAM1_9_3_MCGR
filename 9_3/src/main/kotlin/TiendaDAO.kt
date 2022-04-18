import java.sql.Connection
import java.sql.SQLException
//Clase que hereda de DAO y especifica la forma de hacer las cosas para gestionar tiendas
class TiendaDAO(
    nombre_tabla: String, c: Connection, nomID: String = "id_tienda"
) : DAO<Tienda>(nombre_tabla, c, nomID) {
//Queries


    override val CREATE_TABLE =
        "CREATE TABLE $nombre_tabla (ID_TIENDA NUMBER(10,0) CONSTRAINT PK_ID_TIENDA PRIMARY KEY AUTO_INCREMENT, NOMBRE_TIENDA VARCHAR2(40), DIRECCION_TIENDA VARCHAR2(200) );"
    override val INSERT = "INSERT INTO $nombre_tabla (nombre_tienda, direccion_tienda) VALUES (?, ?)"
    override val UPDATE = "update $nombre_tabla set nombre_tienda = ?, direccion_tienda = ? where id_tienda = ?"


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
    override fun update(tienda: Tienda, id: Int): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE).use { st ->
                st.setString(1, tienda.nombre)
                st.setString(2, tienda.direccion)
                st.setInt(3, id)
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