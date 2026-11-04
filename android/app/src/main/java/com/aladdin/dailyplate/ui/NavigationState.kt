package com.aladdin.dailyplate.ui

/** Stable route IDs are independent of translated labels and domain record IDs. */
enum class Destination { Welcome, Today, Menu, Add, Progress, Profile, Setup, Account }

data class NavigationState(val stack: List<Destination> = listOf(Destination.Welcome)) {
    val current: Destination get() = stack.last()
    fun open(destination: Destination) =
        if (current == destination) this else copy(stack = stack + destination)
    fun back() = if (stack.size > 1) copy(stack = stack.dropLast(1)) else this
    fun encode() = stack.joinToString("|") { it.name }
    companion object {
        fun restore(value: String): NavigationState {
            val routes = value.split('|').mapNotNull { id -> Destination.entries.find { it.name == id } }
            return NavigationState(routes.ifEmpty { listOf(Destination.Welcome) })
        }
    }
}
