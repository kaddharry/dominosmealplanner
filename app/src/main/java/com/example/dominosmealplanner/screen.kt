package com.example.dominosmealplanner

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview

val DominoRed = Color(0xFFE10A34)
val DominoBlue = Color(0xFF0055A5)
val DominoWhite = Color(0xFFFFFFFF)
val DominoBlack = Color(0xFF000000)
val DominoDarkGray = Color(0xFF333333)
val LightGrey = Color(0xFFEBEBEB)
val heading = Color(0xFFF12D6B)

var savedMeals = mutableStateListOf<List<MenuItem>>()

@Composable
fun DominosMealApp() {
    var budget by remember { mutableStateOf("") }
    var showSavedMeals by remember { mutableStateOf(false) }
    val combinations = remember(budget) { calculateCombinations(budget.toIntOrNull() ?: 0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DominoBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(45.dp))

            Box(
                modifier = Modifier
                    .width(400.dp)
                    .height(80.dp)
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Domino's Meal Planner",
                    fontSize = 32.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = heading,
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White, shape = CircleShape)
                    .clip(CircleShape)
                    .clickable { showSavedMeals = true },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "Domino's Logo",
                    modifier = Modifier.size(185.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Enter Your budget", color = DominoBlack) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGrey,
                    unfocusedContainerColor = DominoWhite,
                    focusedTextColor = DominoBlack,
                    unfocusedTextColor = DominoBlack,
                    focusedIndicatorColor = DominoBlue,
                    unfocusedIndicatorColor = DominoWhite
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (showSavedMeals) {
                SavedMealsScreen { showSavedMeals = false }
            } else {
                LazyColumn {
                    items(combinations) { combination ->
                        CombinationCard(combination)
                    }
                }
            }
        }
    }
}

@Composable
fun CombinationCard(combination: List<MenuItem>) {
    var isLiked by remember { mutableStateOf(false) }
    val totalPrice = combination.sumOf { it.price }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, DominoRed, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DominoWhite),
        border = BorderStroke(1.dp, DominoRed)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                combination.forEach { item ->
                    Text(text = "${item.name} - ₹${item.price}", fontSize = 16.sp, color = DominoDarkGray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Total: ₹$totalPrice", fontSize = 18.sp, color = DominoBlue)
            }

            IconButton(onClick = {
                isLiked = !isLiked
                if (isLiked) {
                    savedMeals.add(combination)
                } else {
                    savedMeals.remove(combination)
                }
            }) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like Meal",
                    tint = if (isLiked) DominoRed else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Domino's Logo",
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun SavedMealsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Saved Meals",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DominoBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
        LazyColumn {
            items(savedMeals) { combination ->
                CombinationCard(combination)
            }
        }
    }
}

fun calculateCombinations(budget: Int): List<List<MenuItem>> {
    val validCombinations = mutableListOf<List<MenuItem>>()
    for (i in dominoMenu.indices) {
        val combination = mutableListOf<MenuItem>()
        var totalPrice = 0
        for (j in i until dominoMenu.size) {
            val item = dominoMenu[j]
            if (totalPrice + item.price <= budget + 10) {
                combination.add(item)
                totalPrice += item.price
                if (totalPrice in (budget - 10)..(budget + 10)) {
                    validCombinations.add(combination.toList())
                }
            }
        }
    }
    return validCombinations
}


data class MenuItem(
    var name: String,
    var price: Int
)
val dominoMenu = listOf(
    // Pizzas
    MenuItem("Margherita", 200),
    MenuItem("Pepperoni", 250),
    MenuItem("Veggie Paradise", 220),
    MenuItem("Cheese Burst", 300),
    MenuItem("Farmhouse", 280),
    MenuItem("Chicken Dominator", 350),
    MenuItem("Paneer Makhani", 270),
    MenuItem("Spicy Chicken", 320),
    MenuItem("Tandoori Veg", 240),
    MenuItem("Tandoori Chicken", 330),
    MenuItem("Double Cheese Margherita", 310),
    MenuItem("Mexican Green Wave", 290),
    MenuItem("Chicken Golden Delight", 249), // Updated price from image
    MenuItem("Non-Veg Supreme", 319), // Updated price from image
    MenuItem("Veg Extravaganza", 260),
    MenuItem("Pepper Barbecue Chicken & Onion", 229), // From image
    MenuItem("Chicken Sausage", 189), // From image
    MenuItem("Chicken Pepperoni", 319), // From image
    MenuItem("Chicken Fiesta", 249), // From image
    MenuItem("Indi Chicken Tikka", 319), // From image
    MenuItem("Keema Do Pyaza", 189), // From image

    // Sides
    MenuItem("Garlic Breadsticks", 100),
    MenuItem("Stuffed Garlic Bread", 150),
    MenuItem("Paneer Zingy Parcel", 120),
    MenuItem("Chicken Wings", 180),
    MenuItem("Potato Wedges", 90),
    MenuItem("Chicken Pepperoni Stuffed Garlic Bread", 200),
    MenuItem("Veg Pasta Italiano White", 130),
    MenuItem("Non-Veg Pasta Italiano White", 160),
    MenuItem("Veg Pasta Italiano Red", 130),
    MenuItem("Non-Veg Pasta Italiano Red", 160),

    // Desserts
    MenuItem("Choco Lava Cake", 110),
    MenuItem("Butterscotch Mousse Cake", 140),
    MenuItem("New York Cheesecake", 170),
    MenuItem("Dark Fantasy", 120),
    MenuItem("Chocolate Brownie", 100),
    MenuItem("Vanilla Ice Cream", 80),
    MenuItem("Strawberry Ice Cream", 80),
    MenuItem("Chocolate Ice Cream", 80),

    // Beverages
    MenuItem("Pepsi 500ml", 60),
    MenuItem("Mirinda 500ml", 60),
    MenuItem("7Up 500ml", 60),
    MenuItem("Mountain Dew 500ml", 60),
    MenuItem("Water Bottle 1L", 40),
    MenuItem("Iced Tea", 70),
    MenuItem("Cold Coffee", 90),
    MenuItem("Orange Juice", 80),
    MenuItem("Mango Juice", 80),

    // Combos
    MenuItem("Meal for 2: 2 Medium Pizzas + Garlic Bread + Pepsi", 800),
    MenuItem("Meal for 4: 4 Medium Pizzas + Stuffed Garlic Bread + Pepsi", 1500),
    MenuItem("Snack Combo: Garlic Bread + Potato Wedges + Pepsi", 300),
    MenuItem("Dessert Combo: Choco Lava Cake + Brownie + Ice Cream", 250),
    MenuItem("Family Combo: 1 Large Pizza + 1 Medium Pizza + Garlic Bread + Pepsi", 1200)
)

































//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Text
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontStyle
//
////val DominoRed = Color(0xFFE31837) hex code is needed for colour, put0x in front of it
//val DominoRed = Color(0xFFE10A34) // Domino's Red
//val DominoBlue = Color(0xFF0055A5) // Domino's Blue
//val DominoWhite = Color(0xFFFFFFFF) // White
//val DominoBlack = Color(0xFF000000) // Black
//val DominoDarkGray = Color(0xFF333333) // Dark Gray
//val LightGrey = Color(0xFFEBEBEB) // Light grey color
//val heading = Color(0xFFF12D6B)
//
//@Composable
//fun DominosMealApp(){
//    //PASCAL CONVENTION basically says that first letter of every word is capital under composable section like D,M,A here
//    var budget by remember { mutableStateOf("") }
//    val combinations = remember(budget) {
//        calculateCombinations(budget.toIntOrNull() ?: 0)
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(DominoBlue)
//    ){
//        //in general it is used to stack things up but here we r using it for giving a light grey colour to background
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//
//            horizontalAlignment = Alignment.CenterHorizontally
//
////            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//            Spacer(modifier = Modifier.height(45.dp))
//
//            Box(
//                modifier = Modifier
//                    .width(400.dp) // Adjust width to make it a rectangle
//                    .height(80.dp) // Adjust height to balance proportions
//                    .background(Color.White, shape = RoundedCornerShape(24.dp)) // Rounded rectangle shape
//                    .clip(RoundedCornerShape(24.dp)), // Ensures the shape is maintained
//                contentAlignment = Alignment.Center // Centers content inside
//            )
//            {
//                Text(
//                    text = "Domino's Meal Planner",
//                    fontSize = 32.sp,
//                    fontStyle = FontStyle.Italic,
//                    fontWeight = FontWeight.SemiBold,
//                    fontFamily = FontFamily.SansSerif, // Sans-Serif Condensed
//                    color = heading,
//                )
//            }
//
//            Spacer(modifier = Modifier.height(5.dp))
//            Box(
//                modifier = Modifier
//                    .size(150.dp) // Overall size of the circular icon
//                    .background(Color.White, shape = CircleShape) // White circular background
//                    .clip(CircleShape), // Ensures the image stays within a circle
//                contentAlignment = Alignment.Center // Centers the image inside the circle
//            ) {
//                Image(
//                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
//                    contentDescription = "Domino's Logo",
//                    modifier = Modifier
//                        .size(185.dp) // Slightly smaller than the Box to maintain padding
//                        .clip(CircleShape), // Ensures image fits within the circle
//                    contentScale = ContentScale.Fit
//                )
//            }
//
//            Spacer(modifier = Modifier.height(5.dp))
//            TextField(
//                value = budget,
//                onValueChange = {budget = it },
//                label = {Text("Enter Your budget", color = DominoBlack)},
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.fillMaxWidth().padding(10.dp),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = LightGrey,
//                    unfocusedContainerColor = DominoWhite,
//                    focusedTextColor = DominoBlack,
//                    unfocusedTextColor = DominoBlack,
//                    focusedIndicatorColor = DominoBlue,
//                    unfocusedIndicatorColor = DominoWhite
//                )
//
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            LazyColumn{
//                items(combinations) { combination ->
//                    CombinationCard(combination)
//                }
//            }
//
//
//        }
//    }
//
//}
//data class MenuItem(
//    var name: String,
//    var price: Int
//)
//val dominoMenu = listOf(
//    // Pizzas
//    MenuItem("Margherita", 200),
//    MenuItem("Pepperoni", 250),
//    MenuItem("Veggie Paradise", 220),
//    MenuItem("Cheese Burst", 300),
//    MenuItem("Farmhouse", 280),
//    MenuItem("Chicken Dominator", 350),
//    MenuItem("Paneer Makhani", 270),
//    MenuItem("Spicy Chicken", 320),
//    MenuItem("Tandoori Veg", 240),
//    MenuItem("Tandoori Chicken", 330),
//    MenuItem("Double Cheese Margherita", 310),
//    MenuItem("Mexican Green Wave", 290),
//    MenuItem("Chicken Golden Delight", 249), // Updated price from image
//    MenuItem("Non-Veg Supreme", 319), // Updated price from image
//    MenuItem("Veg Extravaganza", 260),
//    MenuItem("Pepper Barbecue Chicken & Onion", 229), // From image
//    MenuItem("Chicken Sausage", 189), // From image
//    MenuItem("Chicken Pepperoni", 319), // From image
//    MenuItem("Chicken Fiesta", 249), // From image
//    MenuItem("Indi Chicken Tikka", 319), // From image
//    MenuItem("Keema Do Pyaza", 189), // From image
//
//    // Sides
//    MenuItem("Garlic Breadsticks", 100),
//    MenuItem("Stuffed Garlic Bread", 150),
//    MenuItem("Paneer Zingy Parcel", 120),
//    MenuItem("Chicken Wings", 180),
//    MenuItem("Potato Wedges", 90),
//    MenuItem("Chicken Pepperoni Stuffed Garlic Bread", 200),
//    MenuItem("Veg Pasta Italiano White", 130),
//    MenuItem("Non-Veg Pasta Italiano White", 160),
//    MenuItem("Veg Pasta Italiano Red", 130),
//    MenuItem("Non-Veg Pasta Italiano Red", 160),
//
//    // Desserts
//    MenuItem("Choco Lava Cake", 110),
//    MenuItem("Butterscotch Mousse Cake", 140),
//    MenuItem("New York Cheesecake", 170),
//    MenuItem("Dark Fantasy", 120),
//    MenuItem("Chocolate Brownie", 100),
//    MenuItem("Vanilla Ice Cream", 80),
//    MenuItem("Strawberry Ice Cream", 80),
//    MenuItem("Chocolate Ice Cream", 80),
//
//    // Beverages
//    MenuItem("Pepsi 500ml", 60),
//    MenuItem("Mirinda 500ml", 60),
//    MenuItem("7Up 500ml", 60),
//    MenuItem("Mountain Dew 500ml", 60),
//    MenuItem("Water Bottle 1L", 40),
//    MenuItem("Iced Tea", 70),
//    MenuItem("Cold Coffee", 90),
//    MenuItem("Orange Juice", 80),
//    MenuItem("Mango Juice", 80),
//
//    // Combos
//    MenuItem("Meal for 2: 2 Medium Pizzas + Garlic Bread + Pepsi", 800),
//    MenuItem("Meal for 4: 4 Medium Pizzas + Stuffed Garlic Bread + Pepsi", 1500),
//    MenuItem("Snack Combo: Garlic Bread + Potato Wedges + Pepsi", 300),
//    MenuItem("Dessert Combo: Choco Lava Cake + Brownie + Ice Cream", 250),
//    MenuItem("Family Combo: 1 Large Pizza + 1 Medium Pizza + Garlic Bread + Pepsi", 1200)
//)
//
//@Composable
//fun CombinationCard(combination: List<MenuItem>) {
//    val totalPrice = combination.sumOf { it.price }
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .border(2.dp, DominoRed, shape = RoundedCornerShape(16.dp)),
//        colors = CardDefaults.cardColors(
//            containerColor = DominoWhite
//        ),
//        border = BorderStroke(1.dp, DominoRed)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                combination.forEach { item ->
//                    Text(text = "${item.name} - ₹${item.price}", fontSize = 16.sp, color = DominoDarkGray)
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "Total: ₹$totalPrice", fontSize = 18.sp, color = DominoBlue)
//            }
//
//            // Domino's Logo on the Right Side of the Card
//            Image(
//                painter = painterResource(id = R.mipmap.ic_launcher_foreground), // Add your logo to res/drawable
//                contentDescription = "Domino's Logo",
//                modifier = Modifier.size(50.dp), // Adjust size as needed
//                contentScale = ContentScale.Fit
//            )
//        }
//    }
//}
//
//fun calculateCombinations(budget: Int): List<List<MenuItem>> {
//    val validCombinations = mutableListOf<List<MenuItem>>() // List to store valid combinations
//
//    // Loop through all menu items
//    for (i in dominoMenu.indices) {
//        val combination = mutableListOf<MenuItem>() // Temporary list for a single combination
//        var totalPrice = 0
//
//        // Add items one by one to the combination
//        for (j in i until dominoMenu.size) {
//            val item = dominoMenu[j]
//            if (totalPrice + item.price <= budget + 10) {
//                combination.add(item) // Add item to the combination
//                totalPrice += item.price // Update the total price
//
//                // Check if this combination fits the budget range
//                if (totalPrice in (budget - 10)..(budget + 10)) {
//                    validCombinations.add(combination.toList()) // Add a copy of the combination
//                }
//            }
//        }
//    }
//
//    return validCombinations // Return all valid combinations
//}