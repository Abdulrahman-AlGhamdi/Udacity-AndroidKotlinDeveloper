package com.udacity.project4

import android.app.Application
import com.udacity.project4.database.LocalDatabase
import com.udacity.project4.repository.RemindersLocalRepository
import com.udacity.project4.repository.RemindersRepository
import com.udacity.project4.ui.RemindersListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val appModule = module {
            single { LocalDatabase.createRemindersDao(this@App) }
            single { RemindersLocalRepository(get()) as RemindersRepository }
            viewModel { RemindersListViewModel(get()) }
        }

        startKoin {
            androidContext(this@App)
            modules(listOf(appModule))
        }
    }
}