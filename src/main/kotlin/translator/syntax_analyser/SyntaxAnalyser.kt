package translator.syntax_analyser

import translator.model.Token
import translator.token_analyser.Constants
import translator.utils.Tree
import java.util.*

class SyntaxAnalyser {

    object Const {
        val SIGNAL_PROGRAM = -1;
        val PROGRAM = -2;
        val BLOCK = -3;
        val DECLARATION = -4;
        val VARIABLE_DECLARATIONS = -5;
        val DECLARATIONS_LIST = -6;
        val DIMENSION = -7;
        val VARIABLE_IDENTIFIER = -8;
        val VARIABLE = -9;
        val STATEMENTS_LIST = -10;
        val STATEMENT = -11;
        val PROCEDURE_IDENTIFIER = -12;
        val IDENTIFIER = -13;
        val UNSIGNED_INTEGER = -14;
        val ATTRIBUTE = -15
        val RANGE = -16
        val EXPRESSION = -17
    }

    val tree = Tree(Const.SIGNAL_PROGRAM)
    val errors = ArrayList<String>()
    val constants = Constants()
    val identifiers = HashMap<String, Int?>()

    var errorInLine = -1
    var tokens: ArrayList<Token>
    var identifiersTable: HashMap<String, Int>
    var numbersTable: HashMap<String, Int>

    constructor(tokens: ArrayList<Token>,
                identifiersTable: HashMap<String, Int>,
                numbersTable: HashMap<String, Int>) {

        this.tokens = tokens
        this.identifiersTable = identifiersTable
        this.numbersTable = numbersTable
        identifiersTable.forEach{ k, v -> identifiers.put(k, null)}
    }

    fun findIdentifier(id: Int): String? {
        for (k in identifiersTable.keys) {
            if (identifiersTable[k] == id) return k
        }
        return null
    }

    fun parse(): Boolean {
        var pointer = 0
        var address = 0
        var end = false
        var error = false

        var buffer: String?
        var cur_node = tree.addChild(Const.PROGRAM)
        var block_node: Tree<Int>? = null
        var last_loops = ArrayList<Tree<Int>>()

        var ep = -1
        for (t in tokens) {
            ep++
            if (t.tokenId == -1) {
                end = true
                error = true
                pointer = ep
            }
        }
        loop@ while (!end) when (address) {

            0 -> {
                /*
                PROGRAM <procedure-identifier> ;
                 */
                if (pointer + 3 > tokens.size) {
                    error = true
                    break@loop;
                }
                if (tokens[pointer].tokenId == constants.keywordsTable["PROGRAM"]){
                    cur_node.addChild(constants.keywordsTable["PROGRAM"]!!)
                    pointer++
                } else {
                    error = true
                    break@loop;
                }
                if (tokens[pointer].tokenId > 1000) {
                    buffer = findIdentifier(tokens[pointer].tokenId)
                    identifiers.remove(buffer)

                    cur_node.addChild(Const.PROCEDURE_IDENTIFIER)
                            .addChild(Const.IDENTIFIER)
                            .addChild(tokens[pointer++].tokenId)
                } else {
                    error = true
                    break@loop;
                }
                if (tokens[pointer++].tokenId == constants.oneSymbolDelimiters[';']) {
                    address = 1
                    cur_node.addChild(constants.oneSymbolDelimiters[';']!!)
                } else {
                    error = true
                    break@loop;
                }
            }

            /*
            <block> --> <variable-declaration> BEGIN ...
            <variable-declaration> --> VAR ...
             */
            1 -> {
                block_node = cur_node.addChild(Const.BLOCK)
                cur_node = block_node
                if (pointer + 1 > tokens.size) {
                    error = true
                    break@loop
                }
                if (tokens[pointer].tokenId == constants.keywordsTable["VAR"]) {
                    cur_node = cur_node
                            .addChild(Const.VARIABLE_DECLARATIONS)
                            .addChild(constants.keywordsTable["VAR"]!!)
                            .parent!!
                            .addChild(Const.DECLARATIONS_LIST)
                    address = 2
                } else if (tokens[pointer].tokenId == constants.keywordsTable["BEGIN"]) {
                    cur_node = cur_node
                            .addChild(Const.VARIABLE_DECLARATIONS).parent!!
                            .addChild(constants.keywordsTable["BEGIN"]!!)
                            .parent!!
                            .addChild(Const.STATEMENTS_LIST)
                    address = 3
                } else {
                    error = true
                    break@loop
                }
                pointer++
            }

            /*
            <declarations-list> --> <declaration><declaration-list> | <empty>
            BEGIN
             */
            2 -> {
                if (tokens[pointer].tokenId == constants.keywordsTable["BEGIN"]) {
                    cur_node = block_node!!
                            .addChild(constants.keywordsTable["BEGIN"]!!)
                            .parent!!
                            .addChild(Const.STATEMENTS_LIST)
                    address = 3
                    pointer++
                } else if (tokens[pointer].tokenId > 1000) {
                    cur_node = cur_node.addChild(Const.DECLARATION)
                    address = 4
                } else {
                    error = true
                    break@loop
                }
            }

            /*
            <statements-list>
             */
            3 -> {
                if (pointer + 2 > tokens.size) {
                    error = true
                    break@loop
                }
                if (last_loops.size == 0 && tokens[pointer].tokenId == constants.keywordsTable["END"] &&
                        tokens[pointer + 1].tokenId == constants.oneSymbolDelimiters['.']) {

                    block_node!!
                            .addChild(tokens[pointer++].tokenId)
                            .parent!!.parent!!
                            .addChild(tokens[pointer++].tokenId)
                    if (pointer < tokens.size) {
                        error = true
                        break@loop
                    }
                    end = true
                } else if (last_loops.size > 0 && tokens[pointer].tokenId == constants.keywordsTable["ENDLOOP"] &&
                        tokens[pointer + 1].tokenId == constants.oneSymbolDelimiters[';']) {

                    cur_node = last_loops.removeAt(last_loops.size - 1)
                            .addChild(tokens[pointer++].tokenId).parent!!
                            .addChild(tokens[pointer++].tokenId).parent!!.parent!!
                            .addChild(Const.STATEMENTS_LIST)
                    address = 3
                } else if (tokens[pointer].tokenId > 1000) {
                    cur_node = cur_node.addChild(Const.STATEMENT)
                    address = 5
                } else if (tokens[pointer].tokenId == constants.keywordsTable["LOOP"]) {
                    cur_node = cur_node
                            .addChild(Const.STATEMENT)
                            .addChild(tokens[pointer++].tokenId).parent!!
                            .addChild(Const.STATEMENTS_LIST)

                    last_loops.add(cur_node.parent!!)
                    address = 3
                } else {
                    error = true
                    break@loop
                }
            }

            /*
            <declaration>
             */
            4 -> {
                if (pointer + 4 > tokens.size) {
                    error = true
                    break@loop
                }
                if (identifiers[tokens[pointer].tokenName] == null) {
                    identifiers[tokens[pointer].tokenName] = tokens[pointer].tokenId
                    cur_node.addChild(Const.VARIABLE_IDENTIFIER)
                            .addChild(Const.IDENTIFIER)
                            .addChild(tokens[pointer++].tokenId)
                } else {
                    error = true
                    break@loop
                }
                if (tokens[pointer].tokenId == constants.oneSymbolDelimiters[':']) {
                    cur_node.addChild(tokens[pointer++].tokenId)
                } else {
                    error = true
                    break@loop
                }
                if (tokens[pointer].tokenId == constants.keywordsTable["INTEGER"] ||
                        tokens[pointer].tokenId == constants.keywordsTable["FLOAT"]) {
                    cur_node.addChild(Const.ATTRIBUTE)
                            .addChild(tokens[pointer++].tokenId)

                } else if (pointer + 5 <= tokens.size &&
                        tokens[pointer].tokenId == constants.oneSymbolDelimiters['['] &&
                        tokens[pointer + 1].tokenId > 700 &&
                        tokens[pointer + 2].tokenId == constants.twoSymbolDelimiters[".."] &&
                        tokens[pointer + 3].tokenId > 700 &&
                        tokens[pointer + 4].tokenId == constants.oneSymbolDelimiters[']']) {

                    cur_node.addChild(Const.ATTRIBUTE)
                            .addChild(tokens[pointer++].tokenId)
                            .parent!!
                            .addChild(Const.RANGE)
                            .addChild(Const.UNSIGNED_INTEGER)
                            .addChild(tokens[pointer++].tokenId)
                            .parent!!.parent!!
                            .addChild(tokens[pointer++].tokenId)
                            .parent!!
                            .addChild(Const.UNSIGNED_INTEGER)
                            .addChild(tokens[pointer++].tokenId)
                            .parent!!.parent!!.parent!!
                            .addChild(tokens[pointer++].tokenId)
                } else {
                    error = true
                    break@loop
                }
                if (tokens[pointer].tokenId == constants.oneSymbolDelimiters[';']) {
                    cur_node = cur_node
                            .addChild(tokens[pointer++].tokenId)
                            .parent!!.parent!!
                            .addChild(Const.DECLARATIONS_LIST)
                    address = 2
                } else {
                    error = true
                    break@loop
                }
            }

            /*
            <variable>
             */
            5 -> {
                if (pointer + 2 > tokens.size) {
                    error = true
                    break@loop
                }
                if (identifiers[tokens[pointer].tokenName] != null) {
                    cur_node = cur_node
                            .addChild(Const.VARIABLE)
                            .addChild(Const.VARIABLE_IDENTIFIER)
                            .addChild(Const.IDENTIFIER)
                            .addChild(tokens[pointer++].tokenId).parent!!.parent!!.parent!!
                            .addChild(Const.DIMENSION)

                    if (tokens[pointer].tokenId == constants.oneSymbolDelimiters['[']) {
                        cur_node.addChild(tokens[pointer++].tokenId)
                        address = 6
                    } else {
                        cur_node = cur_node.parent!!
                        address = 7
                    }
                } else {
                    error = true
                    break@loop
                }
            }

            /*
            <expression>
             */
            6 -> {
                if (pointer + 1 > tokens.size) {
                    error = true
                    break@loop
                }
                if (tokens[pointer].tokenId > 1000) {
                    cur_node = cur_node.addChild(Const.EXPRESSION)
                    address = 5
                } else if (tokens[pointer].tokenId > 700) {
                    cur_node = cur_node.addChild(Const.EXPRESSION)
                            .addChild(Const.UNSIGNED_INTEGER)
                            .addChild(tokens[pointer++].tokenId).parent!!.parent!!
                    address = 8
                }
            }

            /*
            end variable
             */
            7 -> {
                if (pointer + 1 > tokens.size) {
                    error = true
                    break@loop
                }
                cur_node = cur_node.parent!!
                if (cur_node.data == Const.STATEMENT) {
                    if (tokens[pointer].tokenId == constants.twoSymbolDelimiters[":="]) {
                        cur_node.addChild(tokens[pointer++].tokenId)
                        address = 6
                    } else {
                        error = true
                        break@loop
                    }
                } else if (cur_node.data == Const.EXPRESSION) {
                    address = 8
                }
            }

            /*
            end expression
             */
            8 -> {
                if (pointer + 1 > tokens.size) {
                    error = true
                    break@loop
                }
                cur_node = cur_node.parent!!
                if (cur_node.data == Const.STATEMENT) {
                    if (tokens[pointer].tokenId == constants.oneSymbolDelimiters[';']) {
                        cur_node = cur_node
                                .addChild(tokens[pointer++].tokenId)
                                .parent!!.parent!!
                                .addChild(Const.STATEMENTS_LIST)
                        address = 3
                    } else {
                        error = true
                        break@loop
                    }
                } else if (cur_node.data == Const.DIMENSION) {
                    if (tokens[pointer].tokenId == constants.oneSymbolDelimiters[']']) {
                        cur_node = cur_node
                                .addChild(tokens[pointer++].tokenId)
                                .parent!!
                                .parent!!
                        address = 7
                    }
                }
            }
        }

        if (error) {
            errorInLine = tokens[pointer].lineNumber
            return false
        }

        return true
    }
}

infix fun String.mul(t: Int): String = buildString { for (i in 0..t) append(this@mul) }

fun valueKey(map: HashMap<String, Int>, v: Int): String? {
    for (k in map.keys)
        if (map[k] == v) return k
    return null
}

fun lol(map: HashMap<Char, Int>, v: Int): String? {
    for (k in map.keys)
        if (map[k] == v) return k.toString()
    return null
}

val consts = Constants()

fun leafStr(id: Int): String {
    when (id) {
        -1 -> return "<signal-program>"
        -2 -> return "<program>"
        -3 -> return "<block>"
        -4 -> return "<declarations>"
        -5 -> return "<variable-declarations>"
        -6 -> return "<declarations-list>"
        -7 -> return "<dimension>"
        -8 -> return "<variable-identifier>"
        -9 -> return "<variable>"
        -10 -> return "<statements-list>"
        -11 -> return "<statement>"
        -12 -> return "<procedure-identifier>"
        -13 -> return "<identifier>"
        -14 -> return "<unsigned-integer>"
        -15 -> return "<attribute>"
        -16 -> return "<range>"
        -17 -> return "<expression>"
        else -> {
            if (valueKey(consts.keywordsTable, id) != null)
                return valueKey(consts.keywordsTable, id) + "($id)"
            else if (valueKey(consts.twoSymbolDelimiters, id) != null)
                return valueKey(consts.twoSymbolDelimiters, id) + "($id)"
            else if (lol(consts.oneSymbolDelimiters, id) != null)
                return lol(consts.oneSymbolDelimiters, id) + "($id)"
        }
    }
    return "$id"
}

fun treeInText(node: Tree<Int>, offset: Int): String = buildString {
    append((" " mul offset) + leafStr(node.data!!) + "\n")
    if (!node.isLeaf()) node.children.forEach { append(treeInText(it, offset + 13)) }
}
