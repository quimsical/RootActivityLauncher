package tk.zwander.rootactivitylauncher.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tk.zwander.rootactivitylauncher.R
import tk.zwander.rootactivitylauncher.data.component.BaseComponentInfo
import tk.zwander.rootactivitylauncher.data.model.AppModel
import tk.zwander.rootactivitylauncher.data.model.MainModel
import tk.zwander.rootactivitylauncher.util.extractApk
import tk.zwander.rootactivitylauncher.views.components.AppList
import tk.zwander.rootactivitylauncher.views.components.BottomBar
import tk.zwander.rootactivitylauncher.views.dialogs.FilterDialog

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainView(
    isForTasker: Boolean,
    mainModel: MainModel,
    onItemSelected: (BaseComponentInfo) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appListState = remember {
        LazyStaggeredGridState()
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showingFilterDialog by remember {
        mutableStateOf(false)
    }
    var extractInfo: AppModel? by remember {
        mutableStateOf(null)
    }

    val extractLauncher = rememberLauncherForActivityResult(
        object : ActivityResultContracts.OpenDocumentTree() {
            override fun createIntent(context: Context, input: Uri?): Intent {
                return super.createIntent(context, input).also {
                    it.putExtra(
                        Intent.EXTRA_TITLE,
                        context.resources.getString(R.string.choose_extract_folder_msg)
                    )
                }
            }
        }
    ) { result ->
        if (result != null) {
            scope.launch(Dispatchers.IO) {
                extractInfo?.let {
                    context.extractApk(result, it)
                }
                extractInfo = null
            }
        }
    }

    val enabledFilterMode by mainModel.enabledFilterMode.collectAsState()
    val exportedFilterMode by mainModel.exportedFilterMode.collectAsState()
    val permissionFilterMode by mainModel.permissionFilterMode.collectAsState()
    val query by mainModel.query.collectAsState()
    val apps by mainModel.apps.collectAsState()
    val filteredApps by mainModel.filteredApps.collectAsState()
    val progress by mainModel.progress.collectAsState()
    val isSearching by mainModel.isSearching.collectAsState()
    val useRegex by mainModel.useRegex.collectAsState()
    val includeComponents by mainModel.includeComponents.collectAsState()

    LaunchedEffect(extractInfo) {
        if (extractInfo != null) {
            extractLauncher.launch(null)
        }
    }

    LaunchedEffect(
        isSearching,
        useRegex,
        includeComponents
    ) {
        if (isSearching) {
            mainModel.update()
        }
    }

    LaunchedEffect(
        apps,
        enabledFilterMode,
        exportedFilterMode,
        permissionFilterMode,
        query
    ) {
        mainModel.update()
    }

    val refreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = onRefresh
    )

    Surface(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(
                    state = refreshState
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .captionBarPadding()
                    .imePadding()
            ) {
                AppList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    appListState = appListState,
                    isForTasker = isForTasker,
                    onItemSelected = onItemSelected,
                    extractCallback = {
                        extractInfo = it
                    },
                    filteredApps = filteredApps
                )

                BottomBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    isSearching = isSearching,
                    useRegex = useRegex,
                    includeComponents = includeComponents,
                    query = query,
                    progress = progress,
                    apps = apps,
                    appListState = appListState,
                    onShowFilterDialog = {
                        showingFilterDialog = true
                    },
                    onQueryChanged = {
                        mainModel.query.value = it
                    },
                    onIncludeComponentsChanged = {
                        mainModel.includeComponents.value = it
                    },
                    onIsSearchingChanged = {
                        mainModel.isSearching.value = it
                    },
                    onUseRegexChanged = {
                        mainModel.useRegex.value = it
                    }
                )
            }

            PullRefreshIndicator(
                refreshing = false,
                state = refreshState,
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            ScrimView(
                modifier = Modifier.fillMaxSize(),
                progress = progress
            )
        }
    }

    FilterDialog(
        showing = showingFilterDialog,
        initialEnabledMode = mainModel.enabledFilterMode.collectAsState().value,
        initialExportedMode = mainModel.exportedFilterMode.collectAsState().value,
        initialPermissionMode = mainModel.permissionFilterMode.collectAsState().value,
        onDismissRequest = { enabled, exported, permission ->
            mainModel.enabledFilterMode.value = enabled
            mainModel.exportedFilterMode.value = exported
            mainModel.permissionFilterMode.value = permission
            showingFilterDialog = false
        }
    )
}
