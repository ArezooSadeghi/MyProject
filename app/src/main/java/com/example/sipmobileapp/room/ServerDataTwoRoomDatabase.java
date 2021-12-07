package com.example.sipmobileapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sipmobileapp.model.ServerDataTwo;


@Database(entities = {ServerDataTwo.class}, version = 1)
public abstract class ServerDataTwoRoomDatabase extends RoomDatabase {

    public abstract ServerDataTwoDao serverDataTwoDao();

    private static ServerDataTwoRoomDatabase INSTANCE;

    public static ServerDataTwoRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ServerDataTwoRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ServerDataTwoRoomDatabase.class, "server_data_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}