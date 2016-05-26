package translator.token_analyser

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader

/**
 * Created by dnt on 3/23/16.
 */
class Constants(var lastKeywordCode: Int = 400, var lastDelimitersCode: Int = 500) {

    enum class CommentSymbol(val value: String) {
        OPEN("<*"),
        CLOSE("*>")
    }

    val digits = ('0'..'9')

    val letters = ('A'..'Z')

    val whitespaces = listOf<Char>(
            32.toChar(),
            13.toChar(),
            10.toChar(),
            9.toChar(),
            11.toChar(),
            12.toChar()
    )

    val oneSymbolDelimiters = hashMapOf(
            ';' to ';'.toInt(),
            ':' to ':'.toInt(),
            '[' to '['.toInt(),
            ']' to ']'.toInt(),
            '.' to '.'.toInt()
    )

    val keywordsTable = hashMapOf(
            "BEGIN" to ++lastKeywordCode,
            "END" to ++lastKeywordCode,
            "PROGRAM" to ++lastKeywordCode,
            "VAR" to ++lastKeywordCode,
            "INTEGER" to ++lastKeywordCode,
            "FLOAT" to ++lastKeywordCode,
            "LOOP" to ++lastKeywordCode,
            "ENDLOOP" to ++lastKeywordCode
    )

    val twoSymbolDelimiters = hashMapOf(
            ":=" to ++lastDelimitersCode,
            ".." to ++lastDelimitersCode
    )
}

