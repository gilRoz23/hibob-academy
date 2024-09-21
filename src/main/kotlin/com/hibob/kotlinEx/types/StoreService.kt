package com.hibob.kotlinEx.types

import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class StoreService {
    fun pay(carts: List<Cart>, payment: Payment): Map<String, Check> {
        return carts.associate { cart ->
            cart.clientId to checkout(cart, payment)
        }
    }
}
