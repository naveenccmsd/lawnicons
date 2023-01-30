package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun IconRequest(
    navController: NavController
) {
    MultipleChoiceQuestion(
        titleResourceId = "Icon Request",
        directionsResourceId = "select_all",
        possibleAnswers = listOf(
           "read",
            "work_out",
            "draw",
            "play_games",
            "dance",
            "watch_movies",
        )
    )
}

@Composable
fun MultipleChoiceQuestion(
    titleResourceId: Any,
    directionsResourceId: Any,
    possibleAnswers: List<String>
) {
        val selected = possibleAnswers.isNotEmpty()
        CheckboxRow(
            modifier = Modifier.padding(vertical = 8.dp),
            text = "",
            selected = false
//            onOptionSelected = { onOptionSelected(!selected, it) }
        )
}


@Composable
fun CheckboxRow(
    text: String,
    selected: Boolean,
//    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
//            .clickable(onClick = onOptionSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                Checkbox(selected, onCheckedChange = null)
            }
        }
    }
}


