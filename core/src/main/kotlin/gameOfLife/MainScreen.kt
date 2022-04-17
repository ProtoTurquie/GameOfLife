package gameOfLife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.github.kwhat.jnativehook.GlobalScreen
import kotlin.system.exitProcess


class MainScreen : Screen
{

    lateinit var game : GameOfLife
    var multiplexer = InputMultiplexer(Handler()).apply {
        Gdx.input.inputProcessor = this
    }
    lateinit var customHotbar : CustomHotbar
    override fun show() {

        initialiseGlobalVariables()
        initialiseGameStuff(this)
        try{
            GlobalScreen.registerNativeHook()}catch (e: Exception){
            exitProcess(1)
        }
        GlobalScreen.addNativeMouseMotionListener(
            MouseHook()
        )

        game = GameOfLife(WIDTH, HEIGHT)
        game.centerCamera()

    }
    override fun render(delta: Float) {

        input(this, delta)
        logic(this, delta)
        draw(this, delta)
        drawui(this, delta)

    }
    /*
    TODO:   put the window movement and maybe the resize into another thread, get local mouse coords with
            coords - windowpos don't forget to add thread.sleep(1/1000f) that so the cpu usage
            isn't 20% per core
     */
    override fun dispose() {
        GlobalScreen.unregisterNativeHook()

    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
        viewport.setScreenBounds(0, 0, Gdx.graphics.width, Gdx.graphics.height - HOTBAR_HEIGHT.toInt())
        uiViewport.update(width, height, true)
    }
    override fun hide() {
    }
}