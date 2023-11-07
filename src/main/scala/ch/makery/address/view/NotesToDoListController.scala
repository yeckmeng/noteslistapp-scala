package ch.makery.address.view

import ch.makery.address.model.Notes
import ch.makery.address.MainApp
import scalafx.scene.control.{Alert, TableColumn, TableView}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.TableCell
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.TextField

@sfxml
class NotesToDoListController(
                             private val notesTable: TableView[Notes],
                             private val notesColumn: TableColumn[Notes, String],
                             private val descriptionColumn: TableColumn[Notes, String],
                             private val doneColumn: TableColumn[Notes, String],
                             private val priorityColumn: TableColumn[Notes,String],
                             private val searchField: TextField

                             ) {

  private def showNotesDetails(notes: Option[Notes]): Unit = {
    notes match {
      case Some(notes) =>
        notesTable.selectionModel().select(notes)

      case None =>
        notesTable.selectionModel().clearSelection()
    }
  }

  notesTable.items = MainApp.notesData
  notesColumn.cellValueFactory = {
    _.value.title
  }

  doneColumn.cellValueFactory = { cellData =>
    cellData.value.markDone.asString
  }
  doneColumn.cellFactory = { _ =>
    new TableCell[Notes, String] {
      item.onChange { (_, _, newValue) =>
        if (newValue != null) {
          text = if (newValue == "true") "Done" else "Not Done"
        } else {
          text = ""
        }
      }
    }
  }

  descriptionColumn.cellValueFactory = {
    _.value.description
  }

  priorityColumn.cellValueFactory = { cellData =>
    cellData.value.priority
  }

  priorityColumn.cellFactory = { _ =>
    new TableCell[Notes, String] {
      item.onChange { (_, _, newValue) =>
        if (newValue != null) {
          text = newValue match {
            case "Low" => "~"
            case "Medium" => "!!"
            case "High" => "!!!"
            case _ => ""
          }
        } else {
          text = ""
        }
      }
    }
  }

  showNotesDetails(None);

  notesTable.selectionModel().selectedItem.onChange(
    (_, _, newValue) => showNotesDetails(Some(newValue))
  )

  private var filteredNotes: ObservableBuffer[Notes] = ObservableBuffer(MainApp.notesData)

  private def updateFilteredNotes(): Unit = {
    val searchText = searchField.text.value.toLowerCase()

    filteredNotes = ObservableBuffer(MainApp.notesData.filter { note =>
      note.title.value.toLowerCase.contains(searchText) ||
        note.description.value.toLowerCase.contains(searchText)
    })

    notesTable.items = filteredNotes
  }

  def handleDeleteNotes(action: ActionEvent): Unit = {
    val selectedIndex = notesTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
      val selectedNote = notesTable.selectionModel().selectedItem.value
      MainApp.notesData.remove(selectedNote)
      filteredNotes.remove(selectedNote)
      selectedNote.delete()
    } else {
      val alert = new Alert(AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Notes Selected"
        contentText = "Please select a note in the table."
      }
      alert.showAndWait()
    }
  }

  def handleNewNotes(action: ActionEvent): Unit = {
    val isMarkDone: Boolean = false
    val notes = new Notes("", isMarkDone)
    val okClicked = MainApp.showNotesToDoEdit(notes)
    if (okClicked) {
      val result = notes.save()
      if (result.isSuccess) {
        MainApp.notesData += notes
        filteredNotes.add(notes)
      }
    }
  }

  def handleEditNotes(action: ActionEvent): Unit = {
    val selectedNotes = notesTable.selectionModel().selectedItem.value
    if (selectedNotes != null) {
      val okClicked = MainApp.showNotesToDoEdit(selectedNotes)

      if (okClicked) {
        showNotesDetails(Some(selectedNotes))
        selectedNotes.save()
      }

    } else {
      val alert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Notes Selected"
        contentText = "Please select a note in the table."
      }
      alert.showAndWait()
    }
  }
  def handleSearch(event: ActionEvent): Unit = {
    updateFilteredNotes()
  }
}
