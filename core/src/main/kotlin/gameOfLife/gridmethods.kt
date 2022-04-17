package gameOfLife


fun String.toGrid() : Grid<Boolean>
{
    return Grid(this.trimIndent().toList2D(mutableMapOf('0' to false, '1' to true), false), false)
}
fun Int.intIndices() = 0 until this

infix fun Int.reverseIndex(size: Int): Int {
    return (size - this)-1
}
infix fun Int.ri(size: Int): Int {
    return size - this-1
}

/**
 * if the other strings are longer than the first, it will cause an OutOfBoundsException
 * if the other strings are smaller, it will cause blank areas
 * if they are equal nothing will happen.
 */
fun <T> String.toList2D(convertTo: MutableMap<Char, T>, default: T) : MutableList<MutableList<T>>
{
    val lines = this.split("\n")
    val result = MutableList(lines.size){MutableList(lines[0].length){default} }
    for(row in lines.indices)
    {
        for(col in lines[row].indices)
        {
            result[row][col] = convertTo[lines[row][col]]?:default
        }
    }
    return result
}
fun <T> MutableList<MutableList<T>>.resetToDefault(default: T)
{
    for (row in this.indices)
    {
        for (col in this[row].indices)
        {
            this[row][col] = default
        }
    }
}
fun <T> createEmptyList2D(width: Int, height: Int, default : T) : MutableList<MutableList<T>>
{
    return MutableList(height) { MutableList(width) { default } }
}
fun <T> MutableList<MutableList<T>>.toGrid(default: T) : Grid<T>
{
    return Grid(this, default)
}

fun <T> MutableList<MutableList<T>>.copy() : MutableList<MutableList<T>>
{
    var toReturn : MutableList<MutableList<T?>?> = MutableList(this.size) {null}
    for(index in this.indices)
    {
        toReturn[index] = MutableList(this[index].size){null}

        for(indexJ in this[index].indices)
        {
            toReturn[index]!![indexJ] =  this[index][indexJ]
        }
    }
    return toReturn as MutableList<MutableList<T>>
}

/**
 * @return true if there will be a conflict
 */
fun <T> Grid<T>.checkConflicts(other: Grid<T>): Boolean
{
    if(this.width != other.width || this.height != other.height)
    {
        throw IllegalArgumentException("The other grid must be the same size as this one")
    }
    // this is the main, and the other is the one to be put into this grid
    for(row in this.grid.indices)
    {
        for(col in this.grid[row].indices)
        {
            if (this.grid[row][col] == this.default ||
                other.grid[row][col] == this.default)
            {
                continue
            }
            else
                return true

        }
    }
    return false
}
fun <T> Grid<T>.checkConflicts(other: Grid<T>, otherRow: Int, otherCol: Int): Boolean
{
    // this is the main, and the other is the one to be put into this grid
    for(row in other.grid.indices)
    {
        for(col in other.grid[row].indices)
        {
            if (this.grid[row+otherRow][col+otherCol] == this.default ||
                other.grid[row][col] == this.default)
            {
                continue
            }
            else
                return true

        }
    }
    return false
}

fun <T> MutableList<MutableList<T>>.checkEquality(other: MutableList<MutableList<T>>) : Boolean
{
    if(other.size != this.size)
        return false
    try {
        for( row in this.indices)
        {
            for( col in this[row].indices)
            {
                if(this[row][col]?.equals(other[row][col]) == false)
                    return false
            }
        }
    }
    catch (e: java.lang.IndexOutOfBoundsException){ return false }
    return true
}

/**
 * returns the bottommost column for every row where there value isn't default
 * if a col is completely default then it will return -1
 * @return Array(row, col | row, col | row, col)
 * tested ✅
 */
fun <T> MutableList<MutableList<T>>.GetLatestRows(default: T, bottomBegin: Boolean = true) : Array<Int>
{
    var rowsToReturn: Array<Int> = Array(this[0].size){-1}
    var colsToReturn: Array<Int> = Array(this[0].size){-1}

    col@ for(col in this[0].size.intIndices())
    {
        colsToReturn[col] = col
        row@ for(row in if(bottomBegin)this.size.intIndices().reversed() else this.size.intIndices())
        {
            if(this[row][col] != default)
            {
                rowsToReturn[col] = row
                break@row
            }
        }
    }
    var toReturn = Array(colsToReturn.size*2){Int.MIN_VALUE}
    for(i in toReturn.size.intIndices().step(2))
    {
        toReturn[i] = rowsToReturn[i/2]
        toReturn[i+1] = colsToReturn[i/2]
    }
    return toReturn
}

/**
 * returns the rightmost column for every row where there value isn't default
 * if a row is completely default then it will return -1
 * @return Array<Pair< RowIndex, ColumnIndex >>
 * tested ✅
 */
fun <T> MutableList<MutableList<T>>.GetLatestCols(default: T, rightBegin: Boolean = true) : Array<Int>
{
    var rowsToReturn: Array<Int> = Array(this.size){-1}
    var colsToReturn: Array<Int> = Array(this.size){-1}

    row@ for(row in this.size.intIndices())
    {
        rowsToReturn[row] = row
        col@ for(col in if(rightBegin)this[0].size.intIndices().reversed() else this[0].size.intIndices())
        {
            if(this[row][col] != default)
            {
                colsToReturn[row] = col
                break@col
            }
        }
    }
    var toReturn = Array(rowsToReturn.size*2){Int.MIN_VALUE}
    for(i in toReturn.size.intIndices().step(2))
    {
        toReturn[i] = rowsToReturn[i/2]
        toReturn[i+1] = colsToReturn[i/2]
    }
    return toReturn

}

fun <T> MutableList<MutableList<T>>.IsColumnEmpty(col: Int, emptySubject: T) : Boolean
{
    for(row in this.indices)
    {
        if(this[row][col] != emptySubject)
            return false
    }
    return true
}
fun <T> MutableList<MutableList<T>>.IsRowEmpty(row: Int, emptySubject: T) : Boolean
{
    for(col in this[row].indices)
    {
        if(this[row][col] != emptySubject)
            return false
    }
    return true
}


fun <T> MutableList<MutableList<T>>.Rotate90Degrees() : MutableList<MutableList<T>>
{
    var newRows = this[0].size
    var newCols = this.size
    val result : MutableList<MutableList<T?>> = MutableList(newRows){MutableList(newCols){null}}
    for(row in this.indices)
    {
        for(col in this[row].indices)
        {
            result[col][row] = this[row][col]
        }
    }
    return result as MutableList<MutableList<T>>
}

fun <T> Grid<T>.removeColumnIfEmpty(column: Int, empty: T) : Boolean
{
    if(!this.grid.IsColumnEmpty(column, empty)) {
        return false
    }
    for(rowI in height.intIndices())
    {
        this.grid[rowI].removeAt(column)
    }

    return true
}
fun <T> Grid<T>.removeRowIfEmpty(row1: Int, empty: T) : Boolean
{
    if(!this.grid.IsRowEmpty(row1, empty)) {
        return false
    }
    this.grid.removeAt(row1)
    return true
}
