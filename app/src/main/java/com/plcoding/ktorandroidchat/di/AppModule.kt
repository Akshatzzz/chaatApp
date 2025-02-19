package com.plcoding.ktorandroidchat.di

import com.plcoding.ktorandroidchat.data.remote.dto.ChatSocketServiceImpl
import com.plcoding.ktorandroidchat.data.remote.dto.MessageApiInterfaceImpl
import com.plcoding.ktorandroidchat.domain.model.ChatSocketService
import com.plcoding.ktorandroidchat.domain.model.MessageApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }
    @Provides
    @Singleton
    fun provideMessageApiInterface(client: HttpClient): MessageApiInterface {
        return MessageApiInterfaceImpl(client)
    }

    @Provides
    @Singleton
    fun provideChatSocketService(client: HttpClient): ChatSocketService {
        return ChatSocketServiceImpl(client)
    }
}