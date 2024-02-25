package com.fduhole.danxi.ui.page.fdu

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fduhole.danxi.R
import com.fduhole.danxi.model.fdu.CardRecord
import com.fduhole.danxi.ui.component.fdu.feature.ECardFeature
import com.fduhole.danxi.ui.page.common.LazyPagingColumn
import com.fduhole.danxi.ui.page.common.NavigationScaffold

@Composable
fun FudanECardPage(
    fudanECardFeatureStateHolder: ECardFeature,
    canNavigateUp: Boolean = false,
    navigateUp: () -> Unit = {},
) {
    NavigationScaffold(
        title = stringResource(R.string.fudan_ecard_balance),
        canNavigateUp = canNavigateUp,
        navigateUp = navigateUp,
    ) {
        LazyPagingColumn(
            lazyPagingFlow = fudanECardFeatureStateHolder.lazyPagingCardRecords,
            key = { "${it.time}-${it.balance}" },
            modifier = Modifier.padding(8.dp)
        ) {
            ECardItem(it)
        }
    }
}

@Composable
fun ECardItem(record: CardRecord) {
    ListItem(
        headlineContent = { Text(record.location) },
        supportingContent = { Text(record.time.toString()) },
        trailingContent = { Text("￥${record.amount}") }
    )
}