package com.everis.listadecontatos.helpers

import android.content.ContentValues
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

    fun buscarContatos(busca: String, isBuscaPorId: Boolean = false) : List<ContatosVO> {
        val db = readableDatabase ?: return mutableListOf()
        val lista = mutableListOf<ContatosVO>()
        var where: String? = null
        var args: Array<String> = arrayOf()
        if (isBuscaPorId) {
            where = "$COLUMN_ID = ?"
            args = arrayOf("$busca")
        } else {
            where = "$COLUMN_NOME LIKE ?"
            args = arrayOf("%$busca%")
        }

        var cursor = db.query(TABLE_NAME, null, where, args, null, null, null)
        if (cursor == null) {
            db.close()
            return mutableListOf()
        }
        while (cursor.moveToNext()) {
            val contato = ContatosVO(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TELEFONE))
            )
            lista.add(contato)
        }
        db.close()
        return lista
    }

    fun salvarContato(contato: ContatosVO) {
        val db = writableDatabase ?: return
        val content = ContentValues()
        content.put(COLUMN_NOME, contato.nome)
        content.put(COLUMN_TELEFONE, contato.telefone)
        db.insert(TABLE_NAME, null, content)
        db.close()
    }

    fun deletarContato(id: Int) {
        val db = writableDatabase ?: return
        val sql = "DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val args = arrayOf("$id")
        db.execSQL(sql, args)
        db.close()
    }

    fun updateContato(contato: ContatosVO) {
        val db = writableDatabase ?: return
        val sql = "UPDATE $TABLE_NAME SET $COLUMN_NOME = ?, $COLUMN_TELEFONE = ? WHERE $COLUMN_ID = ?"
        val args = arrayOf(contato.nome, contato.telefone, contato.id)
        db.execSQL(sql, args)
        db.close()
    }
}