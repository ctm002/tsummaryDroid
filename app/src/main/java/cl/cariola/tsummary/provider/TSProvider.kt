package cl.cariola.tsummary.provider

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import cl.cariola.tsummary.util.SelectionBuilder

class TSProvider: ContentProvider() {

    private val AUTHORITY = TSContract.AUTHORITY

    val ROUTE_HORAS = 1
    val ROUTE_HORAS_ID = 2
    val ROUTE_PROYECTOS = 3
    val ROUTE_PROYECTOS_ID = 4
    val JOIN_DIR_TYPE = 10

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    init
    {
        sUriMatcher.addURI(AUTHORITY, "Horas", ROUTE_HORAS)
        sUriMatcher.addURI(AUTHORITY, "Horas/#", ROUTE_HORAS_ID)
        sUriMatcher.addURI(AUTHORITY, "Proyectos", ROUTE_PROYECTOS)
        sUriMatcher.addURI(AUTHORITY, "Proyectos/#", ROUTE_PROYECTOS_ID)
    }

    lateinit private var mDataBaseHelper: DatabaseClient

    override fun onCreate():Boolean
    {
        mDataBaseHelper = DatabaseClient(context)
        return true
    }

    override fun getType(uri:Uri):String
    {
        val match = sUriMatcher.match(uri)
        when (match)
        {
            ROUTE_HORAS -> return TSContract.RegistroHora.CONTENT_TYPE
            ROUTE_HORAS_ID -> return TSContract.RegistroHora.CONTENT_ITEM_TYPE
            ROUTE_PROYECTOS -> return TSContract.Proyecto.CONTENT_TYPE
            ROUTE_PROYECTOS_ID -> return TSContract.Proyecto.CONTENT_ITEM_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun query(uri:Uri, projection:Array<String>, selection:String?, selectionArgs:Array<String>?, sortOrder:String?):Cursor
    {
        val db = mDataBaseHelper.readableDatabase
        val builder = SelectionBuilder()
        val uriMatch = sUriMatcher.match(uri)
        when (uriMatch) {
            ROUTE_HORAS ->
            {
                val c= db.query("${TSContract.RegistroHora.TABlE_NAME} r LEFT OUTER JOIN ${TSContract.Proyecto.TABlE_NAME} p ON r.${TSContract.RegistroHora.COL_PRO_ID}= p.${TSContract.Proyecto.COL_ID}",
                        projection, selection, selectionArgs,null, null, sortOrder)
                val ctx = context
                assert(ctx != null)
                c.setNotificationUri(ctx!!.contentResolver, uri)
                return c
            }

            ROUTE_HORAS_ID -> {
                val id = uri.getLastPathSegment()
                builder.where(TSContract.RegistroHora.COL_ID + "=?", id)
                builder.table(TSContract.RegistroHora.TABlE_NAME)
                        .where(selection!!, selectionArgs!!.joinToString {""})
                val c = builder.query(db, projection, sortOrder!!)
                val ctx = context
                assert(ctx != null)
                c.setNotificationUri(ctx!!.contentResolver, uri)
                return c
            }

            ROUTE_PROYECTOS ->
            {
                val c= db.query(TSContract.Proyecto.TABlE_NAME, projection, selection, selectionArgs,null, null, sortOrder)
                val ctx = context
                assert(ctx != null)
                c.setNotificationUri(ctx!!.contentResolver, uri)
                return c
            }

            ROUTE_PROYECTOS_ID ->
            {
                val id = uri.getLastPathSegment()
                builder.where(TSContract.Proyecto.COL_ID + "=?", id)
                builder.table(TSContract.Proyecto.TABlE_NAME)
                        .where(selection!!, selectionArgs!!.joinToString {""})
                val c = builder.query(db, projection, sortOrder!!)

                val ctx = context
                assert(ctx != null)
                c.setNotificationUri(ctx!!.contentResolver, uri)
                return c
            }

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun insert(uri:Uri, values:ContentValues):Uri
    {
        val db = mDataBaseHelper.writableDatabase
        assert(db != null)
        val match = sUriMatcher.match(uri)
        val result:Uri

        when (match) {
            ROUTE_HORAS ->
            {
                val id = db!!.insertOrThrow(TSContract.RegistroHora.TABlE_NAME, null, values)
                result = Uri.parse("${TSContract.RegistroHora.CONTENT_URI}/${id}")
            }

            ROUTE_HORAS_ID -> throw UnsupportedOperationException("Insert not supported on URI: $uri")

            ROUTE_PROYECTOS ->
            {
                val id = db!!.insertOrThrow(TSContract.Proyecto.TABlE_NAME, null, values)
                result = Uri.parse("${TSContract.Proyecto.CONTENT_URI}/${id}")
            }

            ROUTE_PROYECTOS_ID -> throw UnsupportedOperationException("Insert not supported on URI: $uri")

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        // Send broadcast to registered ContentObservers, to refresh UI.
        val ctx = context
        assert(ctx != null)
        ctx!!.contentResolver.notifyChange(uri, null, false)
        return result
    }

    override fun delete(uri:Uri, selection:String, selectionArgs:Array<String>):Int
    {
        val builder = SelectionBuilder()
        val db = mDataBaseHelper.writableDatabase
        val match = sUriMatcher.match(uri)
        val count:Int

        when (match) {

            ROUTE_HORAS -> count = builder.table(TSContract.RegistroHora.TABlE_NAME)
                .where(selection, selectionArgs.joinToString { " " })
                .delete(db)

            ROUTE_HORAS_ID ->
            {
                val id = uri.getLastPathSegment()
                count = builder.table(TSContract.RegistroHora.TABlE_NAME)
                        .where(TSContract.RegistroHora.COL_ID + "=?", id)
                        .where(selection, selectionArgs.joinToString {  "" })
                        .delete(db)
            }

            ROUTE_PROYECTOS -> count = builder.table(TSContract.Proyecto.TABlE_NAME)
                    .where(selection, selectionArgs.joinToString {" "})
                    .delete(db)

            ROUTE_PROYECTOS_ID -> {
                val id = uri.getLastPathSegment()
                count = builder.table(TSContract.Proyecto.TABlE_NAME)
                        .where(TSContract.RegistroHora.COL_ID + "=?", id)
                        .where(selection, selectionArgs.joinToString {" "})
                        .delete(db)
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        // Send broadcast to registered ContentObservers, to refresh UI.
        val ctx = context
        assert(ctx != null)
        ctx!!.contentResolver.notifyChange(uri, null, false)
        return count
    }

    override fun update(uri:Uri, values:ContentValues, selection:String, selectionArgs:Array<String>):Int
    {
        val builder = SelectionBuilder()
        val db = mDataBaseHelper.writableDatabase
        val match = sUriMatcher.match(uri)
        val count:Int

        when (match) {

            ROUTE_HORAS -> count = builder.table(TSContract.RegistroHora.TABlE_NAME)
                .where(selection, selectionArgs.joinToString { " " })
                .update(db, values)

            ROUTE_HORAS_ID -> {
                val id = uri.getLastPathSegment()
                count = builder.table(TSContract.RegistroHora.TABlE_NAME)
                .where(TSContract.RegistroHora.COL_ID + "=?", id)
                .where(selection, selectionArgs.joinToString {  " " })
                .update(db, values)
            }

            ROUTE_PROYECTOS -> count = builder.table(TSContract.Proyecto.TABlE_NAME)
                    .where(selection, selectionArgs.joinToString { " " })
                    .update(db, values)

            ROUTE_PROYECTOS_ID -> {
                val id = uri.getLastPathSegment()
                count = builder.table(TSContract.Proyecto.TABlE_NAME)
                        .where(TSContract.RegistroHora.COL_ID + "=?", id)
                        .where(selection, selectionArgs.joinToString {  " " })
                        .update(db, values)
            }

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        val ctx = context
        assert(ctx != null)
        ctx!!.contentResolver.notifyChange(uri, null, false)
        return count
    }

    internal class DatabaseClient(context:Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
    {
        override fun onCreate(db:SQLiteDatabase) {
            db.execSQL(SQL_CREATE_TB_HORAS)
            db.execSQL(SQL_CREATE_TB_PROYECTOS)
        }

        override fun onUpgrade(db:SQLiteDatabase, oldVersion:Int, newVersion:Int) {
            db.execSQL(SQL_DELETE_TB_HORAS)
            db.execSQL(SQL_DELETE_TB_PROYECTOS)
            onCreate(db)
        }

        companion object
        {
            val DATABASE_VERSION = 1
            val DATABASE_NAME = "tsummary.db"

            private val SQL_CREATE_TB_HORAS = TSContract.RegistroHora.CREATE_TABLE
            private val SQL_CREATE_TB_PROYECTOS = TSContract.Proyecto.CREATE_TABLE

            private val SQL_DELETE_TB_HORAS = "DROP TABLE IF EXISTS " + TSContract.RegistroHora.TABlE_NAME
            private val SQL_DELETE_TB_PROYECTOS = "DROP TABLE IF EXISTS " + TSContract.Proyecto.TABlE_NAME
        }
    }

}