package com.example.registros.data.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Clientes")
data class Cliente (
    @PrimaryKey
    val ClienteId: Int?=null,
    var nombre: String = "",
    var apellido: String = "",
    var edad: String = "",
    var numero: String = "",
)