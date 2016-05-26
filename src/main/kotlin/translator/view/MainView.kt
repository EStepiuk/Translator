package translator.view

import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
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
import javafx.scene.text.Text
import translator.generator.Generator
import translator.syntax_analyser.SyntaxAnalyser
import translator.syntax_analyser.treeInText

class MainView : View() {
    override val root: BorderPane by fxml()
    val table: TableView<Token> by fxid()
    val select_btn: Button by fxid()
    val process_btn: Button by fxid()
    val identifiers: TableView<Token> by fxid()
    val parse_message: Text by fxid()
    val top: HBox by fxid()
    val tree: Text by fxid()
    val controller: MyController by inject()

    init {
        title = messages["title"]

        top.apply {

            with(select_btn) {
                setOnAction {
                    controller.dataReceiver.fileName = FileChooser().showOpenDialog(primaryStage).toString()
                }
            }
            with(process_btn) {
                setOnAction {
                    with (controller) {
                        tokenAnalyser.analyse(dataReceiver)

                        // Load data from the controller
                        table.items = recentTokens()
                        identifiers.items = getIdentifiers()

                        val sa = SyntaxAnalyser(tokenAnalyser.tokenArray, tokenAnalyser.identifiersTable, tokenAnalyser.numbersTable)
                        sa.parse()
                        tree.text = treeInText(sa.tree, 0)
                        if (sa.errorInLine > 0) {
                            parse_message.text = "  Error in line ${sa.errorInLine}"
                        } else {
                            parse_message.text = "  Complete! OK."
                            val gen = Generator(controller.dataReceiver.fileName, sa.tree, tokenAnalyser.identifiersTable, tokenAnalyser.numbersTable)
                            gen.generate()
                        }
                    }
                }
            }
        }

        with (table) {
            // Create table columns and bind to the data model
            column(messages["token"], Token::tokenNameProperty).prefWidth = 120.0
            column(messages["id"], Token::tokenIdProperty).prefWidth = 70.0
        }

        with (identifiers) {
            column(messages["identifier"], Token::tokenNameProperty).prefWidth = 120.0
            column(messages["id"], Token::tokenIdProperty).prefWidth = 70.0
        }

    }

}