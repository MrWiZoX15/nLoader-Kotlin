package club.reaper.loader.accessor

import java.lang.reflect.Field

class FieldAccess(private val field: Field) {

    constructor(target: Class<*>, name: String) : this(target.getDeclaredField(name).apply { isAccessible = true })

    @Suppress("UNCHECKED_CAST")
    fun <T> read(instance: Any): T = field.get(instance) as T

    fun <T> set(instance: Any, value: T) { field.set(instance, value) }
}
