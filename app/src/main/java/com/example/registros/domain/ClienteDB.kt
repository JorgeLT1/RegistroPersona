package com.example.registros.domain
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.registros.data.local.dao.ClienteDao
import com.example.registros.data.local.entities.Cliente
@Database(
    entities = [Cliente::class],
    version = 2
)
abstract class ClienteDb : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
}
