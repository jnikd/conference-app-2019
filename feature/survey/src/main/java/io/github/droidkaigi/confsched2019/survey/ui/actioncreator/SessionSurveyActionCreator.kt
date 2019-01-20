package io.github.droidkaigi.confsched2019.survey.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionFeedback
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionSurveyActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val sessionRepository: SessionRepository,
    @PageScope private val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope,
    ErrorHandler {

    fun load(session: String) = launch {
        try {
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
            dispatcher.dispatch(Action.SessionSurveyLoaded(sessionRepository.sessionFeedback(session)))
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADED))
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.INITIALIZED))
        }
    }

    fun submit(session: Session.SpeechSession, sessionFeedback: SessionFeedback) = launch {
        try {
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
            sessionRepository.submitSessionFeedback(session, sessionFeedback)
            sessionRepository.saveSessionFeedback(sessionFeedback)
            dispatcher.dispatch(Action.SessionSurveySubmitted)
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADED))
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.INITIALIZED))
        }
    }

    fun changeSessionFeedback(sessionFeedback: SessionFeedback) = launch {

    }
}
