package gameOfLife


/**
 *     this Thing's grid's elements are in the notation of grid[y][x] which is grid[row][column]
 *     so the grid.size = Height, grid[0].size = Width
 *
 *     you can access the the elements by either using the xy or the colrow versions of the functions
 *
 *
 */

class Grid<T> {
    val width
    get() = grid[0].size
    val height
    get() = grid.size
    var default: T
    var grid : MutableList<MutableList<T>>

    constructor(reference: MutableList<MutableList<T>>, default: T) {
        grid = reference
        this.default = default
    }
    constructor(width: Int, height: Int, default: T)
    {
        //             as in rows        as in columns
        if(width < 1 || height < 1)
            throw IllegalArgumentException("width and height must be positive")
        grid = MutableList(height) { MutableList(width) { default } }
        this.default = default
    }
    fun isOvershoot(x: Int, y: Int) : Boolean // is plane agnostic (i think)
    {
        // I thought I'd switch the width and height but then i realized that i already do it behind
        // the doors
        if (     x < 0 || y < 0 ||
            x >= width ||  y >= height)
            return true
        return false
    }
    fun getElementAt_colrow(col: Int, row: Int, options: MutabilityOptions = MutabilityOptions.throwError) : T?
    {
        if (isOvershoot(col, row))
        {   // attention I've converted this to colrow but i didn't change the checks
            if(options == MutabilityOptions.throwError) throw IndexOutOfBoundsException("col: $col, row: $row")
            if(options == MutabilityOptions.clamp) return getElementAt_colrow(col.coerceIn(0, width - 1), row.coerceIn(0, height - 1), MutabilityOptions.throwError)
            if(options == MutabilityOptions.wrap) return getElementAt_colrow(col.SignIndependentModulo(width), row.SignIndependentModulo(height), MutabilityOptions.throwError)
            if(options == MutabilityOptions.ignore) return null
        }
        return grid[row][col]
    }
    fun setElementAt_colrow(value: T, vararg pos: Int, options : MutabilityOptions = MutabilityOptions.throwError)
    {
        require(pos.size%2==0) { "pos must be even" }
        for(i in pos.indices step 2)
        {
            setElementAt_colrow(pos[i], pos[i+1], value, options)
        }
    }
    fun setElementAt_colrow(col: Int, row: Int, value: T, options: MutabilityOptions = MutabilityOptions.throwError)
    {  // convert the body to setElementAt(x, y reverseInt)
        if (isOvershoot(col, row))
        {
            if(options == MutabilityOptions.throwError) throw IndexOutOfBoundsException("x: $col, y: $row")
            if(options == MutabilityOptions.clamp) setElementAt_colrow(col.coerceIn(0, width - 1), row.coerceIn(0, height - 1), value, MutabilityOptions.throwError)
            if(options == MutabilityOptions.wrap) setElementAt_colrow(col.rem(width), row.rem(height), value, MutabilityOptions.throwError)
            return // there was no way that the program could get to this line anyway, but with ignore option it can get here, it works
        }
        grid[row][col] = value
    }

    fun getElementAt(x: Int, y: Int, options: MutabilityOptions = MutabilityOptions.throwError) : T?
    {
        if (isOvershoot(x, y))
        {   // attention I've converted this to colrow but i didn't change the checks
            if(options == MutabilityOptions.throwError) throw IndexOutOfBoundsException("x: $x, y: $y")
            if(options == MutabilityOptions.clamp) return getElementAt(x.coerceIn(0, width - 1), y.coerceIn(0, height - 1), MutabilityOptions.throwError)
            if(options == MutabilityOptions.wrap) return getElementAt(x.rem(width), y.rem(height), MutabilityOptions.throwError)
            if(options == MutabilityOptions.ignore) return null
        }
        return grid[y reverseIndex height][x]
    }
    fun setElementAt(value: T, vararg pos: Int, options : MutabilityOptions = MutabilityOptions.throwError)
    {
        require(pos.size%2==0) { "pos must be even" }
        for(i in pos.indices step 2)
        {
            setElementAt(pos[i], pos[i+1], value, options)
        }
    }
    fun setElementAt(x: Int, y: Int, value: T, options: MutabilityOptions = MutabilityOptions.throwError)
    {
        if (isOvershoot(x, y))
        {
            if(options == MutabilityOptions.throwError) throw IndexOutOfBoundsException("x: $x, y: $y")
            if(options == MutabilityOptions.clamp) setElementAt(x.coerceIn(0, width - 1), y.coerceIn(0, height - 1), value, MutabilityOptions.throwError)
            if(options == MutabilityOptions.wrap) setElementAt(x.rem(width), y.rem(height), value, MutabilityOptions.throwError)
            return
        }
        grid[y reverseIndex height][x] = value
    }
    fun print_colrow(replace: MutableMap<T, Any?>? = null)
    {
        print(false, replace)
    }
    fun print(flipY: Boolean = true, replace: MutableMap<T, Any?>? = null)
    {
        var yIndice = height.intIndices() as IntProgression
        if(flipY) yIndice = yIndice.reversed()
        for (y in yIndice)
        {
            for (x in width.intIndices())
            {
                if(replace == null)
                    print(getElementAt(x, y))
                else{
                    print(replace[getElementAt(x,y)]?:default)
                }
            }
            println()
        }
    }
    fun clear()
    {
        for(row in height.intIndices())
        {
            for(col in width.intIndices())
            {
                grid[row][col] = default
            }
        }
    }
    /**
     * for the cartesian plane
     * will crash if the point is outside the grid
     */
    operator fun get(x: Int, y: Int) : T {
        return getElementAt(x, y)!!
    }



    fun getOverridden(other: Grid<T>,
                      otherX: Int, otherY: Int, burnInto: Boolean = false): MutableList<MutableList<T>>
    {
        return getOverridden_colrow(other, otherX, otherY reverseIndex other.height, burnInto)
    }
    fun getOverridden_colrow(other: Grid<T>,
                             otherCol: Int, otherRow: Int, burnInto: Boolean = false): MutableList<MutableList<T>>
    {   // this thing "should" work                      // the minus 1 is because a 2 wide block should fit in a 2 wide plane
                                                        // the other minus 1 is for trial and error
        require(!isOvershoot(otherCol + other.width-1 -1, otherRow + other.height-1 -1) &&
                !isOvershoot(otherCol, otherRow)){ "other grid is out of bounds" }

        val result = MutableList(height) { MutableList(width) { default } }

        for(row in result.indices)
            for(col in result[row].indices)
                result[row][col] = grid[row][col]

        for (row in other.height.intIndices())
        {
            for (col in other.width.intIndices())
            {
                if(other.grid[row][col] != other.default || burnInto)
                    result[otherRow + row][otherCol + col] = other.grid[row][col]
                else
                    result[otherRow + row][otherCol + col] = grid[otherRow + row][otherCol + col]
            }
        }
        return result
    }
    fun getOverridden(other: Grid<T>, burnInto: Boolean = false) : MutableList<MutableList<T>>
    {
        if(other.width != width || other.height != height) throw IllegalArgumentException("Grid sizes do not match")
        // ATTENTION: This will only work if the Grids only contain primitives
        val result = MutableList(height) { MutableList(width) { default } }

        for(row in result.indices)
            for(col in result[row].indices)
                result[row][col] = grid[row][col]

        for (row in height.intIndices())
            for (col in width.intIndices())
                if(other.grid[row][col] != other.default || burnInto)
                    result[row][col] = other.getElementAt_colrow(col, row)!!
                else
                    result[row][col] = grid[row][col]

        return result
    }
    fun override(other: Grid<T>) : MutableList<MutableList<T>>
    {
        return other.getOverridden(this)
    }
    enum class MutabilityOptions{
        throwError, clamp, wrap, ignore
    }
}