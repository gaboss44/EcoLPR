package com.github.gaboss44.ecolpr.util

import net.luckperms.api.context.ContextSet
import net.luckperms.api.query.QueryOptions

object QueryUtils{
    @JvmStatic
    fun options(vararg strings: String): QueryOptions =
        QueryOptions.contextual(ContextUtils.context(*strings))

    @JvmStatic
    fun options(list: List<String>): QueryOptions =
        QueryOptions.contextual(ContextUtils.context(list))

    @JvmStatic
    fun options(ctx: ContextSet): QueryOptions =
        QueryOptions.contextual(ctx)
}
