package translator.view

import translator.controller.MyController
import javafx.scene.layout.BorderPane
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import tornadofx.*
import translator.model.Token
import translator.token_analyser.TokenAnalyser

class MainView : View() {
    override val root: BorderPane by fxml()
    val table: TableView<Token> by fxid()
    val identifiers: TableView<Token> by fxid()
    val top: HBox by fxid()
    val controller: MyController by inject()

    init {
        title = messages["title"]

        top.apply {

            button("Select File") {
                setOnAction {
                    controller.dataReceiver.fileName = FileChooser().showOpenDialog(primaryStage).toString()
                }
            }
            button("Analyse") {
                setOnAction {
                    with(controller) {
                        tokenAnalyser.analyse(dataReceiver)

                        // Load data from the controller
                        table.items = recentTokens()
                        identifiers.items = getIdentifiers()
                    }
                }
            }
        }

        with (table) {
            // Create table columns and bind to the data model
            column(messages["token"], Token::tokenNameProperty).prefWidth = 200.0
            column(messages["id"], Token::tokenIdProperty).prefWidth = 100.0
        }

        with (identifiers) {
            column(messages["identifier"], Token::tokenNameProperty).prefWidth = 200.0
            column(messages["id"], Token::tokenIdProperty).prefWidth = 100.0
        }

    }

}