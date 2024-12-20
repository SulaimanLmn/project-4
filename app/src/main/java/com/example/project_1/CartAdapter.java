package com.example.project_1;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    ArrayList<Cart> carts;
    TextView totalPrice;

    public CartAdapter(Context context, ArrayList<Cart> carts, TextView totalPrice){
        this.context = context;
        this.carts = carts;
        this.totalPrice = totalPrice;


    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utils utils = Utils.getInstance(context);
        int userId = utils.getUserId();

        Cart cart = carts.get(position);
        Coffee coffee = cart.getCoffee();
        String coffeeTotalPrice = String.valueOf(cart.getCoffee().getPrice() * cart.getAmount());


        holder.coffeeImageIv.setImageResource(coffee.getProductImage());
        holder.coffeeNameTv.setText(coffee.getName());
        holder.coffeePriceTv.setText("Rp. " + coffeeTotalPrice);
        holder.itemAmountTv.setText(cart.getAmountString());
        totalPrice.setText("Rp. " +  utils.getTotalPriceString(userId));

        addAmountHandler(holder.addItemButtonFab, cart);
        removeAmountHandler(holder.removeItemButtonFab, cart);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView coffeeImageIv;
        TextView coffeeNameTv, coffeePriceTv, itemAmountTv;
        FloatingActionButton addItemButtonFab, removeItemButtonFab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coffeeImageIv = itemView.findViewById(R.id.cartCoffeeImage);
            coffeeNameTv = itemView.findViewById(R.id.cartCoffeeName);
            coffeePriceTv = itemView.findViewById(R.id.itemPrice);
            itemAmountTv = itemView.findViewById(R.id.itemAmountText);
            addItemButtonFab = itemView.findViewById(R.id.cartAddItem);
            removeItemButtonFab = itemView.findViewById(R.id.cartRemoveItem);
        }
    }

    private void addAmountHandler(FloatingActionButton addItemBtn, Cart cart){
        addItemBtn.setOnClickListener((v) ->{
            cart.setAmount(cart.getAmount() + 1);
            notifyDataSetChanged();
        });

    }
    private void removeAmountHandler(FloatingActionButton removeItemBtn, Cart cart){
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(context);
        Utils utils = Utils.getInstance(context);
        int userId = utils.getUserId();
        removeItemBtn.setOnClickListener((v) ->{
            //Check if the cart amount is 1 because if we pressed remove when the amount is 1, that means
            // the item will be deleted from the cart
            if(cart.getAmount() != 1){
                cart.setAmount(cart.getAmount() - 1);
                notifyDataSetChanged();
            } else{
                confirmationDialog.setTitle("Delete Item")
                        .setMessage("Do you want to delete this item?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // check if carts only had 1 element cause recycle view wouldn't rebuild again once
                            // carts is empty
                            if(utils.deleteFromCart(userId, cart)){
                                totalPrice.setText("Rp. 0");
                            }
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.cancel()).show();
            }
        });
    }
}
