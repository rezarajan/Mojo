package bluefirelabs.mojo.handlers.online;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import bluefirelabs.mojo.R;

public class Payments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        final CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Card cardToSave = mCardInputWidget.getCard();
                if (cardToSave == null) {
                    //mErrorDialogHandler.showError("Invalid Card Data");
                    Snackbar.make(view, "Invalid Card Data", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    cardToSave.validateNumber();
                    cardToSave.validateCVC();
                    if(!cardToSave.validateCard()) {
                        Snackbar.make(view, "Card Data Invalid", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "Card Data Valid", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });
    }

}
