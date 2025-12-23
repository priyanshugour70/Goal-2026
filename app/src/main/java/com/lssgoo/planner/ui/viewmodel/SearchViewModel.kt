package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.data.search.SearchManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * ViewModel for Search feature
 */
class SearchViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val searchManager = SearchManager(storageManager)
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()
    
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()
    
    private val _searchFilters = MutableStateFlow(SearchFilters())
    val searchFilters: StateFlow<SearchFilters> = _searchFilters.asStateFlow()
    
    init {
        _recentSearches.value = storageManager.getRecentSearches()
        
        // Setup search debouncing
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotEmpty()) {
                        performSearch(query)
                    } else {
                        _searchResults.value = emptyList()
                    }
                }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun updateSearchFilters(filters: SearchFilters) {
        _searchFilters.value = filters
        performSearch(_searchQuery.value)
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
    
    private fun performSearch(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch(Dispatchers.IO) {
            val results = searchManager.search(
                query = query,
                goals = storageManager.getGoals(),
                tasks = storageManager.getTasks(),
                notes = storageManager.getNotes(),
                events = emptyList(), // Add these later
                reminders = storageManager.getReminders(),
                habits = storageManager.getHabits(),
                journalEntries = storageManager.getJournalEntries(),
                transactions = storageManager.getTransactions(),
                filters = _searchFilters.value
            )
            _searchResults.value = results
            
            // Save search history (on UI thread to avoid concurrent modification issues if necessary)
            if (results.isNotEmpty()) {
                storageManager.addRecentSearch(query)
                _recentSearches.value = storageManager.getRecentSearches()
            }
        }
    }
}
