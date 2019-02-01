package net.octyl.rawr.android.data.entity

import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class Song(
        @Embedded val id: SongId
)