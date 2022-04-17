package gameOfLife


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.*
import kotlin.math.sqrt

var BACKGROUND_COLOR = hexToColor(0x282C34) // or 0x2C384E
var WIDTH = 100; var HEIGHT = 100 // setting w*h>1mil will crash your pc
var CELL_LENGTH = 10f
var WRAP_EDGES_IN_NEIGHBOR_CHECK = false
var KEEP_CAMERA_INBOUNDS = true
var generation_advance_time = 1/10f
const val HOTBAR_HEIGHT = 45f
var window_movement_threshold = 0.40f
var dragSpreadState = true
var reverseOnSpread = false // very broken
var max_zoom = 0.25f
var shift_multiplier = 2f

var advance_key = Input.Keys.SPACE

var mouse_lastx = 0 ; var mouse_lasty = 0
private var _lastValidLocalXHookPos = 0
val hook_mouse_lastX_local  : Int
get() {
    if(mouse_lastx -platformcode.getWindowPos().first >= 0)
        _lastValidLocalXHookPos = mouse_lastx -platformcode.getWindowPos().first
    return  _lastValidLocalXHookPos
}
private var _lastValidLocalYHookPos = 0
val hook_mouse_lastY_local : Int
get() {
    if(mouse_lasty - platformcode.getWindowPos().second >= 0)
    {
        _lastValidLocalYHookPos = mouse_lasty - platformcode.getWindowPos().second
    }
    return _lastValidLocalYHookPos
}

var doStuffOnMouseUpList = mutableListOf<DoStuffOnMouseUp<Any?>>()

var base_speed_persec = 1f // for a map size of 1x1
var zoom_multiplier = 15f
var scroll_movement_inverse_coefficient = 100/15f
private var _wCache = 0 ; private var _hCache = 0 ; private var _last = 1f //^^ 100(base)/zoom_multiplier
val mSrelative : Float
get() {
    if(_wCache == WIDTH && _hCache == HEIGHT ) return _last
    else
    {
        _last = sqrt(WIDTH* WIDTH + HEIGHT* HEIGHT *1f).coerceAtLeast(250f) * base_speed_persec
        _wCache = WIDTH ; _hCache = HEIGHT
        return _last
    }
}

lateinit var close_button : Texture ; lateinit var close_button_held : Texture
lateinit var minimize_button : Texture ; lateinit var minimize_button_held : Texture
lateinit var toggle_button : Texture ;  lateinit var toggle_button_held : Texture

lateinit var alive_texture : Texture ; lateinit var dead_texture : Texture
lateinit var platformcode : PlatformSpecific

lateinit var renderer : ShapeRenderer
lateinit var uiViewport : Viewport
lateinit var uiBatch : SpriteBatch
lateinit var viewport : ExtendViewport
lateinit var batch : SpriteBatch
lateinit var font : BitmapFont
fun initialiseGlobalVariables()
{
    close_button = Texture("close.png") ; close_button_held = Texture("close_held.png")
    minimize_button = Texture("minimise.png") ; minimize_button_held = Texture("minimise_held.png")
    toggle_button = Texture("togglefs.png") ; toggle_button_held = Texture("togglefs_held.png")

    alive_texture = Texture("alive.png") ;  dead_texture = Texture("dead.png")
    renderer = ShapeRenderer()
    uiViewport = StretchViewport(640f, 480f)
    uiBatch = SpriteBatch()
    viewport = ExtendViewport(WIDTH * 1f, HEIGHT * 1f)
        .apply {
            this.setScreenBounds(0, 0, WIDTH, Gdx.graphics.height - HOTBAR_HEIGHT.toInt())
        }
    batch = SpriteBatch()
    font = BitmapFont()
}
lateinit var centralScreen : MainScreen

var lastWindowedWidth = 0 ; var lastWindowedHeight = 0