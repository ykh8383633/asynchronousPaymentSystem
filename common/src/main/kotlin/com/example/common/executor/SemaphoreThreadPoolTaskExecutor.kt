package com.example.common.executor

import com.example.common.lock.ResizeableSemaphore
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Semaphore


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

    fun addPermits(permits: Int): Int {
        var next = _semaphore.permits + permits

        if(next < 0){
            next = 0
        }

        return _semaphore.resize(next)
    }
}