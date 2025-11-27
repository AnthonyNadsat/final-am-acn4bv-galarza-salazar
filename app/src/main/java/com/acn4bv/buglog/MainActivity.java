package com.acn4bv.buglog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText etNombreJuego, etDescripcion, etImagenUrl;
    private Spinner spPlataforma, spTipoBug;
    private RadioGroup rgGravedad;
    private Button btnReportar, btnVerLista, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!UserRole.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        etNombreJuego = findViewById(R.id.etNombreJuego);
        etDescripcion = findViewById(R.id.etDescripcion);
        etImagenUrl   = findViewById(R.id.etImagenUrl);
        spPlataforma  = findViewById(R.id.spPlataforma);
        spTipoBug     = findViewById(R.id.spTipoBug);
        rgGravedad    = findViewById(R.id.rgGravedad);
        btnReportar   = findViewById(R.id.btnReportar);
        btnVerLista   = findViewById(R.id.btnVerLista);
        btnLogout     = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> logout());

        ArrayAdapter<CharSequence> adapterPlataforma = ArrayAdapter.createFromResource(
                this,
                R.array.plataformas,
                R.layout.spinner_item
        );
        adapterPlataforma.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spPlataforma.setAdapter(adapterPlataforma);

        ArrayAdapter<CharSequence> adapterTipoBug = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_bug,
                R.layout.spinner_item
        );
        adapterTipoBug.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spTipoBug.setAdapter(adapterTipoBug);

        btnReportar.setOnClickListener(v -> reportar());

        btnVerLista.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ListaBugsActivity.class))
        );
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        UserRole.clear();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void reportar() {
        String nombre = etNombreJuego.getText().toString().trim();
        String desc   = etDescripcion.getText().toString().trim();
        String imagenUrl = etImagenUrl.getText().toString().trim(); // opcional

        int posPlat = spPlataforma.getSelectedItemPosition();
        int posTipo = spTipoBug.getSelectedItemPosition();

        int checked = rgGravedad.getCheckedRadioButtonId();
        String gravedad = null;
        if (checked == R.id.rbBaja)  gravedad = getString(R.string.gravedad_baja);
        if (checked == R.id.rbMedia) gravedad = getString(R.string.gravedad_media);
        if (checked == R.id.rbAlta)  gravedad = getString(R.string.gravedad_alta);

        if (TextUtils.isEmpty(nombre)) { toast(R.string.error_nombre_vacio); return; }
        if (posPlat == 0)              { toast(R.string.error_plataforma);   return; }
        if (posTipo == 0)              { toast(R.string.error_tipo_bug);     return; }
        if (gravedad == null)          { toast(R.string.error_gravedad);     return; }
        if (TextUtils.isEmpty(desc))   { toast(R.string.error_descripcion);  return; }

        String plataforma = spPlataforma.getSelectedItem().toString();
        String tipo       = spTipoBug.getSelectedItem().toString();

        Bug nuevo = new Bug(nombre, plataforma, tipo, gravedad, desc);

        if (!imagenUrl.isEmpty()) {
            nuevo.setImagenUrl(imagenUrl);
        }

        FirestoreRepository.agregarBug(nuevo);

        toast(R.string.bug_reportado);

        etNombreJuego.setText("");
        etDescripcion.setText("");
        etImagenUrl.setText("");
        spPlataforma.setSelection(0);
        spTipoBug.setSelection(0);
        rgGravedad.clearCheck();
    }

    private void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}