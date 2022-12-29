import java.io.File

const val treesChar = "A"
const val arrayTentChar = "T"

fun main() {
    val constInvalidOption = "Resposta invalida"

    do {
        var userOption = drawMenuAndGetUserOption()
        if (userOption != null && userOption == 1) {
            val totalLines = getUserBoardRowsSettings("Quantas linhas?", constInvalidOption)
            val totalColumns = getUserBoardRowsSettings("Quantas colunas?", constInvalidOption)
            val isValidMap = validaTamanhoMapa(totalLines, totalColumns)
            val isHardMap = totalLines == 10 && totalColumns == 10

            if (isValidMap) {
                val birthDateText = if (isHardMap) { getUserBirthDateForHardMap() } else { null }
                if (birthDateText == null) {
                    drawBoard(totalLines, totalColumns)

                    println("Coordenadas da tenda? (ex: 1,B)")
                    val coord = readln()

                    if (coord.lowercase() == "sair"){
                        userOption = 0
                    }
                    else{
                        val validatedCoords = processaCoordenadas(coord, totalLines, totalColumns)
                    }
                }
            }
            else {
                println("Terreno invalido")
            }
        }
    } while (userOption != 0)
}

fun drawMenuAndGetUserOption(): Int? {
    println(criaMenu())
    val userOption: Int? = readln().toIntOrNull()

    val isInvalidInput = userOption == null || userOption !in 0..1
    if (isInvalidInput) {
        println("Opcao invalida")
    }

    return userOption
}

fun drawBoard(totalLines :Int, totalColumns :Int){
    val terrainFromFile = leTerrenoDoFicheiro(totalLines, totalColumns)
    val verticalCountersFromFile = leContadoresDoFicheiro(totalLines, totalColumns, true)
    val horizontalCountersFromFile = leContadoresDoFicheiro(totalLines, totalColumns, false)
    println(criaTerreno(terrainFromFile, verticalCountersFromFile, horizontalCountersFromFile, true, true))
}

fun criaMenu(): String {
    return "\nBem vindo ao jogo das tendas\n\n1 - Novo jogo\n0 - Sair\n"
}

fun getUserBoardRowsSettings(requestText: String, errorText: String): Int {
    var rows: Int?

    do {
        println(requestText)
        rows = readln().toIntOrNull() ?: -1

        val isInvalidInput = rows <= 0

        if (isInvalidInput) {
            println(errorText)
        }
    } while (isInvalidInput)

    return rows ?: 0
}

fun criaTerreno(terreno:Array<Array<String?>>,contadoresVerticais: Array<Int?>?, contadoresHorizontais: Array<Int?>?, mostraLegendaHorizontal: Boolean, mostraLegendaVertical: Boolean): String {
    var lineCount = 0
    var boardText = "\n"
    val numColunas = terreno[0].size
    val numLinhas = terreno.size
    val threeEmptySpacesConst = "     "

    if (contadoresHorizontais != null) {
        boardText += "${createHorizontalCountersToBoard(contadoresHorizontais)}\n"
    }
    if (mostraLegendaHorizontal) {
        boardText += "$threeEmptySpacesConst| ${criaLegendaHorizontal(numColunas)}\n"
    }

    while (lineCount < numLinhas) {
        if (contadoresVerticais != null) {
            boardText += "${contadoresVerticais[lineCount]?.toString() ?: " "} "
        }

        if (mostraLegendaVertical) {
            val lineNumber = lineCount+1
            val isSingleDigitLine = lineNumber in 1..9
            boardText += when (isSingleDigitLine) {
                true -> " $lineNumber "
                false -> "$lineNumber "
            }
        }

        boardText += createColumnsForMap(terreno, numColunas, lineCount)

        //Break every line except the last one
        if (lineCount != numLinhas-1) {
            boardText += "\n"
        }

        lineCount++
    }

    return boardText
}

fun criaLegendaHorizontal(numColunas: Int): String {
    var columnCount = 1
    var legendaHorizontal = ""
    var rowChar = 'A'

    while (columnCount <= numColunas) {
        legendaHorizontal += when (columnCount) {
            1 -> "$rowChar "
            numColunas -> "| $rowChar"
            else -> "| $rowChar "
        }
        rowChar++
        columnCount++
    }
    return legendaHorizontal
}

fun validaDataNascimento(data: String?): String? {
    var result: String? = "Data invalida"
    var isCorrectDateFormat = data != null && data.length == 10 && data[2] == '-' && data[5] == '-'

    //if (data == "")
    //    return null

    if (data != null && isCorrectDateFormat) {
        val day = "${data[0]}${data[1]}".toIntOrNull()
        val month = "${data[3]}${data[4]}".toIntOrNull()
        val year = "${data[6]}${data[7]}${data[8]}${data[9]}".toIntOrNull()

        val isValidYear = year in 1901..2022
        if (day != null && month != null && year != null && isValidYear) {
            val isUnderAge = year > 2004 || (year == 2004 && month >= 11 && day >= 1)
            if (isUnderAge) {
                return "Menor de idade nao pode jogar"
            } else {
                val isFebruary = month == 2
                if (isFebruary) {
                    val isLeapYear = ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                    isCorrectDateFormat = when {
                        isLeapYear && day in 1..29 -> true
                        !isLeapYear && day in 1..28 -> true
                        else -> false
                    }
                } else {
                    val isThirtyOneDaysMonth =
                        month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12
                    isCorrectDateFormat = when {
                        isThirtyOneDaysMonth && day in 1..31 -> true
                        !isThirtyOneDaysMonth && day in 1..30 -> true
                        else -> false
                    }
                }
            }
        }
    }

    if (isCorrectDateFormat) {
        result = null
    }

    return result
}

fun validaTamanhoMapa(numLinhas: Int, numColunas: Int): Boolean {

    return when (numLinhas) {
        6 -> numColunas == 5 || numColunas == 6
        8 -> numColunas == 8 || numColunas == 10
        10 -> numColunas == 8 || numColunas == 10
        else -> false
    }
}

fun processaCoordenadas(coordenadasStr: String?, numLines: Int, numColumns: Int):  Pair<Int,Int>?
{
    if (coordenadasStr != null && (coordenadasStr.length == 3 || coordenadasStr.length == 4)) {
        val mapHeaderLabel = criaLegendaHorizontal(numColumns)
        val lastCharCode = mapHeaderLabel[mapHeaderLabel.length - 1].code
        val headerFirstCharCode = 'A'.code
        val isValidLineCoord = coordenadasStr[0].digitToIntOrNull() != null && coordenadasStr[0].digitToIntOrNull() in 1..numLines
        val isValidColumnCoord = coordenadasStr[2].uppercaseChar().code in headerFirstCharCode..lastCharCode

        if(isValidLineCoord && isValidColumnCoord){
            val lineCoord = coordenadasStr[0].toString().toInt() - 1
            val columnCoord = coordenadasStr[2].code - headerFirstCharCode
            return Pair(lineCoord, columnCoord)
        }
    }
    return null
}

fun leContadoresDoFicheiro(numLines: Int, numColumns: Int, verticais: Boolean): Array<Int?> {
    val boardFile = readBoardFromFile(numLines, numColumns)

    //0 is the position of vertical counters
    //1 is the position of Horizontal counters
    val countersFileIndex = if (verticais) { 1 } else { 0 }
    val countersSplitAsStrings = boardFile[countersFileIndex].split(',')
    val countersAsInts = arrayOfNulls<Int>(countersSplitAsStrings.size)

    var countersIdx = 0
    for (counter in countersSplitAsStrings) {
        val counterValue = counter.toInt()
        countersAsInts[countersIdx] = if (counterValue == 0) {
            null
        } else {
            counterValue
        }
        countersIdx++
    }

    return countersAsInts
}

fun readBoardFromFile(numLines: Int, numColumns: Int): List<String> {
    return File("./ficheiros-jogo-tendas-main/${numLines}x${numColumns}.txt").readLines()
}

fun leTerrenoDoFicheiro(numLines: Int, numColumns: Int): Array<Array<String?>> {
    val boardFile = readBoardFromFile(numLines, numColumns)
    val boardFileSize = boardFile.size
    val board = Array(numLines) { arrayOfNulls<String>(numColumns) }

    var coordIndex = 2 //2 because the first two lines are for counters
    while (coordIndex < boardFileSize) {
        val treeCoords = boardFile[coordIndex].split(',')
        val line = treeCoords[0].toInt()
        val column = treeCoords[1].toInt()
        board[line][column] = treesChar
        coordIndex++
    }

    return board
}

fun createHorizontalCountersToBoard(contadoresHorizontais: Array<Int?>?): String {
    var horizontalCountersStr = "     "

    if (contadoresHorizontais != null) {
        var counterIdx = 0
        while (counterIdx < contadoresHorizontais.size){
            horizontalCountersStr+= " " //Espaço por cima dos pipes
            if (contadoresHorizontais[counterIdx] == null) {
                horizontalCountersStr += "  "
            } else {
                horizontalCountersStr += " ${contadoresHorizontais[counterIdx]}"
            }
            val isLastCounter = counterIdx == contadoresHorizontais.size-1
            if (!isLastCounter){
                horizontalCountersStr+= " " //Espaço depois do numero, excepto no ultimo contador
            }

            counterIdx++
        }
    }

    return horizontalCountersStr
}

fun createColumnsForMap(terreno: Array<Array<String?>>, numColunas: Int, lineCount: Int,): String {
    var columnCount = 0
    val tentChar = '\u25B3'
    val emptyBoardSpace = " "

    var columnText = ""

    while (columnCount < numColunas) {
        val isLastColumn = columnCount == numColunas-1
        val isTreeSlot = terreno[lineCount][columnCount] == treesChar
        val isTentSlot = terreno[lineCount][columnCount] == arrayTentChar

        val fieldContent = when {
            isTreeSlot -> treesChar
            isTentSlot -> tentChar
            else -> emptyBoardSpace
        }

        columnText += if (isLastColumn) "| ${fieldContent}" else "| $fieldContent "
        columnCount++
    }

    return columnText
}

fun askBirthDate():String?{
    println("Qual a sua data de nascimento? (dd-mm-yyyy)")
    val birthDate = readln()
    //!= null means it's not a valid date
    return validaDataNascimento(birthDate)
}

fun getUserBirthDateForHardMap():String?{
    var birthDateText :String?

    do {
        birthDateText = askBirthDate()
        if (birthDateText != null){
            println(birthDateText)
        }
    } while (birthDateText != "Menor de idade nao pode jogar" && birthDateText != null)

    return birthDateText
}

fun colocaTenda(terreno: Array<Array<String?>>, coords: Pair<Int, Int>): Boolean{
    val isValidPlacement = true

    if (terreno[coords.first][coords.second] == null){
        terreno[coords.first][coords.second] = arrayTentChar
    }
    else if (terreno[coords.first][coords.second] == arrayTentChar){
        terreno[coords.first][coords.second] = null
    }

    return isValidPlacement
}

fun temArvoreAdjacente(terreno: Array<Array<String?>>, coords: Pair<Int, Int>) : Boolean {
    val isFirstLine = coords.first - 1 >= 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second - 1 >= 0
    val isLastColumn = coords.second + 1 == terreno[0].size
    
    var hasAdjacentTree = false
    
    val isCornerCoord = (isFirstLine && isLastColumn) || isFirstLine && isFirstColumn || (isLastLine && isLastColumn) || (isLastLine && isFirstColumn)
    if (isCornerCoord){
        hasAdjacentTree = checkIfHasEntityWhenCornerCoords(terreno, coords, treesChar)
    }
    
    val isBottomOrTopSidesCoord = (isFirstLine && (!isFirstColumn || !isLastColumn)) || (isLastLine && (!isFirstColumn || !isLastColumn))
    if (isBottomOrTopSidesCoord){
        hasAdjacentTree = checkIfHasEntityWhenTopOrBottomCoords(terreno, coords, treesChar)
    }

    val isLeftOrRightSidesCoord = (isFirstColumn && (!isFirstLine || !isLastLine)) || (isLastColumn && (!isFirstLine || !isLastLine))
    if (isLeftOrRightSidesCoord){
        hasAdjacentTree = checkIfHasEntityWhenLeftOrRightCoords(terreno, coords, treesChar)
    }
    
    val isCenterCoord = !isCornerCoord && !isBottomOrTopSidesCoord && !isLeftOrRightSidesCoord
    if (isCenterCoord){
        hasAdjacentTree = checkIfHasEntityWhenCenterCoords(terreno, coords, treesChar)
    }
    
    return hasAdjacentTree
}
fun temTendaAdjacente(terreno: Array<Array<String?>>, coords: Pair<Int, Int>) : Boolean {
    val isFirstLine = coords.first - 1 >= 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second - 1 >= 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasAdjacentTent = false

    val isCornerCoord = (isFirstLine && isLastColumn) || isFirstLine && isFirstColumn || (isLastLine && isLastColumn) || (isLastLine && isFirstColumn)
    if (isCornerCoord){
        hasAdjacentTent = checkIfHasEntityWhenCornerCoords(terreno, coords, arrayTentChar)
    }

    val isBottomOrTopSidesCoord = (isFirstLine && (!isFirstColumn || !isLastColumn)) || (isLastLine && (!isFirstColumn || !isLastColumn))
    if (isBottomOrTopSidesCoord){
        hasAdjacentTent = checkIfHasEntityWhenTopOrBottomCoords(terreno, coords, arrayTentChar)
    }

    val isLeftOrRightSidesCoord = (isFirstColumn && (!isFirstLine || !isLastLine)) || (isLastColumn && (!isFirstLine || !isLastLine))
    if (isLeftOrRightSidesCoord){
        hasAdjacentTent = checkIfHasEntityWhenLeftOrRightCoords(terreno, coords, arrayTentChar)
    }

    val isCenterCoord = !isCornerCoord && !isBottomOrTopSidesCoord && !isLeftOrRightSidesCoord
    if (isCenterCoord){
        hasAdjacentTent = checkIfHasEntityWhenCenterCoords(terreno, coords, arrayTentChar)
    }

    return hasAdjacentTent
}

fun checkIfHasEntityWhenCornerCoords(terreno: Array<Array<String?>>, coords: Pair<Int, Int>, entityString:String) : Boolean{
    val line = coords.first
    val column = coords.second
    val nextLine = line+1
    val nextColumn = column+1
    val previousLine = line-1
    val previousColumn = column-1

    val isFirstLine = coords.first - 1 >= 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second - 1 >= 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasTreeOnRight = false
    var hasTreeOnLeft = false
    var hasTreeOnTop = false
    var hasTreeOnBottom = false


    //(0,MAX)
    if (isFirstLine && isLastColumn){
        hasTreeOnLeft = terreno[line][previousColumn] == entityString
        hasTreeOnBottom = terreno[nextLine][column] == entityString
    }
    //(0,0)
    else if(isFirstLine && isFirstColumn){
        hasTreeOnRight = terreno[line][nextColumn] == entityString
        hasTreeOnBottom = terreno[nextLine][column] == entityString
    }
    //(MAX,MAX)
    else if (isLastLine && isLastColumn) {
        hasTreeOnTop = terreno[previousLine][column] == entityString
        hasTreeOnLeft = terreno[line][previousColumn] == entityString
    }
    //(MAX,0)
    else if (isLastLine && isFirstColumn){
        hasTreeOnTop = terreno[previousLine][column] == entityString
        hasTreeOnRight = terreno[line][nextColumn] == entityString
    }

    return hasTreeOnRight || hasTreeOnLeft || hasTreeOnTop || hasTreeOnBottom
}
fun checkIfHasEntityWhenTopOrBottomCoords(terreno: Array<Array<String?>>, coords: Pair<Int, Int>, entityString:String) : Boolean{
    val line = coords.first
    val column = coords.second
    val nextLine = line+1
    val nextColumn = column+1
    val previousLine = line-1
    val previousColumn = column-1

    val isFirstLine = coords.first - 1 >= 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second - 1 >= 0
    val isLastColumn = coords.second + 1 == terreno[0].size


    var hasTreeOnRight = false
    var hasTreeOnLeft = false
    var hasTreeOnTop = false
    var hasTreeOnBottom = false

    //(0,ANY)
    if (isFirstLine && (!isFirstColumn || !isLastColumn)){
        hasTreeOnBottom = terreno[nextLine][column] == entityString
        hasTreeOnLeft = terreno[line][previousColumn] == entityString
        hasTreeOnRight = terreno[line][nextColumn] == entityString

    }
    //(MAX,ANY)
    else if (isLastLine && (!isFirstColumn || !isLastColumn)){
        hasTreeOnLeft = terreno[line][previousColumn] == entityString
        hasTreeOnRight = terreno[line][nextColumn] == entityString
        hasTreeOnTop = terreno[previousLine][column] == entityString
    }

    return hasTreeOnRight || hasTreeOnLeft || hasTreeOnTop || hasTreeOnBottom
}
fun checkIfHasEntityWhenLeftOrRightCoords(terreno: Array<Array<String?>>, coords: Pair<Int, Int>, entityString:String) : Boolean{
    val line = coords.first
    val column = coords.second
    val nextLine = line+1
    val nextColumn = column+1
    val previousLine = line-1
    val previousColumn = column-1

    val isFirstLine = coords.first - 1 >= 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second - 1 >= 0
    val isLastColumn = coords.second + 1 == terreno[0].size


    var hasTreeOnRight = false
    var hasTreeOnLeft = false
    var hasTreeOnTop = false
    var hasTreeOnBottom = false

    //(ANY,0)
    if (isFirstColumn && (!isFirstLine || !isLastLine)){
        hasTreeOnBottom = terreno[nextLine][column] == entityString
        hasTreeOnLeft = terreno[line][previousColumn] == entityString
        hasTreeOnRight = terreno[line][nextColumn] == entityString

    }
    //(ANY,MAX)
    else if (isLastColumn && (!isFirstLine || !isLastLine)){
        hasTreeOnLeft = terreno[line][previousColumn] == entityString
        hasTreeOnRight = terreno[line][nextColumn] == entityString
        hasTreeOnTop = terreno[previousLine][column] == entityString
    }

    return hasTreeOnRight || hasTreeOnLeft || hasTreeOnTop || hasTreeOnBottom
}
fun checkIfHasEntityWhenCenterCoords(terreno: Array<Array<String?>>, coords: Pair<Int, Int>, entityString:String) : Boolean{
    val line = coords.first
    val column = coords.second
    val nextLine = line+1
    val nextColumn = column+1
    val previousLine = line-1
    val previousColumn = column-1

    val isFirstLine = coords.first - 1 >= 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second - 1 >= 0
    val isLastColumn = coords.second + 1 == terreno[0].size


    var hasTreeOnRight = false
    var hasTreeOnLeft = false
    var hasTreeOnTop = false
    var hasTreeOnBottom = false

    //(ANY\0,MAX-1)
    if (isFirstColumn && (!isFirstLine || !isLastLine)){
        hasTreeOnTop = terreno[previousLine][column] == entityString
        hasTreeOnBottom = terreno[nextLine][column] == entityString
        hasTreeOnLeft = terreno[line][previousColumn] == entityString
        hasTreeOnRight = terreno[line][nextColumn] == entityString

    }

    return hasTreeOnRight || hasTreeOnLeft || hasTreeOnTop || hasTreeOnBottom
}