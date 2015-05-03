package com.example.hjcyz1991.project407;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by hjcyz1991 on 5/2/15.
 */
public class PaymentMethodDialog extends DialogFragment implements View.OnClickListener{

    Button settleByCash, settleByPaypal;
    Communicator communicator;
    String receiver;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        receiver = getArguments().getString("receiver");
        View view = inflater.inflate(R.layout.fragment_payment_method_dialog, null);
        settleByCash = (Button) view.findViewById(R.id.cash_button);
        settleByPaypal = (Button) view.findViewById(R.id.paypal_button);
        settleByCash.setOnClickListener(this);
        settleByPaypal.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    public void onClick(View view){
        if(view.getId() == R.id.cash_button){
            dismiss();
            communicator.onDialogMessage("Thank you, " + receiver + " is notified");
        }else{
            dismiss();
        }
    }

    interface Communicator{
        public void onDialogMessage(String message);
    }

}
