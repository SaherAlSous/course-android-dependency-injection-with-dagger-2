package com.techyourchance.dagger2course.questions

import com.techyourchance.dagger2course.Constants
import com.techyourchance.dagger2course.networking.StackoverflowApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * the role of this class is to encapsulates the fetch of questions into one place, it can also be called interactor.
 * it encapsulates domain flows
 */
class FetchQuestionsUseCase {


    /**
     * We want to return the [Result] for the caller function that initiated the call
     * and it do whatever with it. therefore we create a sealed class
     */
    sealed class Result{
        class Success(val questions: List<Question>) : Result()
        object Failure: Result()
    }


    // init retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val stackoverflowApi: StackoverflowApi = retrofit.create(StackoverflowApi::class.java)

    /**
     * since we have Coroutines in the main function, we use suspend
     * for this function. and add the [withContext] to include Coroutines functions
     * here.
     */
    suspend fun fetchLatestQuestions(): Result {
        return withContext(Dispatchers.IO){
        try {
            val response = stackoverflowApi.lastActiveQuestions(20)
            if (response.isSuccessful && response.body() != null) {
                //returning success
                    return@withContext Result.Success(response.body()!!.questions)

            //viewMvc.bindQuestions(response.body()!!.questions)
            //isDataLoaded = true

            } else {
                //Returning Failure
                    return@withContext Result.Failure
                //onFetchFailed()
            }
        } catch (t: Throwable) {
            if (t !is CancellationException) {
                return@withContext Result.Failure
            } else{
                throw t
            }
        }
      }
    }
}