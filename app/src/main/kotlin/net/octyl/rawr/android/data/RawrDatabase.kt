package net.octyl.rawr.android.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.octyl.rawr.android.data.dao.DateTimeTagDao
import net.octyl.rawr.android.data.dao.DurationTagDao
import net.octyl.rawr.android.data.dao.ImageTagDao
import net.octyl.rawr.android.data.dao.NumberTagDao
import net.octyl.rawr.android.data.dao.StringTagDao
import net.octyl.rawr.android.data.entity.DateTimeTag
import net.octyl.rawr.android.data.entity.DurationTag
import net.octyl.rawr.android.data.entity.ImageTag
import net.octyl.rawr.android.data.entity.NumberTag
import net.octyl.rawr.android.data.entity.StringTag

@Database(
        entities = [
            StringTag::class,
            NumberTag::class,
            DurationTag::class,
            DateTimeTag::class,
            ImageTag::class
        ],
        views = [
        ],
        version = 1
)
@TypeConverters(RawrConversions::class)
abstract class RawrDatabase : RoomDatabase() {
    abstract fun stringTagDao(): StringTagDao
    abstract fun numberTagDao(): DurationTagDao
    abstract fun durationTagDao(): NumberTagDao
    abstract fun dateTimeTagDao(): DateTimeTagDao
    abstract fun imageTagDao(): ImageTagDao
}