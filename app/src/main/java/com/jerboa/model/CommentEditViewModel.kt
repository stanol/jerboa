package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommentResponse
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.EditComment
import com.jerboa.db.entity.Account
import com.jerboa.ui.components.common.Initializable
import kotlinx.coroutines.launch

class CommentEditViewModel : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

    var commentView = mutableStateOf<CommentView?>(null)
        private set
    var editCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun initialize(
        newCommentView: CommentView,
    ) {
        commentView.value = newCommentView
    }

    fun editComment(
        content: String,
        focusManager: FocusManager,
        account: Account,
        onSuccess: (CommentView) -> Unit,
    ) {
        viewModelScope.launch {
            commentView.value?.also { cv ->
                val form = EditComment(
                    content = content,
                    comment_id = cv.comment.id,
                    auth = account.jwt,
                )

                editCommentRes = ApiState.Loading
                editCommentRes =
                    apiWrapper(
                        API.getInstance().editComment(form),
                    )
                focusManager.clearFocus()

                // Update all the views which might have your comment
                when (val res = editCommentRes) {
                    is ApiState.Success -> {
                        onSuccess(res.data.comment_view)
                    }

                    else -> {}
                }
            }
        }
    }
}