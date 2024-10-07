package com.example.retrofitsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.retrofitsample.data.RetrofitServiceFactory
import com.example.retrofitsample.ui.theme.RetrofitSampleTheme

import com.example.retrofitsample.data.model.Result

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            RetrofitSampleTheme {
                var movieList by remember { mutableStateOf(emptyList<Result>()) } // Aquí es donde se puede dar el error
                var errorMessage by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    lifecycleScope.launch {
                        try {
                            val service = RetrofitServiceFactory.makeRetrofitService()
                            val movies = withContext(Dispatchers.IO) {
                                service.listPopularMovies(apiKey = "d30e1f350220f9aad6c4110df385d380", region = "US")
                            }
                            movieList = movies.results
                        } catch (e: Exception) {
                            errorMessage = "Error al cargar las películas: ${e.message}"
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, modifier = Modifier.padding(innerPadding))
                    } else {
                        MovieList(movies = movieList, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun MovieList(movies: List<Result>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(movies) { movie ->
            Text(text = movie.title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RetrofitSampleTheme {
        Text("Hello Android!")
    }
}
