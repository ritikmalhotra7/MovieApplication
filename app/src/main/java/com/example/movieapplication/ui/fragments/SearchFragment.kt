package com.example.movieapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.R
import com.example.movieapplication.adapters.MovieAdapter
import com.example.movieapplication.models.Result
import com.example.movieapplication.utils.Constants
import com.example.movieapplication.utils.Constants.Companion.isOnline
import com.example.movieapplication.utils.Resources
import com.example.movieapplication.viewmodels.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var movieAdapter: MovieAdapter
    private val viewModel: MovieViewModel by viewModels()

    var isScrolling = false
    var position = 0
    var searched :String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(isOnline(requireActivity())){
            viewModel()
            var job : Job? = null
            etSearch.addTextChangedListener { editable ->
                job?.cancel()
                job = MainScope().launch {
                    delay(500L)
                    editable?.let {
                        if(editable.toString().isNotEmpty()){
                            viewModel.searchPage = 1
                            viewModel.getSearch(editable.toString())
                            searched = editable.toString()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(activity,"No Internet",Toast.LENGTH_LONG).show()
        }
    }

    private fun viewModel(){
        viewModel.searchDetails.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Success -> {
                    response.data?.let { movieResponse ->
                        hideProgress()
                        setupRV(movieResponse.results!!.toList())
                    }
                }
                is Resources.Error -> {
                    response.data?.let {
                        hideProgress()
                        Toast.makeText(
                            requireContext(),
                            "An Error occurred $it",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resources.Loading -> {
                    showProgress()
                }
            }
        }
    }
    private val onScroll = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val scrolledOutItems = layoutManager.findFirstVisibleItemPosition()
            val currentItems = layoutManager.childCount
            val totalItems = layoutManager.itemCount

            if(isScrolling && (currentItems + scrolledOutItems >= totalItems)){
                viewModel.getSearch(searched!!)
                isScrolling = false
                viewModel.searchPage++
                position = totalItems-(currentItems/2)
            }
        }
    }
    private fun setupRV(list:List<Result>){
        movieAdapter = MovieAdapter(list = list, null, recyclerViewType = Constants.POPULARRECYCLERVIEW)
        rvSearchResult.apply {
            layoutManager = GridLayoutManager(activity,2)
            adapter = movieAdapter
            addOnScrollListener(onScroll)
            scrollToPosition(position)
        }
        movieAdapter.setOnItemCLickListener{
            val bundle = Bundle().apply {
                putSerializable("movie",it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_movieFragment,bundle)
        }
    }
    private fun showProgress(){
        progressSearch.visibility = View.VISIBLE
    }
    private fun hideProgress(){
        progressSearch.visibility = View.GONE
    }
}