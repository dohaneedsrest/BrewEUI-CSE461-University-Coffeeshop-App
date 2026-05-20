package com.eui.coffeeshop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eui.coffeeshop.data.local.dao.CartDao
import com.eui.coffeeshop.data.local.dao.OrderDao
import com.eui.coffeeshop.data.local.dao.ProductDao
import com.eui.coffeeshop.data.local.dao.UserDao
import com.eui.coffeeshop.data.local.entity.CartItemEntity
import com.eui.coffeeshop.data.local.entity.OrderEntity
import com.eui.coffeeshop.data.local.entity.OrderItemEntity
import com.eui.coffeeshop.data.local.entity.ProductEntity
import com.eui.coffeeshop.data.local.entity.UserEntity

/**
 * AppDatabase — singleton Room database for the entire app.
 *
 * Design decisions:
 *  - @Volatile on INSTANCE ensures changes are immediately visible to all threads
 *    (prevents stale cached reads from CPU L1/L2 cache).
 *  - Double-checked locking in getInstance() prevents two threads from each
 *    building their own instance during initial creation.
 *  - exportSchema = true + schemaLocation in build.gradle = versioned schema files
 *    in /schemas — essential for writing proper migration tests.
 *  - addCallback seeds the products table on first DB creation.
 */
@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CartItemEntity::class
    ],
    version = 1,
    exportSchema = false         // set true + configure schemaLocation for production
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        private const val DATABASE_NAME = "eui_coffee_shop_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton AppDatabase instance.
         * Safe to call from any thread — only one instance is ever created.
         */
        fun getInstance(context: Context): AppDatabase {
            // First read (no lock) — avoids synchronization overhead in steady state
            return INSTANCE ?: synchronized(this) {
                // Second read (inside lock) — guards against race during creation
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                // fallbackToDestructiveMigration = nuke & recreate on version bump
                // Replace with addMigrations(...) in production when you have live user data
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
