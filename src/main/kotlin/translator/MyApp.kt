package translator

import javafx.application.Application
import tornadofx.App
import tornadofx.importStylesheet
import translator.generator.Generator
import translator.syntax_analyser.SyntaxAnalyser
import translator.token_analyser.TokenAnalyser
import translator.utils.Tree
import translator.view.MainView
import utils.DataReceiver

class MyApp: App() {
    override val primaryView = MainView::class

    init {
        importStylesheet("/app.css")
    }
}

infix fun String.mul(t: Int): String = buildString { for (i in 0..t) append(this@mul) }
fun leafStr(id: Int): String {
    when (id) {
        -1 -> return "SIGNAL_PROGRAM"
        -2 -> return "PROGRAM"
        -3 -> return "BLOCK"
        -4 -> return "DECLARATIONS"
        -5 -> return "VARIABLE_DECLARATIONS"
        -6 -> return "DECLARATIONS_LIST"
        -7 -> return "DIMENSION"
        -8 -> return "VARIABLE_IDENTIFIER"
        -9 -> return "VARIABLE"
        -10 -> return "STATEMENTS_LIST"
        -11 -> return "STATEMENT"
        -12 -> return "PROCEDURE_IDENTIFIER"
        -13 -> return "IDENTIFIER"
        -14 -> return "UNSIGNED_INTEGER"
        -15 -> return "ATTRIBUTE"
        -16 -> return "RANGE"
        -17 -> return "EXPRESSION"
        else -> return id.toString()
    }
}

fun printTree(node: Tree<Int>, branch_l: Int) {
    println(("|" mul branch_l) + leafStr(node.data!!))
    if (!node.isLeaf()) node.children.forEach { printTree(it, branch_l + 3) }
}

fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
//    val ta = TokenAnalyser()
//    val data_receiver = DataReceiver()
//    data_receiver.fileName = "/home/dnt/IdeaProjects/1/test.txt"
//    ta.analyse(data_receiver)
//    val sa = SyntaxAnalyser(ta.tokenArray, ta.identifiersTable, ta.numbersTable)
//    println(sa.parse() + "\n")
//    printTree(sa.tree, 3)
//    val gen = Generator(data_receiver.fileName, sa.tree, ta.identifiersTable, ta.numbersTable)
//    gen.generate()
}