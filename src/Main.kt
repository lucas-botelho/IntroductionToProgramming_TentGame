import java.io.File

const val treeIcon = '\u25B3'
const val arrayTreeChar = "A"
const val arrayTentChar = "T"
const val constInvalidOption = "Resposta invalida"
const val constInvalidCoords = "Coordenadas invalidas"
const val constInvalidTentPlacement = "Tenda nao pode ser colocada nestas coordenadas"
const val fiveEmptySpacesConst = "     "
const val endGameMessage = "Parabens! Terminou o jogo!"

fun main() {
    do {
        var userOption = drawMenuAndGetUserOption()
        if (userOption != null && userOption == 1) {
            val totalLines = getUserBoardRowsSettings("Quantas linhas?", constInvalidOption)
            val totalColumns = getUserBoardRowsSettings("Quantas colunas?", constInvalidOption)
            val isValidMap = validaTamanhoMapa(totalLines, totalColumns)
            val isHardMap = totalLines == 10 && totalColumns == 10

            if (isValidMap) {
                val birthDateText = if (isHardMap) {
                    getUserBirthDateForHardMap()
                } else {
                    null
                }
                if (birthDateText == null) {
                    val terreno = leTerrenoDoFicheiro(totalLines, totalColumns)
                    var gameEnded = false
                    var showBoard = true

                    while (!gameEnded) {
                        if (showBoard) {
                            drawBoard(totalLines, totalColumns, terreno)
                        }
                        val userInputCoords = getUserPlayInput()

                        if (userInputCoords.lowercase() == "sair") {
                            userOption = 0
                        } else {
                            val proccessedCoords = processaCoordenadas(userInputCoords, totalLines, totalColumns)

                            if (proccessedCoords == null) {
                                print(constInvalidCoords)
                            } else {
                                val line = proccessedCoords.first
                                val column = proccessedCoords.second
                                if (colocaTenda(terreno, proccessedCoords)) {
                                    terreno[line][column] = when (terreno[line][column]) {
                                        arrayTentChar -> null
                                        else -> arrayTentChar
                                    }
                                    showBoard = true
                                    val verticalCounters = leContadoresDoFicheiro(totalLines, totalColumns, true)
                                    val horizontalCounters = leContadoresDoFicheiro(totalLines, totalColumns, false)
                                    gameEnded = terminouJogo(terreno, verticalCounters, horizontalCounters)
                                } else {
                                    println(constInvalidTentPlacement)
                                    showBoard = false
                                }
                            }
                        }
                    }
                    println(endGameMessage)
                }
            } else {
                println("Terreno invalido")
            }
        }
    } while (userOption != 0)
}

fun getUserPlayInput(): String {
    println("Coordenadas da tenda? (ex: 1,B)")
    return readln()
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

fun drawBoard(totalLines: Int, totalColumns: Int, terreno: Array<Array<String?>>) {
    val verticalCountersFromFile = leContadoresDoFicheiro(totalLines, totalColumns, true)
    val horizontalCountersFromFile = leContadoresDoFicheiro(totalLines, totalColumns, false)
    println(criaTerreno(terreno, verticalCountersFromFile, horizontalCountersFromFile, true, true))
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

fun criaTerreno(
    terreno: Array<Array<String?>>,
    contadoresVerticais: Array<Int?>?,
    contadoresHorizontais: Array<Int?>?,
    mostraLegendaHorizontal: Boolean,
    mostraLegendaVertical: Boolean
): String {
    var lineCount = 0
    var boardText = "\n"
    val numColunas = terreno[0].size
    val numLinhas = terreno.size

    if (contadoresHorizontais != null) {
        boardText += "${criaLegendaContadoresHorizontal(contadoresHorizontais)}\n"
    }
    if (mostraLegendaHorizontal) {
        boardText += "$fiveEmptySpacesConst| ${criaLegendaHorizontal(numColunas)}\n"
    }

    while (lineCount < numLinhas) {
        if (contadoresVerticais != null) {
            boardText += "${contadoresVerticais[lineCount]?.toString() ?: " "} "
        }

        if (mostraLegendaVertical) {
            val lineNumber = lineCount + 1
            val isSingleDigitLine = lineNumber in 1..9
            boardText += when (isSingleDigitLine) {
                true -> " $lineNumber "
                false -> "$lineNumber "
            }
        }

        boardText += createColumnsForMap(terreno, numColunas, lineCount)

        //Break every line except the last one
        if (lineCount != numLinhas - 1) {
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

fun processaCoordenadas(coordenadasStr: String?, numLines: Int, numColumns: Int): Pair<Int, Int>? {
    if (coordenadasStr != null && (coordenadasStr.length in 3..4)) {
        val mapHeaderLabel = criaLegendaHorizontal(numColumns)
        val lastCharCode = mapHeaderLabel[mapHeaderLabel.length - 1].code
        val headerFirstCharCode = 'A'.code
        val isValidLineCoord =
            coordenadasStr[0].digitToIntOrNull() != null && coordenadasStr[0].digitToIntOrNull() in 1..numLines
        val isValidColumnCoord = coordenadasStr[2].uppercaseChar().code in headerFirstCharCode..lastCharCode

        if (isValidLineCoord && isValidColumnCoord) {
            val lineCoord = coordenadasStr[0].toString().toInt() - 1
            val columnCoord = coordenadasStr[2].uppercaseChar().code - headerFirstCharCode
            return Pair(lineCoord, columnCoord)
        }
    }
    return null
}

fun leContadoresDoFicheiro(numLines: Int, numColumns: Int, verticais: Boolean): Array<Int?> {
    val boardFile = readBoardFromFile(numLines, numColumns)

    //0 is the position of vertical counters
    //1 is the position of Horizontal counters
    val countersFileIndex = if (verticais) {
        1
    } else {
        0
    }
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
        board[line][column] = arrayTreeChar
        coordIndex++
    }

    return board
}

fun criaLegendaContadoresHorizontal(contadoresHorizontal: Array<Int?>?): String {
    var horizontalCountersStr = "     "

    if (contadoresHorizontal != null) {
        var counterIdx = 0
        while (counterIdx < contadoresHorizontal.size) {
            horizontalCountersStr += " " //Espaço por cima dos pipes
            if (contadoresHorizontal[counterIdx] == null) {
                horizontalCountersStr += "  "
            } else {
                horizontalCountersStr += " ${contadoresHorizontal[counterIdx]}"
            }
            val isLastCounter = counterIdx == contadoresHorizontal.size - 1
            if (!isLastCounter) {
                horizontalCountersStr += " " //Espaço depois do numero, excepto no ultimo contador
            }

            counterIdx++
        }
    }

    return horizontalCountersStr
}

fun createColumnsForMap(terreno: Array<Array<String?>>, numColunas: Int, lineCount: Int): String {
    var columnCount = 0
    val emptyBoardSpace = " "

    var columnText = ""

    while (columnCount < numColunas) {
        val isLastColumn = columnCount == numColunas - 1
        val isTreeSlot = terreno[lineCount][columnCount] == arrayTreeChar
        val isTentSlot = terreno[lineCount][columnCount] == arrayTentChar

        val fieldContent = when {
            isTreeSlot -> treeIcon
            isTentSlot -> arrayTentChar
            else -> emptyBoardSpace
        }

        columnText += if (isLastColumn) "| ${fieldContent}" else "| $fieldContent "
        columnCount++
    }

    return columnText
}

fun askBirthDate(): String? {
    println("Qual a sua data de nascimento? (dd-mm-yyyy)")
    val birthDate = readln()
    //!= null means it's not a valid date
    return validaDataNascimento(birthDate)
}

fun getUserBirthDateForHardMap(): String? {
    var birthDateText: String?

    do {
        birthDateText = askBirthDate()
        if (birthDateText != null) {
            println(birthDateText)
        }
    } while (birthDateText != "Menor de idade nao pode jogar" && birthDateText != null)

    return birthDateText
}

fun colocaTenda(terreno: Array<Array<String?>>, coords: Pair<Int, Int>): Boolean {

    val isTreeSlot = terreno[coords.first][coords.second] == arrayTreeChar
    val isTentSlot = terreno[coords.first][coords.second] == arrayTentChar
    val isEmptySlot = terreno[coords.first][coords.second] == null
    val isValidSlot = (isEmptySlot && temArvoreAdjacente(terreno, coords) && !temTendaAdjacente(terreno, coords))

    return (isTentSlot || isValidSlot) && !isTreeSlot
}

fun temArvoreAdjacente(terreno: Array<Array<String?>>, coords: Pair<Int, Int>): Boolean {
    val isFirstLine = coords.first == 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second == 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasAdjacentTree = false

    val isCornerCoord =
        (isFirstLine && isLastColumn) || (isFirstLine && isFirstColumn) || (isLastLine && isLastColumn) || (isLastLine && isFirstColumn)
    if (isCornerCoord) {
        hasAdjacentTree = checkIfHasEntityWhenCornerCoords(terreno, coords, arrayTreeChar)
    }

    val isBottomOrTopSidesCoord = (isFirstLine && !isCornerCoord) || (isLastLine && !isCornerCoord)
    if (isBottomOrTopSidesCoord) {
        hasAdjacentTree = checkIfHasEntityWhenTopOrBottomCoords(terreno, coords, arrayTreeChar)
    }

    val isLeftOrRightSidesCoord = (isFirstColumn && !isCornerCoord) || (isLastColumn && !isCornerCoord)
    if (isLeftOrRightSidesCoord) {
        hasAdjacentTree = checkIfHasEntityWhenLeftOrRightCoords(terreno, coords, arrayTreeChar)
    }

    val isCenterCoord = !isCornerCoord && !isBottomOrTopSidesCoord && !isLeftOrRightSidesCoord
    if (isCenterCoord) {
        hasAdjacentTree = checkIfHasEntityWhenCenterCoords(terreno, coords, arrayTreeChar)
    }

    return hasAdjacentTree
}

fun temTendaAdjacente(terreno: Array<Array<String?>>, coords: Pair<Int, Int>): Boolean {
    val isFirstLine = coords.first == 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second == 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasAdjacentTent = false

    val isCornerCoord =
        (isFirstLine && isLastColumn) || isFirstLine && isFirstColumn || (isLastLine && isLastColumn) || (isLastLine && isFirstColumn)
    if (isCornerCoord) {
        hasAdjacentTent = checkIfHasEntityWhenCornerCoords(terreno, coords, arrayTentChar)
        if (!hasAdjacentTent) {
            hasAdjacentTent = checkDiagonallyIfHasEntityWhenCornerCoords(terreno, coords, arrayTentChar)
        }
    }

    val isBottomOrTopSidesCoord = (isFirstLine && !isCornerCoord) || (isLastLine && !isCornerCoord)
    if (isBottomOrTopSidesCoord) {
        hasAdjacentTent = checkIfHasEntityWhenTopOrBottomCoords(terreno, coords, arrayTentChar)
        if (!hasAdjacentTent) {
            hasAdjacentTent = checkDiagonallyIfHasEntityWhenTopOrBottomCoords(terreno, coords, arrayTentChar)
        }
    }

    val isLeftOrRightSidesCoord = (isFirstColumn && !isCornerCoord) || (isLastColumn && !isCornerCoord)
    if (isLeftOrRightSidesCoord) {
        hasAdjacentTent = checkIfHasEntityWhenLeftOrRightCoords(terreno, coords, arrayTentChar)
        if (!hasAdjacentTent) {
            hasAdjacentTent = checkDiagonallyIfHasEntityWhenLeftOrRightCoords(terreno, coords, arrayTentChar)
        }
    }

    val isCenterCoord = !isCornerCoord && !isBottomOrTopSidesCoord && !isLeftOrRightSidesCoord
    if (isCenterCoord) {
        hasAdjacentTent = checkIfHasEntityWhenCenterCoords(terreno, coords, arrayTentChar)
        if (!hasAdjacentTent) {
            hasAdjacentTent = checkDiagonallyIfHasEntityWhenCenterCoords(terreno, coords, arrayTentChar)
        }
    }

    return hasAdjacentTent
}

fun checkIfHasEntityWhenCornerCoords(
    terreno: Array<Array<String?>>,
    coords: Pair<Int, Int>,
    entityString: String
): Boolean {
    val line = coords.first
    val column = coords.second
    val nextLine = line + 1
    val nextColumn = column + 1
    val previousLine = line - 1
    val previousColumn = column - 1

    val isFirstLine = coords.first == 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second == 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasEntityOnRight = false
    var hasEntityOnLeft = false
    var hasEntityOnTop = false
    var hasEntityOnBottom = false


    //(0,MAX)
    if (isFirstLine && isLastColumn) {
        hasEntityOnLeft = terreno[line][previousColumn] == entityString
        hasEntityOnBottom = terreno[nextLine][column] == entityString
    }
    //(0,0)
    else if (isFirstLine && isFirstColumn) {
        hasEntityOnRight = terreno[line][nextColumn] == entityString
        hasEntityOnBottom = terreno[nextLine][column] == entityString
    }
    //(MAX,MAX)
    else if (isLastLine && isLastColumn) {
        hasEntityOnTop = terreno[previousLine][column] == entityString
        hasEntityOnLeft = terreno[line][previousColumn] == entityString
    }
    //(MAX,0)
    else if (isLastLine && isFirstColumn) {
        hasEntityOnTop = terreno[previousLine][column] == entityString
        hasEntityOnRight = terreno[line][nextColumn] == entityString
    }

    return hasEntityOnRight || hasEntityOnLeft || hasEntityOnTop || hasEntityOnBottom
}

fun checkIfHasEntityWhenTopOrBottomCoords(
    terreno: Array<Array<String?>>,
    coords: Pair<Int, Int>,
    entityString: String
): Boolean {
    val line = coords.first
    val column = coords.second
    val nextLine = line + 1
    val nextColumn = column + 1
    val previousLine = line - 1
    val previousColumn = column - 1

    val isFirstLine = coords.first == 0
    val isLastLine = coords.first + 1 == terreno.size

    var hasEntityOnRight = false
    var hasEntityOnLeft = false
    var hasEntityOnTop = false
    var hasEntityOnBottom = false

    //(0,ANY)
    if (isFirstLine) {
        hasEntityOnBottom = terreno[nextLine][column] == entityString
        hasEntityOnLeft = terreno[line][previousColumn] == entityString
        hasEntityOnRight = terreno[line][nextColumn] == entityString

    }
    //(MAX,ANY)
    else if (isLastLine) {
        hasEntityOnLeft = terreno[line][previousColumn] == entityString
        hasEntityOnRight = terreno[line][nextColumn] == entityString
        hasEntityOnTop = terreno[previousLine][column] == entityString
    }

    return hasEntityOnRight || hasEntityOnLeft || hasEntityOnTop || hasEntityOnBottom
}

fun checkIfHasEntityWhenLeftOrRightCoords(
    terreno: Array<Array<String?>>,
    coords: Pair<Int, Int>,
    entityString: String
): Boolean {
    val line = coords.first
    val column = coords.second
    val nextLine = line + 1
    val nextColumn = column + 1
    val previousLine = line - 1
    val previousColumn = column - 1

    val isFirstColumn = coords.second == 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasEntityOnRight = false
    var hasEntityOnLeft = false
    val hasEntityOnTop = terreno[previousLine][column] == entityString
    val hasEntityOnBottom = terreno[nextLine][column] == entityString

    //(ANY,0)
    if (isFirstColumn) {
        hasEntityOnRight = terreno[line][nextColumn] == entityString

    }
    //(ANY,MAX)
    else if (isLastColumn) {
        hasEntityOnLeft = terreno[line][previousColumn] == entityString
    }

    return hasEntityOnRight || hasEntityOnLeft || hasEntityOnTop || hasEntityOnBottom
}

fun checkIfHasEntityWhenCenterCoords(
    terreno: Array<Array<String?>>,
    coords: Pair<Int, Int>,
    entityString: String
): Boolean {
    val line = coords.first
    val column = coords.second
    val nextLine = line + 1
    val nextColumn = column + 1
    val previousLine = line - 1
    val previousColumn = column - 1

    val hasEntityOnTop = terreno[previousLine][column] == entityString
    val hasEntityOnBottom = terreno[nextLine][column] == entityString
    val hasEntityOnLeft = terreno[line][previousColumn] == entityString
    val hasEntityOnRight = terreno[line][nextColumn] == entityString

    return hasEntityOnRight || hasEntityOnLeft || hasEntityOnTop || hasEntityOnBottom
}

fun checkDiagonallyIfHasEntityWhenCornerCoords(
    terreno: Array<Array<String?>>,
    coords: Pair<Int, Int>,
    entityString: String
): Boolean {
    val line = coords.first
    val column = coords.second
    val nextLine = line + 1
    val nextColumn = column + 1
    val previousLine = line - 1
    val previousColumn = column - 1

    val isFirstLine = coords.first == 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second == 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasEntityOnTopRight = false
    var hasEntityOnBottomRight = false
    var hasEntityOnTopLeft = false
    var hasEntityOnBottomLeft = false


    //(0,MAX)
    if (isFirstLine && isLastColumn) {
        hasEntityOnBottomLeft = terreno[nextLine][previousColumn] == entityString
    }
    //(0,0)
    else if (isFirstLine && isFirstColumn) {
        hasEntityOnBottomRight = terreno[nextLine][nextColumn] == entityString
    }
    //(MAX,MAX)
    else if (isLastLine && isLastColumn) {
        hasEntityOnTopLeft = terreno[previousLine][previousColumn] == entityString
    }
    //(MAX,0)
    else if (isLastLine && isFirstColumn) {
        hasEntityOnTopRight = terreno[previousLine][nextColumn] == entityString
    }

    return hasEntityOnTopRight || hasEntityOnTopLeft || hasEntityOnBottomLeft || hasEntityOnBottomRight
}

fun checkDiagonallyIfHasEntityWhenCenterCoords(
    terreno: Array<Array<String?>>,
    coords: Pair<Int, Int>,
    entityString: String
): Boolean {
    val line = coords.first
    val column = coords.second
    val nextLine = line + 1
    val nextColumn = column + 1
    val previousLine = line - 1
    val previousColumn = column - 1

    val hasEntityOnBottomLeft = terreno[nextLine][previousColumn] == entityString
    val hasEntityOnBottomRight = terreno[nextLine][nextColumn] == entityString
    val hasEntityOnTopLeft = terreno[previousLine][previousColumn] == entityString
    val hasEntityOnTopRight = terreno[previousLine][nextColumn] == entityString

    return hasEntityOnTopRight || hasEntityOnTopLeft || hasEntityOnBottomLeft || hasEntityOnBottomRight
}

fun checkDiagonallyIfHasEntityWhenTopOrBottomCoords(
    terreno: Array<Array<String?>>,
    coords: Pair<Int, Int>,
    entityString: String
): Boolean {
    val line = coords.first
    val column = coords.second
    val nextLine = line + 1
    val nextColumn = column + 1
    val previousLine = line - 1
    val previousColumn = column - 1

    val isFirstLine = coords.first == 0
    val isLastLine = coords.first + 1 == terreno.size

    var hasEntityOnTopRight = false
    var hasEntityOnBottomRight = false
    var hasEntityOnTopLeft = false
    var hasEntityOnBottomLeft = false

    //(0,ANY)
    if (isFirstLine) {
        hasEntityOnBottomLeft = terreno[nextLine][previousColumn] == entityString
        hasEntityOnBottomRight = terreno[nextLine][nextColumn] == entityString
    }
    //(MAX,ANY)
    else if (isLastLine) {
        hasEntityOnTopLeft = terreno[previousLine][previousColumn] == entityString
        hasEntityOnTopRight = terreno[previousLine][nextColumn] == entityString
    }

    return hasEntityOnTopRight || hasEntityOnTopLeft || hasEntityOnBottomRight || hasEntityOnBottomLeft
}

fun checkDiagonallyIfHasEntityWhenLeftOrRightCoords(
    terreno: Array<Array<String?>>,
    coords: Pair<Int, Int>,
    entityString: String
): Boolean {
    val line = coords.first
    val column = coords.second
    val nextLine = line + 1
    val nextColumn = column + 1
    val previousLine = line - 1
    val previousColumn = column - 1

    val isFirstColumn = coords.second == 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasEntityOnTopRight = false
    var hasEntityOnBottomRight = false
    var hasEntityOnTopLeft = false
    var hasEntityOnBottomLeft = false

    //(ANY,0)
    if (isFirstColumn) {
        hasEntityOnTopRight = terreno[previousLine][nextColumn] == entityString
        hasEntityOnBottomRight = terreno[nextLine][nextColumn] == entityString
    }
    //(ANY,MAX)
    else if (isLastColumn) {
        hasEntityOnTopLeft = terreno[previousLine][previousColumn] == entityString
        hasEntityOnBottomLeft = terreno[nextLine][previousColumn] == entityString
    }

    return hasEntityOnBottomLeft || hasEntityOnTopLeft || hasEntityOnBottomRight || hasEntityOnTopRight
}

fun contaTendasColuna(terreno: Array<Array<String?>>, coluna: Int): Int {
    var count = 0

    for (row in terreno.indices) {
        for (col in terreno[row].indices) {
            if (col == coluna && terreno[row][col] == arrayTentChar) {
                count++
            }

        }
    }

    return count
}

fun contaTendasLinha(terreno: Array<Array<String?>>, linha: Int): Int {
    var count = 0

    for (row in terreno.indices) {
        if (row == linha) {
            for (col in terreno[row].indices) {
                if (terreno[row][col] == arrayTentChar) {
                    count++
                }
            }
        }
    }


    return count
}

fun terminouJogo(
    terreno: Array<Array<String?>>,
    contadoresVerticais: Array<Int?>,
    contadoresHorizontais: Array<Int?>
): Boolean {
    val numLines = terreno.size
    val numColumns = terreno[0].size
    var gameEnded = false
    val boardFile = readBoardFromFile(numLines, numColumns)
    val boardFileSize = boardFile.size
    var treesHaveAdjacentTent: Boolean

    var treeCoordIndex = 2 //2 because the first two lines are for counters
    do {
        val treeCoords = boardFile[treeCoordIndex].split(',')
        val line = treeCoords[0].toInt()
        val column = treeCoords[1].toInt()

        val coords = Pair(line, column)
        treesHaveAdjacentTent = treeHasAdjacentTent(terreno, coords)

        treeCoordIndex++
    } while (treesHaveAdjacentTent && treeCoordIndex < boardFileSize)

    if (treesHaveAdjacentTent) {
        var lineIndex = 0
        var lineHasCorrectAmountOfTents: Boolean

        do {
            val totalTentsOnLine = contaTendasLinha(terreno, lineIndex)

            lineHasCorrectAmountOfTents = if (contadoresVerticais[lineIndex] == null && totalTentsOnLine == 0) {
                true
            } else {
                contadoresVerticais[lineIndex] == totalTentsOnLine
            }

            lineIndex++

        } while (lineHasCorrectAmountOfTents && lineIndex < numLines)

        var columnIndex = 0
        var columnHasCorrectAmountOfTents: Boolean

        do {
            val totalTentsOnColumn = contaTendasColuna(terreno, columnIndex)

            columnHasCorrectAmountOfTents = if (contadoresHorizontais[columnIndex] == null && totalTentsOnColumn == 0) {
                true
            } else {
                contadoresHorizontais[columnIndex] == totalTentsOnColumn
            }

            columnIndex++
        } while (columnHasCorrectAmountOfTents && columnIndex < numColumns)

        gameEnded = columnHasCorrectAmountOfTents && lineHasCorrectAmountOfTents
    }

    return gameEnded
}

fun treeHasAdjacentTent(terreno: Array<Array<String?>>, coords: Pair<Int, Int>): Boolean {
    val isFirstLine = coords.first == 0
    val isLastLine = coords.first + 1 == terreno.size
    val isFirstColumn = coords.second == 0
    val isLastColumn = coords.second + 1 == terreno[0].size

    var hasAdjacentTent = false

    val isCornerCoord =
        (isFirstLine && isLastColumn) || isFirstLine && isFirstColumn || (isLastLine && isLastColumn) || (isLastLine && isFirstColumn)
    if (isCornerCoord) {
        hasAdjacentTent = checkIfHasEntityWhenCornerCoords(terreno, coords, arrayTentChar)
    }

    val isBottomOrTopSidesCoord = (isFirstLine && !isCornerCoord) || (isLastLine && !isCornerCoord)
    if (isBottomOrTopSidesCoord) {
        hasAdjacentTent = checkIfHasEntityWhenTopOrBottomCoords(terreno, coords, arrayTentChar)
    }

    val isLeftOrRightSidesCoord = (isFirstColumn && !isCornerCoord) || (isLastColumn && !isCornerCoord)
    if (isLeftOrRightSidesCoord) {
        hasAdjacentTent = checkIfHasEntityWhenLeftOrRightCoords(terreno, coords, arrayTentChar)
    }

    val isCenterCoord = !isCornerCoord && !isBottomOrTopSidesCoord && !isLeftOrRightSidesCoord
    if (isCenterCoord) {
        hasAdjacentTent = checkIfHasEntityWhenCenterCoords(terreno, coords, arrayTentChar)
    }

    return hasAdjacentTent
}

