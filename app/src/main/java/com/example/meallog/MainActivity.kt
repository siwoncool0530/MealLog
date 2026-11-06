package com.example.meallog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.meallog.ui.theme.MealLogTheme
import androidx.core.net.toUri

data class FoodItem(
    val name: String,
    val weight: Float,
    val energy: Float,
    val carbs: Float,
    val protein: Float,
    val fat: Float,
    val sugars: Float,
    val fiber: Float,
    val sodium: Float,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MealLogTheme {
                FoodListScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen() {
    val context = LocalContext.current
    var foodList by remember { mutableStateOf<List<FoodItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        foodList = loadFoodData(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("식품 영양 정보 리스트") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (foodList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DataSourceAttributionCard()
                }
                items(foodList) { food ->
                    FoodItemCard(food = food)
                }
            }
        }
    }
}

@Composable
fun DataSourceAttributionCard() {
    val context = LocalContext.current
    val url = "https://various.foodsafetykorea.go.kr/nutrient/general/down/historyList.do"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "데이터 출처",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "식품의약품안전처 식품영양성분 데이터베이스",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "웹사이트 방문하기 ↗",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FoodItemCard(food: FoodItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "${food.energy} kcal",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientInfo(label = "중량", value = "${food.weight}g")
                NutrientInfo(label = "탄수화물", value = "${food.carbs}g")
                NutrientInfo(label = "단백질", value = "${food.protein}g")
                NutrientInfo(label = "지방", value = "${food.fat}g")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientInfo(label = "당류", value = "${food.sugars}g")
                NutrientInfo(label = "식이섬유", value = "${food.fiber}g")
                NutrientInfo(label = "나트륨", value = "${food.sodium}mg")
                Spacer(modifier = Modifier.width(40.dp))
            }
        }
    }
}

@Composable
fun NutrientInfo(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun loadFoodData(context: Context): List<FoodItem> {
    val foodList = mutableListOf<FoodItem>()
    try {
        context.assets.open("food_data.csv").bufferedReader(Charsets.UTF_8).use { reader ->
            val lines = reader.readLines()
            for (i in 1 until lines.size) {
                val line = lines[i].trim()
                if (line.isEmpty()) continue
                val tokens = line.split(",")
                if (tokens.size >= 9) {
                    foodList.add(
                        FoodItem(
                            name = tokens[0].trim(),
                            weight = tokens[1].trim().toFloatOrNull() ?: 0f,
                            energy = tokens[2].trim().toFloatOrNull() ?: 0f,
                            carbs = tokens[3].trim().toFloatOrNull() ?: 0f,
                            protein = tokens[4].trim().toFloatOrNull() ?: 0f,
                            fat = tokens[5].trim().toFloatOrNull() ?: 0f,
                            sugars = tokens[6].trim().toFloatOrNull() ?: 0f,
                            fiber = tokens[7].trim().toFloatOrNull() ?: 0f,
                            sodium = tokens[8].trim().toFloatOrNull() ?: 0f
                        )
                    )
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return foodList
}

@Preview(showBackground = true)
@Composable
fun FoodListScreenPreview() {
    MealLogTheme {
        FoodListScreen()
    }
}
