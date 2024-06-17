package com.example.common.executor

import com.example.common.lock.ResizeableSemaphore
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.Semaphore
import java.util.concurrent.ThreadPoolExecutor


class SemaphoreThreadPoolTaskExecutor(
    initialPermits: Int
): ThreadPoolTaskExecutor() {
    private val _semaphore: ResizeableSemaphore

    init {
        _semaphore = ResizeableSemaphore(initialPermits, true)
    }

    override fun execute(task: Runnable) = _semaphore.executeWithRock {
        super.execute(task)
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> = _semaphore.submitWithRock {
         super.submit(task);
    }

    override fun submit(task: Runnable): Future<*> = _semaphore.submitWithRock {
        super.submit(task)
    }

    fun addPermits(permits: Int): Int {
        var next = _semaphore.permits + permits

        if(next < 0){
            next = 0
        }

        return _semaphore.resize(next)
    }
}