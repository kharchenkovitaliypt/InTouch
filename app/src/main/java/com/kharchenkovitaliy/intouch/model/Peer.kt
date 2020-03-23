package com.kharchenkovitaliy.intouch.model

inline class PeerId(val value: String)

data class Peer(
    val id: PeerId,
    val name: String
)