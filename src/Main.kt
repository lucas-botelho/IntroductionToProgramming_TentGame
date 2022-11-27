fun main() {
    var userOption: Int?
    var allUserInputsAreValid = true
    var totalLines = 0
    var totalColumns = 0
    var birthDate: String?
    var birthDateText: String?

    val constInvalidOption = "Resposta invalida"

    do {
        userOption = drawMenu()

        if (userOption == 1) {
            if (allUserInputsAreValid) {
                totalLines = getUserBoardRowsSettings("Quantas linhas?", constInvalidOption)
                totalColumns = getUserBoardRowsSettings("Quantas colunas?", constInvalidOption)

                allUserInputsAreValid = validaTamanhoMapa(totalLines, totalColumns)
               if (allUserInputsAreValid) {
                    println("Qual a sua data de nascimento? (dd-mm-yyyy)")
                    birthDate = readln()
                    birthDateText = validaDataNascimento(birthDate)

                    //!= null means it's not a valid date
                    if (birthDateText != null) {
                        print(birthDateText)
                        allUserInputsAreValid = false
                    }
                }
            }
        }

        allUserInputsAreValid = allUserInputsAreValid || userOption == 0
    } while (!allUserInputsAreValid)

    if (userOption != 0) {
        println(criaTerreno(totalLines, totalColumns))
        println("Coordenadas da tenda? (ex: 1,B)")
        var coord = readln()
        if(!processaCoordenadas(coord, totalLines, totalColumns)){
            print("Coordenadas invalidas")
        }
    }
}

fun drawMenu(): Int? {
    var userOption: Int?
    var isInvalidInput = false

    do {
        println(criaMenu())
        userOption = readln().toIntOrNull() ?: -1

        isInvalidInput = userOption < 0

        if (isInvalidInput) {
            print("Opcao invalida")
        }
    } while (isInvalidInput)

    return userOption
}

fun criaMenu(): String {
    return "\nBem vindo ao jogo das tendas\n\n1 - Novo jogo\n0 - Sair\n"
}

fun getUserBoardRowsSettings(requestText: String, errorText: String): Int {
    var rows: Int?
    var isInvalidInput = false

    do {
        println(requestText)
        rows = readln().toIntOrNull() ?: -1

        isInvalidInput = rows <= 0

        if (isInvalidInput) {
            print(errorText)
        }
    } while (isInvalidInput)

    return rows ?: 0
}

fun criaTerreno(
    numLinhas: Int,
    numColunas: Int,
    mostraLegendaHorizontal: Boolean = true,
    mostraLegendaVertical: Boolean = true
): String {
    var lineCount = 1
    var columnCount :Int
    var boardText = ""
    val tentChar = '\u25B3'

    if (mostraLegendaHorizontal) {
        boardText += "   | ${criaLegendaHorizontal(numColunas)}\n"
    }

    while (lineCount <= numLinhas) {
        columnCount = 1
        var isSingleDigitLine = lineCount in 1..9

        while (columnCount <= numColunas) {
            var isFirstColumn = columnCount == 1
            var isLastColumn = columnCount == numColunas
            if (isFirstColumn && mostraLegendaVertical) {
                boardText += when (isSingleDigitLine) {
                    true -> " $lineCount |   |"
                    false -> "$lineCount |   |"
                }
            } else {
                boardText += when (isLastColumn) {
                    true -> " $tentChar"
                    false -> "   |"
                }
            }
            columnCount++
        }

        if(lineCount != numLinhas) {
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
        legendaHorizontal += when (columnCount){
            1 -> "$rowChar "
            numColunas -> "| $rowChar"
            else -> "| $rowChar "
        }
        rowChar++
        columnCount++
    }
    return "$legendaHorizontal"
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

        if (day != null && month != null && year != null && year <= 2022) {
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

    return when (numColunas) {
        6 -> numLinhas == 5 || numLinhas == 6
        8 -> numLinhas == 8 || numLinhas == 10
        10 -> numLinhas == 8 || numLinhas == 10
        else -> false
    }
}

fun processaCoordenadas(coordenadasStr: String?, numLines: Int, numColumns: Int): Boolean {

    var isValidCoord = false
    if (coordenadasStr != null && coordenadasStr.length == 3 ){
        val mapHeaderLabel = criaLegendaHorizontal(numColumns)
        val lastCharCode = mapHeaderLabel[mapHeaderLabel.length-1].code

        val isValidLineCoord = coordenadasStr[0].digitToIntOrNull() != null && coordenadasStr[0].digitToIntOrNull() in 1..numLines
        val isValidColumnCoord = coordenadasStr[2].uppercaseChar().code in 'A'.code..lastCharCode

        isValidCoord = isValidLineCoord && isValidColumnCoord
    }

    return isValidCoord
}
