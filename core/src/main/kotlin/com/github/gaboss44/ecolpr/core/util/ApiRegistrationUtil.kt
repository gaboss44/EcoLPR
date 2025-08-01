package com.github.gaboss44.ecolpr.core.util

import com.github.gaboss44.ecolpr.api.EcoLpr
import com.github.gaboss44.ecolpr.api.EcoLprProvider
import java.lang.reflect.Method

object ApiRegistrationUtil {
    private val registerMethod: Method
    private val unregisterMethod: Method

    init {
        try {
            registerMethod = EcoLprProvider::class.java.getDeclaredMethod("register", EcoLpr::class.java)
            registerMethod.isAccessible = true

            unregisterMethod = EcoLprProvider::class.java.getDeclaredMethod("unregister")
            unregisterMethod.isAccessible = true
        } catch (ex: NoSuchMethodException) {
            throw ExceptionInInitializerError(ex)
        }
    }

    @JvmStatic
    fun register(api: EcoLpr) {
        try {
            registerMethod.invoke(EcoLprProvider, api)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun unregister() {
        try {
            unregisterMethod.invoke(EcoLprProvider)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
