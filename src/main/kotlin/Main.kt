import helper.intFromBytes
import helper.stringFromBytes
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Expecting 1 argument: String with hex data")
        exitProcess(13)
    }

    println(Parser(args[0]).parseData())
}