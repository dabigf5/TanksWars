package tools.important.tankswars.util

import tanks.Team

// this function should really be a method in Team in vanilla
fun teamsAllied(a: Team?, b: Team?): Boolean {
    if (a == null || b == null) return false
    return a == b
}