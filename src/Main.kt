import java.io.File

const val treeIcon = '\u25B3'
const val arrayTreeChar = "A"
const val arrayTentChar = "T"
const val constInvalidOption = "Resposta invalida"
const val constInvalidCoords = "Coordenadas invalidas"
const val constInvalidTentPlacement = "Tenda nao pode ser colocada nestas coordenadas"
const val fiveEmptySpacesConst = "     "
const val threeEmptySpacesConst = "   "
const val twoEmptySpacesConst = "  "
const val endGameMessage = "Parabens! Terminou o jogo!"

fun main() {
    runGame()
}

fun runGame() {
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
                            gameEnded = true
                        } else {
                            val proccessedCoords = processaCoordenadas(userInputCoords, totalLines, totalColumns)

                            if (proccessedCoords == null) {
                                print(constInvalidCoords)
                            } else {
                                if (colocaTenda(terreno, proccessedCoords)) {
                                    showBoard = true
                                    val verticalCounters = leContadoresDoFicheiro(totalLines, totalColumns, true)
                                    val horizontalCounters = leContadoresDoFicheiro(totalLines, totalColumns, false)
                                    gameEnded = terminouJogo(terreno, verticalCounters, horizontalCounters)

                                    if (gameEnded){
                                        drawBoard(totalLines, totalColumns, terreno)
                                        println("$endGameMessage")
                                    }
                                } else {
                                    println(constInvalidTentPlacement)
                                    showBoard = false
                                }
                            }
                        }
                    }
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
    println("\n${criaTerreno(terreno, verticalCountersFromFile, horizontalCountersFromFile, true, true)}")
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
    var boardText = ""
    val numColunas = terreno[0].size
    val numLinhas = terreno.size

    if (contadoresVerticais != null) {
        boardText += "$fiveEmptySpacesConst  ${criaLegendaContadoresHorizontal(contadoresVerticais)}\n"
    }
    if (mostraLegendaHorizontal) {
        boardText += "$fiveEmptySpacesConst| ${criaLegendaHorizontal(numColunas)}\n"
    }

    while (lineCount < numLinhas) {
        if (contadoresHorizontais != null && lineCount in contadoresHorizontais.indices) {
            boardText += "${contadoresHorizontais[lineCount]?.toString() ?: " "} "
        } else {
            boardText += twoEmptySpacesConst
        }

        if (mostraLegendaVertical) {
            val lineNumber = lineCount + 1
            val isSingleDigitLine = lineNumber in 1..9
            boardText += when (isSingleDigitLine) {
                true -> " $lineNumber "
                false -> "$lineNumber "
            }
        } else {
            boardText += threeEmptySpacesConst
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
        0
    } else {
        1

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
    return File("${numLines}x${numColumns}.txt").readLines()
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
    var horizontalCountersStr = ""

    if (contadoresHorizontal != null) {
        var counterIdx = 0
        while (counterIdx < contadoresHorizontal.size) {
            //Espaço por cima dos pipes
            if (horizontalCountersStr != "") {
                horizontalCountersStr += " "
            }
            //Espaço + espaço no lugar do numero
            if (contadoresHorizontal[counterIdx] == null) {
                horizontalCountersStr += "  "
            } else {
                horizontalCountersStr += if (horizontalCountersStr == "") {
                    "${contadoresHorizontal[counterIdx]}"
                } else {
                    " ${contadoresHorizontal[counterIdx]}"

                }
            }
            val isLastCounter = counterIdx == contadoresHorizontal.size - 1
            if (!isLastCounter && horizontalCountersStr != "  ") {
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

    var tentWasPlaced = false
    if ((isTentSlot || isValidSlot) && !isTreeSlot) {
        terreno[coords.first][coords.second] = when (terreno[coords.first][coords.second]) {
            arrayTentChar -> null
            else -> arrayTentChar
        }
        tentWasPlaced = true
    }


    return tentWasPlaced
}

fun temArvoreAdjacente(terreno: Array<Array<String?>>, coords: Pair<Int, Int>): Boolean {
    return checkIfLocationIsValidAndHasEntity(terreno, coords.first, coords.second, arrayTreeChar)
}

fun checkIfLocationIsValidAndHasEntity(
    terreno: Array<Array<String?>>,
    line: Int,
    column: Int,
    entityChar: String
): Boolean {
    val nextLine = line + 1
    val previousLine = line - 1
    val nextColumn = column + 1
    val previousColumn = column - 1

    val maxLine = nextLine
    var lineIndex = previousLine
    while (lineIndex <= maxLine) {
        val isLineInBounds = lineIndex in terreno.indices
        if (isLineInBounds) {

            val maxColumn = nextColumn
            var columnIndex = previousColumn

            while (columnIndex <= maxColumn) {
                val isColumnInBounds = columnIndex in terreno[lineIndex].indices
                if (isColumnInBounds) {

                    val isSelf = lineIndex == line && columnIndex == column
                    val isTopLeft = lineIndex == previousLine && columnIndex == previousColumn
                    val isTopRight = lineIndex == previousLine && columnIndex == nextColumn
                    val isBottomLeft = lineIndex == nextLine && columnIndex == previousColumn
                    val isBottomRight = lineIndex == nextLine && columnIndex == nextColumn
                    val isDiagonal = isTopLeft || isTopRight || isBottomLeft || isBottomRight
                    if (!(entityChar == arrayTreeChar && (isDiagonal)) && !isSelf) {
                        if (terreno[lineIndex][columnIndex] == entityChar) {
                            return true
                        }
                    }
                }
                columnIndex++
            }
        }
        lineIndex++
    }
    return false
}

fun temTendaAdjacente(terreno: Array<Array<String?>>, coords: Pair<Int, Int>): Boolean {
    return checkIfLocationIsValidAndHasEntity(terreno, coords.first, coords.second, arrayTentChar)
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

        var lineIndex = 0
        var lineHasCorrectAmountOfTents: Boolean

        do {
            val totalTentsOnLine = contaTendasLinha(terreno, lineIndex)

            lineHasCorrectAmountOfTents = if (contadoresHorizontais[lineIndex] == null && totalTentsOnLine == 0) {
                true
            } else {
                contadoresHorizontais[lineIndex] == totalTentsOnLine
            }

            lineIndex++

        } while (lineHasCorrectAmountOfTents && lineIndex < numLines)

        var columnIndex = 0
        var columnHasCorrectAmountOfTents: Boolean

        do {
            val totalTentsOnColumn = contaTendasColuna(terreno, columnIndex)

            columnHasCorrectAmountOfTents = if (contadoresVerticais[columnIndex] == null && totalTentsOnColumn == 0) {
                true
            } else {
                contadoresVerticais[columnIndex] == totalTentsOnColumn
            }

            columnIndex++
        } while (columnHasCorrectAmountOfTents && columnIndex < numColumns)

        gameEnded = columnHasCorrectAmountOfTents && lineHasCorrectAmountOfTents

    return gameEnded
}