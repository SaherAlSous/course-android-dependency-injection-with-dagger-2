package com.techyourchance.dagger2course.screens.questionslist

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.techyourchance.dagger2course.Constants
import com.techyourchance.dagger2course.networking.StackoverflowApi
import com.techyourchance.dagger2course.questions.FetchQuestionsUseCase
import com.techyourchance.dagger2course.questions.Question
import com.techyourchance.dagger2course.screens.common.dialogs.ServerErrorDialogFragment
import com.techyourchance.dagger2course.screens.questiondetails.QuestionDetailsActivity
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class QuestionsListActivity : AppCompatActivity() , QuestionsListViewMvc.Listener{

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)



    private var isDataLoaded = false

    private lateinit var viewMvc: QuestionsListViewMvc

    /**
     * Instantiate the [FetchQuestionsUseCase] to use it's functions in the app
     * i used [lazy] because kotln asked for getter.
     */
    val fetchQuestionsUseCase by lazy {
        FetchQuestionsUseCase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       viewMvc = QuestionsListViewMvc(LayoutInflater.from(this), null)

        setContentView(viewMvc.rootView) //getting the view from viewMvc

//        fetchQuestionsUseCase = FetchQuestionsUseCase()



    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        if (!isDataLoaded) {
            fetchQuestions()
        }
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        coroutineScope.coroutineContext.cancelChildren()
    }

    private fun fetchQuestions() {
        coroutineScope.launch {
            viewMvc.showProgressIndication()

           try {
            /**
             * applying the functionality from [fetchQuestionsUseCase]
             */
           val result = fetchQuestionsUseCase.fetchLatestQuestions()
            when(result){
                is FetchQuestionsUseCase.Result.Success ->{
                    viewMvc.bindQuestions(result.questions)
                    isDataLoaded = true
                }
                is FetchQuestionsUseCase.Result.Failure -> onFetchFailed()
            }
        }finally {
               viewMvc.hideProgressIndication()

           }
        }
    }

    private fun onFetchFailed() {
        supportFragmentManager.beginTransaction()
                .add(ServerErrorDialogFragment.newInstance(), null)
                .commitAllowingStateLoss()
    }

    override fun onRefreshClicked() {
        fetchQuestions()
    }

    override fun onQuestionClicked(clickedQuestion: Question) {
        QuestionDetailsActivity.start(this, clickedQuestion.id)
    }

}