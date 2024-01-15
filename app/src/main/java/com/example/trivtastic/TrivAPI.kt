package com.example.trivtastic
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface TrivAPI {
    @GET
    fun getQuiz(@Url url: String): Call<QuizResponse>
}