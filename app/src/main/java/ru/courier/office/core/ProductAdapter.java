package ru.courier.office.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ru.courier.office.R;

import java.util.List;

    public class ProductAdapter extends ArrayAdapter<String> {

        Context context;
        List<Product> Products;

        public ProductAdapter(Context context, List<Product> Products) {
            super(context, R.layout.product_item);
            this.context = context;
            this.Products = Products;
        }

        @Override
        public int getCount() {
            return Products.size();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.product_item, parent, false);
                holder.tvId = (TextView) convertView.findViewById(R.id.tvId);
                holder.tvAmount = (TextView) convertView.findViewById(R.id.tvAmount);
                holder.tvCreated = (TextView) convertView.findViewById(R.id.tvCreated);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Product Product = Products.get(position);

            holder.tvId.setText(Product.Id);
            holder.tvAmount.setText(Product.Amount);
            holder.tvAmount.setText(Product.Created);
            return convertView;
        }

        static class ViewHolder {
            TextView tvId;
            TextView tvAmount;
            TextView tvCreated;
        }
    }


