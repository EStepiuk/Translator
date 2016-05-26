package translator.generator

import translator.utils.Tree
import java.io.File
import translator.syntax_analyser.SyntaxAnalyser.Const
import translator.token_analyser.Constants
import java.util.*

class Generator(val filename: String,
                val tree: Tree<Int>,
                val identifiers: HashMap<String, Int>,
                val numbers: HashMap<String, Int>) {

    val constants = Constants()
    var loop_c = 0

    fun findNumber(id: Int): String {
        for (k in numbers.keys)
            if (numbers[k] == id) return k
        return ""
    }

    fun writeLine(s: String) {
        File("$filename.asm").appendText(s + "\n")
    }

    fun clearFile() {
        File("$filename.asm").printWriter().print("")
    }

    fun generate() {
        clearFile()
        val block_node = tree[0][3]
        var vars = false

        if (!block_node[0].isLeaf() && !block_node[0][1].isLeaf()) {
                writeLine("data segment")
                generateVarList(block_node[0][1])
                writeLine("data ends\n")
                vars = true
        }

        writeLine("code segment\nstart:")
        writeLine(buildString {
            append("    assume cs:code")
            if (vars) append(", ds:data\n    mov ax,data\n    mov ds,ax")
        })
        if (!block_node[2].isLeaf()) generateStatementsList(block_node[2])
        writeLine("    mov ax, 4c00h\n    int 21h\ncode ends\nend begin")
    }

    private fun generateStatementsList(list_n: Tree<Int>) {
        val s_n = list_n[0]
        if (s_n[0].data == constants.keywordsTable["LOOP"]) {
            val cur_loop_n = loop_c++
            writeLine("loop$cur_loop_n:")
            generateStatementsList(s_n[1])
            writeLine("    jmp loop$cur_loop_n")
        } else {
            if (s_n[2][0].data == Const.UNSIGNED_INTEGER) writeLine("    mov ax, ${findNumber(s_n[2][0][0].data!!)}")
            else if (s_n[2][0][1].isLeaf()) writeLine("    mov ax, v${s_n[2][0][0][0][0].data}")
            else {
                generateIndex(s_n[2][0][1][1], "cx")
                writeLine("    mov ax, v${s_n[2][0][1].data}[cx]")
            }

            if (s_n[0][1].isLeaf()) writeLine("    mov v${s_n[0][0][0][0].data}, ax")
            else {
                generateIndex(s_n[0][1][1], "bx")
                writeLine("    mov v${s_n[0][0][0][0].data}[bx], ax")
            }
        }

        if (!list_n[1].isLeaf()) generateStatementsList(list_n[1])
    }

    private fun generateIndex(exp_n: Tree<Int>, s: String) {
        if (exp_n[0].data == Const.UNSIGNED_INTEGER) writeLine("    mov $s, ${findNumber(exp_n[0][0].data!!)}")
        else if (exp_n[0][1].isLeaf()) writeLine("    mov $s, v${exp_n[0][0][0][0].data}")
        else {
            generateIndex(exp_n[0][1][1], s)
            writeLine("   mov $s, v${exp_n[0][0][0][0].data}[$s]")
        }
    }

    fun generateVarList(list_n: Tree <Int>) {
        val dec_n = list_n[0]
        writeLine(buildString {
            append("    v" + dec_n[0][0][0].data + " ")
            when (dec_n[2][0].data) {
                constants.keywordsTable["INTEGER"] -> append("dw ?")
                constants.keywordsTable["FLOAT"] -> append("dd ?")
                else -> {
                    append("db ")
                    //Тут запись длинны
                    append("dup (?)")
                }
            }
        })
        if (!list_n[1].isLeaf()) generateVarList(list_n[1])
    }
}