package translator.token_analyser

import translator.token_analyser.Constants
import translator.model.Token
import translator.token_analyser.Constants.CommentSymbol.*
import utils.DataReceiver
import java.util.*

/**
 * Created by dnt on 3/23/16.
 */
class TokenAnalyser{

    var lastIdentifierCode = 1000
    var lastNumbersCode = 700

    val constants = Constants()
    val tokenArray = ArrayList<Token>()
    val identifiersTable = HashMap<String, Int>()
    val numbersTable = HashMap<String, Int>()
    var commentFlag = false

    fun createWord(input: ArrayList<Char>): String = buildString {
        while (!input.isEmpty() && (input[0] in constants.letters || input[0] in constants.digits))
            append(input.removeAt(0))
    }

    fun isCommentSymbol(line: ArrayList<Char>, symbol: String): Boolean{
        val builder = StringBuilder();
        builder.append(line.removeAt(0));
        if (!line.isEmpty()){
            builder.append(line.removeAt(0));
            if(symbol == builder.toString())
                return true;

            line.add(0, builder[1]);
            line.add(0, builder[0]);
            return false;
        }
        line.add(0, builder[0]);
        return false;
    }

    fun deleteComment(line: ArrayList<Char>) {
        var endCom = false;
        while (!line.isEmpty()){
            if(isCommentSymbol(line, CLOSE.value)){
                endCom = true;
                break;
            }
            line.removeAt(0);
        }
        if (endCom) commentFlag = false;
    }

    fun separateLine(input: String, line: Int) {
        val charArray = ArrayList<Char>()
        input.forEach { charArray.add(it) }

        if (commentFlag) deleteComment(charArray)

        loop@ while (!charArray.isEmpty()) {

            when (charArray[0]) {

                in constants.whitespaces -> charArray.removeAt(0)

                in constants.letters -> {
                    val word = createWord(charArray)
                    if (word in constants.keywordsTable)
                        tokenArray.add(Token(word, constants.keywordsTable[word] as Int, line))
                    else {
                        if (word !in identifiersTable)
                            identifiersTable.put(word, ++lastIdentifierCode)
                        tokenArray.add(Token(word, identifiersTable[word] as Int, line))
                    }
                }

                in constants.digits -> {
                    val word = createWord(charArray)
                    for (c in word) if (c !in constants.digits) {
                        tokenArray.add(Token(word, -1, line))
                        continue@loop
                    }
                    if (word !in numbersTable)
                        numbersTable.put(word, ++lastNumbersCode)
                    tokenArray.add(Token(word, numbersTable[word] as Int, line))
                }

                else -> {

                    commentFlag = isCommentSymbol(charArray, OPEN.value)
                    if (commentFlag) {
                        deleteComment(charArray)
                        continue@loop
                    }

                    if (charArray.size > 1) {
                        val chars = buildString {
                            append(charArray.removeAt(0))
                            append(charArray.removeAt(0))
                        }
                        if (chars in constants.twoSymbolDelimiters) {
                            tokenArray.add(Token(chars, constants.twoSymbolDelimiters[chars] as Int, line))
                            continue@loop
                        }
                        else chars.forEachIndexed { i, c -> charArray.add(i, c) }
                    }
                    val c = charArray.removeAt(0)
                    if (c !in constants.oneSymbolDelimiters)
                        tokenArray.add(Token(c.toString(), -1, line))
                    else
                        tokenArray.add(Token(c.toString(),
                                constants.oneSymbolDelimiters[c] as Int, line))
                }
            }
        }
    }

    fun analyse(dataReceiver: DataReceiver) {
        identifiersTable.clear()
        numbersTable.clear()
        lastIdentifierCode = 1000
        lastNumbersCode = 700
        commentFlag = false
        tokenArray.clear()
        dataReceiver.open()
        var str: String? = dataReceiver.bufferedReader!!.readLine()
        var line = 1
        while (str != null) {
            separateLine(str.toUpperCase(), line++)
            str = dataReceiver.bufferedReader!!.readLine()
        }
        dataReceiver.close()
    }

}
