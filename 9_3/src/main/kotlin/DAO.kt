import java.sql.Connection
import java.sql.SQLException
//Clase abstracta DAO que crea las funciones básicas y queries de la que heredan los DAO
abstract class DAO<T>(open val nombre_tabla: String, open val c: Connection,open val nomID: String)
{

    abstract fun update(elemento: T, id: Int): Boolean
    abstract fun selectById(id: Int): T
    abstract fun selectAll(): List<T>
    private fun createTable() {
        //println(CREATE_TABLE)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statement from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE)
                //crearSecuencia()
                //st.execute(CREATE_TRIGGER)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }
    fun crearIndice(columna: String){
    val consulta = "CREATE INDEX IDX_$columna ON $nombre_tabla($columna)"
               try {c.createStatement().use{ st ->
            st.execute(consulta) }}
               catch (e: SQLException){
                   printSQLException(e)
               }
    }

    fun dropTable() {
        println(DROP_TABLE)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(DROP_TABLE)
                createTable()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }

    }

    fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con drop, sino la crea
        if(rs.next()){
            dropTable()
        } else createTable()
    }

    fun deleteById(id: Int): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE).use { st ->
                st.setInt(1, id)
                rowDeleted = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowDeleted
    }
    open fun printSQLException(ex: SQLException) {
        for (e in ex) {
            if (e is SQLException) {
                e.printStackTrace(System.err)
                System.err.println("SQLState: " + e.sqlState)
                System.err.println("Error Code: " + e.errorCode)
                System.err.println("Message: " + e.message)
                var t = ex.cause
                while (t != null) {
                    println("Cause: $t")
                    t = t.cause
                }
            }
        }
    }

    open val SCHEMA = "PROG"
    open val TABLE = "$nombre_tabla"
    open val DROP_TABLE = "drop table $nombre_tabla cascade constraints"
    open val CREATE_TABLE =
        ""
    open val INSERT = ""
    open val SELECT_BYID = "select * from $nombre_tabla where $nomID =?"
    open val SELECT_ALL = "select * from $nombre_tabla"
    open val DELETE = "delete from $nombre_tabla where $nomID = ?"
    open val UPDATE = ""

}
