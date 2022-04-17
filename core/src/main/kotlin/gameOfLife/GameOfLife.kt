package gameOfLife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.viewport.Viewport
import kotlin.random.Random
import kotlin.random.nextUInt

class GameOfLife {
    var grid : Grid<Boolean>
    var _temp : MutableList<MutableList<Boolean>>
    constructor(width: Int = WIDTH, height: Int = HEIGHT) {
        grid = Grid(width, height, false)
        _temp = createEmptyList2D(width, height, false)
    }
    fun plot(x: Int, y: Int, value: Boolean, option : Grid.MutabilityOptions = Grid.MutabilityOptions.throwError) {
        grid.setElementAt(x, y, value)
    }
    var game_timer = GDXTimer(
        0f, {advance()}, generation_advance_time, null, true, false, false, false
            )
    var timerMode = TimerMode.AdvanceOnHold
    enum class TimerMode{AdvanceOnHold, Toggle}
    var holdSpreadStateMultiplexer = DoStuffOnMouseUp<SetCellOptions?>(
        null,
        {
            var x = !getCell(Gdx.input.x, Gdx.input.y)
            it.value = if(reverseOnSpread) SetCellOptions.REVERSE else if(x) SetCellOptions.TRUE else SetCellOptions.FALSE
        },
        {
            it.value = null // null stands for not holding
        }, true
    )

    fun keyDown(keyCode: Int)
    {
        if(keyCode == advance_key)
        {
            when(timerMode)
            {
                TimerMode.Toggle ->
                {
                    if (game_timer.active) game_timer.disable() else game_timer.activate()
                }
                TimerMode.AdvanceOnHold ->
                {
                    game_timer.activate()
                }
            }
        }
    }
    fun keyUp(keyCode: Int)
    {
        if(keyCode == advance_key)
        {
            if(timerMode == TimerMode.AdvanceOnHold)
            {
                game_timer.reset().disable()
            }
        }
    }

    private var _neighbors = 0; private var option = Grid.MutabilityOptions.ignore
    private var _alive = false;
    fun advance(doesWrap : Boolean = WRAP_EDGES_IN_NEIGHBOR_CHECK)
    {
        _temp.resetToDefault(false)
        option = if(doesWrap) Grid.MutabilityOptions.wrap else Grid.MutabilityOptions.ignore
        for(row in grid.grid.indices)
        {
            for(col in grid.grid[row].indices)
            {
                _alive = grid.grid[row][col]
                _temp[row][col] = _alive
                if(grid.getElementAt_colrow(col-1, row-1, option) == true) _neighbors++
                if(grid.getElementAt_colrow(col+0, row-1, option) == true) _neighbors++
                if(grid.getElementAt_colrow(col+1, row-1, option) == true) _neighbors++
                if(grid.getElementAt_colrow(col-1, row+0, option) == true) _neighbors++
                if(grid.getElementAt_colrow(col+1, row+0, option) == true) _neighbors++
                if(grid.getElementAt_colrow(col-1, row+1, option) == true) _neighbors++
                if(grid.getElementAt_colrow(col+0, row+1, option) == true) _neighbors++
                if(grid.getElementAt_colrow(col+1, row+1, option) == true) _neighbors++

                if ((_neighbors < 2 || _neighbors > 3) && _alive) _temp[row][col] = false
                else if(_neighbors == 3 && !_alive) _temp[row][col] = true
                _neighbors = 0
            }
        }
        grid.grid = _temp.copy()
    }

    fun centerCamera(_viewport: Viewport = viewport)
    {
        _viewport.camera.position.set(grid.width* CELL_LENGTH/2f, grid.height* CELL_LENGTH/2f, 0f)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun randomise(chanceOrSeed: Float, method: RandomiseMethod = RandomiseMethod.Chance)
    {
        grid.clear()
        if(method == RandomiseMethod.Chance)
        {
            for(r in grid.grid.indices)
            {
                for(c in grid.grid[r].indices)
                {
                    grid.grid[r][c] = Random.nextUInt((1/chanceOrSeed).toUInt()) == 0u // unsure if this thing is theoratically correct
                }
            }
        }
    }
    enum class RandomiseMethod()
    {
        Chance, Noise
    }

}