package com.example.jokeapp

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.jokeapp.ui.screens.categories.CategorySelectScreen
import com.example.jokeapp.ui.screens.jokes.JokeListScreen
import com.example.jokeappcompose.R
import kotlinx.serialization.Serializable

/**
 * sealed interface representing the different destinations in the app.

 */

sealed interface  JokeAppDestinations {
    // titleResId The string resource ID for the title of the destination.
    @get:StringRes val titleResId: Int
    @Serializable
    data object JokeCategories: JokeAppDestinations{
        override val titleResId: Int
            get() = R.string.category_screen_title
    }
    @Serializable
    data class JokesList(val category: String): JokeAppDestinations{
        override val titleResId: Int
            get() = R.string.jokes_screen_title
    }
}

/**
 * The main entry point of the app, which sets up the navigation and top bar.
 * @param navController The NavHostController for managing navigation.
 */
@Composable
fun JokeApp(navController: NavHostController = rememberNavController()) {
    // Getting current screen name from backStackEntry Destination
    val backStackEntry by navController.currentBackStackEntryAsState()

    val screenNameResId = when {
        backStackEntry?.destination?.hasRoute<JokeAppDestinations.JokeCategories>() == true ->
            backStackEntry?.toRoute<JokeAppDestinations.JokeCategories>()?.titleResId
        backStackEntry?.destination?.hasRoute<JokeAppDestinations.JokesList>() == true ->
            backStackEntry?.toRoute<JokeAppDestinations.JokesList>()?.titleResId
        else ->
            R.string.app_name
    } ?: R.string.app_name
    val currentScreenTitle = stringResource(screenNameResId)


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            JokeAppTopBar(
                title = currentScreenTitle,
                showNavigationIcon = navController.previousBackStackEntry != null
            ) {
                navController.navigateUp()
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = JokeAppDestinations.JokeCategories,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable<JokeAppDestinations.JokeCategories>{
                // Start screen with category selection.
                // onCategorySelected will navigate to the jokes list screen with a selected category.
                CategorySelectScreen(
                    onCategorySelected = { category ->
                        navController.navigate(JokeAppDestinations.JokesList(category))
                    }
                )
            }
            composable<JokeAppDestinations.JokesList> { backStackEntry ->
                // Jokes list screen with the selected category passed as an argument.
                // The viewModel is gets the argument from the backStackEntry
                // via the savedStateHandle used in the view model factory.
                JokeListScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokeAppTopBar(
    title: String,
    showNavigationIcon: Boolean,
    onNavigateUp: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.headlineMedium) },
        navigationIcon = {
            if (showNavigationIcon)
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
        })
}