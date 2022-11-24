fun main() {
    var userOption :Int?
    var isInvalidInput = false

    do {
        println(criaMenu())
        userOption = readln().toIntOrNull()

        isInvalidInput = userOption != null && (userOption != 0 && userOption != 1)

        if (isInvalidInput) {
            println("Opcao invalida\n")
        }
    } while (isInvalidInput)

    if (userOption == 1){

        var totalLines :Int?

        do {
            println("Quantas linhas?")
            totalLines = readln().toIntOrNull()

            isInvalidInput = totalLines == null || totalLines <= 0

            if (isInvalidInput) {
                println("Opcao invalida\n")
            }
        }while (isInvalidInput)

        var totalColumns :Int?

    }
}

fun criaMenu(): String {
    return "Bem vindo ao jogo das tendas\n\n" + "1 - Novo jogo\n" + "0 - Sair"
}