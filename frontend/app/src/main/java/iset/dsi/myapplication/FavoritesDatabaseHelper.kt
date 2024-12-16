package iset.dsi.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FavoritesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "favorites.db"
        private const val DATABASE_VERSION = 1

        // Table des favoris
        const val TABLE_FAVORITES = "favorites"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_FAVORITES_TABLE = ("CREATE TABLE $TABLE_FAVORITES ("
                + "$COLUMN_ID INTEGER PRIMARY KEY,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_DESCRIPTION TEXT)")
        db.execSQL(CREATE_FAVORITES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        onCreate(db)
    }

    // Ajouter un favori
    fun addFavorite(resource: Resource) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ID, resource.id)
        values.put(COLUMN_NAME, resource.nom)
        values.put(COLUMN_DESCRIPTION, resource.description)

        db.insert(TABLE_FAVORITES, null, values)
        db.close()
    }

    // Récupérer tous les favoris
    fun getAllFavorites(): List<Resource> {
        val favoritesList = mutableListOf<Resource>()
        val selectQuery = "SELECT * FROM $TABLE_FAVORITES"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        cursor.use {
            while (it.moveToNext()) {
                val resource = Resource(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    nom = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    categorie_id = 0,
                    status = null.toString()
                )

                favoritesList.add(resource)
            }
        }
        db.close()
        return favoritesList
    }
    // Supprimer un favori en utilisant l'ID
    fun removeFavorite(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_FAVORITES, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

}
