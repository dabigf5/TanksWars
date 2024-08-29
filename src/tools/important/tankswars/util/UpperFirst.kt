package tools.important.tankswars.util

fun String.upperFirst(): String {
    return this.replaceFirstChar {if (it.isLowerCase()) it.uppercase() else it.toString()}
}