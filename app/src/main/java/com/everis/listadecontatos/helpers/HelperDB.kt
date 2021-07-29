package com.everis.listadecontatos.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.everis.listadecontatos.feature.listacontatos.model.ContatosVO

class HelperDB(
    context: Context
) : SQLiteOpenHelper(context, NOME_BANCO, null, VERSAO_ATUAL) {

    companion object {
        private val NOME_BANCO = "contato.db"
        private val VERSAO_ATUAL = 1

    }

    val TABLE_NAME = "contato"
    val COLUMN_ID = "id"
    val COLUMN_NOME = "nome"
    val COLUMN_TELEFONE = "telefone"
    val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_ID INTEGER NOT NULL," +
            "$COLUMN_NOME TEXT NOT NULL," +
            "$COLUMN_TELEFONE TEXT NOT NULL," +
            "PRIMARY KEY($COLUMN_ID AUTOINCREMENT)" +
            ")"


//    criacao do banco de dados da aplicacao se nao criado
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            db?.execSQL(DROP_TABLE)
            onCreate(db)
        }
    }

    fun buscarContatos(busca: String) : List<ContatosVO> {
        val db = readableDatabase ?: return mutableListOf()
        val lista = mutableListOf<ContatosVO>()
        val sql = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(sql, null) ?: return mutableListOf()
        while (cursor.moveToNext()) {
            val contato = ContatosVO(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TELEFONE))
            )
            lista.add(contato)
        }
        return lista
    }
}