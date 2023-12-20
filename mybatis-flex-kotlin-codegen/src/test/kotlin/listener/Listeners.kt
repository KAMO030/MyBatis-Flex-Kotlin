package listener

import com.mybatisflex.annotation.InsertListener
import com.mybatisflex.annotation.UpdateListener
import com.mybatisflex.core.FlexGlobalConfig

inline fun <reified T : Any> onInsert(noinline listener: (T) -> Unit): InsertListener {
    val insertListener = InsertListener {
        listener(it as T)
    }
    FlexGlobalConfig.getDefaultConfig().registerInsertListener(insertListener, T::class.java)
    return insertListener
}

inline fun <reified T : Any> onUpdate(noinline listener: (T) -> Unit): UpdateListener {
    val updateListener = UpdateListener {
        listener(it as T)
    }
    FlexGlobalConfig.getDefaultConfig().registerUpdateListener(updateListener, T::class.java)
    return updateListener
}
