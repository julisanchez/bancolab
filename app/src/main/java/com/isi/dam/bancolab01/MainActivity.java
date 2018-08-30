package com.isi.dam.bancolab01;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.isi.dam.bancolab01.modelo.Cliente;
import com.isi.dam.bancolab01.modelo.Moneda;
import com.isi.dam.bancolab01.modelo.PlazoFijo;

public class MainActivity extends AppCompatActivity {

    private PlazoFijo pf;
    private Cliente cliente;

    // widgets de la vista
    private EditText edtMail;
    private EditText edtCuit;
    private RadioGroup optMoneda;
    private EditText edtMonto;
    private SeekBar seekDias;
    private TextView tvDiasSeleccionados;
    private TextView tvIntereses;
    private Switch swAvisarVencimiento;
    private ToggleButton togAccion;
    private CheckBox chkAceptoTerminos;
    private Button btnHacerPF;
    private EditText edtResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pf = new PlazoFijo(getResources().getStringArray(R.array.tasas));
        cliente = new Cliente();

        // widgets de la vista
        edtMail= (EditText) findViewById(R.id.edtMail);
        edtCuit= (EditText) findViewById(R.id.edtCuit);
        optMoneda= (RadioGroup) findViewById(R.id.optMoneda);
        edtMonto= (EditText) findViewById(R.id.edtMonto);
        seekDias= (SeekBar) findViewById(R.id.seekDias);
        tvDiasSeleccionados= (TextView)  findViewById(R.id.tvDiasSeleccionados);
        tvIntereses= (TextView) findViewById(R.id.tvIntereses);
        swAvisarVencimiento= (Switch) findViewById(R.id.swAvisarVencimiento);
        togAccion= (ToggleButton) findViewById(R.id.togAccion);
        chkAceptoTerminos= (CheckBox) findViewById(R.id.chkAceptoTerminos);
        btnHacerPF = (Button) findViewById(R.id.btnHacerPF);
        edtResultados = (EditText) findViewById(R.id.edtResultados);

        btnHacerPF.setEnabled(false);
        tvDiasSeleccionados.setText(seekDias.getProgress()+" dias de plazo.");

        seekDias.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged (SeekBar seekBar,int i, boolean b){
                // actualizar el textview de dias
                tvDiasSeleccionados.setText(i+" dias de plazo.");
                // setear los dias en el plazo fijo
                pf.setDias(i);
                // actualizar el caluclo de los intereses pagados
                if(!edtMonto.getText().toString().isEmpty())
                    pf.setMonto(Double.parseDouble(edtMonto.getText().toString()));
                tvIntereses.setText("$"+pf.intereses());
            }
            @Override
            public void onStartTrackingTouch (SeekBar seekBar){ // no hacer nada
            }
            @Override
            public void onStopTrackingTouch (SeekBar seekBar){ // no hacer nada
            }
        });

        chkAceptoTerminos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /// RESOLVER logica
                btnHacerPF.setEnabled(b);
                if(!b){
                    String text = "Es obligatorio aceptar las condiciones";
                    Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
                }
            } });

        btnHacerPF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                View toastLayout;

                if(verificarCampos()){
                    pf.setMonto(Double.parseDouble(edtMonto.getText().toString()));
                    pf.setAvisarVencimiento(swAvisarVencimiento.isChecked());
                    pf.setRenovarAutomaticamente(togAccion.isChecked());

                    //Obtener moneda seleccionada
                    int selectedRadioButtonID = optMoneda.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
                    String selectedRadioButtonText = selectedRadioButton.getText().toString();

                    //Setear moneda
                    if(selectedRadioButtonText.equals(getString(R.string.lblMonedaDolar))) pf.setMoneda(Moneda.DOLAR);
                    else pf.setMoneda(Moneda.PESO);

                    edtResultados.setTextColor(Color.BLUE);
                    edtResultados.setText("El plazo fijo se realizó exitosamente\n");
                    edtResultados.getText().append(pf.toString());

                    toastLayout = inflater.inflate(R.layout.toast_pf_correctamente, (ViewGroup) findViewById(R.id.toast_pf_correctamente_layout));
                }
                else{
                    toastLayout = inflater.inflate(R.layout.toast_pf_error, (ViewGroup) findViewById(R.id.toast_pf_error_layout));
                }
                toast.setView(toastLayout);
                toast.show();
            }
        });
    }

    private Boolean verificarCampos(){
        Boolean valido = true;

        edtResultados.getText().clear();

        if(edtMail.getText().toString().isEmpty()){
            valido = false;
            edtResultados.getText().append("El mail no debe ser vacío\n");
        }
        if(edtCuit.getText().toString().isEmpty()){
            valido = false;
            edtResultados.getText().append("El cuit/cuil no debe ser vacio\n");
        }
        if(edtMonto.getText().toString().isEmpty()){
            valido = false;
            edtResultados.getText().append("El monto no debe ser vacio\n");
        }
        else if(Double.parseDouble(edtMonto.getText().toString())<=0){
           valido = false;
           edtResultados.getText().append("El monto debe ser mayor que 0\n");
        }
        if(seekDias.getProgress() <= 10){
            valido = false;
            edtResultados.getText().append("La cantidad de dias de plazo debe ser mayor que 10\n");
        }

        if(!valido) edtResultados.setTextColor(Color.RED);

        return valido;
    }

}
