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

class RegistroHoraProvider: ContentProvider() {

    lateinit private var mDataBaseHelper: DatabaseClient

    override fun onCreate():Boolean {
        mDataBaseHelper = DatabaseClient(context)
        return true
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
     override fun getType(uri:Uri):String {
        val match = sUriMatcher.match(uri)
        when (match) {
            ROUTE_ENTRIES -> return RegistroHoraContract.Entry.CONTENT_TYPE
            ROUTE_ENTRIES_ID -> return RegistroHoraContract.Entry.CONTENT_ITEM_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    /**
     * Perform a database query by URI.
     *
     *
     * Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     */
    override fun query(uri:Uri, projection:Array<String>, selection:String, selectionArgs:Array<String>,
        sortOrder:String):Cursor
    {
        val db = mDataBaseHelper.readableDatabase
        val builder = SelectionBuilder()
        val uriMatch = sUriMatcher.match(uri)
        when (uriMatch) {
            ROUTE_ENTRIES_ID -> {
                 // Return a single entry, by ID.
                val id = uri.getLastPathSegment()
                builder.where(RegistroHoraContract.Entry.COL_ID + "=?", id)
                 // Return all known entries.
                builder.table(RegistroHoraContract.Entry.TABlE_NAME)
                .where(selection, selectionArgs.joinToString { " " })
                val c = builder.query(db, projection, sortOrder)
                 // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                val ctx = context
                assert(ctx != null)
                c.setNotificationUri(ctx!!.contentResolver, uri)
                return c
            }
            ROUTE_ENTRIES ->
            {
                builder.table(RegistroHoraContract.Entry.TABlE_NAME).where(selection, selectionArgs.joinToString { " " })
                val c = builder.query(db, projection, sortOrder)
                val ctx = context
                assert(ctx != null)
                c.setNotificationUri(ctx!!.contentResolver, uri)
                return c
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    /**
     * Insert a new entry into the database.
     */
    override fun insert(uri:Uri, values:ContentValues):Uri
    {
        val db = mDataBaseHelper.writableDatabase
        assert(db != null)
        val match = sUriMatcher.match(uri)
        val result:Uri

        when (match) {
            ROUTE_ENTRIES -> {
                val id = db!!.insertOrThrow(RegistroHoraContract.Entry.TABlE_NAME, null, values)
                result = Uri.parse("${RegistroHoraContract.Entry.CONTENT_URI}/${id}")
            }
            ROUTE_ENTRIES_ID -> throw UnsupportedOperationException("Insert not supported on URI: $uri")
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        // Send broadcast to registered ContentObservers, to refresh UI.
        val ctx = context
        assert(ctx != null)
        ctx!!.contentResolver.notifyChange(uri, null, false)
        return result
    }

    /**
     * Delete an entry by database by URI.
     */
    override fun delete(uri:Uri, selection:String, selectionArgs:Array<String>):Int {
        val builder = SelectionBuilder()
        val db = mDataBaseHelper.writableDatabase
        val match = sUriMatcher.match(uri)
        val count:Int

        when (match) {

            ROUTE_ENTRIES -> count = builder.table(RegistroHoraContract.Entry.TABlE_NAME)
                .where(selection, selectionArgs.joinToString { " " })
                 .delete(db)
            ROUTE_ENTRIES_ID -> {
                val id = uri.getLastPathSegment()
                count = builder.table(RegistroHoraContract.Entry.TABlE_NAME)
                .where(RegistroHoraContract.Entry.COL_ID + "=?", id)
                .where(selection, selectionArgs.joinToString {  "" })
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

    /**
     * Update an etry in the database by URI.
     */
     override fun update(uri:Uri, values:ContentValues, selection:String, selectionArgs:Array<String>):Int {
        val builder = SelectionBuilder()
        val db = mDataBaseHelper.writableDatabase
        val match = sUriMatcher.match(uri)
        val count:Int

        when (match) {

            ROUTE_ENTRIES -> count = builder.table(RegistroHoraContract.Entry.TABlE_NAME)
                .where(selection, selectionArgs.joinToString { " " })
                .update(db, values)
            ROUTE_ENTRIES_ID -> {
                val id = uri.getLastPathSegment()
                count = builder.table(RegistroHoraContract.Entry.TABlE_NAME)
                .where(RegistroHoraContract.Entry.COL_ID + "=?", id)
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

    /**
     * SQLite backend for @{link FeedProvider}.
     *
     * Provides access to an disk-backed, SQLite datastore which is utilized by FeedProvider. This
     * database should never be accessed by other parts of the application directly.
     */
    internal class DatabaseClient(context:Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
    {
        override fun onCreate(db:SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db:SQLiteDatabase, oldVersion:Int, newVersion:Int) {
            db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }

        companion object
        {
                /** Schema version.  */
                 val DATABASE_VERSION = 1
                /** Filename for SQLite file.  */
                 val DATABASE_NAME = "feed.db"

                private val TYPE_TEXT = " TEXT"
                private val TYPE_INTEGER = " INTEGER"


                private val SQL_CREATE_ENTRIES = RegistroHoraContract.Entry.CREATE_TABLE

                /** SQL statement to drop "entry" table.  */
                private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + RegistroHoraContract.Entry.TABlE_NAME
        }
    }

    companion object {

        /**
         * Content authority for this provider.
         */
        private val AUTHORITY = RegistroHoraContract.AUTHORITY

         // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
            // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
            // IDs.
            //
            // When a incoming URI is run through sUriMatcher, it will be tested against the defined
            // URI patterns, and the corresponding route ID will be returned.
            /**
         * URI ID for route: /entries
         */
             val ROUTE_ENTRIES = 1

        /**
         * URI ID for route: /entries/{ID}
         */
             val ROUTE_ENTRIES_ID = 2

        /**
         * UriMatcher, used to decode incoming URIs.
         */
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        init{
            sUriMatcher.addURI(AUTHORITY, "entries", ROUTE_ENTRIES)
            sUriMatcher.addURI(AUTHORITY, "entries/*", ROUTE_ENTRIES_ID)
        }
    }
}