package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "my_app_database.db"
        const val DATABASE_VERSION = 2 // 🚀 Importante: subimos versión a 2

        // Tabla Usuarios
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PASSWORD = "password"

        // Tabla Trabajadores
        const val TABLE_TRABAJADORES = "trabajadores"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla usuarios
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()

        // Crear tabla trabajadores
        val createTrabajadoresTable = """
            CREATE TABLE $TABLE_TRABAJADORES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                zona TEXT,
                tipoTrabajo TEXT,
                precioHora TEXT,
                numeroContacto TEXT,
                fotoResId INTEGER
            )
        """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createTrabajadoresTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Puedes actualizar esto en el futuro si cambias tablas
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRABAJADORES")
        onCreate(db)
    }

    // 🚀 FUNCIONES PARA USUARIOS

    fun insertUser(name: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(name: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_NAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(name, password)
        val cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null)
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    // 🚀 FUNCIONES PARA TRABAJADORES

    fun insertarTrabajador(
        nombre: String,
        zona: String,
        tipoTrabajo: String,
        precioHora: String,
        numeroContacto: String,
        fotoResId: Int
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("zona", zona)
            put("tipoTrabajo", tipoTrabajo)
            put("precioHora", precioHora)
            put("numeroContacto", numeroContacto)
            put("fotoResId", fotoResId)
        }
        db.insert(TABLE_TRABAJADORES, null, values)
        db.close()
    }

    fun obtenerTrabajadores(): List<Trabajador> {
        val db = readableDatabase
        val lista = mutableListOf<Trabajador>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TRABAJADORES", null)

        if (cursor.moveToFirst()) {
            do {
                val trabajador = Trabajador(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    zona = cursor.getString(cursor.getColumnIndexOrThrow("zona")),
                    tipoTrabajo = cursor.getString(cursor.getColumnIndexOrThrow("tipoTrabajo")),
                    precioHora = cursor.getString(cursor.getColumnIndexOrThrow("precioHora")),
                    numeroContacto = cursor.getString(cursor.getColumnIndexOrThrow("numeroContacto")),
                    fotoResId = cursor.getInt(cursor.getColumnIndexOrThrow("fotoResId"))
                )
                lista.add(trabajador)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return lista
    }

    fun borrarTodosLosTrabajadores() {
        val db = writableDatabase
        db.delete(TABLE_TRABAJADORES, null, null)
        db.close()
    }
}
