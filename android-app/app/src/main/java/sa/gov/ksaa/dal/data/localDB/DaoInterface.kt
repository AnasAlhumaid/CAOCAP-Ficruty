package sa.gov.ksaa.dal.data.localDB

import androidx.lifecycle.LiveData
import androidx.room.*
import sa.gov.ksaa.dal.data.models.MyModel

@Dao
interface DaoInterface {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert (myModel: MyModel): Long

    @Query("SELECT * FROM my_model")
    fun getAllModels(): LiveData<List<MyModel>>

    @Delete
    suspend fun deleteModel(myModel: MyModel)
}