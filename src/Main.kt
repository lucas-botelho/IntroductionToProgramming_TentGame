fun main() {

    val userOption = DrawMenu()

    if (userOption == 1) {

        val totalLines = GetUserBoardRowsSettings("Quantas linhas?", "Opcao invalida\n")
        val totalColumns = GetUserBoardRowsSettings("Quantas colunas?", "Opcao invalida\n")

       println(criaTerreno(totalLines, totalColumns, true, true))
    }
}

fun DrawMenu() : Int? {
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

    if (mostraLegendaHorizontal) {
        boardText += criaLegendaHorizontal(numColunas)
    }

    while (lineCount <= numLinhas) {
        columnCount = 1
        var isSingleDigitLine = lineCount in 1..9

        while (columnCount <= numColunas) {
            var isFirstColumn = columnCount == 1

            boardText += if (isFirstColumn) {
                if (isSingleDigitLine) {
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

/*fun validaDataNascimento(data: String?) : String?{
    var resultado: String? = "Data invalida"

    if(data != null && data.length == 10){
        var dia = (data[0].toString() + data[1].toString()).toInt()
        var mes = (data[3].toString() + data[4].toString()).toInt()
        var ano = (data[6].toString() + data[7].toString() + data[8].toString() + data[9].toString()).toInt()

        if (ano >= 2004)

        var dataValida = false
        if(mes in 1..12){
            if (mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12){
                if (dia in 1..31){
                    dataValida = true
                }
            }else if (mes == 4 || mes == 6 || mes == 9 || mes == 11){
                if (dia in 1..30){
                    dataValida = true
                }
            }else{
                if(((ano % 4 == 0 && ano % 100 != 0) || ano % 400 == 0) && dia in 1..29){
                    dataValida = true
                }else{
                    if (dia in 1..28){
                        dataValida = true
                    }
                }
            }
        }
        if(dataValida){
            resultado = null
            if(ano > 2004 || (ano == 2004 && mes >= 11)){
                resultado = "Menor de idade nao pode jogar"
            }
        }
    }

    return resultado
}*/