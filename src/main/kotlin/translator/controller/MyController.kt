package translator.controller

import javafx.collections.ObservableList
import tornadofx.*
import translator.model.Token
import translator.token_analyser.TokenAnalyser
import utils.DataReceiver
import java.util.*

class MyController : Controller() {
    val dataReceiver = DataReceiver()
    val tokenAnalyser = TokenAnalyser()

    fun recentTokens(): ObservableList<Token> = tokenAnalyser.tokenArray.observable()
    fun getIdentifiers(): ObservableList<Token> {
        val res = ArrayList<Token>()
        tokenAnalyser.identifiersTable.forEach { s, i -> res.add(Token(s, i, 0)) }
        return res.observable()
    }
    fun getNumbers(): ObservableList<Token> {
        val res = ArrayList<Token>()
        tokenAnalyser.numbersTable.forEach { s, i -> res.add(Token(s, i, 0)) }
        return res.observable()
    }
}

