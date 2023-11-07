package ch.makery.address.model

import scalafx.beans.property.{StringProperty, BooleanProperty, ObjectProperty}
import java.time.LocalDate
import ch.makery.address.util.Database
import ch.makery.address.util.DateUtil._
import scalikejdbc._
import scala.util.{ Try, Success, Failure }


class Notes(notes: String, isMarkDone: Boolean) {
  var title = new StringProperty(notes)
  var description = new StringProperty("Enter your description..")
  var date = ObjectProperty[LocalDate](LocalDate.of(2000, 4, 18))
  var time = new StringProperty("2:23 AM")
  var markDone = new BooleanProperty()
  markDone.value = isMarkDone
  var priority = new StringProperty("Low")

  def save(): Try[Int] = {
    if (!isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into notes (title, description,
            date, time, markDone, priority) values
            (${title.value}, ${description.value}, ${date.value.asString},
              ${time.value}, ${markDone.value}, ${priority.value})
        """.update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
        update notes
        set
        description   = ${description.value},
        date     = ${date.value.asString},
        time = ${time.value},
        markDone       = ${markDone.value},
        priority       = ${priority.value}
         where title = ${title.value}
        """.update.apply()
      })
    }
  }

  def delete(): Try[Int] = {
    if (isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
        delete from notes where
          title = ${title.value}
        """.update.apply()
      })
    } else {
      Failure(new Exception("Notes does not exist in Database"))
    }
  }

  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
        select * from notes where
        title = ${title.value}
      """.map(rs => rs.string("title")).single.apply().isDefined
    }
  }
}

object Notes extends Database {
  def apply(
             titleS: String,
             descriptionS: String,
             dateS: String,
             timeS: String,
             markDoneS: String,
             priorityS: String
           ): Notes = {
    val note = new Notes(titleS, markDoneS.toBoolean)
    note.description.value = descriptionS
    note.date.value = dateS.parseLocalDate
    note.time.value = timeS
    note.markDone.value = markDoneS.toBoolean
    note.priority.value = priorityS
    note
  }

  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
      create table notes (
        id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
        title varchar(64),
        description varchar(200),
        date varchar(64),
        time varchar(64),
        markDone boolean,
        priority varchar(64)
      )
      """.execute.apply()
    }
  }

  def getAllNotes: List[Notes] = {
    DB readOnly { implicit session =>
      sql"select * from notes".map(rs => Notes(rs.string("title"),
        rs.string("description"), rs.string("date"),
        rs.string("time"), rs.boolean("markDone").toString, rs.string("priority"))).list.apply()
    }
  }
}

