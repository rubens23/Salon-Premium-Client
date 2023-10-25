package com.rubens.salonpremiumclient.viewmodel.di

import com.rubens.month_selector.MonthSelectorHelper
import com.rubens.salonpremiumclient.data.repositories.FirebaseRepository
import com.rubens.salonpremiumclient.data.repositories.FirebaseRepositoryImpl
import com.rubens.salonpremiumclient.utils.AgendamentoHelper
import com.rubens.salonpremiumclient.utils.AgendamentoHelperImpl
import com.rubens.salonpremiumclient.utils.CadastroValidator
import com.rubens.salonpremiumclient.utils.CadastroValidatorImpl
import com.rubens.salonpremiumclient.utils.CalendarHelper
import com.rubens.salonpremiumclient.utils.CalendarHelperImpl
import com.rubens.salonpremiumclient.utils.TypeConverter
import com.rubens.salonpremiumclient.utils.TypeConverterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelHiltModule {

    @Provides
    fun providesMonthSelector(): MonthSelectorHelper{
        return MonthSelectorHelper()
    }

    @Provides
    fun providesTypeConverters(): TypeConverter{
        return TypeConverterImpl()
    }

    @Provides
    fun providesCalendarHelper(): CalendarHelper{
        return CalendarHelperImpl()
    }

    @Provides
    fun providesFirebaseRepository(): FirebaseRepository{
        return FirebaseRepositoryImpl()
    }

    @Provides
    fun providesAgendamentoHelper(): AgendamentoHelper{
        return AgendamentoHelperImpl()
    }

    @Provides
    fun providesCadastroValidator(): CadastroValidator{
        return CadastroValidatorImpl()
    }
}