package net.octyl.rawr.android.dagger

import android.app.Activity
import androidx.fragment.app.Fragment
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
        modules = [
            FragsModule::class,
            ActivitiesModule::class,
            AndroidInjectionModule::class,
            AndroidSupportInjectionModule::class
        ]
)
@Singleton
abstract class RawrComponent {
    abstract val activityInjector: DispatchingAndroidInjector<Activity>
    abstract val fragmentInjector: DispatchingAndroidInjector<Fragment>
}