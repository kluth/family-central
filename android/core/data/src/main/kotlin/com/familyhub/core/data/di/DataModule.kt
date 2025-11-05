package com.familyhub.core.data.di

import com.familyhub.core.data.repository.FirebaseAuthRepository
import com.familyhub.core.data.repository.FirebaseCalendarRepository
import com.familyhub.core.data.repository.FirebaseChatRepository
import com.familyhub.core.data.repository.FirebaseFamilyRepository
import com.familyhub.core.data.repository.FirebaseShoppingListRepository
import com.familyhub.core.data.src.main.kotlin.com.familyhub.core.data.repository.FirebaseTaskRepository
import com.familyhub.core.domain.repository.AuthRepository
import com.familyhub.core.domain.repository.CalendarRepository
import com.familyhub.core.domain.repository.ChatRepository
import com.familyhub.core.domain.repository.FamilyRepository
import com.familyhub.core.domain.repository.ShoppingListRepository
import com.familyhub.core.domain.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for data layer dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFamilyRepository(
        impl: FirebaseFamilyRepository
    ): FamilyRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: FirebaseTaskRepository
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: FirebaseChatRepository
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(
        impl: FirebaseCalendarRepository
    ): CalendarRepository

    @Binds
    @Singleton
    abstract fun bindShoppingListRepository(
        impl: FirebaseShoppingListRepository
    ): ShoppingListRepository
}
