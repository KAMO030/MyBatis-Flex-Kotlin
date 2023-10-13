package com.mybatisflex.kotlin.test

import com.mybatisflex.core.paginate.Page
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.kproperty.eq
import com.mybatisflex.kotlin.scope.buildBootstrap
import com.mybatisflex.kotlin.test.entity.emp.Emp
import org.junit.jupiter.api.Test


class QueryChainTest {
    init {
        buildBootstrap {
            it.addMapper(Emp::class.java)
        }.start()
    }

    @Test
    fun chain() {
        val wrapper = com.mybatisflex.core.query.QueryChain.of(Emp::class.java)
            .select(Emp::id.column())
            .from(Emp::class.java)
            .where(Emp::id eq 1)
            .page(Page(1, 10))
        println(wrapper.records)
    }
}