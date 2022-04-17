package gameOfLife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics

class DesktopImplementation : PlatformSpecific{
    override fun sayHello() {
        println("hello from desktop!")
    }
    override fun setWindowPos(x: Int, y: Int)
    {
        (Gdx.graphics as Lwjgl3Graphics).window.setPosition(
            x, y)
    }

    override fun centerWindow()
    {
        (Gdx.graphics as Lwjgl3Graphics).window.setPosition(
            Gdx.graphics.width / 2 , Gdx.graphics.height / 2)
    }

    override fun getWindowPos(): Pair<Int, Int> {
        return (Gdx.graphics as Lwjgl3Graphics).window.positionX to
               (Gdx.graphics as Lwjgl3Graphics).window.positionY

    }

    override fun isMaximised() : Boolean
    {
        return (Gdx.graphics as Lwjgl3Graphics).window.positionX == 0 && (Gdx.graphics as Lwjgl3Graphics).displayMode.width == Gdx.graphics.width
    }
    override fun setWindowMaximised() {
        (Gdx.graphics as Lwjgl3Graphics).apply {
            this.window.maximizeWindow()

        }
    }
    override fun setWindowUnMaximised()
    {
        (Gdx.graphics as Lwjgl3Graphics).apply {
            this.window.restoreWindow()
        }
    }
    override fun minimiseWindow()
    {
        (Gdx.graphics as Lwjgl3Graphics).window.iconifyWindow()
    }
}