package com.example.common.lock

import java.util.concurrent.Semaphore
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs

class ResizeableSemaphore(
    initialPermits: Int,
    fair: Boolean = false
): Semaphore(initialPermits, fair) {
    private var _permits: Int
    private var lock: ReentrantLock = ReentrantLock()

    init {
        _permits = initialPermits
    }

    val permits: Int
        get(){
            return _permits
        }

    fun resize(newPermits: Int): Int {
        lock.lock()
        try{
            val delta = newPermits - _permits
            if(delta == 0) return _permits
            if(delta > 0){
                release(delta)
            }
            else {
                reducePermits(abs(delta))
            }

            _permits += delta
            return _permits
        }
        finally {
            lock.unlock()
        }
    }

    fun executeWithRock(action: () -> Unit){
        try{
            acquire()
            action();
        }
        finally {
            release()
        }
    }

    fun <T> submitWithRock(func: () -> T): T {
        try{
            acquire()
            return func()
        }
        finally {
            release()
        }
    }
}