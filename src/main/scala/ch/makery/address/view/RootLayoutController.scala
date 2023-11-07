package ch.makery.address.view

import ch.makery.address.MainApp
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml


@sfxml
class RootLayoutController() {
  def handleClose(): Unit = {
    System.exit(0)
  }

  def handleDelete(action: ActionEvent): Unit = {

    MainApp.overallController match {
      case Some(control) =>
        control.handleDeleteNotes(action)
      case None =>

    }
  }
}