package net.octyl.rawr.android.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.octyl.rawr.android.RawrMainActivity

@Module
abstract class ActivitiesModule {
    @get:ContributesAndroidInjector(modules = [])
    abstract val rawrMainActivity: RawrMainActivity
}