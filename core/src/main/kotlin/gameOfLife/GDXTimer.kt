package gameOfLife
/**
 * @param goBeyondEnd basically tells the code to not loop back to 0 or do weird stuff it the tick_timer has rung, however it will cause problems with proper tracking of time as it will be clamped to a max.
 * needs to be manually activated after initialisation
 */
data class GDXTimer(var startAt: Float = 0f, var action: ((GDXTimer?)->Any?)? = null,
                    var end : Float, var callOnDisable:  ((GDXTimer?)->Any?)? = null,
                    var loop:Boolean = false,
                    var reduceWhileDisabled: Boolean = false,
                    var goBeyondEnd: Boolean = false,
                    var countDown: Boolean = false, )
{
    var hasSurpassedOnceInOverflowMode = false
    var reduceCoEfficient = 1f
    var currentTime : Float = startAt
    init {
        if(countDown) currentTime = startAt
    }
    /**
     * get Progress in 0.0 to 1.0
     */
    fun getProgress() : Float
    {
        return if(!countDown)currentTime/end else 1-(currentTime/startAt)
    }
    private var _active = false
    var active
        get() = _active
        set(value) {if(value) activate() else disable()}
    val isActive get() = active
    fun update(delta: Float): GDXTimer
    {
        if(!active && !reduceWhileDisabled) return this
        if(!countDown)
        {
            if (!active && reduceWhileDisabled) {
                currentTime -= delta * reduceCoEfficient
                currentTime = currentTime.coerceAtLeast(0f)
                return this
            }
            currentTime += delta
            if (currentTime >= end) {
                if (!goBeyondEnd && currentTime >= end && !loop) { // this part is a boolean logic hell
                    currentTime = end
                    if(!hasSurpassedOnceInOverflowMode)
                    {
                        hasSurpassedOnceInOverflowMode = true
                        action?.invoke(this)
                    }
                    return this
                }
                if (!loop && !goBeyondEnd) {
                    disable()
                }
                else if(loop)
                    currentTime %= end

                action?.invoke(this) // could be changed to repeat((currentTime / end).ToInt())
            }
            else
            {
                hasSurpassedOnceInOverflowMode = false // this thing works
            }
        }
        else
        {
            // startAt > end
            if (!active && reduceWhileDisabled) {
                currentTime += delta * reduceCoEfficient
                currentTime = currentTime.coerceAtMost(startAt)
                return this
            }
            currentTime -= delta
            if (currentTime <= end) {
                if (!goBeyondEnd && currentTime <= end) {
                    currentTime = end
                    if(!hasSurpassedOnceInOverflowMode)
                    {
                        hasSurpassedOnceInOverflowMode = true
                        action?.invoke(this)
                    }
                    return this
                }

                if (!loop && !goBeyondEnd) {
                    disable()
                    currentTime = end
                }
                else if(loop) currentTime =  startAt + currentTime // currentTime is always negative
                action?.invoke(this) // could be changed to repeat((currentTime / end).ToInt())
            }
            else
            {
                hasSurpassedOnceInOverflowMode = false
            }
        }
        return this
    }
    fun surpassed() : Boolean
    {
        if(!countDown) return currentTime >= end
        else return currentTime <= end
    }
    fun reset():GDXTimer
    {
        currentTime = startAt
        hasSurpassedOnceInOverflowMode = false
        return this
    }

    /**
     * inverse for countdown
     * for countdowndont use if you wanna set it to a time later than the start time
     * and vice versa for countup
     */
    fun decrease(amount: Float)
    {
        if(!countDown)
        {
            currentTime -= amount
            currentTime = currentTime.coerceAtLeast(0f)
        }
        else
        {
            currentTime += amount
            currentTime = currentTime.coerceAtMost(startAt)
        }
    }
    /**
     * inverse for countdown
     * for countdown  dont use if you wanna set it to a time earlier than the start time
     * and vice versa for normal countUp
     */
    fun increase(amount: Float)
    {
        if(!countDown)
        {
            currentTime += amount
            currentTime = currentTime.coerceAtMost(end)
        }
        else
        {
            currentTime -= amount
            currentTime = currentTime.coerceAtLeast(startAt)
        }
    }
    fun activate():GDXTimer{_active = true; return this}
    fun disable():GDXTimer{_active = false; callOnDisable?.invoke(this); return this}
}
data class GDXTimerButSelfInLambda(var startAt: Float = 0f, var action: ((GDXTimerButSelfInLambda)->Any?)? = null,
                    var end : Float, var callOnDisable:  ((GDXTimerButSelfInLambda)->Any?)? = null,
                    var loop:Boolean = false,
                    var reduceWhileDisabled: Boolean = false,
                    var goBeyondEnd: Boolean = false,
                    var countDown: Boolean = false, )
{
    var hasSurpassedOnceInOverflowMode = false
    var reduceCoEfficient = 1f
    var currentTime : Float = startAt
    init {
        if(countDown) currentTime = startAt
    }
    /**
     * get Progress in 0.0 to 1.0
     */
    fun getProgress() : Float
    {
        return if(!countDown)currentTime/end else 1-(currentTime/startAt)
    }
    private var _active = false
    var active
        get() = _active
        set(value) {if(value) activate() else disable()}
    val isActive get() = active
    fun update(delta: Float): GDXTimerButSelfInLambda
    {
        if(!active && !reduceWhileDisabled) return this
        if(!countDown)
        {
            if (!active && reduceWhileDisabled) {
                currentTime -= delta * reduceCoEfficient
                currentTime = currentTime.coerceAtLeast(0f)
                return this
            }
            currentTime += delta
            if (currentTime >= end) {
                if (!goBeyondEnd && currentTime >= end && !loop) { // this part is a boolean logic hell
                    currentTime = end // to avoid occurrences where the program might think this is the normal case
                    if(!hasSurpassedOnceInOverflowMode)
                    {
                        hasSurpassedOnceInOverflowMode = true
                        action?.invoke(this)
                    }
                    return this
                }
                if (!loop && !goBeyondEnd) {
                    disable()
                }
                else if(loop)
                    currentTime %= end

                action?.invoke(this) // could be changed to repeat((currentTime / end).ToInt())
            }
            else
            {
                hasSurpassedOnceInOverflowMode = false // this thing works
            }
        }
        else
        {
            // startAt > end
            if (!active && reduceWhileDisabled) {
                currentTime += delta * reduceCoEfficient
                currentTime = currentTime.coerceAtMost(startAt)
                return this
            }
            currentTime -= delta
            if (currentTime <= end) {
                if (!goBeyondEnd && currentTime <= end) {
                    currentTime = end // to avoid occurrences where the program might think this is the normal case
                    if(!hasSurpassedOnceInOverflowMode)
                    {
                        hasSurpassedOnceInOverflowMode = true
                        action?.invoke(this)
                    }
                    return this
                }

                if (!loop && !goBeyondEnd) {
                    disable()
                    currentTime = end
                }
                else if(loop) currentTime =  startAt + currentTime // currentTime is always negative
                action?.invoke(this) // could be changed to repeat((currentTime / end).ToInt())
            }
            else
            {
                hasSurpassedOnceInOverflowMode = false
            }
        }
        return this
    }
    fun surpassed() : Boolean
    {
        if(!countDown) return currentTime >= end
        else return currentTime <= end
    }
    fun reset():GDXTimerButSelfInLambda
    {
        currentTime = startAt
        hasSurpassedOnceInOverflowMode = false
        return this
    }

    /**
     * inverse for countdown
     * for countdowndont use if you wanna set it to a time later than the start time
     * and vice versa for countup
     */
    fun decrease(amount: Float)
    {
        if(!countDown)
        {
            currentTime -= amount
            currentTime = currentTime.coerceAtLeast(0f)
        }
        else
        {
            currentTime += amount
            currentTime = currentTime.coerceAtMost(startAt)
        }
    }
    /**
     * inverse for countdown
     * for countdown  dont use if you wanna set it to a time earlier than the start time
     * and vice versa for normal countUp
     */
    fun increase(amount: Float)
    {
        if(!countDown)
        {
            currentTime += amount
            currentTime = currentTime.coerceAtMost(end)
        }
        else
        {
            currentTime -= amount
            currentTime = currentTime.coerceAtLeast(startAt)
        }
    }
    fun activate():GDXTimerButSelfInLambda{_active = true; return this}
    fun disable():GDXTimerButSelfInLambda{_active = false; callOnDisable?.invoke(this); return this}
}
