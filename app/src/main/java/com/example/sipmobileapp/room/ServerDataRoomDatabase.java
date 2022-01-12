package com.example.sipmobileapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sipmobileapp.model.ServerData;

@Database(entities = {ServerData.class}, version = 1)
public abstract class ServerDataRoomDatabase extends RoomDatabase {
    private static ServerDataRoomDatabase serverDataRoomDatabase;

    public abstract ServerDataDao getServerDataDao();

    public static ServerDataRoomDatabase getServerDataRoomDatabase(final Context context) {
        if (serverDataRoomDatabase == null) {
            synchronized (ServerDataRoomDatabase.class) {
                if (serverDataRoomDatabase == null) {
                    serverDataRoomDatabase = Room
                            .databaseBuilder(context.getApplicationContext(), ServerDataRoomDatabase.class, "server_data_tb")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return serverDataRoomDatabase;
    }
}
