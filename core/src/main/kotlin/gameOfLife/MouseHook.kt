package gameOfLife

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener

class MouseHook : NativeMouseMotionListener{
    override fun nativeMouseMoved(nativeEvent: NativeMouseEvent?) {
        mouse_lastx = nativeEvent!!.x
        mouse_lasty = nativeEvent.y
    }

    override fun nativeMouseDragged(nativeEvent: NativeMouseEvent?) {
        mouse_lastx = nativeEvent!!.x
        mouse_lasty = nativeEvent.y
    }
}