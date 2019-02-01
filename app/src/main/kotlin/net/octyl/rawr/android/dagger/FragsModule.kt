package net.octyl.rawr.android.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.octyl.rawr.android.frag.AvailablePlaylistFragment

@Module
abstract class FragsModule {
    @get:ContributesAndroidInjector(modules = [])
    abstract val availablePlaylistFragment: AvailablePlaylistFragment
}