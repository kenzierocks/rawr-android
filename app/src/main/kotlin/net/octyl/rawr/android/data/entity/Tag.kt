package net.octyl.rawr.android.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.Instant
import java.util.Objects


sealed class Tag<T> {
    /**
     * The ID of this tag. Unique over all tags.
     */
    abstract var id: TagId
    /**
     * The ID of the song this tag is attached to.
     */
    abstract var songId: SongId
    /**
     * The data held in the tag.
     */
    abstract var data: T
}

@Entity
data class StringTag(
        @PrimaryKey @Embedded override var id: TagId,
        @Embedded(prefix = "song_") override var songId: SongId,
        override var data: String
) : Tag<String>()

@Entity
data class NumberTag(
        @PrimaryKey @Embedded override var id: TagId,
        @Embedded(prefix = "song_") override var songId: SongId,
        override var data: Long
) : Tag<Long>()

@Entity
data class DurationTag(
        @PrimaryKey @Embedded override var id: TagId,
        @Embedded(prefix = "song_") override var songId: SongId,
        override var data: Duration
) : Tag<Duration>()

@Entity
data class DateTimeTag(
        @PrimaryKey @Embedded override var id: TagId,
        @Embedded(prefix = "song_") override var songId: SongId,
        override var data: Instant
) : Tag<Instant>()

@Entity
data class ImageTag(
        @PrimaryKey @Embedded override var id: TagId,
        @Embedded(prefix = "song_") override var songId: SongId,
        override var data: ByteArray
) : Tag<ByteArray>() {
    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other is ImageTag -> {
                id == other.id && data.contentEquals(other.data)
            }
            else -> false
        }
    }

    override fun hashCode() = Objects.hash(id, data.contentHashCode())
}
