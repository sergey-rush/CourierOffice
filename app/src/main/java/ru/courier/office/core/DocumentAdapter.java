package ru.courier.office.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ru.courier.office.R;

/**
 * Created by rash on 31.08.2017.
 */

public class DocumentAdapter extends ArrayAdapter<String> {

    Context context;
    List<Document> documentList;

    public DocumentAdapter(Context context, List<Document> documentList) {
        super(context, R.layout.document_item);
        this.context = context;
        this.documentList = documentList;
    }

    @Override
    public int getCount() {
        return documentList.size();
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ru.courier.office.core.DocumentAdapter.ViewHolder holder = new ru.courier.office.core.DocumentAdapter.ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.document_item, parent, false);
            holder.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            holder.tvCount = (TextView) view.findViewById(R.id.tvCount);
            view.setTag(holder);
        } else {
            holder = (ru.courier.office.core.DocumentAdapter.ViewHolder) view.getTag();
        }

        Document document = documentList.get(position);
        holder.tvTitle.setText(document.Title);
        holder.tvTitle.setTag(document.Id);
        holder.tvCount.setText(Integer.toString(document.Count));

        return view;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvCount;
    }
}
