package com.example.sipmobileapp.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.sipmobileapp.model.ServerDataTwo;

import java.util.List;

@Dao
public interface ServerDataTwoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ServerDataTwo serverData);

    @Query("DELETE FROM server_data_table WHERE centerName LIKE :centerName")
    void delete(String centerName);

    @Query("SELECT * FROM server_data_table")
    LiveData<List<ServerDataTwo>> getServerDataList();

    @Query("SELECT * FROM server_data_table WHERE centerName LIKE :centerName")
    ServerDataTwo getServerData(String centerName);
}
