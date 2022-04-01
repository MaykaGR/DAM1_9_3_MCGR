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
            //val h2DAO = DAO(c.connection)

            // Creamos la tabla o la vaciamos si ya existe
            //h2DAO.prepareTable()


            // Buscar un usuario
            //var u = h2DAO.select()




            //h2DAO.delete()


            //println(h2DAO.selectAll())
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

class ConnectionBuilder {
    // TODO Auto-generated catch block
    lateinit var connection: Connection
    private val jdbcURL = "jdbc:h2:mem:default"
    private val jdbcUsername = ""
    private val jdbcPassword = ""

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


class BookDAO(private val c: Connection) {

    companion object {
        private const val SCHEMA = "PROG2"
        private const val TABLE = "catalogo"
        private const val DROP_TABLE = "drop table catalogo cascade constraints"
        private const val DROP_SEQUENCE = "drop sequence CAT_SEQ"
        private const val CREATE_TABLE =
            "CREATE TABLE catalogo(book_id char(5) constraint catalogo_pk primary key, author varchar(50), title varchar(50), genre varchar(30), price number(5,2), publish_date varchar(12), description varchar(300))"
        private const val CREATE_SEQUENCE= "CREATE SEQUENCE cat_seq START WITH 1"
        //private const val CREATE_TRIGGER = "CREATE OR REPLACE TRIGGER tuser BEFORE INSERT ON USERS FOR EACH ROW BEGIN SELECT dept_seq.NEXTVAL INTO :new.ID FROM dual; END;"
        private const val INSERT = "INSERT INTO catalogo (book_id, author, title, genre, price, publish_date, description) VALUES (?, ?, ?, ?, ?, ?, ?)"
        private const val SELECT_BYID = "select book_id,author,title,genre,price,publish_date,description from catalogo where book_id =?"
        private const val SELECT_ALL = "select * from catalogo"
        private const val DELETE = "delete from catalogo where book_id = ?"
        private const val UPDATE = "update catalogo set author = ?,title= ?, genre =?, price =?, publish_date =?, description =? where book_id = ?"
    }

    fun crearId(): String{
        val query = "SELECT cat_seq.NEXTVAL FROM dual"
        val number = c.prepareStatement(query).executeQuery()
        number.next()
        val n = number.getInt("NEXTVAL")
        var id: String
        if (n<10){
            id = "bk10$n"} else  id = "bk1$n"
        return id
    }

    fun prepareTable() {
        val metaData = c.metaData
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        if (!rs.next()) createTable() else dropTable()
    }

    fun dropTable() {
        println(DROP_TABLE)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(DROP_TABLE)
                st.execute(DROP_SEQUENCE)
                createTable()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }

    }

    private fun createTable() {
        println(CREATE_TABLE)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statement from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE)
                st.execute(CREATE_SEQUENCE)
                //st.execute(CREATE_TRIGGER)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun insert(book: Books) {
        println(INSERT)
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT).use { st ->
                st.setString(1, book.book_id)
                st.setString(2, book.author)
                st.setString(3, book.title)
                st.setString(4, book.genre)
                st.setFloat(5, book.price)
                st.setString(6, book.publishdate)
                st.setString(7, book.description)
                println(st)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun select(book_id: String): Books {
        var book = Books("","","","",0F,"","")
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_BYID).use { st ->
                st.setString(1, book_id)
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val author = rs.getString("author")
                    val title = rs.getString("title")
                    val genre = rs.getString("genre")
                    val price = rs.getFloat("price")
                    val publishdate = rs.getString("publish_date")
                    val description = rs.getString("description")
                    book = Books(book_id, author, title, genre, price, publishdate, description)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return book
    }

    fun selectAll(): List<Books> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val books: MutableList<Books> = ArrayList()
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_ALL).use { st ->
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getString("book_id")
                    val autor = rs.getString("author")
                    val title = rs.getString("title")
                    val genre = rs.getString("genre")
                    val price = rs.getFloat("price")
                    val publishdate = rs.getString("publish_date")
                    val description = rs.getString("description")
                    books.add(Books(id, autor, title, genre, price, publishdate, description))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return books
    }

    fun delete(id: String): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE).use { st ->
                st.setString(1, id)
                rowDeleted = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowDeleted
    }

    fun update(book_id: String): Boolean {
        var rowUpdated = false
        var book = select(book_id)
        try {
            c.prepareStatement(UPDATE).use { st ->
                st.setString(1, book.author)
                st.setString(2, book.title)
                st.setString(3, book.genre)
                st.setFloat(4, book.price)
                st.setString(5, book.publishdate)
                st.setString(6, book.description)
                st.setString(7,book.book_id)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }

    private fun printSQLException(ex: SQLException) {
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


}