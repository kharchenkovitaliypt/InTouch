package com.vitaliykharchenko.intouch.model

inline class PeerId(val value: String)

data class Peer(
    val id: String,
    val name: String
)