package com.mybatisflex.kotlin.extensions.tableDef

import com.mybatisflex.core.query.QueryTable
import com.mybatisflex.core.table.TableDef

operator fun TableDef.component1(): String? {
    return schema
}

operator fun TableDef.component2(): String {
    return tableName
}

operator fun QueryTable.component1(): String? {
    return schema
}

operator fun QueryTable.component2(): String {
    return name
}

operator fun QueryTable.component3(): String? {
    return alias
}
