package tk.zwander.rootactivitylauncher.views.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import tk.zwander.rootactivitylauncher.data.model.AppModel
import tk.zwander.rootactivitylauncher.data.component.BaseComponentInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppList(
    appListState: LazyStaggeredGridState,
    filteredApps: List<AppModel>,
    isForTasker: Boolean,
    onItemSelected: (BaseComponentInfo) -> Unit,
    extractCallback: (AppModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(400.dp),
        modifier = modifier,
        contentPadding = WindowInsets.statusBars.asPaddingValues().run {
            PaddingValues(
                start = 8.dp + this.calculateStartPadding(layoutDirection),
                top = 8.dp + this.calculateTopPadding(),
                end = 8.dp + this.calculateEndPadding(layoutDirection),
                bottom = 8.dp + this.calculateBottomPadding()
            )
        },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = appListState
    ) {
        items(items = filteredApps, key = { it.info.packageName }) { app ->
            AppItem(
                info = app,
                isForTasker = isForTasker,
                selectionCallback = {
                    onItemSelected(it)

                },
                extractCallback = extractCallback,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
