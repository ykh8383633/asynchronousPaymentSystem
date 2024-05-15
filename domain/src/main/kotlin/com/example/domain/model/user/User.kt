package com.example.domain.model.user

import java.time.Instant

class User(
    var id: Long? = null,
    var uid: String? = null,
    var email: String? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {

}