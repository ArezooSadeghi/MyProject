package com.example.sipmobileapp.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.sipmobileapp.model.ServerData;

import java.util.List;

@Dao
public interface ServerDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ServerData serverData);

    @Query("DELETE FROM server_data_tb WHERE centerName LIKE :centerName")
    void delete(String centerName);

    @Query("SELECT * FROM server_data_tb")
    LiveData<List<ServerData>> fetchServerDataList();

    @Query("SELECT * FROM server_data_tb WHERE centerName LIKE :centerName")
    ServerData fetchServerData(String centerName);
}
