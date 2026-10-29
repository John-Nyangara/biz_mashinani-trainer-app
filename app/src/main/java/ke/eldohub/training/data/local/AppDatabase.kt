package ke.eldohub.training.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ManualEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun manualDao(): ManualDao
}
