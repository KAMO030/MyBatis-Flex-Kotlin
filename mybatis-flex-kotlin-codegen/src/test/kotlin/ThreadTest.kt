import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.math.max

object ThreadTest {
    @Test
    fun pool() {
        val pool = Executors.newFixedThreadPool(10)
    }

    @Test
    fun latestLottery() {
        val random = Random()
        val vec = arrayListOf(10, 5, 20, 50, 100, 200, 500, 800, 2, 80, 300, 700)
        val task = {
            val res = ArrayList<Int>()
            while (vec.size != 0) {
                val rand = random.nextInt(0, vec.size)
                synchronized(Unit) {
                    if (vec.size > rand) {
                        res.add(vec.removeAt(rand))
                    }
                }
            }
            res
        }

        val tasks = ArrayList<FutureTask<ArrayList<Int>>>()
        tasks.add(FutureTask(task))
        tasks.add(FutureTask(task))
        tasks.forEach {
            Thread(it).start()
        }
        Thread.sleep(1000)
        println(tasks.maxOf { it.get().maxOf { num -> num } })
    }

    @Test
    fun lottery() {
        val random = Random()
        val vec = arrayListOf(10, 5, 20, 50, 100, 200, 500, 800, 2, 80, 300, 700)
        fun inner(string: String) {
            while (vec.size != 0) {
                val rand = random.nextInt(0, vec.size)
                synchronized(Unit) {
                    if (vec.size > rand) {
                        println("${string}奖池，奖金：${vec.removeAt(rand)}")
                    }
                }
            }
        }
        thread { inner("一") }
        thread { inner("二") }
    }

    @Test
    fun newLottery() {
        val random = Random()
        val vec = arrayListOf(10, 5, 20, 50, 100, 200, 500, 800, 2, 80, 300, 700)
        fun inner(string: String) {
            val al = ArrayList<Int>()
            while (vec.size != 0) {
                val rand = random.nextInt(0, vec.size)
                synchronized(Unit) {
                    if (vec.size > rand) {
                        al.add(vec.removeAt(rand))
                    }
                }
                Thread.sleep(1000)
            }
            var maxVal = 0
            var sumVal = 0
            al.forEach {
                maxVal = max(maxVal, it)
                sumVal += it
            }
            println("${string}号奖池，最大值：${maxVal}，总和：${sumVal}，值：${al}")
        }
        thread { inner("一") }
        thread { inner("二") }
    }

    @Test
    fun hongBao() {
        val random = Random()
        var hongBao = 100
        var submit = 3
        fun inner(str: String) {
            var got = false
            var sum = 0
            while (submit > 0 && !got) {
                synchronized(Unit) {
                    if (submit > 1) {
                        val rand = random.nextInt(1, 100)
                        if (rand >= hongBao) {
                            return@synchronized
                        } else {
                            sum += rand
                            hongBao -= rand
                        }
                    } else if (submit == 1) {
                        sum += hongBao
                        hongBao = 0
                    }
                    submit -= 1
                    got = true
                }
            }
            if (sum != 0) println("${str}号红包：${sum}") else println("${str}号没抢到红包")
        }
        thread { inner("一") }
        thread { inner("二") }
        thread { inner("三") }
        thread { inner("四") }
        thread { inner("五") }
    }

    @Test
    fun sum() {
        val array = (1..100).toList()
        var index = 0
        thread(name = "线程一") {
            while (index < 100) {
                synchronized(Unit::class.java) {
                    val idx = ++index
                    if (idx >= array.size) return@synchronized
                    if (array[idx] % 2 != 0) println("${Thread.currentThread()}: ${array[idx]}")
                }
            }
        }
        thread(name = "线程二") {
            while (index < 100) {
                synchronized(Unit::class.java) {
                    val idx = ++index
                    if (idx >= array.size) return@synchronized
                    if (array[idx] % 2 != 0) println("${Thread.currentThread()}: ${array[idx]}")
                }
            }
        }
    }

    @Test
    fun gift() {
        val gifts = AtomicInteger(100)
        val t1 = thread {
            while (gifts.get() > 10) {
                println("第一个：${gifts.getAndDecrement()}")
            }
        }
        val t2 = thread {
            while (gifts.get() > 10) {
                println("第二个：${gifts.getAndDecrement()}")
            }
        }
        t1.join()
        t2.join()
    }

    class AbqCook(private val abq: ArrayBlockingQueue<Int>) : Runnable {
        override fun run() {
            while (true) {
                abq.put(114514)
                println("放了一碗面")
                Thread.sleep(1000)
            }
        }
    }

    class AbqEater(private val abq: ArrayBlockingQueue<Int>) : Runnable {
        override fun run() {
            while (true) {
                abq.take()
                println("吃了一碗面")
                Thread.sleep(100)
            }
        }
    }

    @Test
    fun flowOfNoodles() = runBlocking {
        flow {
            var i = 1
            while (i <= 10) {
                emit(i)
                i += 1
                delay(1000)
            }
        }.collect {
            println("吃了第${it}碗面！")
            delay(100)
            println("没有面吃了！")
        }
    }

    @Test
    fun atomic() {
        val atomicInt = AtomicInteger(100)
        val t1 = thread {
            while (atomicInt.get() > 0) {
                println("一号窗口，剩余票数：${atomicInt.getAndDecrement()}")
            }
        }
        val t2 = thread {
            while (atomicInt.get() > 0) {
                println("二号窗口，剩余票数：${atomicInt.getAndDecrement()}")
            }
        }
        val t3 = thread {
            while (atomicInt.get() > 0) {
                println("三号窗口，剩余票数：${atomicInt.getAndDecrement()}")
            }
        }
        t1.join()
        t2.join()
        t3.join()
        println(atomicInt.get())
    }

    @Test
    fun async() {
        var atomicInt = 100
        val t1 = thread {
            while (true) {
                synchronized(Unit) {
                    if (atomicInt <= 0) return@thread
                    println("一号窗口，剩余票数：${atomicInt--}")
                }
                Thread.sleep(10)
            }
        }
        val t2 = thread {
            while (true) {
                synchronized(Unit) {
                    if (atomicInt <= 0) return@thread
                    println("二号窗口，剩余票数：${atomicInt--}")
                }
                Thread.sleep(10)
            }
        }
        val t3 = thread {
            while (true) {
                synchronized(Unit) {
                    if (atomicInt <= 0) return@thread
                    println("三号窗口，剩余票数：${atomicInt--}")
                }
                Thread.sleep(10)
            }
        }
        t1.join()
        t2.join()
        t3.join()
        println(atomicInt)
    }
}