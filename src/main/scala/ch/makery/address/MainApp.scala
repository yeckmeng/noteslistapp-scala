package ch.makery.address

import ch.makery.address.model.Notes
import ch.makery.address.util.Database
import ch.makery.address.view.{NotesToDoListController, NotesToDoListEditController}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import scalafx.stage.{Modality, Stage}


object MainApp extends JFXApp {
  Database.setupDB()

  val notesData = new ObservableBuffer[Notes]()

  notesData ++= Notes.getAllNotes

  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load();
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  stage = new PrimaryStage {
    title = "To Do List"
    scene = new Scene {
      stylesheets += getClass.getResource ("view/ThemeCSS.css").toString()
      root = roots
    }
  }

  var overallController: Option[NotesToDoListController#Controller] = None
  def showNotesToDoList() = {
    val resource = getClass.getResource("view/NotesToDoList.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    val control = loader.getController[NotesToDoListController#Controller]()
    overallController = Option(control)
    this.roots.setCenter(roots)
  }

  def showMainMenu() = {
    val resource = getClass.getResource("view/MainMenu.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showNotesToDoEdit(notes: Notes): Boolean = {
    val resource = getClass.getResourceAsStream("view/NotesToDoListEdit.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[NotesToDoListEditController#Controller]

    val dialog = new Stage() {
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      scene = new Scene {
        stylesheets += getClass.getResource("view/ThemeCSS.css").toString
        root = roots2
      }
    }
    control.dialogStage = dialog
    control.notes = notes
    dialog.showAndWait()
    control.okClicked
  }

  showMainMenu()
}
