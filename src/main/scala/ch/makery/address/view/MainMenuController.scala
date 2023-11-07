package ch.makery.address.view

import ch.makery.address.MainApp
import scalafxml.core.macros.sfxml


@sfxml
class MainMenuController() {
  def checkNotes(): Unit = {
    MainApp.showNotesToDoList()
  }
}