package net.octyl.rawr.android.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.octyl.rawr.android.R

class TracksFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_tracks, container, false)
    }
}