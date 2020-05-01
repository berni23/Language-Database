package com.berni.android.prototype1lanbase.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.berni.android.prototype1lanbase.db.Cat
import com.berni.android.prototype1lanbase.db.Repository
import com.berni.android.prototype1lanbase.db.Word

// Main view model for everything related with accessing or updating database

class MainViewModel(private val repos: Repository ) : ViewModel(){

    suspend fun addCat(cat: Cat)   =  repos.addCat(cat)

    suspend fun addWord(word: Word)  =  repos.addWord(word)

    suspend fun updateWord(word: Word) = repos.updateWord(word)

    suspend fun updateCat(oldName:String,newName:String) = repos.updateCat(oldName, newName)

    fun deleteWordsInCat(currentCatId :Int) = repos.deleteWordsInCat(currentCatId)

    fun deleteCat(currentCat: Cat) = repos.deleteCat(currentCat)

    fun deleteWord(currentWord: Word) = repos.deleteWord(currentWord)

    fun wordsInCat(currentCatId:Int) = repos.wordsInCat(currentCatId)

    fun wordsInCatAlphabetic(currentCatName: String) = repos.wordsInCatAlphabetic(currentCatName)

    fun maxNum() = repos.maxNum()

    fun filterExample(currentCatName: String)  = repos.filterExample(currentCatName)

    fun filterNoExample(currentCatName:String) = repos.filterNoExample(currentCatName)

    fun validCatName(catName: String) : Boolean { return repos.validCatName(catName).isEmpty() }

    fun validWordId(wordId:String): Boolean {return repos.validWordId(wordId).isEmpty()}

    val allCats: LiveData<List<Cat>> = repos.getAllCats()

    val allWords : LiveData<List<Word>> = repos.getAllWordsLive()

    suspend fun getAllWords(): List<Word> {return repos.getAllWords()}  //val allWords: LiveData<List<Word>> =


}



