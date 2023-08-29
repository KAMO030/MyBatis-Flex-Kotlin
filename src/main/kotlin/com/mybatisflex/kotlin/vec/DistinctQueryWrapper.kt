package com.mybatisflex.kotlin.vec

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.flexStream.QueryFunctions

/**
 * 去重的QueryWrapper。
 *
 * 注意：该功能目前是实验性的。
 *
 * 目前尚不能保证它在任何时候生成的sql语句（例如调用[toSQL]时，调用BaseMapper中查询的相关方法生成的sql时）都一定会去重。
 *
 * 它去重的原理相当简单：使用匿名类重写ArrayList，并在这个匿名类进行初始化时向其添加一个[QueryFunctions.distinct]，以表示占位符。
 * 因为在默认情况下，如果我们没有显式的指明列，执行查询时将查询所有列。为了在此种情况下也能保证去重，故添加一个占位符用于在这种情况下查询所有列。
 *
 * 我们使用了一个boolean变量removed来监测原本的占位符是否被删除。在向这个匿名类添加[QueryColumn]时，它将进行一次判断来确定原本的占位符被删除，
 * 然后判断是否为空，如果为空，那么向其添加去重的[QueryColumn]来保证去重，如果不为空，由于已去重，故可以直接添加。
 * @see QueryWrapper
 */
@ExperimentalDistinct
class DistinctQueryWrapper : QueryWrapper() {
    init {
        selectColumns = object : ArrayList<QueryColumn>() {
            init {
                super.add(QueryFunctions.distinct(QueryColumn("*")))
            }

            private var removed = false

            override fun add(element: QueryColumn): Boolean {
                if (!removed) {
                    removed = true
                    removeAt(0)
                }
                return if (isEmpty()) super.add(QueryFunctions.distinct(element))
                else super.add(element)
            }

            override fun add(index: Int, element: QueryColumn) {
                if (!removed) {
                    removed = true
                    removeAt(0)
                }
                if (isEmpty()) super.add(index, QueryFunctions.distinct(element))
                else super.add(index, element)
            }

            override fun addAll(elements: Collection<QueryColumn>): Boolean {
                if (elements.isEmpty()) return super.addAll(elements)
                if (!removed) {
                    removed = true
                    removeAt(0)
                }
                if (isEmpty()) add(QueryFunctions.distinct(elements.first()))
                return super.addAll(elements.drop(1))
            }
        }
    }
}