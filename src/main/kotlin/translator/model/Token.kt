package translator.model

import tornadofx.*
import javax.json.JsonObject

class Token{
    var tokenName by property<String>()
    fun tokenNameProperty() = getProperty(Token::tokenName)

    var tokenId by property<Int>()
    fun tokenIdProperty() = getProperty(Token::tokenId)

    var lineNumber by property<Int>()
    fun lineNumberProperty() = getProperty(Token::lineNumber)

    constructor(name: String, id: Int, l: Int) {
        tokenName = name
        tokenId = id
        lineNumber = l
    }
}

