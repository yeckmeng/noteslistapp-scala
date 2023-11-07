package ch.makery.address.util

import scalikejdbc._
import ch.makery.address.model.Notes


trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;"
  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "me", "mine")

  implicit val session = AutoSession
}

object Database extends Database {
  def setupDB() = {
    if (!hasDBInitialize)
      Notes.initializeTable()
  }

  def hasDBInitialize: Boolean = {

    DB getTable "Notes" match {
      case Some(x) => true
      case None => false
    }

  }
}
