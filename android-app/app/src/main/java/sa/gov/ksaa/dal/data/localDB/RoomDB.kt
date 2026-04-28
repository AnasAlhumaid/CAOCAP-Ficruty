package sa.gov.ksaa.dal.data.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sa.gov.ksaa.dal.data.models.MyModel

@Database(entities = [MyModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class RoomDB: RoomDatabase() {
//
//    abstract fun getModelDao(): DaoInterface
//
//    companion object {
//        @Volatile
//        private var instance: RoomDB? = null
//        private val LOCK = Any()
//
//        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
//            instance?: createDB(context).also {
//                instance = it
//            }
//        }
//
//        private fun createDB(context: Context) =
//            Room.databaseBuilder(context.applicationContext, RoomDB::class.java, "MyDB").build()
//    }
}