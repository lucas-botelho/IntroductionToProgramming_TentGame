fun main() {
    var userOption :Int?
    var isInvalidInput = false

    do {
        println(criaMenu())
        userOption = readln().toIntOrNull() ?: -1

        isInvalidInput = userOption.digitToChar() !in "01"

        if (isInvalidInput) {
            println("Opcao invalida\n")
        }
    } while (isInvalidInput)

    if (userOption == 1){

        var totalLines :Int?

        do {
            println("Quantas linhas?")
            totalLines = readln().toIntOrNull() ?: -1

            isInvalidInput = totalLines <= 0

            if (isInvalidInput) {
                println("Opcao invalida\n")
            }
        }while (isInvalidInput)

        var totalColumns :Int?

    }
}

fun criaMenu(): String {
    return "\nBem vindo ao jogo das tendas\n\n" + "1 - Novo jogo\n" + "0 - Sair\n"
}