package translator.utils

import java.util.*

class Tree<T> {
    var children = ArrayList<Tree<T>>()
    var parent: Tree<T>? = null
    var data: T? = null

    constructor(data: T) {
        this.data = data
    }

    constructor(data: T, parent: Tree<T>) {
        this.data = data
        this.parent = parent
    }

    operator fun get(i: Int): Tree<T> = children[i]

    fun addChild(data: T): Tree<T> {
        val child = Tree(data)
        child.parent = this
        this.children.add(child)
        return child
    }

    fun addChild(child: Tree<T>) {
        child.parent = this
        this.children.add(child)
    }

    fun isRoot() = this.parent == null

    fun isLeaf() = this.children.size == 0

    fun removeParent() {
        this.parent = null
    }
}
