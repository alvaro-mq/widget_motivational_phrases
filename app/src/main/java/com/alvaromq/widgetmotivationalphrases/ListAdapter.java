package com.alvaromq.widgetmotivationalphrases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.alvaromq.widgetmotivationalphrases.database.Phrase;
import java.util.ArrayList;




public class ListAdapter extends ArrayAdapter<Phrase> {

    public ListAdapter(@NonNull Context context, ArrayList<Phrase> phrasesArrayList) {
        super(context, R.layout.list_item, phrasesArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Phrase phrase = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView tvDescription = convertView.findViewById(R.id.tvDescriptionItemList);
        TextView tvAuthor = convertView.findViewById(R.id.tvAuthorItemList);
        TextView tvNickAvatar = convertView.findViewById(R.id.tvNickAvatarItemList);
        TextView tvNick = convertView.findViewById(R.id.tvNickItemList);

        tvDescription.setText(phrase.getDescription());
        tvAuthor.setText(phrase.getAuthor());
        tvNickAvatar.setText(Utils.generateNickAvatar(phrase.getAuthor()));
        tvNick.setText(Utils.generateNick(phrase.getAuthor()));

        return  convertView;
    }
}
