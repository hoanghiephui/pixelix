package com.daniebeler.pfpixelix.ui.composables.followers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.empty
import pixelix.app.generated.resources.explore_trending_profiles
import pixelix.app.generated.resources.not_following_anyone
import pixelix.app.generated.resources.the_profiles_you_follow_will_appear_here

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FollowingComposable(
    navController: NavController,
    viewModel: FollowersViewModel = injectViewModel(key = "followers-viewmodel-key") { followersViewModel }
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(state = lazyListState, content = {
        items(viewModel.followingState.following, key = {
            it.id
        }) {
            FollowerElementComposable(account = it, navController)
        }

        if (viewModel.followingState.following.isNotEmpty() && viewModel.followingState.isLoading && !viewModel.followingState.isRefreshing) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }
        }

        if (viewModel.followingState.endReached && viewModel.followingState.following.size > 10) {
            item {
                EndOfListComposable()
            }
        }
    })

    if (!viewModel.followingState.isLoading && viewModel.followingState.error.isEmpty() && viewModel.followingState.following.isEmpty()) {

        val message = if (viewModel.loggedInAccountId == viewModel.accountId)
            stringResource(Res.string.the_profiles_you_follow_will_appear_here)
        else
            stringResource(Res.string.not_following_anyone)

        FullscreenEmptyStateComposable(
            emptyState = EmptyState(icon = Icons.Outlined.Groups,
                heading = stringResource(Res.string.empty),
                message = message,
                buttonText = stringResource(Res.string.explore_trending_profiles),
                onClick = {
                    navController.navigate(Destination.Search(1))
                })
        )
    }

    InfiniteListHandler(lazyListState = lazyListState) {
        viewModel.getFollowingPaginated()
    }

    LoadingComposable(isLoading = viewModel.followingState.isLoading && viewModel.followingState.following.isEmpty())
    ErrorComposable(message = viewModel.followingState.error)
}