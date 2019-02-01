package net.octyl.rawr.android

import android.app.Application
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import net.octyl.rawr.android.dagger.DaggerRawrComponent
import net.octyl.rawr.android.dagger.RawrComponent

class RawrApp : Application(), HasActivityInjector, HasSupportFragmentInjector {

    private lateinit var component: RawrComponent

    override fun activityInjector() = component.activityInjector
    override fun supportFragmentInjector() = component.fragmentInjector

    override fun onCreate() {
        super.onCreate()
        component = DaggerRawrComponent.create()
    }
}