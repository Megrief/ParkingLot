package parking

import kotlin.system.exitProcess

fun main() {
    ParkingLot()
}

class ParkingLot {
    private var parking = emptyArray<Car>()
    data class Car(val regNum: String = "", val color: String = "") {
        override fun toString(): String {
            return regNum + color
        }
    }
    init {
        command()
    }
    private fun command(input: String = readln()) {
        val list = listOf("park", "leave", "status", "reg_by_color", "spot_by_color", "spot_by_reg")
        Regex("[a-zA-Z_]+").find(input)?.value.let {
            when  {
                it == "create" -> creatingLot(input)
                it == "park" && parking.isNotEmpty() -> addingCar(input.drop(5))
                it == "leave" && parking.isNotEmpty()-> removing(input)
                it == "status" && parking.isNotEmpty() -> printStat()
                it == "reg_by_color" && parking.isNotEmpty() -> regByCol(input)
                it == "spot_by_color" && parking.isNotEmpty() -> spotByCol(input)
                it == "spot_by_reg" && parking.isNotEmpty() -> spotByReg(input)
                it == "exit" -> exitProcess(0)
                it in list && parking.isEmpty() -> println("Sorry, a parking lot has not been created.")
                else -> println("Wrong input")
            }
        }
        command()
    }
    private fun spotByReg(input: String) {
        input.trim().takeLastWhile { it != ' ' }.let { reg ->
            println(
                if (reg in parking.map { it.regNum }) {
                    parking.indexOfFirst { it.regNum == reg } + 1
                } else "No cars with registration number $reg were found."
            )
        }
    }
    private fun regByCol(input: String) {
        input.trim().takeLastWhile { it != ' ' }.let { color ->
            if (color.lowercase() in parking.map { car -> car.color.lowercase() }) {
                println(
                    parking.filter { it.color.lowercase() == color.lowercase() }.joinToString(", ") { it.regNum }
                )
            } else println("No cars with color $color were found.")
        }
    }
    private fun spotByCol(input: String) {
        input.trim().takeLastWhile { it != ' ' }.let { color ->
            if (color.lowercase() in parking.map { car -> car.color.lowercase() }) {
                println(
                    buildList {
                        parking.forEachIndexed { index, car -> if (car.color.lowercase() == color.lowercase()) this.add(index + 1) }
                    }.joinToString(", ")
                )
            } else println("No cars with color $color were found.")
        }
    }
    private fun printStat() {
        if (parking.none { it.toString().isNotBlank() }) {
            println("Parking lot is empty.")
        } else {
            parking.forEachIndexed { index, car ->
                if (car.toString().isNotEmpty()) {
                    println("${ index + 1 } ${ car.regNum } ${ car.color }")
                }
            }
        }
    }
    private fun creatingLot(input: String) {
        val size = input.takeLastWhile { it != ' ' }.let {
            if (it.matches(Regex("\\d+"))) it.toInt() else return println("Wrong input")
        }
        parking = Array(size) { Car() }
        println("Created a parking lot with $size spots.")
    }
    private fun addingCar(input: String) {
        input.split(' ').let {
            val free = parking.indexOfFirst { car -> car.toString().isBlank() }.let { num ->
                if (num != -1) num else return println("Sorry, the parking lot is full.")
            }
            if (checkAddIn(it)) {
                parking[free] = Car(it.first(), it.last())
                println("${ it.last() } car parked in spot ${ free + 1 }.")
            } else println("Wrong input")
        }
    }
    private fun checkAddIn(input: List<String>): Boolean {
        return input.size == 2 && input.first().matches(Regex("[\\w-]+"))
                && input.last().matches(Regex("[a-zA-Z]+"))
    }
    private fun removing(input: String) {
        val spot = input.takeLastWhile { it != ' ' }.let {
            if (it.matches(Regex("\\d+"))) it.toInt() else return println("Wrong input")
        }
        if (checkRemoveIn(spot)) {
            parking[spot - 1] = Car()
            println("Spot $spot is free.")
        } else println("There is no car in spot $spot.")
    }
    private fun checkRemoveIn(input: Int): Boolean {
        return input - 1 in 0..parking.lastIndex
                && parking[input - 1].toString().isNotEmpty()
    }
}