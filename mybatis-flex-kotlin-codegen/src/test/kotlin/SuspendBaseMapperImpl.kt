import com.mybatisflex.core.BaseMapper
import com.mybatisflex.kotlin.extensions.db.mapper

@JvmInline
value class SuspendBaseMapperImpl<T>(private val baseMapper: BaseMapper<T> = mapper()) : BaseMapper<T> by baseMapper, Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        println("The exception is $e, happened in $t")
    }
}