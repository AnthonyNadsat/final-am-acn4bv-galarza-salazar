package com.acn4bv.buglog;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreRepository {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void agregarBug(Bug bug) {
        Map<String, Object> data = new HashMap<>();
        data.put("nombreJuego", bug.getNombreJuego());
        data.put("plataforma",  bug.getPlataforma());
        data.put("tipo",        bug.getTipo());
        data.put("gravedad",    bug.getGravedad());
        data.put("descripcion", bug.getDescripcion());

        if (bug.getImagenUrl() != null && !bug.getImagenUrl().trim().isEmpty()) {
            data.put("imagenUrl", bug.getImagenUrl());
        }

        db.collection("bugs").add(data);
    }
}