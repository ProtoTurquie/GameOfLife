package gameOfLife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

class CustomHotbar {
    interface ImplementsDraw{fun drawImplementation(a: SpriteBatch, x: Float, y: Float, w: Float, h: Float)}
    var implementsDraw: ImplementsDraw? = null
    enum class Orientation{
        Top, Bottom, Left, Right
    }
    var doubleClickTimer : GDXTimer   // needs to be updated every frame
    var DoubleClickMaxInterval = 0.20f
    set(value) {
        field = value
        doubleClickTimer.end = value
    }

    var currentOrientation : Orientation = Orientation.Top
    var screenViewport : Viewport = uiViewport
    var height = 10f // in screen-viewport units
    var font: BitmapFont
    var textOffset = Vector2(0f, 0f) // multiplicitive
    var textsize = GlyphLayout()
    var titleText = ""
        get() = field
        set(value) {
            field = value
            textsize.setText(font, value)
        }

    private var begin_drag_pos = 0 to 0 ; var isDraggingCurrently = false
    var heldButton : TitleButton? = null
    var _lastHeldButton : TitleButton? = null
    var buttons = mutableListOf<TitleButton>()
    private var _blockedUntilMouseUp = false
    private var _TouchedButtonWhenFirstPressed = false
    private var _lastRect = Rectangle()


    constructor(
        patch: ImplementsDraw?, height : Float, font: BitmapFont, titleText: String = "", screenViewport : Viewport = uiViewport, textOffset : Vector2 = Vector2(1f, 1f))
    {
        doubleClickTimer = GDXTimer(0f, {it!!.reset().disable() }, DoubleClickMaxInterval, null,
            false, false, false, false)
        this.implementsDraw = patch
        this.height = height
        this.font = font // needs to be above titleText cuz uninitializedaccess stuff
        this.titleText = titleText
        this.screenViewport = screenViewport
        this.textOffset = textOffset
    }
    val boundingBox : Rectangle
    get()
    {
        _lastRect = when(currentOrientation) {
            Orientation.Top -> {
                Rectangle(
                    0f, screenViewport.worldHeight - height, screenViewport.worldWidth, height)
            }
            Orientation.Bottom -> {
                Rectangle(
                    0f, 0f, screenViewport.worldWidth, height
                )
            }
            Orientation.Left -> {
                Rectangle(
                    0f, 0f, height, screenViewport.worldHeight
                )
            }
            Orientation.Right -> {
                Rectangle(
                    screenViewport.worldWidth - height, 0f, height, screenViewport.worldHeight
                )
            }
        }
        return _lastRect
    }
    fun getCurrentlyHeldButton(
        holdDownShouldHaveStartedInsideButton : Boolean = false,
        shouldntHaveLeftBoundsOnce : Boolean = false
    ) : TitleButton?
    {
        if((!_TouchedButtonWhenFirstPressed && holdDownShouldHaveStartedInsideButton)) return null
        if(heldButton?.checkAABB(Gdx.input.x, Gdx.input.y) == true)
            return heldButton
        return null
    }
    fun onMouseDown(screenX: Int, screenY: Int)
    {
        if(doubleClickTimer.active)
        {
            doubleClickTimer.disable().reset()
            if(Gdx.graphics.width * Gdx.graphics.height
                >= Gdx.graphics.displayMode.height * Gdx.graphics.displayMode.width * window_movement_threshold)
                platformcode.setWindowUnMaximised()
            else
            {
                lastWindowedWidth = Gdx.graphics.width
                lastWindowedHeight = Gdx.graphics.height
                platformcode.setWindowMaximised()
            }
        }
        else doubleClickTimer.activate()

        for(button in buttons) button.mousePressed(screenX, screenY)
        _TouchedButtonWhenFirstPressed = heldButton != null
        var _w = Gdx.graphics.width / screenViewport.worldWidth ; var _h = Gdx.graphics.height / screenViewport.worldHeight

        // if it doesn't start inside the titlebar it's blocked
        _blockedUntilMouseUp = !checkAABB(
            screenX *1f,
            Gdx.graphics.height - screenY*1f, 0f, 0f,
            _lastRect.x * _w, _lastRect.y * _h, _lastRect.width * _w, _lastRect.height * _h
        )
        if(heldButton != null) _blockedUntilMouseUp = true


    }

    fun onMouseDrag(screenX: Int, screenY: Int) : Boolean
    {
        if(heldButton != null) return false
        if(_blockedUntilMouseUp) return false
        var _w = Gdx.graphics.width / screenViewport.worldWidth ; var _h = Gdx.graphics.height / screenViewport.worldHeight
        boundingBox
        if(isDraggingCurrently)
        {
            platformcode.setWindowPos(
                mouse_lastx - begin_drag_pos.x(),
                mouse_lasty - begin_drag_pos.y()
            )
        }
        if(!checkAABB(
                screenX *1f,
                Gdx.graphics.height - screenY*1f, 0f, 0f,
                _lastRect.x * _w, _lastRect.y * _h, _lastRect.width * _w, _lastRect.height * _h
        )) return true



        if(!isDraggingCurrently)
        {
            isDraggingCurrently = true
            begin_drag_pos = screenX to screenY
            onMouseDrag(screenX, screenY)
        }
        return true
    }
    fun onMouseUp()
    {
        for (button in buttons)
        {
            button.mouseUp()
        }
        _lastHeldButton = heldButton
        heldButton = null
        _blockedUntilMouseUp = true
        isDraggingCurrently = false

    }
    fun draw(batch: SpriteBatch)
    {
        boundingBox //recache
        implementsDraw?.drawImplementation(
            batch, _lastRect.x, _lastRect.y, _lastRect.width, _lastRect.height
        )
        for(i in buttons.indices.reversed())
            buttons[i].draw(batch)
    }
    fun drawText(batch: SpriteBatch)
    {
        if(!(currentOrientation == Orientation.Top || currentOrientation == Orientation.Bottom)) return
        boundingBox //recache
        font.draw(
            batch,
            titleText,
            _lastRect.x + ( _lastRect.width * textOffset.x) -textsize.width/2,
            _lastRect.y + (_lastRect.height * textOffset.y) +textsize.height/2
        )
    }
    fun Pair<Int, Int>.x() = this.first
    fun Pair<Int, Int>.y() = this.second
}
class TitleButton
{
    var hotbar: CustomHotbar
    var heldTexture : CustomHotbar.ImplementsDraw? = null
    var texture: CustomHotbar.ImplementsDraw
    var clickAction: (() -> Unit)? = null
    var offset: Vector2 = Vector2(0f, 0f)
    var size: Vector2 = Vector2(0f, 0f)
    constructor(
        hotbar: CustomHotbar,
        texture: CustomHotbar.ImplementsDraw,
        heldTexture: CustomHotbar.ImplementsDraw? = null,
        clickAction: (() -> Unit)? = null,
        offset: Vector2 = Vector2(0f, 0f),
        size: Vector2 = Vector2(1f, 1f)
    )
    {
        this.heldTexture = heldTexture
        this.hotbar = hotbar
        this.texture = texture
        this.clickAction = clickAction
        this.offset = offset
        this.size = size
    }
    private var _lastBounds = Rectangle()
    val getBounds : Rectangle
    get() {
        _lastBounds = Rectangle(
            hotbar.boundingBox.x + (hotbar.boundingBox.width * offset.x),
            hotbar.boundingBox.y + (hotbar.boundingBox.height * offset.y),
            size.x,
            size.y
        )
        return _lastBounds
    }
    fun draw(batch: SpriteBatch, overrideTexture : CustomHotbar.ImplementsDraw? = null)
    {
        getBounds //recache
        (overrideTexture ?: texture).drawImplementation(
            batch,
            _lastBounds.x,
            _lastBounds.y,
            _lastBounds.width,
            _lastBounds.height
        )
    }
    fun mousePressed(screenX: Int, screenY: Int)
    {

        var _w = Gdx.graphics.width / hotbar.screenViewport.worldWidth
        var _h = Gdx.graphics.height / hotbar.screenViewport.worldHeight
        getBounds

        if (     checkAABB(
                screenX *1f,
                Gdx.graphics.height - screenY*1f, 0f, 0f,
                _lastBounds.x * _w, _lastBounds.y * _h,
                _lastBounds.width * _w, _lastBounds.height * _h)
            &&
                hotbar.heldButton == null)
        {
            hotbar.heldButton = this
        }

    }
    fun mouseUp()
    {
        var _w = Gdx.graphics.width / hotbar.screenViewport.worldWidth
        var _h = Gdx.graphics.height / hotbar.screenViewport.worldHeight

        if(hotbar.heldButton == this)
        {
            hotbar._lastHeldButton = hotbar.heldButton
            hotbar.heldButton = null
            if(checkAABB(
                    Gdx.input.x *1f,
                    Gdx.graphics.height - Gdx.input.y*1f, 0f, 0f,
                    _lastBounds.x * _w, _lastBounds.y * _h,
                    _lastBounds.width * _w, _lastBounds.height * _h)
            )
            {
                clickAction?.invoke()
            }
        }

    }
    fun checkAABB(pointX: Int, PointY: Int) : Boolean
    {
        var _w = Gdx.graphics.width / hotbar.screenViewport.worldWidth
        var _h = Gdx.graphics.height / hotbar.screenViewport.worldHeight
        return checkAABB(
            pointX *1f,
            Gdx.graphics.height - PointY*1f, 0f, 0f,
            _lastBounds.x * _w, _lastBounds.y * _h,
            _lastBounds.width * _w, _lastBounds.height * _h)
    }

}