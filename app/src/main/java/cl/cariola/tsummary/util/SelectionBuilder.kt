package cl.cariola.tsummary.util

//import android.support.test.espresso.core.internal.deps.guava.collect.Lists
//import android.support.test.espresso.core.internal.deps.guava.collect.Maps
//import android.support.test.espresso.core.internal.deps.guava.collect.Maps
//import android.support.test.espresso.core.internal.deps.guava.collect.Lists

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import com.google.common.collect.Lists
import com.google.common.collect.Lists.newArrayList
import com.google.common.collect.Maps
import com.google.common.collect.Maps.newHashMap
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

/**
 * Helper for building selection clauses for [SQLiteDatabase]. Each
 * appended clause is combined using `AND`. This class is *not*
 * thread safe.
 */
class SelectionBuilder {

    private var mTable: String? = null
    private val mProjectionMap = newHashMap<String, String>()
    private val mSelection = StringBuilder()
    private val mSelectionArgs = newArrayList<String>()

    /**
     * Return selection string for current internal state.
     *
     * @see .getSelectionArgs
     */
    val selection: String
        get() = mSelection.toString()

    /**
     * Return selection arguments for current internal state.
     *
     * @see .getSelection
     */
    val selectionArgs: Array<String>
        get() = mSelectionArgs.toTypedArray()

    /**
     * Reset any internal state, allowing this builder to be recycled.
     */
    fun reset(): SelectionBuilder {
        mTable = null
        mSelection.setLength(0)
        mSelectionArgs.clear()
        return this
    }

    /**
     * Append the given selection clause to the internal state. Each clause is
     * surrounded with parenthesis and combined using `AND`.
     */
    fun where(selection: String, vararg selectionArgs: String): SelectionBuilder {
        if (TextUtils.isEmpty(selection)) {
            if (selectionArgs != null && selectionArgs.size > 0) {
                throw IllegalArgumentException(
                        "Valid selection required when including arguments=")
            }

            // Shortcut when clause is empty
            return this
        }

        if (mSelection.length > 0) {
            mSelection.append(" AND ")
        }

        mSelection.append("(").append(selection).append(")")
        if (selectionArgs != null) {
            Collections.addAll(mSelectionArgs, *selectionArgs)
        }

        return this
    }

    fun table(table: String): SelectionBuilder {
        mTable = table
        return this
    }

    private fun assertTable() {
        if (mTable == null) {
            throw IllegalStateException("Table not specified")
        }
    }

    fun mapToTable(column: String, table: String): SelectionBuilder {
        mProjectionMap.put(column, "$table.$column")
        return this
    }

    fun map(fromColumn: String, toClause: String): SelectionBuilder {
        mProjectionMap.put(fromColumn, "$toClause AS $fromColumn")
        return this
    }

    private fun mapColumns(columns: Array<String>) {
        for (i in columns.indices) {
            val target = mProjectionMap.get(columns[i])
            if (target != null) {
                columns[i] = target
            }
        }
    }

    override fun toString(): String {
        return ("SelectionBuilder[table=" + mTable + ", selection=" + selection
                + ", selectionArgs=" + Arrays.toString(selectionArgs) + "]")
    }

    /**
     * Execute query using the current internal state as `WHERE` clause.
     */
    fun query(db: SQLiteDatabase, columns: Array<String>, orderBy: String): Cursor {
        return query(db, columns, null, null, orderBy, null)
    }

    /**
     * Execute query using the current internal state as `WHERE` clause.
     */
    fun query(db: SQLiteDatabase, columns: Array<String>?, groupBy: String?,
              having: String?, orderBy: String, limit: String?): Cursor {
        assertTable()
        if (columns != null) mapColumns(columns)
        Log.v(TAG, "query(columns=" + Arrays.toString(columns) + ") " + this)
        return db.query(mTable, columns, selection, selectionArgs, groupBy, having,
                orderBy, limit)
    }

    /**
     * Execute update using the current internal state as `WHERE` clause.
     */
    fun update(db: SQLiteDatabase, values: ContentValues): Int {
        assertTable()
        Log.v(TAG, "update() " + this)
        return db.update(mTable, values, selection, selectionArgs)
    }

    /**
     * Execute delete using the current internal state as `WHERE` clause.
     */
    fun delete(db: SQLiteDatabase): Int {
        assertTable()
        Log.v(TAG, "delete() " + this)
        return db.delete(mTable, selection, selectionArgs)
    }

    companion object {
        private val TAG = "TSProvider"
    }
}
