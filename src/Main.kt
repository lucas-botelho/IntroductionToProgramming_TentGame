fun main() {
    var userOption: Int?
    var isInvalidInput = false

    do {
        println(criaMenu())
        userOption = readln().toIntOrNull()

        isInvalidInput = userOption == null || userOption !in 0..1

        if (isInvalidInput) {
            println("Opcao invalida\n")
        }
    } while (isInvalidInput)

    if (userOption == 1) {

        val totalLines = GetUserBoardSettings("Quantas linhas?", "Opcao invalida\n")
        val totalColumns = GetUserBoardSettings("Quantas colunas?", "Opcao invalida\n")

       println(criaTerreno(totalLines, totalColumns, true, true))
    }
}

fun criaMenu(): String {
    return "\nBem vindo ao jogo das tendas\n\n" + "1 - Novo jogo\n" + "0 - Sair\n"
}

fun GetUserBoardSettings(requestText: String, errorText: String): Int {
    var rows: Int?
    var isInvalidInput = false

    do {
        println(requestText)
        rows = readln().toIntOrNull()

        isInvalidInput = rows == null || rows <= 0

        if (isInvalidInput) {
            println(errorText)
        }
    } while (isInvalidInput)

    return rows?.toInt() ?: 0
}

fun criaTerreno(numLinhas: Int, numColunas: Int, mostraLegendaHorizontal: Boolean = true, mostraLegendaVertical: Boolean = true): String {
    var lineCount = 1
    var columnCount = 1
    var boardText = ""

    if (mostraLegendaHorizontal) {
        boardText += criaLegendaHorizontal(numColunas)
    }

    while (lineCount <= numLinhas) {
        columnCount = 1
        while (columnCount <= numColunas) {
            var isSingleDigit = lineCount in 1..9
            var isFirstColumn = columnCount == 1

            boardText += if (isFirstColumn) {
                if (isSingleDigit) {
                    " ${lineCount}|"
                } else {
                    "${lineCount}|"
                }
            } else {
                "   |"
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

    while (columnCount <= numColunas){
         legendaHorizontal += "| $rowChar "

        rowChar++
        columnCount++
    }
    return "$legendaHorizontal\n"
}
