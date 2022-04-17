package gameOfLife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import kotlin.math.*

fun drawui(screen: MainScreen, delta: Float)
{
    screen.apply {
        uiViewport.apply()
        uiBatch.projectionMatrix = uiViewport.camera.combined
        uiBatch.begin()

        customHotbar.draw(uiBatch)
        customHotbar.drawText(uiBatch)
        _heldButtonTemp = customHotbar.getCurrentlyHeldButton(false, true)
        _heldButtonTemp?.draw(uiBatch, _heldButtonTemp!!.heldTexture)
        uiBatch.end()
    }
}
private var _heldButtonTemp : TitleButton? = null
fun draw(screen: MainScreen, delta: Float)
{
    screen.apply {
        ScreenUtils.clear(BACKGROUND_COLOR)

        viewport.apply()
        batch.projectionMatrix = viewport.camera.combined
        renderer.projectionMatrix = viewport.camera.combined

        batch.begin()
        for(row in game.grid.grid.indices)
        {
            for(col in game.grid.grid[row].indices)
            {
                if(game.grid.grid[row][col] &&
                       viewport.camera.frustum.boundsInFrustum
                           (
                           col * CELL_LENGTH, (row ri game.grid.height) * CELL_LENGTH, 0f,
                           CELL_LENGTH/2, CELL_LENGTH/2, 0f
                       )
                   )
                    batch.draw(alive_texture,
                    col * CELL_LENGTH, (row ri game.grid.height) * CELL_LENGTH, CELL_LENGTH, CELL_LENGTH)
            }
        }


        batch.end()


        renderer.begin(ShapeRenderer.ShapeType.Filled)

        for(row in game.grid.grid.indices)
        {
            for(col in game.grid.grid[row].indices)
            {
/*
                renderer.color =
                    if(game.grid.getElementAt_colrow(col, row) == true) Color.WHITE else Color.BLACK
*/
                renderer.color = BACKGROUND_COLOR
                if(!game.grid.grid[row][col]) renderer.rect(col * CELL_LENGTH, (row ri game.grid.height) * CELL_LENGTH, CELL_LENGTH, CELL_LENGTH)
            }
        }

        renderer.end()


    }
}
fun logic(screen: MainScreen, delta: Float)
{
    screen.apply {
        game.game_timer.update(delta)
        customHotbar.doubleClickTimer.update(delta)
        if(KEEP_CAMERA_INBOUNDS)
        {
            setZoomInbounds()
            keepCameraInBounds()
        }
    }
}
data class twoInt(var first: Int, var second: Int)
var _unproject = Vector2() ; var res : Boolean? = false ; var LSHIFT = 1f
fun input(screen: MainScreen, delta: Float)
{
    screen.apply {
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) LSHIFT = shift_multiplier
        if(Gdx.input.isKeyPressed(Input.Keys.A)) viewport.camera.position.x -= delta * mSrelative * LSHIFT
        if(Gdx.input.isKeyPressed(Input.Keys.D)) viewport.camera.position.x += delta * mSrelative * LSHIFT
        if(Gdx.input.isKeyPressed(Input.Keys.W)) viewport.camera.position.y += delta * mSrelative * LSHIFT
        if(Gdx.input.isKeyPressed(Input.Keys.S)) viewport.camera.position.y -= delta * mSrelative * LSHIFT
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) game.randomise(0.2f)
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) game.grid.clear()

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
        {
            if(!dragSpreadState) setCell(Gdx.input.x, Gdx.input.y, SetCellOptions.REVERSE)
        }
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        {
            if(centralScreen.game.holdSpreadStateMultiplexer.value != null && dragSpreadState && !centralScreen.customHotbar.isDraggingCurrently)
                setCell(Gdx.input.x, Gdx.input.y, centralScreen.game.holdSpreadStateMultiplexer.value!!)
        }

    }
}
fun initialiseGameStuff(screen: MainScreen)
{
    screen.apply {
        customHotbar = CustomHotbar(
            sImplement(Texture("title.png")), HOTBAR_HEIGHT, BitmapFont(), "Game Of Life"
        ).apply {
            this.textOffset = Vector2(0.5f, 0.5f)
        }
        customHotbar.buttons.add(
            TitleButton(
                customHotbar,
                sImplement(close_button),
                sImplement(close_button_held),
                { Gdx.app.exit() ; centralScreen.dispose()},
                Vector2(0.92f, 0.1f),
                Vector2(40f, 40f)
            )
        )
        customHotbar.buttons.add(
            TitleButton(
                customHotbar,
                sImplement(toggle_button),
                sImplement(toggle_button_held),
                {
                if(Gdx.graphics.width * Gdx.graphics.height
                    >= Gdx.graphics.displayMode.height * Gdx.graphics.displayMode.width * window_movement_threshold)
                    platformcode.setWindowUnMaximised()
                    else
                    {
                        lastWindowedWidth = Gdx.graphics.width
                        lastWindowedHeight = Gdx.graphics.height
                        platformcode.setWindowMaximised()
                    }
                },
                Vector2(0.85f, 0.1f),
                Vector2(40f, 40f)
            )
        )
        customHotbar.buttons.add(
            TitleButton(
                customHotbar,
                sImplement(minimize_button),
                sImplement(minimize_button_held),
                { platformcode.minimiseWindow()},
                Vector2(0.78f, 0.1f),
                Vector2(40f, 40f)
            )
        )
        viewport.screenY = 100

    }
}
class Handler : InputProcessor
{
    override fun keyDown(keycode: Int): Boolean {
        centralScreen.game.keyDown(keycode)
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        centralScreen.game.keyUp(keycode)
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        doStuffOnMouseUpList.map { it.downAction?.invoke(it) }
        centralScreen.customHotbar.onMouseDown(screenX, screenY)
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        doStuffOnMouseUpList.map { it.upAction?.invoke(it) }
        centralScreen.customHotbar.onMouseUp()
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        centralScreen.customHotbar.onMouseDrag(screenX, screenY)
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        centralScreen.customHotbar.doubleClickTimer.disable().reset()
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        // I came up with these with the help of complete trial and error
        var unprj =
            uiViewport.unproject(Vector2(Gdx.input.x*1f, Gdx.input.y*1f))
            .sub(uiViewport.worldWidth/2f, uiViewport.worldHeight/2f) // make middle origin point
            .scl(1/(uiViewport.worldWidth/2f), 1/(uiViewport.worldHeight/2f)) // take distance from origin (0.0 - 1.0)
        unprj = Vector2(cos(unprj.angleRad()), sin(unprj.angleRad())) // convert the distance from rectangle to circle
            .scl(unprj.len()) // since that outputs in a 0-1 we need to also apply the length of the line
        (viewport.camera as OrthographicCamera).zoom += amountY.sign * Gdx.graphics.deltaTime * zoom_multiplier
        (viewport.camera as OrthographicCamera)
            .zoom = max((viewport.camera as OrthographicCamera).zoom, max_zoom)
        if((viewport.camera as OrthographicCamera).zoom < 0.40f) return false
        if(amountY <= 0)
        {
            viewport.camera.position.add(
                viewport.worldWidth * unprj.x / scroll_movement_inverse_coefficient,
                viewport.worldHeight * unprj.y / scroll_movement_inverse_coefficient,
                0f
            )
        }
        else
        {
            viewport.camera.position.sub(
                viewport.worldWidth * unprj.x / scroll_movement_inverse_coefficient,
                viewport.worldHeight * unprj.y / scroll_movement_inverse_coefficient,
                0f
            )
        }

        return false
    }

}

fun keepCameraInBounds() // though, if the ratio of w/H doesn't match windowW/windowH it won't keep inbounds
{
    centralScreen.apply{
        var zoom  = (viewport.camera as OrthographicCamera).zoom
        try
        {
            viewport.camera.position.x = viewport.camera.position.x.coerceIn(
                (game.grid.width * CELL_LENGTH) - viewport.worldWidth/2 * zoom,
                viewport.worldWidth/2 *  zoom
            )
        }catch (e: Exception)
        {
            viewport.camera.position.x = viewport.camera.position.x.coerceIn(
                viewport.worldWidth/2 *  zoom,
                (game.grid.width * CELL_LENGTH) - viewport.worldWidth/2 * zoom
            )
        }
        try
        {
            viewport.camera.position.y = viewport.camera.position.y.coerceIn(
                (game.grid.height * CELL_LENGTH) - viewport.worldHeight/2 * zoom,
                viewport.worldHeight/2 * zoom
            )
        }catch (e: Exception)
        {
            viewport.camera.position.y = viewport.camera.position.y.coerceIn(
                viewport.worldHeight/2 * zoom,
                (game.grid.height * CELL_LENGTH) - viewport.worldHeight/2 * zoom
            )
        }

    }

}
fun setZoomInbounds()
{
    centralScreen.apply {
        (viewport.camera as OrthographicCamera).zoom =
            (viewport.camera as OrthographicCamera).zoom.coerceAtMost(
                (game.grid.width * CELL_LENGTH) / viewport.worldWidth
            )
    }
}