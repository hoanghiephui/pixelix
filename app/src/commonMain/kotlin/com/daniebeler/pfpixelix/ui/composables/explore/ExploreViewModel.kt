package com.daniebeler.pfpixelix.ui.composables.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.SavedSearchItem
import com.daniebeler.pfpixelix.domain.model.SavedSearchType
import com.daniebeler.pfpixelix.domain.model.SavedSearches
import com.daniebeler.pfpixelix.domain.service.hashtag.SearchService
import com.daniebeler.pfpixelix.domain.service.search.SavedSearchesService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class ExploreViewModel @Inject constructor(
    private val searchService: SearchService,
    private val savedSearchesService: SavedSearchesService
) : ViewModel() {
    var searchState by mutableStateOf(SearchState())
    var savedSearches: SavedSearches by mutableStateOf(SavedSearches())

    init {
        viewModelScope.launch { getSavedSearches() }
    }

    private suspend fun getSavedSearches() {
        savedSearchesService.getSavedSearches().collect {
            savedSearches = it
        }
    }

    fun saveAccount(accountUsername: String, account: Account) {
        viewModelScope.launch {
            savedSearchesService.addAccount(accountUsername, account)
        }
    }

    fun saveHashtag(accountId: String) {
        viewModelScope.launch {
            savedSearchesService.addHashtag(accountId)
        }
    }

    fun saveSearch(text: String) {
        if (text.isNotBlank()) {

            val savedSearchesBefore = savedSearches.pastSearches.filter { it.savedSearchType == SavedSearchType.Search }
            if (savedSearchesBefore.find { it.value == text } != null) {
                return
            }

            viewModelScope.launch {
                savedSearchesService.addSearch(text)
            }
        }
    }

    fun deleteSavedSearch(item: SavedSearchItem) {
        viewModelScope.launch {
            savedSearchesService.deleteElement(item)
        }
    }

    fun onSearch(text: String) {
        if (text.isNotBlank()) {
            getSearchResults(text, 20)
        }
    }

    fun textInputChange(text: String) {
        searchDebounced(text)
    }

    private var searchJob: Job? = null

    private fun searchDebounced(searchText: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (searchText.isNotBlank()) {
                getSearchResults(searchText, 5)
            }
        }
    }

    private fun getSearchResults(text: String, limit: Int) {
        searchService.search(text, limit = limit).onEach { result ->
            searchState = when (result) {
                is Resource.Success -> {
                    SearchState(searchResult = result.data)
                }

                is Resource.Error -> {
                    SearchState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    SearchState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}