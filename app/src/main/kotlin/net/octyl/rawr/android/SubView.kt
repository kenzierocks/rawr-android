package net.octyl.rawr.android

import androidx.fragment.app.Fragment
import net.octyl.rawr.android.frag.AvailablePlaylistFragment
import net.octyl.rawr.android.frag.TracksFragment

enum class SubView(val newFragment: () -> Fragment) {
    TRACKS(::TracksFragment),
    AVAILABLE_PLAYLISTS(::AvailablePlaylistFragment);
}