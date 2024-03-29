package com.example.fede.animalarium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentChange.Type;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageException;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Snippets for inclusion in documentation.
 */
@SuppressWarnings({"unused", "Convert2Lambda"})
public class DocSnippets implements DocSnippetsInterface {

    private static final String TAG = "DocSnippets";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4,
            60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private final FirebaseFirestore db;
    MisMascotasAdapter misMascotasAdapter;
    PropietariosAdapter propietariosAdapter;
    MascotasPropietarioActivity mascotasPropietarioActivity;
    FormularioPropietarioActivity formularioPropietarioActivity;
    PropietariosActivity propietariosActivity;
    SplashScreenActivity splashScreenActivity;
    MainActivity mainActivity;
    FormularioActivity formularioActivity;
    TotalesActivity totalesActivity;
    ContactosActivity contactosActivity;
    PeluqueriasContactoActivity peluqueriasContactoActivity;
    PeluqueriasActivity peluqueriasActivity;
    CitasAdapter citasAdapter;
    MisCitasAdapter misCitasAdapter;
    ReservasAdapter reservasAdapter;
    FormularioCitaActivity formularioCitaActivity;
    HotelActivity hotelActivity;
    HotelContactoActivity hotelContactoActivity;
    FormularioHotelActivity formularioHotelActivity;
    MisReservasAdapter misReservasAdapter;
    Context context;

    ProgressDialog progressDialog = null;
    private double totalHotelSemana;
    private int diaSemana=0;


    public DocSnippets(FirebaseFirestore db, SplashScreenActivity splashScreenActivity) {
        this.db = db;
        this.splashScreenActivity = splashScreenActivity;
        context = splashScreenActivity;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("...actualizando perritos...");
    }

    DocSnippets(FirebaseFirestore db, ContactosActivity contactosActivity) {
        this.db = db;
        this.contactosActivity = contactosActivity;
        context = contactosActivity;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("...actualizando perritos...");
    }

    public DocSnippets(FirebaseFirestore db, PropietariosActivity propietariosActivity) {
        this.db = db;
        this.propietariosActivity = propietariosActivity;
        context = propietariosActivity;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("...actualizando propietarios...");
    }

    DocSnippets(FirebaseFirestore db, HotelActivity hotelActivity) {
        this.db = db;
        this.hotelActivity = hotelActivity;
        context = hotelActivity;
    }

    DocSnippets(FirebaseFirestore db, MisReservasAdapter misReservasAdapter) {
        this.db = db;
        this.misReservasAdapter = misReservasAdapter;

    }

    DocSnippets(FirebaseFirestore db, HotelContactoActivity hotelContactoActivity) {
        this.db = db;
        this.hotelContactoActivity = hotelContactoActivity;
        context = hotelContactoActivity;
    }

    DocSnippets(FirebaseFirestore db, FormularioHotelActivity formularioHotelActivity) {
        this.db = db;
        this.formularioHotelActivity = formularioHotelActivity;
        context = formularioHotelActivity;
    }

    DocSnippets(FirebaseFirestore db, PeluqueriasContactoActivity peluqueriasContactoActivity) {
        this.db = db;
        this.peluqueriasContactoActivity = peluqueriasContactoActivity;
        context = peluqueriasContactoActivity;
    }

    DocSnippets(FirebaseFirestore db, PeluqueriasActivity peluqueriasActivity) {
        this.db = db;
        this.peluqueriasActivity = peluqueriasActivity;
        context = peluqueriasActivity;
    }

    DocSnippets(FirebaseFirestore db, CitasAdapter citasAdapter) {
        this.db = db;
        this.citasAdapter = citasAdapter;

    }

    DocSnippets(FirebaseFirestore db, ReservasAdapter reservasAdapter) {
        this.db = db;
        this.reservasAdapter = reservasAdapter;
    }

    DocSnippets(FirebaseFirestore db, MisCitasAdapter citasAdapter) {
        this.db = db;
        this.misCitasAdapter = citasAdapter;
    }

    DocSnippets(FirebaseFirestore db, FormularioActivity fca) {
        this.db = db;
        this.formularioActivity = fca;
        context = fca;
    }

    DocSnippets(FirebaseFirestore db, FormularioCitaActivity fca) {
        this.db = db;
        this.formularioCitaActivity = fca;
        context = fca;
    }

    DocSnippets(FirebaseFirestore db, TotalesActivity fca) {
        this.db = db;
        this.totalesActivity = fca;
        context = fca;
    }

    public DocSnippets(FirebaseFirestore db, MainActivity mainActivity) {
        this.db = db;
        this.mainActivity = mainActivity;
        context = mainActivity;
    }



    public DocSnippets(FirebaseFirestore db, FormularioPropietarioActivity application) {
        this.db = db;
        this.formularioPropietarioActivity = application;
        context = application;
    }

    public DocSnippets(FirebaseFirestore db, MascotasPropietarioActivity mascotasPropietarioActivity) {
        this.db = db;
        this.mascotasPropietarioActivity = mascotasPropietarioActivity;
        context = mascotasPropietarioActivity;
    }

    public DocSnippets(FirebaseFirestore db, PropietariosAdapter propietariosAdapter) {
        this.db = db;
        this.propietariosAdapter = propietariosAdapter;
    }

    public DocSnippets(FirebaseFirestore db, MisMascotasAdapter misMascotasAdapter) {
        this.db = db;
        this.misMascotasAdapter = misMascotasAdapter;
    }


    void runAll() {
        Log.d(TAG, "================= BEGIN RUN ALL ===============");

        // Write example data
        exampleData();

        // Run all other methods
        addAdaLovelace();
        addAlanTuring();
        getAllUsers();
        listenForUsers();
        docReference();
        collectionReference();
        subcollectionReference();
        setDocument();
        dataTypes();
        addDocument();
        newDocument();
        updateDocument();
        updateDocumentNested();
        setFieldWithMerge();
        deleteDocument();
        transactions();
        transactionPromise();
        getDocument();
        listenToDocument();
        listenToDocumentLocal();
        getMultipleDocs();
        getAllDocs();
        listenToMultiple();
        listenToDiffs();
        listenState();
        detachListener();
        handleListenErrors();
        simpleQueries();
        compoundQueries();
        orderAndLimit();
        queryStartAtEndAt();

        // Run methods that should fail
        try {
            compoundQueriesInvalid();
        } catch (Exception e) {
            Log.d(TAG, "compoundQueriesInvalid", e);
        }

        try {
            orderAndLimitInvalid();
        } catch (Exception e) {
            Log.d(TAG, "orderAndLimitInvalid", e);
        }
    }


    void deleteAll() {
        deleteCollection("cities");
        deleteCollection("users");
    }

    private void deleteCollection(final String path) {
        deleteCollection(db.collection(path), 50, EXECUTOR);
    }

    @Override
    public void setup() {
        // [START get_firestore_instance]
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
    }

    @Override
    public void addAdaLovelace() {
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        // [END add_ada_lovelace]
    }


    @Override
    public void addAlanTuring() {
        // [START add_alan_turing]
        // Create a new user with a first, middle, and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Alan");
        user.put("middle", "Mathison");
        user.put("last", "Turing");
        user.put("born", 1912);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        // [END add_alan_turing]
    }

    @Override
    public void getAllUsers() {
        // [START get_all_users]
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        // [END get_all_users]
    }

    @Override
    public void listenForUsers() {
        // [START listen_for_users]
        // Listen for users born before 1900.
        //
        // You will get a first snapshot with the initial results and a new
        // snapshot each time there is a change in the results.
        db.collection("users")
                .whereLessThan("born", 1900)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        Log.d(TAG, "Current users born before 1900: " + snapshots);
                    }
                });
        // [END listen_for_users]
    }

    @Override
    public void docReference() {
        // [START doc_reference]
        DocumentReference alovelaceDocumentRef = db.collection("users").document("alovelace");
        // [END doc_reference]
    }

    @Override
    public void collectionReference() {
        // [START collection_reference]
        CollectionReference usersCollectionRef = db.collection("users");
        // [END collection_reference]
    }

    @Override
    public void subcollectionReference() {
        // [START subcollection_reference]
        DocumentReference messageRef = db
                .collection("rooms").document("roomA")
                .collection("messages").document("message1");
        // [END subcollection_reference]
    }

    @Override
    public void docReferenceAlternate() {
        // [START doc_reference_alternate]
        DocumentReference alovelaceDocumentRef = db.document("users/alovelace");
        // [END doc_reference_alternate]
    }




    // [START city_class]
    public class City {


        private String name;
        private String state;
        private String country;
        private boolean capital;
        private long population;

        public City() {
        }

        public City(String name, String state, String country, boolean capital, long population) {
            // [START_EXCLUDE]
            this.name = name;
            this.state = state;
            this.country = country;
            this.capital = capital;
            this.population = population;
            // [END_EXCLUDE]
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }

        public String getCountry() {
            return country;
        }

        public boolean isCapital() {
            return capital;
        }

        public long getPopulation() {
            return population;
        }

    }
    // [END city_class]

    @Override
    public void setDocument() {
        // [START set_document]
        Map<String, Object> city = new HashMap<>();
        city.put("name", "Los Angeles");
        city.put("state", "CA");
        city.put("country", "USA");

        db.collection("cities").document("LA")
                .set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        // [END set_document]

        Map<String, Object> data = new HashMap<>();

        // [START set_with_id]
        db.collection("cities").document("new-city-id").set(data);
        // [END set_with_id]
    }

    @Override
    public void dataTypes() {
        // [START data_types]
        Map<String, Object> docData = new HashMap<>();
        docData.put("stringExample", "Hello world!");
        docData.put("booleanExample", true);
        docData.put("numberExample", 3.14159265);
        docData.put("dateExample", new Date());
        docData.put("listExample", Arrays.asList(1, 2, 3));
        docData.put("nullExample", null);

        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("a", 5);
        nestedData.put("b", true);

        docData.put("objectExample", nestedData);

        db.collection("data").document("one")
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        // [END data_types]
    }

    @Override
    public void addCustomClass() {
        // [START add_custom_class]
        City city = new City("Los Angeles", "CA", "USA", false, 5000000L);
        db.collection("cities").document("LA").set(city);
        // [END add_custom_class]
    }

    @Override
    public void addDocument() {
        // [START add_document]
        // Add a new document with a generated id.
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Tokyo");
        data.put("country", "Japan");

        db.collection("cities")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        // [END add_document]
    }

    @Override
    public void newDocument() {
        // [START new_document]
        Map<String, Object> data = new HashMap<>();

        DocumentReference newCityRef = db.collection("cities").document();

        // Later...
        newCityRef.set(data);
        // [END new_document]
    }

    @Override
    public void updateDocument() {
        // [START update_document]
        DocumentReference washingtonRef = db.collection("cities").document("DC");

        // Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update("capital", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        // [END update_document]
    }

    @Override
    public void updateDocumentNested() {
        // [START update_document_nested]
        // Assume the document contains:
        // {
        //   name: "Frank",
        //   favorites: { food: "Pizza", color: "Blue", subject: "recess" }
        //   age: 12
        // }
        //
        // To update age and favorite color:
        db.collection("users").document("frank")
                .update(
                        "age", 13,
                        "favorites.color", "Red"
                );
        // [END update_document_nested]
    }

    @Override
    public void setFieldWithMerge() {
        // [START set_field_with_merge]
        // Update one field, creating the document if it does not already exist.
        Map<String, Object> data = new HashMap<>();
        data.put("capital", true);

        db.collection("cities").document("BJ")
                .set(data, SetOptions.merge());
        // [END set_field_with_merge]
    }

    @Override
    public void deleteDocument() {
        // [START delete_document]
        db.collection("cities").document("DC")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        // [END delete_document]
    }

    @Override
    public void transactions() {
        // [START transactions]
        final DocumentReference sfDocRef = db.collection("cities").document("SF");

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                double newPopulation = snapshot.getDouble("population") + 1;
                transaction.update(sfDocRef, "population", newPopulation);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });
        // [END transactions]
    }

    @Override
    public void transactionPromise() {
        // [START transaction_with_result]
        final DocumentReference sfDocRef = db.collection("cities").document("SF");

        db.runTransaction(new Transaction.Function<Double>() {
            @Override
            public Double apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                double newPopulation = snapshot.getDouble("population") + 1;
                if (newPopulation <= 1000000) {
                    transaction.update(sfDocRef, "population", newPopulation);
                    return newPopulation;
                } else {
                    throw new FirebaseFirestoreException("Population too high",
                            FirebaseFirestoreException.Code.ABORTED);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Double>() {
            @Override
            public void onSuccess(Double result) {
                Log.d(TAG, "Transaction success: " + result);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });
        // [END transaction_with_result]
    }

    @Override
    public void writeBatch() {
        // [START write_batch]
        // Get a new write batch
        WriteBatch batch = db.batch();

        // Set the value of 'NYC'
        DocumentReference nycRef = db.collection("cities").document("NYC");
        batch.set(nycRef, new City());

        // Update the population of 'SF'
        DocumentReference sfRef = db.collection("cities").document("SF");
        batch.update(sfRef, "population", 1000000L);

        // Delete the city 'LA'
        DocumentReference laRef = db.collection("cities").document("LA");
        batch.delete(laRef);

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // ...
            }
        });
        // [END write_batch]
    }

    @Override
    public void getDocument() {
        // [START get_document]
        DocumentReference docRef = db.collection("cities").document("SF");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        // [END get_document]
    }

    @Override
    public void customObjects() {
        // [START custom_objects]
        DocumentReference docRef = db.collection("cities").document("BJ");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                City city = documentSnapshot.toObject(City.class);
            }
        });
        // [END custom_objects]
    }

    @Override
    public void listenToDocument() {
        // [START listen_document]
        final DocumentReference docRef = db.collection("cities").document("SF");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        // [END listen_document]
    }

    @Override
    public void listenToDocumentLocal() {
        // [START listen_document_local]
        final DocumentReference docRef = db.collection("cities").document("SF");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.getData());
                } else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });
        // [END listen_document_local]
    }

    @Override
    public void listenWithMetadata() {

    }


    @Override
    public void getMultipleDocs() {
        // [START get_multiple]
        db.collection("cities")
                .whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    @Override
    public void getAllDocs() {
        // [START get_multiple]
        db.collection("cities")
                .whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    public void getFotos() {

        // [START get_multiple_all]
        db.collection("contactos")
                .orderBy("mascota")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (splashScreenActivity != null) {
                                ContactosActivity.setContactos(task.getResult().getDocuments(), splashScreenActivity, "splash_screen", progressDialog);

                            } else if (contactosActivity != null) {
                                contactosActivity.setContactos(task.getResult().getDocuments(), contactosActivity, "contactos", progressDialog);
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getPropietarios()  {

        progressDialog.show();
        // [START get_multiple_all]
        db.collection("propietarios")
                .orderBy("propietario")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (splashScreenActivity != null) {
                                PropietariosActivity.setPropietarios(task.getResult().getDocuments(), splashScreenActivity, "splash_screen",progressDialog);
                                Log.e("splash_docSnippets_propietarios",String.valueOf(task.getResult().size()));

                            } else if (propietariosActivity != null) {
                                propietariosActivity.setPropietarios(task.getResult().getDocuments(), propietariosActivity, "contactos", progressDialog);
                                Log.e("propietarios_docSnippets_propietarios",String.valueOf(task.getResult().size()));

                            }

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        // [END get_multiple_all]
    }

    public void getContactos()  {

        if (!progressDialog.isShowing())
        progressDialog.show();
        // [START get_multiple_all]
        db.collection("contactos")
                .orderBy("mascota")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (splashScreenActivity != null) {
                                ContactosActivity.setContactos(task.getResult().getDocuments(), splashScreenActivity, "splash_screen", progressDialog);

                            } else if (contactosActivity != null) {
                                contactosActivity.setContactos(task.getResult().getDocuments(), contactosActivity, "contactos", progressDialog);
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        // [END get_multiple_all]
    }


    public void getContactoConId(String id) {
        Log.e("id", id);
        final String idd = id;
        final DocumentReference docRef = db.collection("contactos").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            if (idd.equalsIgnoreCase(document.getId())) {
                                Log.e("contactoid", document.getId());
                                if (formularioCitaActivity != null)
                                    formularioCitaActivity.cargarFoto(document);
                                if (peluqueriasActivity != null)
                                    peluqueriasActivity.bindeaYAñadeContacto(document);
                                if (formularioActivity != null)
                                    formularioActivity.bindeaYAñadeContacto(document);
                                if (hotelActivity != null)
                                    hotelActivity.bindeaYAñadeContacto(document);
                            }
                        } catch (NoSuchElementException e) {
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });
    }


    public void getContactosParaNombre(String mascota) {

        progressDialog.show();
        String sig = "", mas = String.valueOf(mascota.charAt(0));
        switch (mas) {
            case "a":
                sig = "b";
                break;
            case "b":
                sig = "c";
                break;
            case "c":
                sig = "d";
                break;
            case "e":
                sig = "f";
                break;
            case "f":
                sig = "g";
                break;
            case "g":
                sig = "h";
                break;
            case "h":
                sig = "i";
                break;
            case "i":
                sig = "j";
                break;
            case "j":
                sig = "k";
                break;
            case "k":
                sig = "l";
                break;
            case "l":
                sig = "m";
                break;
            case "m":
                sig = "n";
                break;
            case "n":
                sig = "ñ";
                break;
            case "ñ":
                sig = "o";
                break;
            case "p":
                sig = "q";
                break;
            case "o":
                sig = "q";
                break;
            case "q":
                sig = "r";
                break;
            case "r":
                sig = "s";
                break;
            case "s":
                sig = "t";
                break;
            case "t":
                sig = "u";
                break;
            case "u":
                sig = "v";
                break;
            case "v":
                sig = "x";
                break;
            case "w":
                sig = "x";
                break;
            case "x":
                sig = "y";
                break;
            case "y":
                sig = "z";
                break;
            case "z":
                sig = "a";
        }
        db.collection("contactos")
                .orderBy("mascota")
                .startAt(mascota)
                .endAt(sig)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().size() > 0) {
                            try {
                                contactosActivity.setContactos(task.getResult().getDocuments(), contactosActivity, "contactos_activity", progressDialog);
                            } catch (NoSuchElementException e) {
                                Toast.makeText(contactosActivity, "No existen mascotas :(", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(contactosActivity, ContactosActivity.class);
                                intent.putExtra("VIENE","docSnippets");
                                contactosActivity.startActivity(intent);
                            }

                        } else {
                            Toast.makeText(contactosActivity, "No existen mascotas :(", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(contactosActivity, ContactosActivity.class);
                            contactosActivity.startActivity(intent);
                        }
                    }
                });
        contactosActivity.buscador.setCursorVisible(true);
    }

    public void getCitas(Date fecha) {
        fecha.setHours(00);
        fecha.setMinutes(00);
        fecha.setSeconds(00);
        Log.e("finicio", (String.valueOf(fecha)));
        Date f = new Date();
        f.setTime(fecha.getTime() + (3600000 * 24));
        Log.e("ffin", (String.valueOf(f)));
        // [START get_multiple_all]
        db.collection("citas")
                .orderBy("fecha")
                .startAt(fecha)
                .endAt(f)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() == 0) {
                                Log.e(TAG, "No hay peluquerias para esa fecha");
                                Toast.makeText(context,"No hay peluquerias para esa fecha", Toast.LENGTH_SHORT).show();
                                peluqueriasActivity.inicimosAdaptador();

                            } else
                                peluqueriasActivity.setPeluquerias(task.getResult().getDocuments());

                        } else {
                            Log.e(TAG, "Error getting documents", task.getException());
                            Toast.makeText(context,"No existen peluquerias para esa fecha", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END get_multiple_all]
    }

    public void getCitaConId(String id) {
        Log.e("id", id);
        final String idd = id;
        final DocumentReference docRef = db.collection("citas").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            if (idd.equalsIgnoreCase(document.getId())) {
                               formularioCitaActivity.bindeaCitaPeluqueria(document);
                            }
                        } catch (NoSuchElementException e) {
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });
    }

    public void getPeluqueriasPorContacto() {
        // [START get_multiple_all]

        Contacto contacto = (Contacto) ComunicadorContacto.getContacto();
        db.collection("citas")
                .whereEqualTo("idContacto", contacto.get_id())
                .orderBy("fecha", Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (peluqueriasContactoActivity!=null){
                                for (DocumentSnapshot document : task.getResult()) {
                                    peluqueriasContactoActivity.bindeaYAñadeCita(document);
                                }
                                peluqueriasContactoActivity.inicimosAdaptador();
                            } else if (formularioActivity!=null) formularioActivity.bindeaCitas(task.getResult());

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple_all]
    }

    public void getMascotasPorPropietario(Propietario propietario) {

        try{
            db.collection("mascotas")
                    .whereEqualTo("idPropietario", propietario.getId())
                    .orderBy("mascota", Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (mascotasPropietarioActivity!=null){
                                    for (DocumentSnapshot document : task.getResult()) {
                                        mascotasPropietarioActivity.bindeaYAñadeMascota(document);
                                    }
                                    mascotasPropietarioActivity.inicimosAdaptador();
                                } else if (propietariosAdapter!=null) propietariosAdapter.setMascotas(task.getResult());

                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            // [END get_multiple_all]
        } catch (NullPointerException e){
            Log.e(TAG,  "Usuario sin mascotas");
        }

    }

    public void getReservasPorContacto() {
        // [START get_multiple_all]

        Contacto contacto = (Contacto) ComunicadorContacto.getContacto();
        db.collection("hoteles")
                .whereEqualTo("idContacto", contacto.get_id())
                .orderBy("fechaInicio", Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (hotelContactoActivity!=null){
                                for (DocumentSnapshot document : task.getResult()) {
                                    hotelContactoActivity.bindeaYAñadeReserva(document,task.getResult().size());
                                }
                                hotelContactoActivity.inicimosAdaptador();
                            } else if (formularioActivity!=null) formularioActivity.bindeaReservas(task.getResult());

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple_all]
    }



    public void getReservas(Date fecha) {
        final Date fechaInicio = new Date();
        fechaInicio.setTime(fecha.getTime());
        fechaInicio.setHours(23);
        fechaInicio.setMinutes(00);
        fechaInicio.setSeconds(00);
        final Date fechaFin = new Date();
        fechaFin.setTime(fechaInicio.getTime());
        fechaFin.setHours(0);
        Log.e("finicioReserva", (String.valueOf(fechaInicio)));
        Log.e("finFinReserva", (String.valueOf(fechaFin)));
        // [START get_multiple_all]
        try {
                db.collection("hoteles")
                        .orderBy("fechaFin")
                       // .endBefore(fecha)
                        .whereGreaterThanOrEqualTo("fechaFin", fechaFin)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() == 0) {
                                        hotelActivity.inicimosAdaptador();
                                        Log.e(TAG, "No hay rReservas para esta fecha");
                                    } else {
                                        int i = 0,j=0;
                                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                            i++;
                                            if ((doc.getDate("fechaInicio").compareTo(fechaInicio) <= 0)&&(doc.getDate("fechaFin").compareTo(fechaFin) >= 0))
                                            {
                                               hotelActivity.bindeaYAñadeReservaHotel(doc,i,task.getResult().size());
                                               j++;
                                            }
                                            Log.e("finicioReserva", doc.getDate("fechaInicio").toString());
                                            Log.e("finFinReserva", doc.getDate("fechaFin").toString());
                                            if ((i==task.getResult().size()&&(j==0))) {
                                                hotelActivity.inicimosAdaptador();
                                                Log.e(TAG, "No hay rEeservas para esta fecha");
                                            }
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "Error getting documents", task.getException());
                                }
                            }
                        });

        } catch (IllegalArgumentException e) {
            Toast.makeText(hotelActivity, "No existen reservas en la base de datos", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }
        // [END get_multiple_all]
    }


    public void getTotalesDia(Date fecha) {
        Log.e("fechaDia", (String.valueOf(fecha)));
        Date fechaSig = new Date();
        fechaSig.setTime(fecha.getTime() + (3600000 * 24));
        // [START get_multiple_all]
        Log.e("fechaSig", (String.valueOf(fechaSig)));
        db.collection("citas")
                .orderBy("fecha")
                .startAt(fecha)
                .endAt(fechaSig)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double total = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                total += (Double) document.get("tarifa");
//                                Log.e("Total",String.valueOf(total));
//                                Log.e("fechaS",String.valueOf(document.getDate("fechaS").getDate()));
                            }
                            if (task.getResult().size() == 0) {
                                Log.e("Resultado", "No hay resultados");
                            }
                            Log.e("TotalesDia", String.valueOf(total));
                            totalesActivity.total_dia.setText(String.valueOf(total));
                        } else {
                            Log.e(TAG, "Error getting documents: " + "No hay documentos");
                        }
                    }
                });
        // [END get_multiple_all]
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getTotalesSemana(Date fecha) {
        Date fecha1 = new Date(fecha.getTime());//Si ponemos fechaS=fechaS->fechaS=fehca1 y altera valores  en gfetTotalesMes(fechaS)
        Log.e("fechaSemana", (String.valueOf(fecha1)));
        Date fechaSig = new Date();
        fechaSig.setTime(fecha.getTime() + (3600000 * 24));
        SimpleDateFormat sd = new SimpleDateFormat("EEEE");
        String dayofweek = sd.format(fecha1.getTime());
        Log.e("DiadelaSemana", dayofweek);
        switch (dayofweek) {
            case "lunes":
                break;
            case "martes":
                fecha1.setTime(fecha.getTime() - (3600000 * 24));
                break;
            case "miércoles":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 2));
                break;
            case "jueves":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 3));
                break;
            case "viernes":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 4));
                break;
            case "sábado":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 5));
                break;
            case "domingo":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 6));
                break;
        }
        Log.e("fechaSemana_fecha_del_lunes", (String.valueOf(fecha1)));

        fechaSig.setTime(fecha1.getTime() + (3600000*24*7));
        // [START get_multiple_all]
        Log.e("fechaSig", (String.valueOf(fechaSig)));
        db.collection("citas")
                .orderBy("fecha")
                .startAt(fecha1)
                .endAt(fechaSig)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double total = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                total += (Double) document.get("tarifa");
//                                Log.e("Total",String.valueOf(total));
//                                Log.e("fechaS",String.valueOf(document.getDate("fechaS").getDate()));
                            }
                            if (task.getResult().size() == 0) {
                                Log.e("Resultado", "No hay resultados");
                            }
                            Log.e("TotalesSemana", String.valueOf(total));
                            totalesActivity.total_semana.setText(String.valueOf(total));
                        } else {
                            Log.e(TAG, "Error getting documents: " + "No hay documentos");
                        }
                    }
                });
        // [END get_multiple_all]
    }

    public void getTotalesMes(Date fecha) {
        Date fecha1 = new Date(fecha.getYear(), fecha.getMonth(), 1);
        Log.e("fechaMes", (String.valueOf(fecha1)));
        Date fechaSig;
        if (fecha.getMonth() == 11) fechaSig = new Date(fecha.getYear() + 1, 0, 1);
        else fechaSig = new Date(fecha.getYear(), fecha.getMonth() + 1, 1);
        // [START get_multiple_all]
        Log.e("fechaMesSig", (String.valueOf(fechaSig)));
        db.collection("citas")
                .orderBy("fecha")
                .startAt(fecha1)
                .endAt(fechaSig)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double total = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                total += (Double) document.get("tarifa");
//                                Log.e("fechaS",String.valueOf(document.getDate("fechaS").getDate()));
//                                Log.e("SuTotal",String.valueOf(total));
                            }
                            if (task.getResult().size() == 0) {
                                Log.e("Resultado", "No hay resultados");
                            }
                            Log.e("TotalesMes", String.valueOf(total));
                            totalesActivity.total_mes.setText(String.valueOf(total));
                        } else {
                            Log.e(TAG, "Error getting documents: " + "No hay documentos");
                        }
                    }
                });
        // [END get_multiple_all]
    }

    public void getTotalesAño(Date fecha) {

        Date fecha1 = new Date(fecha.getTime());
        fecha1.setDate(1);
        fecha1.setMonth(0);
        Date fecha2 = new Date();
        fecha2.setDate(1);
        fecha2.setMonth(0);
        fecha2.setYear(fecha.getYear() + 1);
        Log.e("finicioAño", (String.valueOf(fecha1)));
        Log.e("ffinAño", (String.valueOf(fecha2)));
        db.collection("citas")
                .orderBy("fecha")
                .startAt(fecha1)
                .endAt(fecha2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double total = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                total += (Double) document.get("tarifa");
//                                Log.e("Total",String.valueOf(total));
//                                Log.e("fechaS",String.valueOf(document.getDate("fechaS").getDate()));
                            }
                            if (task.getResult().size() == 0) {
                                Log.e("Resultado", "No hay resultados");
                            }
                            Log.e("Totales", String.valueOf(total));
                            totalesActivity.total_año.setText(String.valueOf(total));
                        } else {
                            Log.e(TAG, "Error getting documents: " + "No hay documentos");
                        }
                    }
                });
        // [END get_multiple_all]
    }


    public void getTotalesHotelDia(Date fecha) {
        final Date fechaInicio = new Date();
        fechaInicio.setTime(fecha.getTime());
        fechaInicio.setHours(23);
        fechaInicio.setMinutes(00);
        fechaInicio.setSeconds(00);
        final Date fechaFin = new Date();
        fechaFin.setTime(fechaInicio.getTime());
        fechaFin.setHours(0);

        Log.e("fecha1394",fechaFin.toString());
        // [START get_multiple_all]
        try {
            db.collection("hoteles")
                    .orderBy("fechaFin")
                    // .endBefore(fecha)
                    .whereGreaterThanOrEqualTo("fechaFin", fechaFin)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() == 0) {
                                    Log.e("No hay valores totaleshotel dia ","0");
                                } else {
                                    double total=0,j=0;
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                        if ((doc.getDate("fechaInicio").compareTo(fechaInicio) <= 0)&&(doc.getDate("fechaFin").compareTo(fechaFin) >= 0))
                                        {
                                           total+=doc.getDouble("precio");
                                           Log.e("totalDia",String.valueOf(doc.getDouble("precio")));
                                        }
                                        j++;
                                        if (j==task.getResult().size()) {
                                            totalesActivity.total_hotel_dia.setText(String.valueOf(total));
                                            Log.e("totalDiaTotal",String.valueOf(total));
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "Error getting documents", task.getException());
                            }
                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getTotalesHotelSemana(Date fecha) {
        diaSemana=0;totalHotelSemana=0;diaSemana=0;
        Date fecha1 = new Date(fecha.getTime());//Si ponemos fechaS=fechaS->fechaS=fehca1 y altera valores  en gfetTotalesMes(fechaS)
        fecha1.setHours(23);
        Date fechaSig = new Date();
        fechaSig.setTime(fecha.getTime() + (3600000 * 24));
        SimpleDateFormat sd = new SimpleDateFormat("EEEE");
        String dayofweek = sd.format(fecha1.getTime());
        switch (dayofweek) {
            case "lunes":
                break;
            case "martes":
                fecha1.setTime(fecha.getTime() - (3600000 * 24));
                break;
            case "miércoles":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 2));
                break;
            case "jueves":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 3));
                break;
            case "viernes":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 4));
                break;
            case "sábado":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 5));
                break;
            case "domingo":
                fecha1.setTime(fecha.getTime() - (3600000 * 24 * 6));
                break;
        }

        calcula(fecha1);
        Date fecha2 = new Date(fecha1.getTime()+(3600000 * 24));
        calcula(fecha2);
        Date fecha3 = new Date(fecha2.getTime()+(3600000 * 24));
        calcula(fecha3);
        Date fecha4 = new Date(fecha3.getTime()+(3600000 * 24));
        calcula(fecha4);
        Date fecha5 = new Date(fecha4.getTime()+(3600000 * 24));
        calcula(fecha5);
        Date fecha6 = new Date(fecha5.getTime()+(3600000 * 24));
        calcula(fecha6);
        Date fecha7 = new Date(fecha6.getTime()+(3600000 * 24));
        calcula(fecha7);


    }

    private void calcula(Date fecha) {
        final Date fechaInicio = new Date();
        fechaInicio.setTime(fecha.getTime());
        fechaInicio.setHours(23);
        fechaInicio.setMinutes(00);
        fechaInicio.setSeconds(00);
        final Date fechaFin = new Date();
        fechaFin.setTime(fechaInicio.getTime());
        fechaFin.setHours(0);

        // [START get_multiple_all]
        try {
            db.collection("hoteles")
                    .orderBy("fechaFin")
                    // .endBefore(fecha)
                    .whereGreaterThanOrEqualTo("fechaFin", fechaFin)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() == 0) {
                                    Log.e(TAG, "No hay rReservas para esta fecha");
                                } else {
                                    double total=0,i=0;
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                        if ((doc.getDate("fechaInicio").compareTo(fechaInicio) <= 0)&&(doc.getDate("fechaFin").compareTo(fechaFin) >= 0))
                                        {
                                            total+=doc.getDouble("precio");
                                        }
                                        i++;
                                    }
                                    if (i==task.getResult().size()) {
                                        totalHotelSemana += total;
                                        diaSemana++;
                                        Log.e("totalhotelsemana:",diaSemana+":"+totalHotelSemana);
                                        if(diaSemana==7) totalesActivity.total_hotel_semana.setText(String.valueOf(totalHotelSemana));
                                    }
                                }
                            } else {
                                Log.e(TAG, "Error getting documents", task.getException());
                            }
                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void getTotalesHotelMes(Date fecha) {
        final Date fechaInicio = new Date();
        fechaInicio.setTime(fecha.getTime());
        fechaInicio.setDate(1);
        fechaInicio.setHours(23);
        fechaInicio.setMinutes(00);
        fechaInicio.setSeconds(00);
        final Date fechaFin = new Date();
        fechaFin.setTime(fechaInicio.getTime());
        switch (fechaFin.getMonth()){
            case 0: fechaFin.setDate(31);break;
            case 1: fechaFin.setDate(28);break;
            case 2: fechaFin.setDate(31);break;
            case 3: fechaFin.setDate(30);break;
            case 4: fechaFin.setDate(31);break;
            case 5: fechaFin.setDate(30);break;
            case 6: fechaFin.setDate(31);break;
            case 7: fechaFin.setDate(31);break;
            case 8: fechaFin.setDate(30);break;
            case 9: fechaFin.setDate(31);break;
            case 10:fechaFin.setDate(30);break;
            case 11:fechaFin.setDate(31);break;
        }
        fechaFin.setHours(0);
        try {
            db.collection("hoteles")
                    .orderBy("fechaFin")
                    // .endBefore(fecha)
                    .whereLessThanOrEqualTo("fechaFin", fechaFin)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() == 0) {
                                } else {
                                    double total=0,i=0;
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                        if ((doc.getDate("fechaInicio").compareTo(fechaInicio) >= 0)&&(doc.getDate("fechaFin").compareTo(fechaFin) <= 0))
                                        {
                                            total+=doc.getDouble("coste");
                                        }
                                        i++;
                                        if (i==task.getResult().size()) {
                                            totalesActivity.total_hotel_mes.setText(String.valueOf(total));
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "Error getting documents", task.getException());
                            }
                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void getTotalesHotelAño(Date fecha) {

        final Date fechaInicio = new Date(fecha.getTime());
        fechaInicio.setDate(1);
        fechaInicio.setMonth(0);
        fechaInicio.setHours(23);
        fechaInicio.setMinutes(00);
        fechaInicio.setSeconds(00);
        final Date fechaFin = new Date();
        fechaFin.setTime(fechaInicio.getTime());
        fechaFin.setMonth(11);
        fechaFin.setHours(0);
        try {
            db.collection("hoteles")
                    .orderBy("fechaFin")
                    // .endBefore(fecha)
                    .whereLessThanOrEqualTo("fechaFin", fechaFin)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() == 0) {
                                } else {
                                    double total=0,i=0;
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                        if ((doc.getDate("fechaInicio").compareTo(fechaInicio) >= 0)&&(doc.getDate("fechaFin").compareTo(fechaFin) <= 0))
                                        {
                                            total+=doc.getDouble("coste");
                                        }
                                        i++;
                                        if (i==task.getResult().size()) {
                                            totalesActivity.total_hotel_año.setText(String.valueOf(total));
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "Error getting documents", task.getException());
                            }
                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
        }
    }



    @Override
    public void listenToMultiple() {
        // [START listen_multiple]
        db.collection("cities")
                .whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<String> cities = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("name") != null) {
                                cities.add(doc.getString("name"));
                            }
                        }
                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });
        // [END listen_multiple]
    }

    @Override
    public void listenToDiffs() {
        // [START listen_diffs]
        db.collection("cities")
                .whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
        // [END listen_diffs]
    }

    @Override
    public void listenState() {
        // [START listen_state]
        db.collection("cities")
                .whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == Type.ADDED) {
                                Log.d(TAG, "New city: " + dc.getDocument().getData());
                            }
                        }

                        if (!snapshots.getMetadata().isFromCache()) {
                            Log.d(TAG, "Got initial state.");
                        }
                    }
                });
        // [END listen_state]
    }

    @Override
    public void detachListener() {
        // [START detach_listener]
        Query query = db.collection("cities");
        ListenerRegistration registration = query.addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    // [START_EXCLUDE]
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        // ...
                    }
                    // [END_EXCLUDE]
                });

        // ...

        // Stop listening to changes
        registration.remove();
        // [END detach_listener]
    }

    @Override
    public void handleListenErrors() {
        // [START handle_listen_errors]
        db.collection("cities")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == Type.ADDED) {
                                Log.d(TAG, "New city: " + dc.getDocument().getData());
                            }
                        }

                    }
                });
        // [END handle_listen_errors]
    }

    @Override
    public void exampleData() {
        // [START example_data]
        CollectionReference cities = db.collection("cities");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "San Francisco");
        data1.put("state", "CA");
        data1.put("country", "USA");
        data1.put("capital", false);
        data1.put("population", 860000);
        cities.document("SF").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Los Angeles");
        data2.put("state", "CA");
        data2.put("country", "USA");
        data2.put("capital", false);
        data2.put("population", 3900000);
        cities.document("LA").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Washington D.C.");
        data3.put("state", null);
        data3.put("country", "USA");
        data3.put("capital", true);
        data3.put("population", 680000);
        cities.document("DC").set(data3);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "Tokyo");
        data4.put("state", null);
        data4.put("country", "Japan");
        data4.put("capital", true);
        data4.put("population", 9000000);
        cities.document("TOK").set(data4);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "Beijing");
        data5.put("state", null);
        data5.put("country", "China");
        data5.put("capital", true);
        data5.put("population", 21500000);
        cities.document("BJ").set(data5);
        // [END example_data]
    }

    @Override
    public void simpleQueries() {
        // [START simple_queries]
        // Create a reference to the cities collection
        CollectionReference citiesRef = db.collection("cities");

        // Create a query against the collection.
        Query query = citiesRef.whereEqualTo("state", "CA");
        // [END simple_queries]

        // [START simple_query_capital]
        Query capitalCities = db.collection("cities").whereEqualTo("capital", true);
        // [END simple_query_capital]

        // [START example_filters]
        citiesRef.whereEqualTo("state", "CA");
        citiesRef.whereLessThan("population", 100000);
        citiesRef.whereGreaterThanOrEqualTo("name", "San Francisco");
        // [END example_filters]
    }

    @Override
    public void compoundQueries() {
        CollectionReference citiesRef = db.collection("cities");

        // [START chain_filters]
        citiesRef.whereEqualTo("state", "CO").whereEqualTo("name", "Denver");
        citiesRef.whereEqualTo("state", "CA").whereLessThan("population", 1000000);
        // [END chain_filters]

        // [START valid_range_filters]
        citiesRef.whereGreaterThanOrEqualTo("state", "CA")
                .whereLessThanOrEqualTo("state", "IN");
        citiesRef.whereEqualTo("state", "CA")
                .whereGreaterThan("population", 1000000);
        // [END valid_range_filters]
    }

    @Override
    public void compoundQueriesInvalid() {
        CollectionReference citiesRef = db.collection("cities");

        // [START invalid_range_filters]
        citiesRef.whereGreaterThanOrEqualTo("state", "CA").whereGreaterThan("population", 100000);
        // [END invalid_range_filters]
    }

    @Override
    public void orderAndLimit() {
        CollectionReference citiesRef = db.collection("cities");

        // [START order_and_limit]
        citiesRef.orderBy("name").limit(3);
        // [END order_and_limit]

        // [START order_and_limit_desc]
        citiesRef.orderBy("name", Direction.DESCENDING).limit(3);
        // [END order_and_limit_desc]

        // [START order_by_multiple]
        citiesRef.orderBy("state").orderBy("population", Direction.DESCENDING);
        // [END order_by_multiple]

        // [START filter_and_order]
        citiesRef.whereGreaterThan("population", 100000).orderBy("population").limit(2);
        // [END filter_and_order]

        // [START valid_filter_and_order]
        citiesRef.whereGreaterThan("population", 100000).orderBy("population");
        // [END valid_filter_and_order]
    }

    @Override
    public void orderAndLimitInvalid() {
        CollectionReference citiesRef = db.collection("cities");

        // [START invalid_filter_and_order]
        citiesRef.whereGreaterThan("population", 100000).orderBy("country");
        // [END invalid_filter_and_order]
    }

    @Override
    public void queryStartAtEndAt() {
        // [START query_start_at_single]
        // Get all cities with a population >= 1,000,000, ordered by population,
        db.collection("cities")
                .orderBy("population")
                .startAt(1000000);
        // [END query_start_at_single]

        // [START query_end_at_single]
        // Get all cities with a population <= 1,000,000, ordered by population,
        db.collection("cities")
                .orderBy("population")
                .endAt(1000000);
        // [END query_end_at_single]

        // [START query_start_at_doc_snapshot]
        // Get the data for "San Francisco"
        db.collection("cities").document("SF")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Get all cities with a population bigger than San Francisco.
                        Query biggerThanSf = db.collection("cities")
                                .orderBy("population")
                                .startAt(documentSnapshot);

                        // ...
                    }
                });
        // [END query_start_at_doc_snapshot]

        // [START query_pagination]
        // Construct query for first 25 cities, ordered by population
        Query first = db.collection("cities")
                .orderBy("population")
                .limit(25);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // ...

                        // Get the last visible document
                        DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);

                        // Construct a new query starting at this document,
                        // get the next 25 cities.
                        Query next = db.collection("cities")
                                .orderBy("population")
                                .startAfter(lastVisible)
                                .limit(25);

                        // Use the query for pagination
                        // ...
                    }
                });
        // [END query_pagination]

        // [START multi_cursor]
        // Will return all Springfields
        db.collection("cities")
                .orderBy("name")
                .orderBy("state")
                .startAt("Springfield");

        // Will return "Springfield, Missouri" and "Springfield, Wisconsin"
        db.collection("cities")
                .orderBy("name")
                .orderBy("state")
                .startAt("Springfield", "Missouri");
        // [END multi_cursor]
    }

    // [START delete_collection]

    /**
     * Delete all documents in a collection. Uses an Executor to perform work on a background
     * thread. This does *not* automatically discover and delete subcollections.
     */
    private Task<Void> deleteCollection(final CollectionReference collection,
                                        final int batchSize,
                                        Executor executor) {

        // Perform the delete operation on the provided Executor, which allows us to use
        // simpler synchronous logic without blocking the main thread.
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Get the first batch of documents in the collection
                Query query = collection.orderBy(FieldPath.documentId()).limit(batchSize);

                // Get a list of deleted documents
                List<DocumentSnapshot> deleted = deleteQueryBatch(query);

                // While the deleted documents in the last batch indicate that there
                // may still be more documents in the collection, page down to the
                // next batch and delete again
                while (deleted.size() >= batchSize) {
                    // Move the query cursor to start after the last doc in the batch
                    DocumentSnapshot last = deleted.get(deleted.size() - 1);
                    query = collection.orderBy(FieldPath.documentId())
                            .startAfter(last.getId())
                            .limit(batchSize);

                    deleted = deleteQueryBatch(query);
                }

                return null;
            }
        });

    }

    /**
     * Delete all results from a query in a single WriteBatch. Must be run on a worker thread
     * to avoid blocking/crashing the main thread.
     */
    @WorkerThread
    private List<DocumentSnapshot> deleteQueryBatch(final Query query) throws Exception {
        QuerySnapshot querySnapshot = Tasks.await(query.get());

        WriteBatch batch = query.getFirestore().batch();
        for (QueryDocumentSnapshot snapshot : querySnapshot) {
            batch.delete(snapshot.getReference());
        }
        Tasks.await(batch.commit());

        return querySnapshot.getDocuments();
    }
    // [END delete_collection]

    @Override
    public void toggleOffline() {
        // [START disable_network]
        db.disableNetwork()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Do offline things
                        // ...
                    }
                });
        // [END disable_network]

        // [START enable_network]
        db.enableNetwork()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Do online things
                        // ...
                    }
                });
        // [END enable_network]
    }

    @Override
    public void offlineListen(FirebaseFirestore db) {
        // [START offline_listen]
        db.collection("cities").whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen error", e);
                            return;
                        }

                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                            if (change.getType() == Type.ADDED) {
                                Log.d(TAG, "New city:" + change.getDocument().getData());
                            }

                            String source = querySnapshot.getMetadata().isFromCache() ?
                                    "local cache" : "server";
                            Log.d(TAG, "Data fetched from " + source);
                        }

                    }
                });
        // [END offline_listen]
    }


    // [START server_timestamp_annotation]
    public class MyObject {

        public String name;
        public @ServerTimestamp
        Date timestamp;

        public MyObject() {
        }
    }
    // [END server_timestamp_annotation]

    @Override
    public void updateWithServerTimestamp() {
        // [START update_with_server_timestamp]
        DocumentReference docRef = db.collection("contactos").document("some-id");

        // Update the timestamp field with the value from the server
        Map<String, Object> updates = new HashMap<>();
        updates.put("timestamp", FieldValue.serverTimestamp());

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            // [START_EXCLUDE]
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
            // [START_EXCLUDE]
        });
        // [END update_with_server_timestamp]
    }

    @Override
    public void updateDeleteField() {
        // [START update_delete_field]
        DocumentReference docRef = db.collection("cities").document("BJ");

        // Remove the 'capital' field from the document
        Map<String, Object> updates = new HashMap<>();
        updates.put("capital", FieldValue.delete());

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            // [START_EXCLUDE]
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
            // [START_EXCLUDE]
        });
        // [END update_delete_field]
    }
}