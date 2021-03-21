package io.grimlocations.framework.util

inline fun <T1 : Any, T2 : Any, R : Any> guardLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> guardLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> guardLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, block: (T1, T2, T3, T4) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, R : Any> guardLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, p5: T5?, block: (T1, T2, T3, T4, T5) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null) block(p1, p2, p3, p4, p5) else null
}

inline fun <R : Any> guardBlankAndLet(p1: String?, p2: String?, block: (String, String) -> R?): R? {
    return guardFunAndLet(p1, p2, String::isNotBlank, block)
}

inline fun <R : Any> guardBlankAndLet(p1: String?, p2: String?, p3: String?, block: (String, String, String) -> R?): R? {
    return guardFunAndLet(p1, p2, p3, String::isNotBlank, block)
}

inline fun <R : Any> guardBlankAndLet(p1: String?, p2: String?, p3: String?, p4: String?, block: (String, String, String, String) -> R?): R? {
    return guardFunAndLet(p1, p2, p3, p4, String::isNotBlank, block)
}

inline fun <R : Any> guardBlankAndLet(p1: String?, p2: String?, p3: String?, p4: String?, p5: String?, block: (String, String, String, String, String) -> R?): R? {
    return guardFunAndLet(p1, p2, p3, p4, p5, String::isNotBlank, block)
}

inline fun <T : Any, R : Any> guardFunAndLet(p1: T?, p2: T?, noinline guardFun: (T) -> Boolean, block: (T, T) -> R?): R? {
    return if (p1 != null && guardFun(p1) && p2 != null && guardFun(p2)) block(p1, p2) else null
}

inline fun <T : Any, R : Any> guardFunAndLet(p1: T?, p2: T?, p3: T?, noinline guardFun: (T) -> Boolean, block: (T, T, T) -> R?): R? {
    return if (p1 != null && guardFun(p1) && p2 != null && guardFun(p2) && p3 != null && guardFun(p3)) block(p1, p2, p3) else null
}

inline fun <T : Any, R : Any> guardFunAndLet(p1: T?, p2: T?, p3: T?, p4: T?, noinline guardFun: (T) -> Boolean, block: (T, T, T, T) -> R?): R? {
    return if (p1 != null && guardFun(p1) && p2 != null && guardFun(p2) && p3 != null && guardFun(p3) && p4 != null && guardFun(p4)) block(p1, p2, p3, p4) else null
}

inline fun <T : Any, R : Any> guardFunAndLet(p1: T?, p2: T?, p3: T?, p4: T?, p5: T?, noinline guardFun: (T) -> Boolean, block: (T, T, T, T, T) -> R?): R? {
    return if (p1 != null && guardFun(p1) && p2 != null && guardFun(p2) && p3 != null && guardFun(p3) && p4 != null && guardFun(p4) && p5 != null && guardFun(p5)) block(p1, p2, p3, p4, p5) else null
}