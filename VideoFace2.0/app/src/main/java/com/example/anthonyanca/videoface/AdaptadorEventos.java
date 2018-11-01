package com.example.anthonyanca.videoface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorEventos extends ArrayAdapter<Eventos> {
    private Activity context;
    ArrayList<Eventos> listaEventos;

    public AdaptadorEventos(Activity context, ArrayList<Eventos> listaEventos) {
        super(context, R.layout.custonlayout, listaEventos);
        this.context = context;
        this.listaEventos = listaEventos;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //LayoutInflater inflater = context.getLayoutInflater();


        convertView = context.getLayoutInflater().inflate(R.layout.custonlayout,null);

        TextView textoTitle = convertView.findViewById(R.id.txtTitle);
        TextView textoDescripcion = convertView.findViewById(R.id.txtDescription);

        final Eventos temEvento = listaEventos.get(position);

        textoTitle.setText(temEvento.getName());
        textoDescripcion.setText(temEvento.getDescription());

        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, EventoActivity.class);
                intent.putExtra("ObjEvento", temEvento);
                context.startActivity(intent);
            }
        });*/

        return convertView;
    }
}
