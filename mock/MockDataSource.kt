package com.eui.coffeeshop.data.mock

import com.eui.coffeeshop.domain.model.Product

/**
 * MockDataSource — 12 products matching the BrewEUI catalogue exactly.
 * Categories: Coffee, Tea, Cold Drinks, Food, Snacks
 */
object MockDataSource {

    fun getProducts(): List<Product> = listOf(
        Product("p001","Cappuccino",45.0,"Coffee",
            "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400",
            "Espresso with steamed milk and velvety foam. Italian perfection.",
            true, 4.8, 124),
        Product("p002","Caramel Latte",50.0,"Coffee",
            "https://images.unsplash.com/photo-1561882468-9110d70d2a78?w=400",
            "Smooth espresso with steamed milk and house-made caramel drizzle.",
            true, 4.6, 98),
        Product("p003","Iced Americano",40.0,"Cold Drinks",
            "https://images.unsplash.com/photo-1510707577719-ae7c14805e3a?w=400",
            "Bold espresso shots over ice. Simple and refreshing.",
            true, 4.5, 87),
        Product("p004","Chocolate Croissant",35.0,"Food",
            "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400",
            "Buttery flaky croissant filled with rich dark chocolate. Baked fresh daily.",
            true, 4.7, 63),
        Product("p005","Matcha Latte",55.0,"Tea",
            "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=400",
            "Ceremonial grade matcha whisked with oat milk. Earthy and creamy.",
            false, 4.4, 72),
        Product("p006","Blueberry Muffin",30.0,"Snacks",
            "https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=400",
            "Soft muffin bursting with fresh blueberries and a golden crumb top.",
            true, 4.3, 55),
        Product("p007","Espresso",35.0,"Coffee",
            "https://images.unsplash.com/photo-1510707577719-ae7c14805e3a?w=400",
            "Rich, concentrated single shot of freshly ground Arabica beans.",
            true, 4.9, 210),
        Product("p008","Cold Brew",48.0,"Cold Drinks",
            "https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=400",
            "Steeped for 20 hours. Silky smooth, low acidity, served over ice.",
            true, 4.7, 143),
        Product("p009","Chai Tea Latte",42.0,"Tea",
            "https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=400",
            "Spiced black tea with cinnamon, cardamom, ginger, and steamed milk.",
            true, 4.5, 91),
        Product("p010","Avocado Toast",60.0,"Food",
            "https://images.unsplash.com/photo-1541519227354-08fa5d50c820?w=400",
            "Sourdough toast topped with smashed avocado, cherry tomatoes, and chili flakes.",
            true, 4.6, 77),
        Product("p011","Vanilla Frappuccino",52.0,"Cold Drinks",
            "https://images.unsplash.com/photo-1577968897966-3d4325b36b61?w=400",
            "Blended espresso, milk, vanilla syrup, and ice. Topped with whipped cream.",
            true, 4.4, 115),
        Product("p012","Almond Cookie",25.0,"Snacks",
            "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400",
            "Crispy almond cookies with a hint of vanilla. Perfect coffee companion.",
            true, 4.2, 48)
    )
}
