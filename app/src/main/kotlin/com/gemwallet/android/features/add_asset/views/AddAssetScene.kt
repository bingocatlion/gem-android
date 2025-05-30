package com.gemwallet.android.features.add_asset.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gemwallet.android.ext.chain
import com.gemwallet.android.features.add_asset.models.TokenSearchState
import com.gemwallet.android.ui.R
import com.gemwallet.android.ui.components.AddressChainField
import com.gemwallet.android.ui.components.CellEntity
import com.gemwallet.android.ui.components.ChainItem
import com.gemwallet.android.ui.components.Table
import com.gemwallet.android.ui.components.buttons.MainActionButton
import com.gemwallet.android.ui.components.designsystem.padding16
import com.gemwallet.android.ui.components.image.AsyncImage
import com.gemwallet.android.ui.components.image.getIconUrl
import com.gemwallet.android.ui.components.progress.CircularProgressIndicator16
import com.gemwallet.android.ui.components.screen.Scene
import com.wallet.core.primitives.Asset

@Composable
fun AddAssetScene(
    searchState: TokenSearchState,
    addressState: MutableState<String>,
    network: Asset,
    token: Asset?,
    onScan: () -> Unit,
    onAddAsset: () -> Unit,
    onChainSelect: (() -> Unit)?,
    onCancel: () -> Unit,
) {
    Scene(
        title = stringResource(id = R.string.wallet_add_token_title),
        mainAction = {
            MainActionButton(
                title = stringResource(id = R.string.wallet_import_action),
                enabled = searchState is TokenSearchState.Idle && token != null,
                onClick = onAddAsset,
            )
        },
        onClose = onCancel,
    ) {
        ChainItem(
            modifier = Modifier.height(64.dp),
            chain = network.chain(),
            title = network.name,
            icon = network.chain().getIconUrl(),
            onClick = onChainSelect,
            dividerShowed = true,
            trailing = if (onChainSelect != null) {
                {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.ChevronRight),
                        contentDescription = "open_provider_select",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            } else null
        )
        Column(
            modifier = Modifier.padding(padding16)
        ) {
            AddressChainField(
                chain = network.chain(),
                label = "Contract Address",
                value = addressState.value,
                searchName = false,
                onValueChange = { input, _ ->
                    addressState.value = input
                },
                onQrScanner = onScan,
            )
        }
        if (searchState is TokenSearchState.Loading) {
            Box {
                CircularProgressIndicator16(modifier = Modifier.align(Alignment.Center))
            }
        }
        if (searchState is TokenSearchState.Error) {
            Box {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding16),
                    text = stringResource(id = R.string.errors_token_unable_fetch_token_information, addressState.value),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }
        AssetInfoTable(token)
    }
}

@Composable
private fun AssetInfoTable(asset: Asset?) {
    if (asset == null) {
        return
    }
    Table(
        items = listOf(
            CellEntity(
                label = stringResource(id = R.string.asset_name),
                data = asset.name,
                trailing = { AsyncImage(model = asset) },
            ),
            CellEntity(
                label = stringResource(id = R.string.asset_symbol),
                data = asset.symbol,
            ),
            CellEntity(
                label = stringResource(id = R.string.asset_decimals),
                data = asset.decimals.toString(),
            ),
        )
    )
}