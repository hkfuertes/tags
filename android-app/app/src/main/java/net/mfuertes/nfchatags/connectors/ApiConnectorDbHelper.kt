package net.mfuertes.nfchatags.connectors

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf

class ApiConnectorDbHelper(context: Context):
SQLiteOpenHelper(context, DATABASE_NAME, null, 1){
    val tableDefinition = """
            CREATE TABLE $TABLE_NAME (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            ip TEXT,
            name TEXT,
            port INTEGER,
            pat TEXT)
        """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(tableDefinition)
    }

    override fun onUpgrade(db: SQLiteDatabase, vog: Int, nv: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    private fun addConnector(connector: ApiConnector): Long{
        val values = contentValuesOf(
            Pair("ip",connector.ip),
            Pair("name", connector.name),
            Pair("port", connector.port),
            Pair("pat", connector.pat)
        )
        val db = this.writableDatabase
        val response = db.insert(TABLE_NAME, null, values)
        db.close()
        return response
    }
    private fun updateConnector(connector: ApiConnector): Int{
        val values = contentValuesOf(
            Pair("ip",connector.ip),
            Pair("name", connector.name),
            Pair("port", connector.port),
            Pair("pat", connector.pat)
        )
        val db = this.writableDatabase
        val response = db.update(TABLE_NAME,  values, "id = ?", arrayOf(connector.id.toString()))
        db.close()
        return response
    }
    fun upsertConnector(connector: ApiConnector): Long {
        return if (connector.id != null){
            updateConnector(connector).toLong()
        }else{
            addConnector(connector)
        }
    }
    @SuppressLint("Range")
    fun getConnectors(): ArrayList<ApiConnector>{
        var connectors = ArrayList<ApiConnector>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if(cursor.count > 0){
            cursor!!.moveToFirst()
            connectors.add(
                ApiConnector(
                    ip = cursor.getString(cursor.getColumnIndex("ip")),
                    name = cursor.getString(cursor.getColumnIndex("name")),
                    pat = cursor.getString(cursor.getColumnIndex("pat")),
                    port = cursor.getInt(cursor.getColumnIndex("port")),
                    id = cursor.getInt(cursor.getColumnIndex("id"))
                )
            )
            while (cursor.moveToNext()){
                connectors.add(
                    ApiConnector(
                        ip = cursor.getString(cursor.getColumnIndex("ip")),
                        name = cursor.getString(cursor.getColumnIndex("name")),
                        pat = cursor.getString(cursor.getColumnIndex("pat")),
                        port = cursor.getInt(cursor.getColumnIndex("port")),
                        id = cursor.getInt(cursor.getColumnIndex("id"))
                    )
                )
            }
        }
        return connectors
    }

    @SuppressLint("Range")
    fun getConnectorById(id: Int): ApiConnector?{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE id = '$id'", null)
        if(cursor.count > 0){
            cursor!!.moveToFirst()
            return ApiConnector(
                ip = cursor.getString(cursor.getColumnIndex("ip")),
                name = cursor.getString(cursor.getColumnIndex("name")),
                pat = cursor.getString(cursor.getColumnIndex("pat")),
                port = cursor.getInt(cursor.getColumnIndex("port")),
                id = cursor.getInt(cursor.getColumnIndex("id"))
            )
        }
        return null
    }

    fun deleteConnector(id: Int): Int{
        return this.writableDatabase.delete(TABLE_NAME, "id = ?", arrayOf(id.toString()))
    }

    companion object{
        const val DATABASE_NAME = "connectors.db"
        const val TABLE_NAME = "api_connector"
    }
}