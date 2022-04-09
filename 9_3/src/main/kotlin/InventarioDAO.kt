import java.sql.Connection
import java.sql.SQLException

class InventarioDAO(
    nombre_tabla: String, nomID: String = "id_articulo", c: Connection
) : DAO<Producto>(nombre_tabla, c, nomID) {
//Queries
    override val CREATE_TABLE =
        "CREATE TABLE $nombre_tabla (\n" +
                "ID_ARTICULO NUMBER(10,0) CONSTRAINT PK_ID_ARTICULO PRIMARY KEY AUTO_INCREMENT, \n" +
                "NOMBRE VARCHAR2(50) UNIQUE, COMENTARIO VARCHAR2(200) NOT\n" +
                "NULL, PRECIO NUMBER(10,2) CHECK(PRECIO>0), \n" +
                "ID_TIENDA NUMBER(10,0) CONSTRAINT FK_ID_TIENDA REFERENCES TIENDAS(ID_TIENDA));"
    override val INSERT = "INSERT INTO $nombre_tabla (nombre, comentario, precio, id_tienda) VALUES (?, ?, ?, ?)"
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
                    producto = Producto(nombre = nombre, comentario = comentario, precio = precio, id_tienda = idTienda)
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
                    producto =
                        Producto(id, nombre = nombre, comentario = comentario, precio = precio, id_tienda = idTienda)
                    productos.add(producto)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return productos}


    //Función que actualiza un libro localizándolo por su id
    override fun update(producto: Producto, id: Int): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE).use { st ->
                st.setString(1, producto.nombre)
                st.setString(2, producto.comentario)
                st.setFloat(3, producto.precio)
                st.setInt(4, id)
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