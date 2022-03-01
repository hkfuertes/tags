package net.mfuertes.nfchatags.connectors

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ConnectorDbHelper(context: Context, factory: SQLiteDatabase.CursorFactory?):
SQLiteOpenHelper(context, "api_connector", factory, 1){
    override fun onCreate(p0: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}