package com.mybatisflex.kotlin.example.entity

import com.mybatisflex.annotation.SetListener


class AccountOnSetListener : SetListener {
	override fun onSet(entity: Any, property: String, value: Any): Any {
		println(">>>>>>> property: $property value:$value")
		return value
	}
}

