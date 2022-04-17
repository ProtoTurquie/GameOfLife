package gameOfLife

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch

fun Int.SignIndependentModulo(mod: Int): Int {
    return (this % mod + mod) % mod
}

interface PlatformSpecific
{
    fun sayHello()
    fun centerWindow()
    fun setWindowPos(x: Int, y: Int)
    fun getWindowPos(): Pair<Int, Int>
    fun setWindowMaximised()
    fun setWindowUnMaximised()
    fun isMaximised(): Boolean
    fun minimiseWindow()
}

fun checkAABB(x1: Float, y1: Float, w1: Float, h1: Float, x2: Float, y2: Float, w2: Float, h2: Float) : Boolean
{
    return (x1 < x2 + w2 &&
            x1 + w1 > x2 &&
            y1 < y2 + h2 &&
            y1 + h1 > y2)
}

fun hexToColor(hex: Int) : Color
{
    val r = (hex shr 16 and 0xFF) / 255f
    val g = (hex shr 8 and 0xFF) / 255f
    val b = (hex and 0xFF) / 255f
    return Color(r, g, b, 1f)
}
class sImplement : Sprite, CustomHotbar.ImplementsDraw
{
    constructor(texture: Texture) : super(texture)

    override fun drawImplementation(a: SpriteBatch, x: Float, y: Float, w: Float, h: Float) {
        a.draw(this, x, y, w, h)
    }
}
data class DoStuffOnMouseUp<T>(var variableInitial: T,
                               var downAction : ((DoStuffOnMouseUp<T>) -> Unit)? = null,
                               var upAction : ((DoStuffOnMouseUp<T>) -> Unit)? = null,
                               var callDownOnceUntilMouseUp : Boolean = true
)
{
    var value: T
    init
    {
        value = variableInitial
        doStuffOnMouseUpList.add(this as DoStuffOnMouseUp<Any?>)
    }
    fun up()
    {
        _flag = false
        upAction?.invoke(this)
    }
    fun down()
    {
        if(callDownOnceUntilMouseUp)
        {
            if(!_flag)
            {
                downAction?.invoke(this)
                _flag = true
            }
        }
        else
        {
            downAction?.invoke(this)
        }
    }
    private var _flag = false
}

enum class SetCellOptions{TRUE, FALSE, REVERSE}
fun setCell(inputX: Int, inputY: Int, options: SetCellOptions)
{
    centralScreen.apply {
        _unproject.set(inputX *1f, inputY *1f) // I think I'm premature optimising too much
        _unproject.set(viewport.unproject(_unproject))
        res = game.grid.getElementAt_colrow(
            (_unproject.x / CELL_LENGTH).toInt(),
            (_unproject.y / CELL_LENGTH).toInt() ri game.grid.height, Grid.MutabilityOptions.ignore)
        if(res != null && _unproject.x >= 0 && _unproject.y >= 0)
        {
            game.grid.setElementAt_colrow(
                if(options == SetCellOptions.TRUE) true else if(options == SetCellOptions.FALSE) false else !res!!,
                (_unproject.x / CELL_LENGTH).toInt(),
                (_unproject.y / CELL_LENGTH).toInt() ri game.grid.height, options = Grid.MutabilityOptions.ignore)
        }
    }
}
fun getCell(inputX: Int, inputY: Int) : Boolean
{
    centralScreen.apply {
        _unproject.set(inputX *1f, inputY *1f) // I think I'm premature optimising too much
        _unproject.set(viewport.unproject(_unproject))
        res = game.grid.getElementAt_colrow(
            (_unproject.x / CELL_LENGTH).toInt(),
            (_unproject.y / CELL_LENGTH).toInt() ri game.grid.height, Grid.MutabilityOptions.ignore)
        if(res != null && _unproject.x >= 0 && _unproject.y >= 0)
        {
            return res!!
        }
        return false
    }

}