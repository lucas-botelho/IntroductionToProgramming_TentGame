fun main() {
    var userOption: Int?
    var allUserInputsAreValid = true
    do {
        userOption = DrawMenu()

        if (userOption == 1) {
            println("Qual a sua data de nascimento? (dd-mm-yyyy)")
            val birthDate = readln()
            var birthDateText = validaDataNascimento(birthDate)

            //!= null means it's not a valid date
            if (birthDateText != null) {
                println(birthDateText)
                allUserInputsAreValid = false
            }

            if (allUserInputsAreValid) {
                val totalLines = GetUserBoardRowsSettings("Quantas linhas?", "Opcao invalida\n")
                val totalColumns = GetUserBoardRowsSettings("Quantas colunas?", "Opcao invalida\n")
                println(criaTerreno(totalLines, totalColumns))
            }
        }

        allUserInputsAreValid = allUserInputsAreValid || userOption == 0
    } while (!allUserInputsAreValid)
}

fun DrawMenu(): Int? {
    var userOption: Int?
    var isInvalidInput = false

    do {
        println(criaMenu())
        userOption = readln().toIntOrNull() ?: -1

        isInvalidInput = userOption < 0

        if (isInvalidInput) {
            println("Opcao invalida\n")
        }
    } while (isInvalidInput)

    return userOption
}

fun criaMenu(): String {
    return "\nBem vindo ao jogo das tendas\n\n" + "1 - Novo jogo\n" + "0 - Sair\n"
}

fun GetUserBoardRowsSettings(requestText: String, errorText: String): Int {
    var rows: Int?
    var isInvalidInput = false

    do {
        println(requestText)
        rows = readln().toIntOrNull() ?: -1

        isInvalidInput = rows <= 0

        if (isInvalidInput) {
            println(errorText)
        }
    } while (isInvalidInput)

    return rows ?: 0
}

fun criaTerreno(numLinhas: Int, numColunas: Int, mostraLegendaHorizontal: Boolean = true, mostraLegendaVertical: Boolean = true): String {
    var lineCount = 1
    var columnCount = 1
    var boardText = ""
    val tentChar = '\u25B3'

    if (mostraLegendaHorizontal) {
        boardText += criaLegendaHorizontal(numColunas)
    }

    while (lineCount <= numLinhas) {
        columnCount = 0
        var isSingleDigitLine = lineCount in 1..9

        while (columnCount <= numColunas) {
            var isFirstColumn = columnCount == 0
            var isLastColumn = columnCount == numColunas
            if (isFirstColumn) {
                boardText += when(isSingleDigitLine){
                    true -> " ${lineCount}|"
                    false -> "${lineCount}|"
                }
            } else {
                boardText += when(isLastColumn){
                    true -> " $tentChar"
                    false -> "   |"
                }
            }
            columnCount++
        }
        boardText += "\n"
        lineCount++
    }

    return boardText
}

fun criaLegendaHorizontal(numColunas: Int): String {
    var columnCount = 1
    var legendaHorizontal = "  "
    var rowChar = 'A'

    while (columnCount <= numColunas) {
        legendaHorizontal += "| $rowChar "

        rowChar++
        columnCount++
    }
    return "$legendaHorizontal\n"
}

fun validaDataNascimento(data: String?): String? {
    var result: String? = "Data invalida"

    if (data != null && data.length == 10) {
        val day = "${data[0]}${data[1]}".toIntOrNull()
        val month = "${data[3]}${data[4]}".toIntOrNull()
        val year = "${data[6]}${data[7]}${data[8]}${data[9]}".toIntOrNull()

        var isCorrectDateFormat = data[2] == '-' && data[5] == '-'
        if (!isCorrectDateFormat || day == null || month == null || year == null) {
            return result
        }

        val isUnderAge = year <= 2004 && month >= 11
        if (isUnderAge) {
            result = "Menor de idade nao pode jogar"
        }

        val isThirtyOneDaysMonth =
            month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12
        if (isThirtyOneDaysMonth) {
            if (day in 1..31) {
                isCorrectDateFormat = true
            }
        }

        val isFebruary = month == 2
        if (isFebruary) {
            val isLeapYear = ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
            if (isLeapYear && day in 1..29) {
                isCorrectDateFormat = true
            }
            if (day in 1..28) {
                isCorrectDateFormat = true
            }
        }

        val isThirtyDaysMonth = month == 4 || month == 6 || month == 9 || month == 11
        if (isThirtyDaysMonth && day in 1..30) {
            isCorrectDateFormat = true
        }

        if (isCorrectDateFormat) {
            result = null
        }
    }

    return result
}