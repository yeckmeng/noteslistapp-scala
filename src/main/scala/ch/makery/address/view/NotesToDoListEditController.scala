package ch.makery.address.view
import ch.makery.address.model.Notes
import ch.makery.address.util.DateUtil
import scalafx.scene.control.{Alert, TextField, CheckBox}
import scalafxml.core.macros.sfxml
import scalafx.event.ActionEvent
import scalafx.stage.Stage
import ch.makery.address.util.DateUtil._
import scalafx.scene.control.ChoiceBox
import scalafx.collections.ObservableBuffer


@sfxml
class NotesToDoListEditController(
                                   private val  titleField: TextField,
                                   private val  descriptionField: TextField,
                                   private val  dateField: TextField,
                                   private val  timeField: TextField,
                                   private val markDoneField: CheckBox,
                                   private val priorityChoiceBox: ChoiceBox[String]
                                 ){
  var dialogStage: Stage  = null
  private var _notes: Notes = null
  var okClicked = false

  def notes = _notes
  def notes_=(x: Notes): Unit = {
    _notes = x

    titleField.text = _notes.title.value
    descriptionField.text = _notes.description.value
    dateField.text = _notes.date.value.asString
    timeField.text = _notes.time.value
    markDoneField.selected = _notes.markDone.value
    val priorityChoices = ObservableBuffer("Low", "Medium", "High")
    priorityChoiceBox.items = priorityChoices
    priorityChoiceBox.selectionModel().select(_notes.priority.value)

    dateField.setPromptText(DateUtil.DATE_PATTERN)
  }

  def handleOk(action: ActionEvent): Unit = {
    if (isInputValid()) {
      _notes.title.value = titleField.text.value
      _notes.description.value = descriptionField.text.value
      _notes.date.value = dateField.text.value.parseLocalDate
      _notes.time.value = timeField.text.value
      _notes.markDone.value = markDoneField.selected.value
      _notes.priority.value = priorityChoiceBox.getSelectionModel.getSelectedItem

      okClicked = true
      dialogStage.close()
    }
  }

  def handleCancel(action: ActionEvent): Unit = {
    dialogStage.close()
  }

  def nullChecking(x: String): Boolean = x == null || x.length == 0

  def isInputValid(): Boolean = {
    var errorMessage = ""

    if (nullChecking(titleField.text.value))
      errorMessage += "No valid title!\n"

    if (nullChecking(descriptionField.text.value))
      errorMessage += "No valid description!\n"

    if (nullChecking(dateField.text.value))
      errorMessage += "No valid date!\n"
    else {
      if (!dateField.text.value.isValid) {
        errorMessage += s"No valid date. Use the format ${DateUtil.DATE_PATTERN}!\n"
      }
    }

    if (nullChecking(timeField.text.value))
      errorMessage += "No valid time!\n"
    else {
      val timeRegex = """^(1[012]|[1-9]):[0-5][0-9](\s?[APap][Mm])?$""".r
      if (!timeField.text.value.matches(timeRegex.regex)) {
        errorMessage += "No valid time. Use the format HH:mm AM/PM!\n"
      }
    }

    if (errorMessage.length() == 0) {
      true
    } else {
      val alert = new Alert(Alert.AlertType.Error) {
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please fix invalid fields"
        contentText = errorMessage
      }
      alert.showAndWait()

      false
    }
  }
}
