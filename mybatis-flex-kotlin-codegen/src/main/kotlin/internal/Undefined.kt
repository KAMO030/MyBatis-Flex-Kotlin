package com.mybatisflex.kotlin.codegen.internal

/**
 * 用来表示还未定义的类型，特定用于当做类型替换时的占位符。
 * 不应该出现在除了替换类型的过程以外的任何地方，也不应该尝试去获取其实例。
 *
 * 问：为什么不使用 Nothing ？因为 Nothing 可能是真的 Nothing 类型。
 */
class Undefined private constructor()
