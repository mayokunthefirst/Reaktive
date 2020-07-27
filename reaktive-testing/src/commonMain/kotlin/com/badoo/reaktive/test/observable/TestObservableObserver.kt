package com.badoo.reaktive.test.observable

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.freeze
import com.badoo.reaktive.utils.isFrozen

class TestObservableObserver<T>(autoFreeze: Boolean = true) : TestObserver(), ObservableObserver<T> {

    private val _values = AtomicReference<MutableList<T>>(ArrayList())
    val values: List<T> get() = _values.value
    private val _isComplete = AtomicBoolean()
    val isComplete: Boolean get() = _isComplete.value

    init {
        if (autoFreeze) {
            freeze()
        }
    }

    override fun onNext(value: T) {
        checkActive()

        if (_values.isFrozen) {
            _values.update { oldList ->
                ArrayList<T>(oldList.size + 1).apply {
                    addAll(oldList)
                    add(value)
                }
            }
        } else {
            _values.value.add(value)
        }
    }

    override fun onComplete() {
        checkActive()

        _isComplete.value = true
    }

    override fun reset() {
        super.reset()

        _values.update { ArrayList() }
        _isComplete.value = false
    }

    override fun checkActive() {
        super.checkActive()

        check(!isComplete) { "Already completed" }
    }
}
