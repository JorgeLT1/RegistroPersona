package com.example.registros.data.local.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.registros.data.local.entities.Cliente
import kotlinx.coroutines.flow.Flow


@Dao
interface ClienteDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(cliente: Cliente)
    @Query(
        """
 SELECT *
 FROM Clientes
 WHERE ClienteId=:id
 LIMIT 1
 """
    )
    suspend fun find(id: Int) : Cliente
    @Delete
    suspend fun delete(cliente: Cliente)
    @Query("SELECT * FROM Clientes")
    fun getAll(): Flow<List<Cliente>>
}
