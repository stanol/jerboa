package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.appendData
import it.vercruysse.lemmyapi.v0x19.datatypes.ListPostLikes
import it.vercruysse.lemmyapi.v0x19.datatypes.ListPostLikesResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.PostId
import kotlinx.coroutines.launch

class PostLikesViewModel(val id: PostId) : ViewModel() {
    var likesRes: ApiState<ListPostLikesResponse> by mutableStateOf(ApiState.Empty)
        private set
    private var page by mutableIntStateOf(1)

    init {
        getLikes()
    }

    fun resetPage() {
        page = 1
    }

    fun getLikes(state: ApiState<ListPostLikesResponse> = ApiState.Loading) {
        viewModelScope.launch {
            likesRes = state
            likesRes = API.getInstance().listPostLikes(getForm()).toApiState()
        }
    }

    private fun getForm(): ListPostLikes {
        return ListPostLikes(
            post_id = id,
            page = page,
        )
    }

    fun appendLikes() {
        viewModelScope.launch {
            val oldRes = likesRes
            when (oldRes) {
                is ApiState.Success -> likesRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            page += 1
            val newRes = API.getInstance().listPostLikes(getForm()).toApiState()

            likesRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val appended = appendData(oldRes.data.post_likes, newRes.data.post_likes)

                        ApiState.Success(oldRes.data.copy(post_likes = appended))
                    }

                    else -> {
                        oldRes
                    }
                }
        }
    }

    companion object {
        class Factory(
            private val id: PostId,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                return PostLikesViewModel(id) as T
            }
        }
    }
}