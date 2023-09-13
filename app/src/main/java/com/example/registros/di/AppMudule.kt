package com.example.registros.di
import android.content.Context
import androidx.room.Room
import com.example.registros.domain.ClienteDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn( SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTicketDatabase(@ApplicationContext appContext:
                              Context): ClienteDb =
        Room.databaseBuilder(
            appContext,
            ClienteDb::class.java,
            "Cliente.db")
            .fallbackToDestructiveMigration()
            .build()
}
