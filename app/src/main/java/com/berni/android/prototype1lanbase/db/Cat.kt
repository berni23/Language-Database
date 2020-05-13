package com.berni.android.prototype1lanbase.db

import android.icu.util.Calendar
import android.icu.util.Calendar.MONTH

import androidx.room.*
import android.os.Parcelable
import org.kodein.di.generic.M
import java.io.Serializable
import java.time.YearMonth
import java.time.ZonedDateTime
import kotlin.properties.Delegates

data class CatWords(

    @Embedded
    val cat: Cat,

    @Relation(
        parentColumn = "catId",
        entityColumn = "catParent")

    val words: List<Word>)

@Entity
data class Cat(

    @ColumnInfo(name = "catName")
    val catName: String,
    @ColumnInfo(name = "catDate")
    val catDate: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "catId")
    val catId: Int = 0

):Serializable

@Entity
data class Word  (

    @ColumnInfo(name  = "wordName")
    val wordName: String,
    @ColumnInfo(name  = "translation")
    val trans1: String,
    @ColumnInfo(name  = "example")
    val ex1: String?,
    @ColumnInfo(name = "translation example")
    val trans_ex1: String?,
    @ColumnInfo(name = "definition")
    val definition: String?,
    @ColumnInfo(name = "date")
    val date:String,
    @ColumnInfo(name  = "catParent")
    val catParent: Int? = 0,
    @ColumnInfo(name = "acquired")
    var acquired:Boolean  = false,
    @ColumnInfo(name = "test")
    var test : Boolean = true,
    @ColumnInfo(name ="lvl" )
    var lvl:Int = 0,
    @ColumnInfo(name  ="lastOK")
    var lastOk: Int = 10,
    @ColumnInfo(name="month")
    var month: ZonedDateTime? = null

):Serializable {

    @PrimaryKey(autoGenerate = true)
    var wordId: Int = 0
}

    /*:Serializable{
    @PrimaryKey(autoGenerate = true)
    var wordId: Int = 0}

    @Entity(primaryKeys = ["catId","wordId"])
    data class CatWordCrossRef (

       val catId:Int,
       val wordId: Int)


         */







