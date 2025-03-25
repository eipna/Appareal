package com.eipna.appareal.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteRawStatement;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context) {
        super(context, "apparel.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createUserTable = "CREATE TABLE IF NOT EXISTS users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "full_name TEXT NOT NULL, " +
                "contact TEXT NOT NULL, " +
                "address TEXT NOT NULL, " +
                "valid_id TEXT NOT NULL, " +
                "valid_id_image BLOB NOT NULL);";

        String createProductTable = "CREATE TABLE IF NOT EXISTS products(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "description TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "stock INTEGER NOT NULL, " +
                "image BLOB NOT NULL);";

        String createCartTable = "CREATE TABLE IF NOT EXISTS carts(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id));";

        String createOrderTable = "CREATE TABLE IF NOT EXISTS orders(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "total REAL NOT NULL, " +
                "status TEXT NOT NULL, " +
                "payment_method TEXT NOT NULL, " +
                "receive_date TEXT, " +
                "items TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id));";

        sqLiteDatabase.execSQL(createUserTable);
        sqLiteDatabase.execSQL(createProductTable);
        sqLiteDatabase.execSQL(createCartTable);
        sqLiteDatabase.execSQL(createOrderTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users;");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM users", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                String d_email = cursor.getString(cursor.getColumnIndex("email"));

                @SuppressLint("Range")
                String d_password = cursor.getString(cursor.getColumnIndex("password"));

                if (d_email.equals(email) && d_password.equals(password)) {
                    cursor.close();
                    database.close();
                    return true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return false;
    }

    public boolean registerUser(String email, String password, String fullName, String address, String contactNumber, String validID, byte[] validIDImage) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("email", email);
        values.put("password", password);
        values.put("full_name", fullName);
        values.put("address", address);
        values.put("contact", contactNumber);
        values.put("valid_id", validID);
        values.put("valid_id_image", validIDImage);

        long result = database.insert("users", null, values);
        return result != -1;
    }

    public int getUserID(String email, String password) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});

        if (cursor.moveToFirst()) {
            int userID = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
            database.close();
            return userID;
        }

        cursor.close();
        database.close();
        return -1;
    }

    public ArrayList<Product> getProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setID(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow("stock")));
                product.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow("image")));
                list.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public Product getProduct(int productID) {
        Product product = new Product();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(productID)});

        if (cursor.moveToFirst()) {
            product.setID(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow("stock")));
            product.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow("image")));
        }
        cursor.close();
        database.close();
        return product;
    }

    public void clearUserCart(int userID) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete("carts", "user_id = ?", new String[]{String.valueOf(userID)});
        database.close();
    }

    public double getProductPrice(int productID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(productID)});

        if (cursor.moveToFirst()) {
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            cursor.close();
            database.close();
            return price;
        }
        cursor.close();
        database.close();
        return -1;
    }

    public void addProduct(String description, double price, int stock, byte[] image) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("description", description);
        values.put("price", price);
        values.put("stock", stock);
        values.put("image", image);
        database.insert("products", null, values);
        database.close();
    }

    public void addProductToCart(int userID, int productID, int quantity) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        if (isProductInCart(productID, userID)) {
            int currentQuantity = getProductQuantity(productID, userID);
            values.put("quantity", currentQuantity + quantity);
            database.update("carts", values, "user_id = ? AND product_id = ?", new String[]{String.valueOf(userID), String.valueOf(productID)});
            database.close();
            return;
        }

        values.put("user_id", userID);
        values.put("product_id", productID);
        values.put("quantity", quantity);
        database.insert("carts", null, values);
        database.close();
    }

    private int getProductQuantity(int productID, int userID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM carts WHERE product_id = ? AND user_id = ?", new String[]{String.valueOf(productID), String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            cursor.close();
            return quantity;
        }
        cursor.close();
        return -1;
    }

    private boolean isProductInCart(int productID, int userID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM carts WHERE product_id = ? AND user_id = ?", new String[]{String.valueOf(productID), String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public String getProductDescription(int productID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(productID)});

        if (cursor.moveToFirst()) {
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            cursor.close();
            database.close();
            return description;
        }
        cursor.close();
        database.close();
        return null;
    }

    public void removeProductToCart(int cartID) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete("carts", "id = ?", new String[]{String.valueOf(cartID)});
        database.close();
    }

    public void editCartItemQuantity(int cardID, int quantity) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("quantity", quantity);
        database.update("carts", values, "id = ?", new String[]{String.valueOf(cardID)});
        database.close();
    }

    public ArrayList<CartItem> getUserCart(int userID) {
        ArrayList<CartItem> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM carts WHERE user_id = ?", new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
                CartItem cartItem = new CartItem();
                cartItem.setID(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                cartItem.setUserID(userID);
                cartItem.setProductID(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                cartItem.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                list.add(cartItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public long addOrder(int userID, double total, String status, String paymentMethod, String date, String items) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", userID);
        values.put("total", total);
        values.put("status", status);
        values.put("payment_method", paymentMethod);
        values.put("receive_date", date);
        values.put("items", items);

        return database.insert("orders", null, values);
    }

    public Order getOrder(int orderID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM orders WHERE id = ?", new String[]{String.valueOf(orderID)});

        if (cursor.moveToFirst()) {
            Order order = new Order();
            order.setID(orderID);
            order.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            order.setItems(cursor.getString(cursor.getColumnIndexOrThrow("items")));
            order.setDate(cursor.getString(cursor.getColumnIndexOrThrow("receive_date")));
            order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            order.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow("total")));

            cursor.close();
            database.close();
            return order;
        }
        cursor.close();
        database.close();
        return null;
    }

    public String getUserName(int userID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
            cursor.close();
            database.close();
            return name;
        }
        cursor.close();
        database.close();
        return null;
    }

    public ArrayList<Order> getUserOrders(int userID) {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM orders WHERE user_id = ?", new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setID(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                order.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow("total")));
                order.setDate(cursor.getString(cursor.getColumnIndexOrThrow("receive_date")));
                order.setItems(cursor.getString(cursor.getColumnIndexOrThrow("items")));
                list.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public ArrayList<Order> getAllOrders() {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM orders", null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setID(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                order.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow("total")));
                order.setDate(cursor.getString(cursor.getColumnIndexOrThrow("receive_date")));
                order.setItems(cursor.getString(cursor.getColumnIndexOrThrow("items")));
                list.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public void addProductStock(int productID, int amount) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE products SET stock = stock + ? WHERE id = ?");
        statement.bindLong(1, amount);
        statement.bindLong(2, productID);
        statement.executeUpdateDelete();
        database.close();
    }

    public void deductProductStock(int productID, int amount) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE products SET stock = stock - ? WHERE id = ?");
        statement.bindLong(1, amount);
        statement.bindLong(2, productID);
        statement.executeUpdateDelete();
        database.close();
    }

    public void decrementProductStock(int productID, int cartID) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE products SET stock = stock - 1 WHERE id = ?");
        statement.bindLong(1, productID);
        statement.executeUpdateDelete();

        incrementCart(cartID);
    }

    public void incrementProductStock(int productID, int cartID) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE products SET stock = stock + 1 WHERE id = ?");
        statement.bindLong(1, productID);
        statement.executeUpdateDelete();

        decrementCart(cartID);
    }

    public void incrementCart(int cartID) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE carts SET quantity = quantity + 1 WHERE id = ?");
        statement.bindLong(1, cartID);
        statement.executeUpdateDelete();
        database.close();
    }

    public void decrementCart(int cartID) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE carts SET quantity = quantity - 1 WHERE id = ?");
        statement.bindLong(1, cartID);
        statement.executeUpdateDelete();
        database.close();
    }

    public int getProductStock(int productID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(productID)});

        if (cursor.moveToFirst()) {
            int stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));
            cursor.close();
            database.close();
            return stock;
        }
        cursor.close();
        database.close();
        return -1;
    }

    public void editUserOrder(int orderID, String date, String status) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("status", status);
        values.put("receive_date", date);
        database.update("orders", values, "id = ?", new String[]{String.valueOf(orderID)});
        database.close();
    }
}