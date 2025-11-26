package com.acn4bv.buglog;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class FirestoreRepository {

    private static final String COLLECTION_NAME = "bugs";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void agregarBug(Bug bug) {
        db.collection(COLLECTION_NAME)
                .add(bug)
                .addOnSuccessListener(doc -> {
                    System.out.println("✅ Bug guardado en Firebase: " + doc.getId());
                })
                .addOnFailureListener(e -> {
                    System.err.println("❌ Error al guardar bug: " + e.getMessage());
                });
    }

    public interface FirestoreCallback {
        void onCallback(ArrayList<Bug> lista);
    }

    public static void obtenerBugs(FirestoreCallback callback) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(result -> {
                    ArrayList<Bug> lista = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : result) {
                        Bug bug = doc.toObject(Bug.class);
                        lista.add(bug);
                    }

                    callback.onCallback(lista);
                })
                .addOnFailureListener(e -> {
                    System.err.println("❌ Error al obtener bugs: " + e.getMessage());
                    callback.onCallback(new ArrayList<>());
                });
    }
}
